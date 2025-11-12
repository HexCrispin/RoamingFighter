package com.battler.Roaming.Fighter.fight;

import com.battler.Roaming.Fighter.entity.Fight;
import com.battler.Roaming.Fighter.entity.Monster;
import com.battler.Roaming.Fighter.fight.dto.CreateFightRequest;
import com.battler.Roaming.Fighter.fight.dto.FightStateDto;
import com.battler.Roaming.Fighter.monster.MonsterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class FightService {

    private final FightRepository fightRepository;
    private final MonsterRepository monsterRepository;

    private final String TEAM_A_NAME = "Team A";
    private final String TEAM_B_NAME = "Team B";


    @Autowired
    public FightService(FightRepository fightRepository, MonsterRepository monsterRepository) {
        this.fightRepository = fightRepository;
        this.monsterRepository = monsterRepository;
    }

    public Fight createFight(CreateFightRequest request) {

        validateTeam(request.getTeamA(), TEAM_A_NAME);
        validateTeam(request.getTeamB(), TEAM_B_NAME);

        List<Monster> teamAMonsters = findTeamMonsters(request.getTeamA());
        List<Monster> teamBMonsters = findTeamMonsters(request.getTeamB());

        UUID activeMonsterA = findFirstHealthyMonster(teamAMonsters);
        UUID activeMonsterB = findFirstHealthyMonster(teamBMonsters);
        validateHealthyFight(activeMonsterA, activeMonsterB);

        Fight fight = new Fight();
        fight.setTeamA(teamAMonsters);
        fight.setTeamB(teamBMonsters);
        fight.setActiveMonsterA(activeMonsterA);
        fight.setActiveMonsterB(activeMonsterB);
        fight.setStatus(FightStatus.ONGOING);

        return fightRepository.save(fight);
    }

    public FightStateDto executeExchange(UUID fightId) {
        Fight fight = fightRepository.findById(fightId)
                .orElseThrow(() -> new IllegalArgumentException("Fight not found with id: " + fightId));

        if (fight.getStatus() != FightStatus.ONGOING) {
            return mapToFightStateDto(fight);
        }

        Fight finalFight = fight;


        Monster monsterA = findMonsterById(fight.getActiveMonsterA(), fight.getTeamA())
                .orElseThrow(() -> new IllegalArgumentException(TEAM_A_NAME + "Active monster not found: " + finalFight.getActiveMonsterA()));
        Monster monsterB = findMonsterById(fight.getActiveMonsterB(), fight.getTeamB())
                .orElseThrow(() -> new IllegalArgumentException(TEAM_B_NAME + "Active monster not found: " + finalFight.getActiveMonsterB()));

        handleCombatDamage(monsterA, monsterB);
        handleSwitching(fight, monsterA, monsterB);
        checkForWin(fight);

        fight = fightRepository.save(fight);
        return mapToFightStateDto(fight);
    }

    public FightStateDto getFightState(UUID fightId) {
        Fight fight = fightRepository.findById(fightId)
                .orElseThrow(() -> new IllegalArgumentException("Fight not found with id: " + fightId));
        return mapToFightStateDto(fight);
    }

    private int calculateDamage(Monster attacker, Monster defender) {
        return Math.max(1, attacker.getAttack() - defender.getDefence());
    }

    private UUID findFirstHealthyMonster(List<Monster> monsters) {
        return monsters.stream()
                .filter(m -> m.getHealth() > 0)
                .map(Monster::getId)
                .findFirst()
                .orElse(null);
    }

    private boolean hasHealthyMonsters(List<Monster> monsters) {
        return monsters.stream().anyMatch(m -> m.getHealth() > 0);
    }

    private void handleCombatDamage(Monster monsterA, Monster monsterB){
        int damageAtoB = calculateDamage(monsterA, monsterB);
        int newHealthB = Math.max(0, monsterB.getHealth() - damageAtoB);
        monsterB.setHealth(newHealthB);
        monsterRepository.save(monsterB);

        int damageBtoA = calculateDamage(monsterB, monsterA);
        int newHealthA = Math.max(0, monsterA.getHealth() - damageBtoA);
        monsterA.setHealth(newHealthA);
        monsterRepository.save(monsterA);
    }

    private void handleSwitching(Fight fight, Monster monsterA, Monster monsterB) {
        if (monsterA.getHealth() == 0) {
            UUID nextActiveA = findFirstHealthyMonster(fight.getTeamA());
            if (nextActiveA != null) {
                fight.setActiveMonsterA(nextActiveA);
            }
        }

        if (monsterB.getHealth() == 0) {
            UUID nextActiveB = findFirstHealthyMonster(fight.getTeamB());
            if (nextActiveB != null) {
                fight.setActiveMonsterB(nextActiveB);
            }
        }
    }

    private void checkForWin(Fight fight) {
        boolean teamAHasHealthyMonsters = hasHealthyMonsters(fight.getTeamA());
        boolean teamBHasHealthyMonsters = hasHealthyMonsters(fight.getTeamB());

        if (!teamAHasHealthyMonsters) {
            fight.setStatus(FightStatus.TEAM_B_WON);
        } else if (!teamBHasHealthyMonsters) {
            fight.setStatus(FightStatus.TEAM_A_WON);
        }
    }

    private Optional<Monster> findMonsterById(UUID id, List<Monster> monsters) {
        return monsters.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }

    private void validateTeam(List<UUID> monsters, String teamName){
        if (monsters == null || monsters.isEmpty()) {
            throw new IllegalArgumentException(teamName + "cannot be null or empty");
        }
    }

    private List<Monster> findTeamMonsters(List<UUID> monsterIds ) {
        return monsterIds.stream()
                .map(id -> monsterRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Monster not found with id: " + id)))
                .collect(Collectors.toList());
    }

    private void validateHealthyFight(UUID activeMonsterA, UUID activeMonsterB){
        if (activeMonsterA == null) {
            throw new IllegalArgumentException(TEAM_A_NAME + " has no healthy monsters");
        }
        if (activeMonsterB == null) {
            throw new IllegalArgumentException(TEAM_B_NAME+"has no healthy monsters");
        }
    }

    private FightStateDto mapToFightStateDto(Fight fight) {
        return new FightStateDto(
                fight.getId(),
                fight.getActiveMonsterA(),
                fight.getActiveMonsterB(),
                fight.getTeamA(),
                fight.getTeamB(),
                fight.getStatus()
        );
    }
}

