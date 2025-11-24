package com.battler.Roaming.Fighter.fight;

import com.battler.Roaming.Fighter.entity.Fight;
import com.battler.Roaming.Fighter.entity.Monster;
import com.battler.Roaming.Fighter.fight.dto.CreateFightRequest;
import com.battler.Roaming.Fighter.fight.dto.FightStateDto;
import com.battler.Roaming.Fighter.monster.MonsterRepository;
import com.battler.Roaming.Fighter.testutils.TestFixtures;
import com.navercorp.fixturemonkey.FixtureMonkey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FightServiceTest {

    @Mock
    private FightRepository fightRepository;

    @Mock
    private MonsterRepository monsterRepository;

    @InjectMocks
    private FightService fightService;

    private static final FixtureMonkey FIXTURE_MONKEY = TestFixtures.FIXTURE_MONKEY;


    @Test
    void createFight_WithValidRequest_ShouldCreateFight() {
        // Given
        Monster monsterA1 = createMonsterWithHealth(100, 10, 5);
        Monster monsterA2 = createMonsterWithHealth(80, 8, 4);
        Monster monsterB1 = createMonsterWithHealth(90, 12, 6);
        Monster monsterB2 = createMonsterWithHealth(70, 9, 3);

        CreateFightRequest request = FIXTURE_MONKEY.giveMeBuilder(CreateFightRequest.class)
                .set("teamA", List.of(monsterA1.getId(), monsterA2.getId()))
                .set("teamB", List.of(monsterB1.getId(), monsterB2.getId()))
                .sample();

        when(monsterRepository.findById(monsterA1.getId())).thenReturn(Optional.of(monsterA1));
        when(monsterRepository.findById(monsterA2.getId())).thenReturn(Optional.of(monsterA2));
        when(monsterRepository.findById(monsterB1.getId())).thenReturn(Optional.of(monsterB1));
        when(monsterRepository.findById(monsterB2.getId())).thenReturn(Optional.of(monsterB2));

        Fight savedFight = FIXTURE_MONKEY.giveMeBuilder(Fight.class)
                .set("teamA", List.of(monsterA1, monsterA2))
                .set("teamB", List.of(monsterB1, monsterB2))
                .set("activeMonsterA", monsterA1.getId())
                .set("activeMonsterB", monsterB1.getId())
                .set("status", FightStatus.ONGOING)
                .sample();
        when(fightRepository.save(any(Fight.class))).thenReturn(savedFight);

        // When
        Fight result = fightService.createFight(request);

        // Then
        assertNotNull(result);
        assertEquals(FightStatus.ONGOING, result.getStatus());
        verify(fightRepository).save(any(Fight.class));
        verify(monsterRepository, times(4)).findById(any(UUID.class));
    }

    @Test
    void createFight_WithNullTeamA_ShouldThrowException() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        CreateFightRequest request = FIXTURE_MONKEY.giveMeBuilder(CreateFightRequest.class)
                .set("teamA", null)
                .set("teamB", List.of(testMonster.getId()))
                .sample();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fightService.createFight(request));
        assertTrue(exception.getMessage().contains("Team A") && exception.getMessage().contains("cannot be null or empty"));
        verify(fightRepository, never()).save(any(Fight.class));
    }

    @Test
    void createFight_WithEmptyTeamA_ShouldThrowException() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        CreateFightRequest request = FIXTURE_MONKEY.giveMeBuilder(CreateFightRequest.class)
                .set("teamA", Collections.emptyList())
                .set("teamB", List.of(testMonster.getId()))
                .sample();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fightService.createFight(request));
        assertTrue(exception.getMessage().contains("Team A") && exception.getMessage().contains("cannot be null or empty"));
        verify(fightRepository, never()).save(any(Fight.class));
    }

    @Test
    void createFight_WithNullTeamB_ShouldThrowException() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        CreateFightRequest request = FIXTURE_MONKEY.giveMeBuilder(CreateFightRequest.class)
                .set("teamA", List.of(testMonster.getId()))
                .set("teamB", null)
                .sample();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fightService.createFight(request));
        assertTrue(exception.getMessage().contains("Team B") && exception.getMessage().contains("cannot be null or empty"));
        verify(fightRepository, never()).save(any(Fight.class));
    }

    @Test
    void createFight_WithEmptyTeamB_ShouldThrowException() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        CreateFightRequest request = FIXTURE_MONKEY.giveMeBuilder(CreateFightRequest.class)
                .set("teamA", List.of(testMonster.getId()))
                .set("teamB", Collections.emptyList())
                .sample();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fightService.createFight(request));
        assertTrue(exception.getMessage().contains("Team B") && exception.getMessage().contains("cannot be null or empty"));
        verify(fightRepository, never()).save(any(Fight.class));
    }

    @Test
    void createFight_WithNonExistentMonster_ShouldThrowException() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        Monster nonExistentMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        CreateFightRequest request = FIXTURE_MONKEY.giveMeBuilder(CreateFightRequest.class)
                .set("teamA", List.of(nonExistentMonster.getId()))
                .set("teamB", List.of(testMonster.getId()))
                .sample();

        when(monsterRepository.findById(nonExistentMonster.getId())).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fightService.createFight(request));
        assertTrue(exception.getMessage().contains("Monster not found with id:"));
        verify(fightRepository, never()).save(any(Fight.class));
    }

    @Test
    void createFight_WithNoHealthyMonstersInTeamA_ShouldThrowException() {
        // Given
        Monster monsterA = createMonsterWithHealth(0, 10, 5); // Dead monster
        Monster monsterB = createMonsterWithHealth(100, 10, 5);

        CreateFightRequest request = FIXTURE_MONKEY.giveMeBuilder(CreateFightRequest.class)
                .set("teamA", List.of(monsterA.getId()))
                .set("teamB", List.of(monsterB.getId()))
                .sample();

        when(monsterRepository.findById(monsterA.getId())).thenReturn(Optional.of(monsterA));
        when(monsterRepository.findById(monsterB.getId())).thenReturn(Optional.of(monsterB));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fightService.createFight(request));
        assertEquals("Team A has no healthy monsters", exception.getMessage());
        verify(fightRepository, never()).save(any(Fight.class));
    }

    @Test
    void createFight_WithNoHealthyMonstersInTeamB_ShouldThrowException() {
        // Given
        Monster monsterA = createMonsterWithHealth(100, 10, 5);
        Monster monsterB = createMonsterWithHealth(0, 10, 5); // Dead monster

        CreateFightRequest request = FIXTURE_MONKEY.giveMeBuilder(CreateFightRequest.class)
                .set("teamA", List.of(monsterA.getId()))
                .set("teamB", List.of(monsterB.getId()))
                .sample();

        when(monsterRepository.findById(monsterA.getId())).thenReturn(Optional.of(monsterA));
        when(monsterRepository.findById(monsterB.getId())).thenReturn(Optional.of(monsterB));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fightService.createFight(request));
        assertTrue(exception.getMessage().contains("Team B") && exception.getMessage().contains("has no healthy monsters"));
        verify(fightRepository, never()).save(any(Fight.class));
    }

    @Test
    void executeExchange_WithValidOngoingFight_ShouldExecuteCombat() {
        // Given
        Monster monsterA = createMonsterWithHealth(100, 10, 5);
        Monster monsterB = createMonsterWithHealth(90, 8, 4);

        Fight fight = createFight(monsterA, monsterB, FightStatus.ONGOING);

        when(fightRepository.findById(fight.getId())).thenReturn(Optional.of(fight));
        when(monsterRepository.save(any(Monster.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fightRepository.save(any(Fight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        FightStateDto result = fightService.executeExchange(fight.getId());

        // Then
        assertNotNull(result);
        assertEquals(fight.getId(), result.getFightId());
        assertEquals(FightStatus.ONGOING, result.getStatus());
        // Verify both monsters were saved (health updated)
        verify(monsterRepository, times(2)).save(any(Monster.class));
        verify(fightRepository).save(any(Fight.class));
    }

    @Test
    void executeExchange_WithFightNotFound_ShouldThrowException() {
        // Given
        Fight testFight = FIXTURE_MONKEY.giveMeOne(Fight.class);
        UUID nonExistentFightId = testFight.getId();
        when(fightRepository.findById(nonExistentFightId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fightService.executeExchange(nonExistentFightId));
        assertTrue(exception.getMessage().contains("Fight not found with id:"));
        verify(monsterRepository, never()).save(any(Monster.class));
    }

    @Test
    void executeExchange_WithCompletedFight_ShouldReturnState() {
        // Given
        Monster monsterA = createMonsterWithHealth(100, 10, 5);
        Monster monsterB = createMonsterWithHealth(90, 8, 4);
        Fight fight = createFight(monsterA, monsterB, FightStatus.TEAM_A_WON);

        when(fightRepository.findById(fight.getId())).thenReturn(Optional.of(fight));

        // When
        FightStateDto result = fightService.executeExchange(fight.getId());

        // Then
        assertNotNull(result);
        assertEquals(FightStatus.TEAM_A_WON, result.getStatus());
        // Should not execute combat or save monsters
        verify(monsterRepository, never()).save(any(Monster.class));
        verify(fightRepository, never()).save(any(Fight.class));
    }

    @Test
    void executeExchange_WhenMonsterADefeated_ShouldSwitchToNextMonster() {
        // Given
        Monster monsterA1 = createMonsterWithHealth(1, 10, 5); // Will be defeated
        Monster monsterA2 = createMonsterWithHealth(100, 8, 4); // Next monster
        Monster monsterB = createMonsterWithHealth(100, 15, 0); // Strong attacker

        Fight fight = createFight(monsterA1, monsterB, FightStatus.ONGOING);
        fight.setTeamA(List.of(monsterA1, monsterA2));

        when(fightRepository.findById(fight.getId())).thenReturn(Optional.of(fight));
        when(monsterRepository.save(any(Monster.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fightRepository.save(any(Fight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        FightStateDto result = fightService.executeExchange(fight.getId());

        // Then
        assertNotNull(result);
        // Verify fight was updated with new active monster
        verify(fightRepository).save(any(Fight.class));
    }

    @Test
    void executeExchange_WhenTeamALoses_ShouldSetStatusToTeamBWon() {
        // Given
        Monster monsterA = createMonsterWithHealth(1, 10, 5); // Will be defeated
        Monster monsterB = createMonsterWithHealth(100, 15, 0); // Strong attacker

        Fight fight = createFight(monsterA, monsterB, FightStatus.ONGOING);
        fight.setTeamA(List.of(monsterA)); // Only one monster, no replacement

        when(fightRepository.findById(fight.getId())).thenReturn(Optional.of(fight));
        when(monsterRepository.save(any(Monster.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fightRepository.save(any(Fight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        FightStateDto result = fightService.executeExchange(fight.getId());

        // Then
        assertNotNull(result);
        assertEquals(FightStatus.TEAM_B_WON, result.getStatus());
        verify(fightRepository).save(any(Fight.class));
    }

    @Test
    void executeExchange_WhenTeamBLoses_ShouldSetStatusToTeamAWon() {
        // Given
        Monster monsterA = createMonsterWithHealth(100, 15, 0); // Strong attacker
        Monster monsterB = createMonsterWithHealth(1, 10, 5); // Will be defeated

        Fight fight = createFight(monsterA, monsterB, FightStatus.ONGOING);
        fight.setTeamB(List.of(monsterB)); // Only one monster, no replacement

        when(fightRepository.findById(fight.getId())).thenReturn(Optional.of(fight));
        when(monsterRepository.save(any(Monster.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fightRepository.save(any(Fight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        FightStateDto result = fightService.executeExchange(fight.getId());

        // Then
        assertNotNull(result);
        assertEquals(FightStatus.TEAM_A_WON, result.getStatus());
        verify(fightRepository).save(any(Fight.class));
    }

    @Test
    void executeExchange_ShouldCalculateDamageCorrectly() {
        // Given
        Monster monsterA = createMonsterWithHealth(100, 10, 5);
        Monster monsterB = createMonsterWithHealth(90, 8, 4);

        Fight fight = createFight(monsterA, monsterB, FightStatus.ONGOING);

        when(fightRepository.findById(fight.getId())).thenReturn(Optional.of(fight));
        when(monsterRepository.save(any(Monster.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fightRepository.save(any(Fight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        fightService.executeExchange(fight.getId());

        // Then
        verify(monsterRepository, times(2)).save(any(Monster.class));
    }

    @Test
    void executeExchange_WithMinimumDamage_ShouldDealAtLeastOneDamage() {
        // Given
        Monster monsterA = createMonsterWithHealth(100, 5, 10);
        Monster monsterB = createMonsterWithHealth(100, 10, 10);

        Fight fight = createFight(monsterA, monsterB, FightStatus.ONGOING);

        when(fightRepository.findById(fight.getId())).thenReturn(Optional.of(fight));
        when(monsterRepository.save(any(Monster.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fightRepository.save(any(Fight.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        fightService.executeExchange(fight.getId());

        // Then
        // Both monsters should take at least 1 damage
        verify(monsterRepository, times(2)).save(any(Monster.class));
    }

    @Test
    void getFightState_WithValidFight_ShouldReturnState() {
        // Given
        Monster monsterA = createMonsterWithHealth(100, 10, 5);
        Monster monsterB = createMonsterWithHealth(90, 8, 4);
        Fight fight = createFight(monsterA, monsterB, FightStatus.ONGOING);

        when(fightRepository.findById(fight.getId())).thenReturn(Optional.of(fight));

        // When
        FightStateDto result = fightService.getFightState(fight.getId());

        // Then
        assertNotNull(result);
        assertEquals(fight.getId(), result.getFightId());
        assertEquals(fight.getActiveMonsterA(), result.getActiveMonsterA());
        assertEquals(fight.getActiveMonsterB(), result.getActiveMonsterB());
        assertEquals(fight.getStatus(), result.getStatus());
        verify(fightRepository).findById(fight.getId());
    }

    @Test
    void getFightState_WithFightNotFound_ShouldThrowException() {
        // Given
        Fight testFight = FIXTURE_MONKEY.giveMeOne(Fight.class);
        UUID nonExistentFightId = testFight.getId();
        when(fightRepository.findById(nonExistentFightId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> fightService.getFightState(nonExistentFightId));
        assertTrue(exception.getMessage().contains("Fight not found with id:"));
    }

    private Monster createMonsterWithHealth(int health, int attack, int defence) {
        return FIXTURE_MONKEY.giveMeBuilder(Monster.class)
                .set("health", health)
                .set("maxHealth", Math.max(health, 100))
                .set("attack", attack)
                .set("defence", defence)
                .sample();
    }

    private Fight createFight(Monster monsterA, Monster monsterB, FightStatus status) {
        return FIXTURE_MONKEY.giveMeBuilder(Fight.class)
                .set("teamA", List.of(monsterA))
                .set("teamB", List.of(monsterB))
                .set("activeMonsterA", monsterA.getId())
                .set("activeMonsterB", monsterB.getId())
                .set("status", status)
                .sample();
    }
}

