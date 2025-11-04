package com.battler.Roaming.Fighter.monster;

import com.battler.Roaming.Fighter.entity.Monster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MonsterRepository extends JpaRepository<Monster, UUID> {
    List<Monster> findByName(String name);
}

