package com.battler.Roaming.Fighter.fight;

import com.battler.Roaming.Fighter.entity.Fight;
import com.battler.Roaming.Fighter.fight.dto.CreateFightRequest;
import com.battler.Roaming.Fighter.fight.dto.FightStateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/fight")
public class FightController {

    private final FightService fightService;

    @Autowired
    public FightController(FightService fightService) {
        this.fightService = fightService;
    }

    @PostMapping
    public ResponseEntity<Fight> createFight(@RequestBody CreateFightRequest request) {
        try {
            Fight createdFight = fightService.createFight(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFight);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{fightId}")
    public ResponseEntity<FightStateDto> executeExchange(@PathVariable UUID fightId) {
        try {
            FightStateDto fightState = fightService.executeExchange(fightId);
            return ResponseEntity.ok(fightState);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{fightId}")
    public ResponseEntity<FightStateDto> getFightState(@PathVariable UUID fightId) {
        try {
            FightStateDto fightState = fightService.getFightState(fightId);
            return ResponseEntity.ok(fightState);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
