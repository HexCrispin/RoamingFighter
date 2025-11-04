package com.battler.Roaming.Fighter.player;

import com.battler.Roaming.Fighter.entity.Monster;
import com.battler.Roaming.Fighter.entity.Player;
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
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerService playerService;

    private static final FixtureMonkey FIXTURE_MONKEY = TestFixtures.FIXTURE_MONKEY;

    @Test
    void getAllPlayers_ShouldReturnListOfPlayers() {
        // Given
        List<Player> players = FIXTURE_MONKEY.giveMe(Player.class, 1);
        when(playerRepository.findAll()).thenReturn(players);

        // When
        List<Player> result = playerService.getAllPlayers();

        // Then
        assertEquals(1, result.size());
        assertEquals(players.getFirst(), result.getFirst());
        verify(playerRepository).findAll();
    }

    @Test
    void getPlayerById_WhenPlayerExists_ShouldReturnPlayer() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(testPlayer));

        // When
        Optional<Player> result = playerService.getPlayerById(playerId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testPlayer, result.get());
        verify(playerRepository).findById(playerId);
    }

    @Test
    void getPlayerById_WhenPlayerDoesNotExist_ShouldReturnEmpty() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // When
        Optional<Player> result = playerService.getPlayerById(playerId);

        // Then
        assertTrue(result.isEmpty());
        verify(playerRepository).findById(playerId);
    }

    @Test
    void getPlayerByName_WhenPlayerExists_ShouldReturnPlayer() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        String playerName = testPlayer.getName();
        when(playerRepository.findByName(playerName)).thenReturn(Optional.of(testPlayer));

        // When
        Optional<Player> result = playerService.getPlayerByName(playerName);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testPlayer, result.get());
        verify(playerRepository).findByName(playerName);
    }

    @Test
    void createPlayer_WhenNameIsUnique_ShouldSaveAndReturnPlayer() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        when(playerRepository.existsByName(testPlayer.getName())).thenReturn(false);
        when(playerRepository.save(any(Player.class))).thenReturn(testPlayer);

        // When
        Player result = playerService.createPlayer(testPlayer);

        // Then
        assertNotNull(result);
        assertEquals(testPlayer, result);
        verify(playerRepository).existsByName(testPlayer.getName());
        verify(playerRepository).save(testPlayer);
    }

    @Test
    void createPlayer_WhenNameAlreadyExists_ShouldThrowException() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        when(playerRepository.existsByName(testPlayer.getName())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> playerService.createPlayer(testPlayer));
        assertEquals("Player with name '" + testPlayer.getName() + "' already exists", exception.getMessage());
        verify(playerRepository).existsByName(testPlayer.getName());
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void updatePlayer_WhenPlayerExists_ShouldUpdateAndReturnPlayer() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        Player updatedPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        updatedPlayer.setName("UpdatedName");

        when(playerRepository.findById(playerId)).thenReturn(Optional.of(testPlayer));
        when(playerRepository.existsByName("UpdatedName")).thenReturn(false);
        when(playerRepository.save(any(Player.class))).thenReturn(testPlayer);

        // When
        Player result = playerService.updatePlayer(playerId, updatedPlayer);

        // Then
        assertNotNull(result);
        verify(playerRepository).findById(playerId);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void updatePlayer_WhenPlayerDoesNotExist_ShouldThrowException() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        Player updatedPlayer = FIXTURE_MONKEY.giveMeBuilder(Player.class)
                                             .set("name","UpdatedName")
                                             .sample();
        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> playerService.updatePlayer(playerId, updatedPlayer));
        assertEquals("Player not found with id: " + playerId, exception.getMessage());
        verify(playerRepository).findById(playerId);
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void deletePlayer_WhenPlayerExists_ShouldDeletePlayer() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        when(playerRepository.existsById(playerId)).thenReturn(true);
        doNothing().when(playerRepository).deleteById(playerId);

        // When
        playerService.deletePlayer(playerId);

        // Then
        verify(playerRepository).existsById(playerId);
        verify(playerRepository).deleteById(playerId);
    }

    @Test
    void deletePlayer_WhenPlayerDoesNotExist_ShouldThrowException() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        when(playerRepository.existsById(playerId)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> playerService.deletePlayer(playerId));
        assertEquals("Player not found with id: " + playerId, exception.getMessage());
        verify(playerRepository).existsById(playerId);
        verify(playerRepository, never()).deleteById(any());
    }

    @Test
    void getPlayerMonsters_WhenPlayerExists_ShouldReturnMonsters() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        Player testPlayer = FIXTURE_MONKEY.giveMeBuilder(Player.class)
                                          .set("monsterBox", List.of(testMonster))
                                          .sample();
        UUID playerId = testPlayer.getId();
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(testPlayer));

        // When
        List<Monster> result = playerService.getPlayerMonsters(playerId);

        // Then
        assertEquals(1, result.size());
        assertEquals(testMonster, result.getFirst());
        verify(playerRepository).findById(playerId);
    }

    @Test
    void addMonsterToPlayer_WhenPlayerExists_ShouldAddMonster() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeBuilder(Player.class)
                                          .set("monsterBox", List.of())
                                          .sample();
        UUID playerId = testPlayer.getId();
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(testPlayer));
        when(playerRepository.save(any(Player.class))).thenReturn(testPlayer);

        // When
        Player result = playerService.addMonsterToPlayer(playerId, testMonster);

        // Then
        assertNotNull(result);
        assertEquals(1, testPlayer.getMonsterBox().size());
        assertTrue(testPlayer.getMonsterBox().contains(testMonster));
        verify(playerRepository).findById(playerId);
        verify(playerRepository).save(testPlayer);
    }

    @Test
    void removeMonsterFromPlayer_WhenPlayerAndMonsterExist_ShouldRemoveMonster() {
        // Given
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        Player testPlayer = FIXTURE_MONKEY.giveMeBuilder(Player.class)
                                          .set("monsterBox", List.of(testMonster))
                                          .sample();
        UUID playerId = testPlayer.getId();
        UUID monsterId = testMonster.getId();
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(testPlayer));
        when(playerRepository.save(any(Player.class))).thenReturn(testPlayer);

        // When
        Player result = playerService.removeMonsterFromPlayer(playerId, monsterId);

        // Then
        assertNotNull(result);
        assertEquals(0, testPlayer.getMonsterBox().size());
        verify(playerRepository).findById(playerId);
        verify(playerRepository).save(testPlayer);
    }

    @Test
    void removeMonsterFromPlayer_WhenMonsterDoesNotExist_ShouldThrowException() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(testPlayer));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> playerService.removeMonsterFromPlayer(playerId, monsterId));
        assertEquals("Monster not found in player's monster box", exception.getMessage());
        verify(playerRepository).findById(playerId);
        verify(playerRepository, never()).save(any(Player.class));
    }
}

