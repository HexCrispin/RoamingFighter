package com.battler.Roaming.Fighter.monster;

import com.battler.Roaming.Fighter.entity.Monster;
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
class MonsterServiceTest {

    @Mock
    private MonsterRepository monsterRepository;

    @InjectMocks
    private MonsterService monsterService;

    private static final FixtureMonkey FIXTURE_MONKEY = TestFixtures.FIXTURE_MONKEY;

    @Test
    void getAllMonsters_ShouldReturnListOfMonsters() {
        // Given
        List<Monster> monsters = FIXTURE_MONKEY.giveMe(Monster.class, 1);
        when(monsterRepository.findAll()).thenReturn(monsters);

        // When
        List<Monster> result = monsterService.getAllMonsters();

        // Then
        assertEquals(1, result.size());
        assertEquals(monsters.getFirst(), result.getFirst());
        verify(monsterRepository).findAll();
    }

    @Test
    void getMonsterById_WhenMonsterExists_ShouldReturnMonster() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        when(monsterRepository.findById(monsterId)).thenReturn(Optional.of(testMonster));

        // When
        Optional<Monster> result = monsterService.getMonsterById(monsterId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testMonster, result.get());
        verify(monsterRepository).findById(monsterId);
    }

    @Test
    void getMonsterById_WhenMonsterDoesNotExist_ShouldReturnEmpty() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        when(monsterRepository.findById(monsterId)).thenReturn(Optional.empty());

        // When
        Optional<Monster> result = monsterService.getMonsterById(monsterId);

        // Then
        assertTrue(result.isEmpty());
        verify(monsterRepository).findById(monsterId);
    }

    @Test
    void getMonstersByName_ShouldReturnListOfMonsters() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        String monsterName = testMonster.getName();
        List<Monster> monsters = FIXTURE_MONKEY.giveMe(Monster.class, 1);
        when(monsterRepository.findByName(monsterName)).thenReturn(monsters);

        // When
        List<Monster> result = monsterService.getMonstersByName(monsterName);

        // Then
        assertEquals(1, result.size());
        assertEquals(monsters.getFirst(), result.getFirst());
        verify(monsterRepository).findByName(monsterName);
    }

    @Test
    void createMonster_WithValidData_ShouldSaveAndReturnMonster() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        when(monsterRepository.save(any(Monster.class))).thenReturn(testMonster);

        // When
        Monster result = monsterService.createMonster(testMonster);

        // Then
        assertNotNull(result);
        assertEquals(testMonster, result);
        verify(monsterRepository).save(testMonster);
    }

    @Test
    void createMonster_WithNullName_ShouldThrowException() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        testMonster.setName(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> monsterService.createMonster(testMonster));
        assertEquals("Monster name cannot be null or empty", exception.getMessage());
        verify(monsterRepository, never()).save(any(Monster.class));
    }

    @Test
    void createMonster_WithEmptyName_ShouldThrowException() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        testMonster.setName("   ");

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> monsterService.createMonster(testMonster));
        assertEquals("Monster name cannot be null or empty", exception.getMessage());
        verify(monsterRepository, never()).save(any(Monster.class));
    }

    @Test
    void createMonster_WithNegativeAttack_ShouldThrowException() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        testMonster.setAttack(-1);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> monsterService.createMonster(testMonster));
        assertEquals("Monster attack must be non-negative", exception.getMessage());
        verify(monsterRepository, never()).save(any(Monster.class));
    }

    @Test
    void createMonster_WithNegativeDefence_ShouldThrowException() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        testMonster.setDefence(-1);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> monsterService.createMonster(testMonster));
        assertEquals("Monster defence must be non-negative", exception.getMessage());
        verify(monsterRepository, never()).save(any(Monster.class));
    }

    @Test
    void createMonster_WithZeroHealth_ShouldThrowException() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        testMonster.setHealth(0);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> monsterService.createMonster(testMonster));
        assertEquals("Monster health must be positive", exception.getMessage());
        verify(monsterRepository, never()).save(any(Monster.class));
    }

    @Test
    void createMonster_WithNegativeHealth_ShouldThrowException() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        testMonster.setHealth(-1);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> monsterService.createMonster(testMonster));
        assertEquals("Monster health must be positive", exception.getMessage());
        verify(monsterRepository, never()).save(any(Monster.class));
    }

    @Test
    void updateMonster_WhenMonsterExists_ShouldUpdateAndReturnMonster() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        Monster updatedMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        updatedMonster.setName("Updated Monster");
        updatedMonster.setAttack(20);
        updatedMonster.setDefence(10);
        updatedMonster.setHealth(200);

        when(monsterRepository.findById(monsterId)).thenReturn(Optional.of(testMonster));
        when(monsterRepository.save(any(Monster.class))).thenReturn(testMonster);

        // When
        Monster result = monsterService.updateMonster(monsterId, updatedMonster);

        // Then
        assertNotNull(result);
        verify(monsterRepository).findById(monsterId);
        verify(monsterRepository).save(any(Monster.class));
    }

    @Test
    void updateMonster_WhenMonsterDoesNotExist_ShouldThrowException() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        Monster updatedMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        updatedMonster.setName("Updated Monster");
        updatedMonster.setAttack(20);
        updatedMonster.setDefence(10);
        updatedMonster.setHealth(200);

        when(monsterRepository.findById(monsterId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> monsterService.updateMonster(monsterId, updatedMonster));
        assertEquals("Monster not found with id: " + monsterId, exception.getMessage());
        verify(monsterRepository).findById(monsterId);
        verify(monsterRepository, never()).save(any(Monster.class));
    }

    @Test
    void deleteMonster_WhenMonsterExists_ShouldDeleteMonster() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        when(monsterRepository.existsById(monsterId)).thenReturn(true);
        doNothing().when(monsterRepository).deleteById(monsterId);

        // When
        monsterService.deleteMonster(monsterId);

        // Then
        verify(monsterRepository).existsById(monsterId);
        verify(monsterRepository).deleteById(monsterId);
    }

    @Test
    void deleteMonster_WhenMonsterDoesNotExist_ShouldThrowException() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        when(monsterRepository.existsById(monsterId)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> monsterService.deleteMonster(monsterId));
        assertEquals("Monster not found with id: " + monsterId, exception.getMessage());
        verify(monsterRepository).existsById(monsterId);
        verify(monsterRepository, never()).deleteById(any());
    }
}

