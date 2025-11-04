package com.battler.Roaming.Fighter.monster;

import com.battler.Roaming.Fighter.entity.Monster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class MonsterService {

    private final MonsterRepository monsterRepository;

    @Autowired
    public MonsterService(MonsterRepository monsterRepository) {
        this.monsterRepository = monsterRepository;
    }

    public List<Monster> getAllMonsters() {
        return monsterRepository.findAll();
    }

    public Optional<Monster> getMonsterById(UUID id) {
        return monsterRepository.findById(id);
    }

    public List<Monster> getMonstersByName(String name) {
        return monsterRepository.findByName(name);
    }

    public Monster createMonster(Monster monster) {
        validateMonster(monster);
        return monsterRepository.save(monster);
    }

    public Monster updateMonster(UUID id, Monster monsterDetails) {
        Monster monster = monsterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Monster not found with id: " + id));
        
        validateMonster(monsterDetails);
        
        monster.setName(monsterDetails.getName());
        monster.setAttack(monsterDetails.getAttack());
        monster.setDefence(monsterDetails.getDefence());
        monster.setHealth(monsterDetails.getHealth());
        
        return monsterRepository.save(monster);
    }

    public void deleteMonster(UUID id) {
        if (!monsterRepository.existsById(id)) {
            throw new IllegalArgumentException("Monster not found with id: " + id);
        }
        monsterRepository.deleteById(id);
    }

    private void validateMonster(Monster monster) {
        if (monster.getName() == null || monster.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Monster name cannot be null or empty");
        }
        if (monster.getAttack() == null || monster.getAttack() < 0) {
            throw new IllegalArgumentException("Monster attack must be non-negative");
        }
        if (monster.getDefence() == null || monster.getDefence() < 0) {
            throw new IllegalArgumentException("Monster defence must be non-negative");
        }
        if (monster.getHealth() == null || monster.getHealth() <= 0) {
            throw new IllegalArgumentException("Monster health must be positive");
        }
    }
}

