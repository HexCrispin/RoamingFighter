package com.battler.Roaming.Fighter.monster;

import com.battler.Roaming.Fighter.entity.Monster;
import com.battler.Roaming.Fighter.testutils.TestFixtures;
import com.navercorp.fixturemonkey.FixtureMonkey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MonsterControllerTest {

    @Mock
    private MonsterService monsterService;

    @InjectMocks
    private MonsterController monsterController;

    private static final FixtureMonkey FIXTURE_MONKEY = TestFixtures.FIXTURE_MONKEY;

    @Test
    void getAllMonsters_ShouldReturnListOfMonsters() {
        // Given
        List<Monster> monsters = FIXTURE_MONKEY.giveMe(Monster.class, 1);
        when(monsterService.getAllMonsters()).thenReturn(monsters);

        // When
        ResponseEntity<List<Monster>> response = monsterController.getAllMonsters();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(monsters, response.getBody());
        verify(monsterService).getAllMonsters();
    }

    @Test
    void getMonsterById_WhenMonsterExists_ShouldReturnMonster() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        when(monsterService.getMonsterById(monsterId)).thenReturn(Optional.of(testMonster));

        // When
        ResponseEntity<Monster> response = monsterController.getMonsterById(monsterId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testMonster, response.getBody());
        verify(monsterService).getMonsterById(monsterId);
    }

    @Test
    void getMonsterById_WhenMonsterDoesNotExist_ShouldReturnNotFound() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        when(monsterService.getMonsterById(monsterId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Monster> response = monsterController.getMonsterById(monsterId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(monsterService).getMonsterById(monsterId);
    }

    @Test
    void getMonstersByName_ShouldReturnListOfMonsters() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        String monsterName = testMonster.getName();
        List<Monster> monsters = FIXTURE_MONKEY.giveMe(Monster.class, 1);
        when(monsterService.getMonstersByName(monsterName)).thenReturn(monsters);

        // When
        ResponseEntity<List<Monster>> response = monsterController.getMonstersByName(monsterName);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(monsters, response.getBody());
        verify(monsterService).getMonstersByName(monsterName);
    }

    @Test
    void createMonster_WithValidData_ShouldReturnCreatedMonster() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        when(monsterService.createMonster(any(Monster.class))).thenReturn(testMonster);

        // When
        ResponseEntity<Monster> response = monsterController.createMonster(testMonster);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testMonster, response.getBody());
        verify(monsterService).createMonster(testMonster);
    }

    @Test
    void createMonster_WithInvalidData_ShouldReturnBadRequest() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        when(monsterService.createMonster(any(Monster.class)))
                .thenThrow(new IllegalArgumentException("Monster name cannot be null or empty"));

        // When
        ResponseEntity<Monster> response = monsterController.createMonster(testMonster);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(monsterService).createMonster(testMonster);
    }

    @Test
    void updateMonster_WhenMonsterExists_ShouldReturnUpdatedMonster() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        when(monsterService.updateMonster(eq(monsterId), any(Monster.class))).thenReturn(testMonster);

        // When
        ResponseEntity<Monster> response = monsterController.updateMonster(monsterId, testMonster);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testMonster, response.getBody());
        verify(monsterService).updateMonster(eq(monsterId), any(Monster.class));
    }

    @Test
    void updateMonster_WhenMonsterDoesNotExist_ShouldReturnNotFound() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        when(monsterService.updateMonster(eq(monsterId), any(Monster.class)))
                .thenThrow(new IllegalArgumentException("Monster not found with id: " + monsterId));

        // When
        ResponseEntity<Monster> response = monsterController.updateMonster(monsterId, testMonster);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(monsterService).updateMonster(eq(monsterId), any(Monster.class));
    }

    @Test
    void deleteMonster_WhenMonsterExists_ShouldReturnNoContent() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        doNothing().when(monsterService).deleteMonster(monsterId);

        // When
        ResponseEntity<Void> response = monsterController.deleteMonster(monsterId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(monsterService).deleteMonster(monsterId);
    }

    @Test
    void deleteMonster_WhenMonsterDoesNotExist_ShouldReturnNotFound() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        doThrow(new IllegalArgumentException("Monster not found with id: " + monsterId))
                .when(monsterService).deleteMonster(monsterId);

        // When
        ResponseEntity<Void> response = monsterController.deleteMonster(monsterId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(monsterService).deleteMonster(monsterId);
    }
}

