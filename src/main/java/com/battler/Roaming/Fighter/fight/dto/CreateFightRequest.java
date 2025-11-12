package com.battler.Roaming.Fighter.fight.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFightRequest {
    private List<UUID> teamA;
    private List<UUID> teamB;
}

