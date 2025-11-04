package com.battler.Roaming.Fighter.player;

import com.battler.Roaming.Fighter.entity.Monster;
import com.battler.Roaming.Fighter.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Optional<Player> getPlayerById(UUID id) {
        return playerRepository.findById(id);
    }

    public Optional<Player> getPlayerByName(String name) {
        return playerRepository.findByName(name);
    }

    public Player createPlayer(Player player) {
        if (playerRepository.existsByName(player.getName())) {
            throw new IllegalArgumentException("Player with name '" + player.getName() + "' already exists");
        }
        return playerRepository.save(player);
    }

    public Player updatePlayer(UUID id, Player playerDetails) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + id));
        
        if (!player.getName().equals(playerDetails.getName()) && 
            playerRepository.existsByName(playerDetails.getName())) {
            throw new IllegalArgumentException("Player with name '" + playerDetails.getName() + "' already exists");
        }
        
        player.setName(playerDetails.getName());
        return playerRepository.save(player);
    }

    public void deletePlayer(UUID id) {
        if (!playerRepository.existsById(id)) {
            throw new IllegalArgumentException("Player not found with id: " + id);
        }
        playerRepository.deleteById(id);
    }

    public List<Monster> getPlayerMonsters(UUID playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + playerId));
        return player.getMonsterBox();
    }

    public Player addMonsterToPlayer(UUID playerId, Monster monster) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + playerId));
        player.getMonsterBox().add(monster);
        return playerRepository.save(player);
    }

    public Player removeMonsterFromPlayer(UUID playerId, UUID monsterId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + playerId));
        
        boolean removed = player.getMonsterBox().removeIf(monster -> monster.getId().equals(monsterId));
        if (!removed) {
            throw new IllegalArgumentException("Monster not found in player's monster box");
        }
        
        return playerRepository.save(player);
    }
}

