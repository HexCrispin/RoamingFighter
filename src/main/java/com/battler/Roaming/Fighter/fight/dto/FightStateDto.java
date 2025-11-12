package com.battler.Roaming.Fighter.fight.dto;

import com.battler.Roaming.Fighter.entity.Monster;
import com.battler.Roaming.Fighter.fight.FightStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FightStateDto {
    private UUID fightId;
    private UUID activeMonsterA;
    private UUID activeMonsterB;
    private List<Monster> teamA;
    private List<Monster> teamB;
    private FightStatus status;
}

