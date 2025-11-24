package com.battler.Roaming.Fighter.entity;

import com.battler.Roaming.Fighter.fight.FightStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fights")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fight {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fight_team_a",
        joinColumns = @JoinColumn(name = "fight_id"),
        inverseJoinColumns = @JoinColumn(name = "monster_id")
    )
    private List<Monster> teamA = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fight_team_b",
        joinColumns = @JoinColumn(name = "fight_id"),
        inverseJoinColumns = @JoinColumn(name = "monster_id")
    )
    private List<Monster> teamB = new ArrayList<>();

    @Column(nullable = false)
    private UUID activeMonsterA;

    @Column(nullable = false)
    private UUID activeMonsterB;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FightStatus status = FightStatus.ONGOING;
}
