package com.battler.Roaming.Fighter.fight;

import com.battler.Roaming.Fighter.entity.Fight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FightRepository extends JpaRepository<Fight, UUID> {
    List<Fight> findByName(String name);
}
