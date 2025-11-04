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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerControllerTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerController playerController;

    private static final FixtureMonkey FIXTURE_MONKEY = TestFixtures.FIXTURE_MONKEY;

    @Test
    void getAllPlayers_ShouldReturnListOfPlayers() {
        // Given
        List<Player> players = FIXTURE_MONKEY.giveMe(Player.class, 1);
        when(playerService.getAllPlayers()).thenReturn(players);

        // When
        ResponseEntity<List<Player>> response = playerController.getAllPlayers();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(players, response.getBody());
        verify(playerService).getAllPlayers();
    }

    @Test
    void getPlayerById_WhenPlayerExists_ShouldReturnPlayer() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        when(playerService.getPlayerById(playerId)).thenReturn(Optional.of(testPlayer));

        // When
        ResponseEntity<Player> response = playerController.getPlayerById(playerId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPlayer, response.getBody());
        verify(playerService).getPlayerById(playerId);
    }

    @Test
    void getPlayerById_WhenPlayerDoesNotExist_ShouldReturnNotFound() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        when(playerService.getPlayerById(playerId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<Player> response = playerController.getPlayerById(playerId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(playerService).getPlayerById(playerId);
    }

    @Test
    void getPlayerByName_WhenPlayerExists_ShouldReturnPlayer() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        String playerName = testPlayer.getName();
        when(playerService.getPlayerByName(playerName)).thenReturn(Optional.of(testPlayer));

        // When
        ResponseEntity<Player> response = playerController.getPlayerByName(playerName);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPlayer, response.getBody());
        verify(playerService).getPlayerByName(playerName);
    }

    @Test
    void createPlayer_WithValidData_ShouldReturnCreatedPlayer() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        when(playerService.createPlayer(any(Player.class))).thenReturn(testPlayer);

        // When
        ResponseEntity<Player> response = playerController.createPlayer(testPlayer);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testPlayer, response.getBody());
        verify(playerService).createPlayer(testPlayer);
    }

    @Test
    void createPlayer_WithDuplicateName_ShouldReturnBadRequest() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        when(playerService.createPlayer(any(Player.class)))
                .thenThrow(new IllegalArgumentException("Player with name already exists"));

        // When
        ResponseEntity<Player> response = playerController.createPlayer(testPlayer);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(playerService).createPlayer(testPlayer);
    }

    @Test
    void updatePlayer_WhenPlayerExists_ShouldReturnUpdatedPlayer() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        when(playerService.updatePlayer(eq(playerId), any(Player.class))).thenReturn(testPlayer);

        // When
        ResponseEntity<Player> response = playerController.updatePlayer(playerId, testPlayer);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPlayer, response.getBody());
        verify(playerService).updatePlayer(eq(playerId), any(Player.class));
    }

    @Test
    void updatePlayer_WhenPlayerDoesNotExist_ShouldReturnNotFound() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        when(playerService.updatePlayer(eq(playerId), any(Player.class)))
                .thenThrow(new IllegalArgumentException("Player not found with id: " + playerId));

        // When
        ResponseEntity<Player> response = playerController.updatePlayer(playerId, testPlayer);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(playerService).updatePlayer(eq(playerId), any(Player.class));
    }

    @Test
    void deletePlayer_WhenPlayerExists_ShouldReturnNoContent() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        doNothing().when(playerService).deletePlayer(playerId);

        // When
        ResponseEntity<Void> response = playerController.deletePlayer(playerId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(playerService).deletePlayer(playerId);
    }

    @Test
    void deletePlayer_WhenPlayerDoesNotExist_ShouldReturnNotFound() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        doThrow(new IllegalArgumentException("Player not found with id: " + playerId))
                .when(playerService).deletePlayer(playerId);

        // When
        ResponseEntity<Void> response = playerController.deletePlayer(playerId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(playerService).deletePlayer(playerId);
    }

    @Test
    void getPlayerMonsters_WhenPlayerExists_ShouldReturnMonsters() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        List<Monster> monsters = FIXTURE_MONKEY.giveMe(Monster.class, 1);
        when(playerService.getPlayerMonsters(playerId)).thenReturn(monsters);

        // When
        ResponseEntity<List<Monster>> response = playerController.getPlayerMonsters(playerId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(monsters, response.getBody());
        verify(playerService).getPlayerMonsters(playerId);
    }

    @Test
    void addMonsterToPlayer_WhenPlayerExists_ShouldReturnUpdatedPlayer() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        when(playerService.addMonsterToPlayer(eq(playerId), any(Monster.class))).thenReturn(testPlayer);

        // When
        ResponseEntity<Player> response = playerController.addMonsterToPlayer(playerId, testMonster);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPlayer, response.getBody());
        verify(playerService).addMonsterToPlayer(eq(playerId), any(Monster.class));
    }

    @Test
    void removeMonsterFromPlayer_WhenPlayerAndMonsterExist_ShouldReturnUpdatedPlayer() {
        // Given
        Player testPlayer = FIXTURE_MONKEY.giveMeOne(Player.class);
        UUID playerId = testPlayer.getId();
        Monster testMonster = FIXTURE_MONKEY.giveMeOne(Monster.class);
        UUID monsterId = testMonster.getId();
        when(playerService.removeMonsterFromPlayer(playerId, monsterId)).thenReturn(testPlayer);

        // When
        ResponseEntity<Player> response = playerController.removeMonsterFromPlayer(playerId, monsterId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testPlayer, response.getBody());
        verify(playerService).removeMonsterFromPlayer(playerId, monsterId);
    }
}

