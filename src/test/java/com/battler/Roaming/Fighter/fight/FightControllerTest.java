package com.battler.Roaming.Fighter.fight;

import com.battler.Roaming.Fighter.entity.Fight;
import com.battler.Roaming.Fighter.entity.Monster;
import com.battler.Roaming.Fighter.fight.dto.CreateFightRequest;
import com.battler.Roaming.Fighter.fight.dto.FightStateDto;
import com.battler.Roaming.Fighter.testutils.TestFixtures;
import com.navercorp.fixturemonkey.FixtureMonkey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FightControllerTest {

    @Mock
    private FightService fightService;

    @InjectMocks
    private FightController fightController;

    private static final FixtureMonkey FIXTURE_MONKEY = TestFixtures.FIXTURE_MONKEY;

    @Test
    void createFight_WithValidRequest_ShouldReturnCreatedFight() {
        // Given
        Monster monsterA1 = createMonsterWithHealth(100, 10, 5);
        Monster monsterA2 = createMonsterWithHealth(80, 8, 4);
        Monster monsterB1 = createMonsterWithHealth(90, 12, 6);
        Monster monsterB2 = createMonsterWithHealth(70, 9, 3);

        CreateFightRequest request = FIXTURE_MONKEY.giveMeBuilder(CreateFightRequest.class)
                .set("teamA", List.of(monsterA1.getId(), monsterA2.getId()))
                .set("teamB", List.of(monsterB1.getId(), monsterB2.getId()))
                .sample();

        Fight createdFight = FIXTURE_MONKEY.giveMeBuilder(Fight.class)
                .set("teamA", List.of(monsterA1, monsterA2))
                .set("teamB", List.of(monsterB1, monsterB2))
                .set("activeMonsterA", monsterA1.getId())
                .set("activeMonsterB", monsterB1.getId())
                .set("status", FightStatus.ONGOING)
                .sample();

        when(fightService.createFight(request)).thenReturn(createdFight);

        // When
        ResponseEntity<Fight> response = fightController.createFight(request);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        Fight responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(createdFight, responseBody);
        assertEquals(FightStatus.ONGOING, responseBody.getStatus());
        verify(fightService).createFight(request);
    }

    @Test
    void createFight_WithInvalidRequest_ShouldReturnBadRequest() {
        // Given
        CreateFightRequest request = FIXTURE_MONKEY.giveMeBuilder(CreateFightRequest.class)
                .set("teamA", null)
                .set("teamB", List.of(UUID.randomUUID()))
                .sample();

        when(fightService.createFight(request))
                .thenThrow(new IllegalArgumentException("Team A cannot be null or empty"));

        // When
        ResponseEntity<Fight> response = fightController.createFight(request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(fightService).createFight(request);
    }

    @Test
    void createFight_WithNonExistentMonster_ShouldReturnBadRequest() {
        // Given
        CreateFightRequest request = FIXTURE_MONKEY.giveMeBuilder(CreateFightRequest.class)
                .set("teamA", List.of(UUID.randomUUID()))
                .set("teamB", List.of(UUID.randomUUID()))
                .sample();

        when(fightService.createFight(request))
                .thenThrow(new IllegalArgumentException("Monster not found with id: " + request.getTeamA().getFirst()));

        // When
        ResponseEntity<Fight> response = fightController.createFight(request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(fightService).createFight(request);
    }

    @Test
    void executeExchange_WithValidFightId_ShouldReturnFightState() {
        // Given
        Fight fight = FIXTURE_MONKEY.giveMeOne(Fight.class);
        UUID fightId = fight.getId();

        FightStateDto fightState = FIXTURE_MONKEY.giveMeBuilder(FightStateDto.class)
                .set("fightId", fightId)
                .set("status", FightStatus.ONGOING)
                .sample();

        when(fightService.executeExchange(fightId)).thenReturn(fightState);

        // When
        ResponseEntity<FightStateDto> response = fightController.executeExchange(fightId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        FightStateDto responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(fightState, responseBody);
        assertEquals(fightId, responseBody.getFightId());
        verify(fightService).executeExchange(fightId);
    }

    @Test
    void executeExchange_WithNonExistentFightId_ShouldReturnNotFound() {
        // Given
        UUID nonExistentFightId = UUID.randomUUID();

        when(fightService.executeExchange(nonExistentFightId))
                .thenThrow(new IllegalArgumentException("Fight not found with id: " + nonExistentFightId));

        // When
        ResponseEntity<FightStateDto> response = fightController.executeExchange(nonExistentFightId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(fightService).executeExchange(nonExistentFightId);
    }

    @Test
    void executeExchange_WithCompletedFight_ShouldReturnFightState() {
        // Given
        Fight fight = FIXTURE_MONKEY.giveMeOne(Fight.class);
        UUID fightId = fight.getId();

        FightStateDto fightState = FIXTURE_MONKEY.giveMeBuilder(FightStateDto.class)
                .set("fightId", fightId)
                .set("status", FightStatus.TEAM_A_WON)
                .sample();

        when(fightService.executeExchange(fightId)).thenReturn(fightState);

        // When
        ResponseEntity<FightStateDto> response = fightController.executeExchange(fightId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        FightStateDto responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(FightStatus.TEAM_A_WON, responseBody.getStatus());
        verify(fightService).executeExchange(fightId);
    }

    @Test
    void getFightState_WithValidFightId_ShouldReturnFightState() {
        // Given
        Fight fight = FIXTURE_MONKEY.giveMeOne(Fight.class);
        UUID fightId = fight.getId();

        FightStateDto fightState = FIXTURE_MONKEY.giveMeBuilder(FightStateDto.class)
                .set("fightId", fightId)
                .set("status", FightStatus.ONGOING)
                .sample();

        when(fightService.getFightState(fightId)).thenReturn(fightState);

        // When
        ResponseEntity<FightStateDto> response = fightController.getFightState(fightId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        FightStateDto responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(fightState, responseBody);
        assertEquals(fightId, responseBody.getFightId());
        verify(fightService).getFightState(fightId);
    }

    @Test
    void getFightState_WithNonExistentFightId_ShouldReturnNotFound() {
        // Given
        UUID nonExistentFightId = UUID.randomUUID();

        when(fightService.getFightState(nonExistentFightId))
                .thenThrow(new IllegalArgumentException("Fight not found with id: " + nonExistentFightId));

        // When
        ResponseEntity<FightStateDto> response = fightController.getFightState(nonExistentFightId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(fightService).getFightState(nonExistentFightId);
    }

    @Test
    void getFightState_WithCompletedFight_ShouldReturnFightState() {
        // Given
        Fight fight = FIXTURE_MONKEY.giveMeOne(Fight.class);
        UUID fightId = fight.getId();

        FightStateDto fightState = FIXTURE_MONKEY.giveMeBuilder(FightStateDto.class)
                .set("fightId", fightId)
                .set("status", FightStatus.TEAM_B_WON)
                .sample();

        when(fightService.getFightState(fightId)).thenReturn(fightState);

        // When
        ResponseEntity<FightStateDto> response = fightController.getFightState(fightId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        FightStateDto responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(FightStatus.TEAM_B_WON, responseBody.getStatus());
        verify(fightService).getFightState(fightId);
    }

    private Monster createMonsterWithHealth(int health, int attack, int defence) {
        return FIXTURE_MONKEY.giveMeBuilder(Monster.class)
                .set("health", health)
                .set("maxHealth", Math.max(health, 100))
                .set("attack", attack)
                .set("defence", defence)
                .sample();
    }
}
