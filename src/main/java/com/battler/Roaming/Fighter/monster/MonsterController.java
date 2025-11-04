package com.battler.Roaming.Fighter.monster;

import com.battler.Roaming.Fighter.entity.Monster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/monster")
public class MonsterController {

    private final MonsterService monsterService;

    @Autowired
    public MonsterController(MonsterService monsterService) {
        this.monsterService = monsterService;
    }

    @GetMapping
    public ResponseEntity<List<Monster>> getAllMonsters() {
        List<Monster> monsters = monsterService.getAllMonsters();
        return ResponseEntity.ok(monsters);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Monster> getMonsterById(@PathVariable UUID id) {
        return monsterService.getMonsterById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Monster>> getMonstersByName(@PathVariable String name) {
        List<Monster> monsters = monsterService.getMonstersByName(name);
        return ResponseEntity.ok(monsters);
    }

    @PostMapping
    public ResponseEntity<Monster> createMonster(@RequestBody Monster monster) {
        try {
            Monster createdMonster = monsterService.createMonster(monster);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMonster);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Monster> updateMonster(@PathVariable UUID id, @RequestBody Monster monsterDetails) {
        try {
            Monster updatedMonster = monsterService.updateMonster(id, monsterDetails);
            return ResponseEntity.ok(updatedMonster);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMonster(@PathVariable UUID id) {
        try {
            monsterService.deleteMonster(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

