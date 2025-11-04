package com.battler.Roaming.Fighter.player;

import com.battler.Roaming.Fighter.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerRepository extends JpaRepository<Player, UUID> {
    Optional<Player> findByName(String name);
    boolean existsByName(String name);
}

