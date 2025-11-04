package com.battler.Roaming.Fighter.player;

import com.battler.Roaming.Fighter.entity.Monster;
import com.battler.Roaming.Fighter.entity.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        List<Player> players = playerService.getAllPlayers();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable UUID id) {
        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Player> getPlayerByName(@PathVariable String name) {
        return playerService.getPlayerByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        try {
            Player createdPlayer = playerService.createPlayer(player);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable UUID id, @RequestBody Player playerDetails) {
        try {
            Player updatedPlayer = playerService.updatePlayer(id, playerDetails);
            return ResponseEntity.ok(updatedPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable UUID id) {
        try {
            playerService.deletePlayer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/monsters")
    public ResponseEntity<List<Monster>> getPlayerMonsters(@PathVariable UUID id) {
        try {
            List<Monster> monsters = playerService.getPlayerMonsters(id);
            return ResponseEntity.ok(monsters);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/monsters")
    public ResponseEntity<Player> addMonsterToPlayer(@PathVariable UUID id, @RequestBody Monster monster) {
        try {
            Player updatedPlayer = playerService.addMonsterToPlayer(id, monster);
            return ResponseEntity.ok(updatedPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/monsters/{monsterId}")
    public ResponseEntity<Player> removeMonsterFromPlayer(
            @PathVariable UUID id,
            @PathVariable UUID monsterId) {
        try {
            Player updatedPlayer = playerService.removeMonsterFromPlayer(id, monsterId);
            return ResponseEntity.ok(updatedPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

