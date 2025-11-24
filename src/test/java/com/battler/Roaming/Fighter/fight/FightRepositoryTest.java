package com.battler.Roaming.Fighter.fight;

import com.battler.Roaming.Fighter.entity.Fight;
import com.battler.Roaming.Fighter.entity.Monster;
import com.battler.Roaming.Fighter.testutils.TestFixtures;
import com.navercorp.fixturemonkey.FixtureMonkey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FightRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FightRepository fightRepository;

    private static final FixtureMonkey FIXTURE_MONKEY = TestFixtures.FIXTURE_MONKEY;

    private Monster monsterA1;
    private Monster monsterA2;
    private Monster monsterB1;
    private Monster monsterB2;

    @BeforeEach
    void setUp() {
        // Create and persist test monsters
        monsterA1 = createMonsterWithHealth(100, 10, 5);
        monsterA2 = createMonsterWithHealth(80, 8, 4);
        monsterB1 = createMonsterWithHealth(90, 12, 6);
        monsterB2 = createMonsterWithHealth(70, 9, 3);

        entityManager.persist(monsterA1);
        entityManager.persist(monsterA2);
        entityManager.persist(monsterB1);
        entityManager.persist(monsterB2);
        entityManager.flush();
    }

    @Test
    void save_WithValidFight_ShouldPersistFight() {
        // Given
        Fight fight = createFight(
                List.of(monsterA1, monsterA2),
                List.of(monsterB1, monsterB2),
                FightStatus.ONGOING
        );

        // When
        
        Fight savedFight = fightRepository.save(fight);
        assertNotNull(savedFight); 

        // Then
        assertNotNull(savedFight.getId());
        assertEquals(FightStatus.ONGOING, savedFight.getStatus());
        assertEquals(monsterA1.getId(), savedFight.getActiveMonsterA());
        assertEquals(monsterB1.getId(), savedFight.getActiveMonsterB());
        assertEquals(2, savedFight.getTeamA().size());
        assertEquals(2, savedFight.getTeamB().size());
    }

    @Test
    void save_WithMultipleFights_ShouldPersistAllFights() {
        // Given
        Fight fight1 = createFight(
                List.of(monsterA1),
                List.of(monsterB1),
                FightStatus.ONGOING
        );
        Fight fight2 = createFight(
                List.of(monsterA2),
                List.of(monsterB2),
                FightStatus.ONGOING
        );

        // When
        Fight savedFight1 = fightRepository.save(fight1);
        Fight savedFight2 = fightRepository.save(fight2);

        // Then
        assertNotNull(savedFight1.getId());
        assertNotNull(savedFight2.getId());
        assertNotEquals(savedFight1.getId(), savedFight2.getId());
    }

    @Test
    void findById_WithExistingFight_ShouldReturnFight() {
        // Given
        Fight fight = createFight(
                List.of(monsterA1),
                List.of(monsterB1),
                FightStatus.ONGOING
        );
        Fight savedFight = fightRepository.save(fight);
        assertNotNull(savedFight); 
        UUID fightId = savedFight.getId();

        // When
        Optional<Fight> foundFight = fightRepository.findById(fightId);

        // Then
        assertTrue(foundFight.isPresent());
        assertEquals(fightId, foundFight.get().getId());
        assertEquals(FightStatus.ONGOING, foundFight.get().getStatus());
    }

    @Test
    void findById_WithNonExistentFight_ShouldReturnEmpty() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<Fight> foundFight = fightRepository.findById(nonExistentId);

        // Then
        assertFalse(foundFight.isPresent());
    }

    @Test
    void findAll_WithMultipleFights_ShouldReturnAllFights() {
        // Given
        Fight fight1 = createFight(
                List.of(monsterA1),
                List.of(monsterB1),
                FightStatus.ONGOING
        );
        Fight fight2 = createFight(
                List.of(monsterA2),
                List.of(monsterB2),
                FightStatus.TEAM_A_WON
        );
        fightRepository.save(fight1);
        fightRepository.save(fight2);

        // When
        List<Fight> allFights = fightRepository.findAll();

        // Then
        assertTrue(allFights.size() >= 2);
        assertTrue(allFights.stream().anyMatch(f -> f.getId().equals(fight1.getId())));
        assertTrue(allFights.stream().anyMatch(f -> f.getId().equals(fight2.getId())));
    }

    @Test
    void findAll_WithNoFights_ShouldReturnEmptyList() {
        // When
        List<Fight> allFights = fightRepository.findAll();

        // Then
        assertTrue(allFights.isEmpty());
    }

    @Test
    void save_WithExistingFight_ShouldUpdateFight() {
        // Given
        Fight fight = createFight(
                List.of(monsterA1),
                List.of(monsterB1),
                FightStatus.ONGOING
        );
        Fight savedFight = fightRepository.save(fight);
        assertNotNull(savedFight); 
        UUID fightId = savedFight.getId();

        // When
        savedFight.setStatus(FightStatus.TEAM_A_WON);
        savedFight.setActiveMonsterA(monsterA2.getId());
        Fight updatedFight = fightRepository.save(savedFight);

        // Then
        assertEquals(fightId, updatedFight.getId());
        assertEquals(FightStatus.TEAM_A_WON, updatedFight.getStatus());
        assertEquals(monsterA2.getId(), updatedFight.getActiveMonsterA());

        Optional<Fight> foundFight = fightRepository.findById(fightId);
        assertTrue(foundFight.isPresent());
        assertEquals(FightStatus.TEAM_A_WON, foundFight.get().getStatus());
        assertEquals(monsterA2.getId(), foundFight.get().getActiveMonsterA());
    }

    @Test
    void delete_WithExistingFight_ShouldRemoveFight() {
        // Given
        Fight fight = createFight(
                List.of(monsterA1),
                List.of(monsterB1),
                FightStatus.ONGOING
        );
        Fight savedFight = fightRepository.save(fight);
        assertNotNull(savedFight); 
        UUID fightId = savedFight.getId();

        // When
        fightRepository.delete(savedFight);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Fight> foundFight = fightRepository.findById(fightId);
        assertFalse(foundFight.isPresent());
    }

    @Test
    void deleteById_WithExistingFight_ShouldRemoveFight() {
        // Given
        Fight fight = createFight(
                List.of(monsterA1),
                List.of(monsterB1),
                FightStatus.ONGOING
        );
        Fight savedFight = fightRepository.save(fight);
        assertNotNull(savedFight); 
        UUID fightId = savedFight.getId();

        // When
        fightRepository.deleteById(fightId);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Fight> foundFight = fightRepository.findById(fightId);
        assertFalse(foundFight.isPresent());
    }

    @Test
    void deleteAll_WithMultipleFights_ShouldRemoveAllFights() {
        // Given
        Fight fight1 = createFight(
                List.of(monsterA1),
                List.of(monsterB1),
                FightStatus.ONGOING
        );
        Fight fight2 = createFight(
                List.of(monsterA2),
                List.of(monsterB2),
                FightStatus.ONGOING
        );
        fightRepository.save(fight1);
        fightRepository.save(fight2);

        // When
        fightRepository.deleteAll();

        // Then
        List<Fight> allFights = fightRepository.findAll();
        assertTrue(allFights.isEmpty());
    }

    @Test
    void existsById_WithExistingFight_ShouldReturnTrue() {
        // Given
        Fight fight = createFight(
                List.of(monsterA1),
                List.of(monsterB1),
                FightStatus.ONGOING
        );
        Fight savedFight = fightRepository.save(fight);
        assertNotNull(savedFight); 
        UUID fightId = savedFight.getId();

        // When
        boolean exists = fightRepository.existsById(fightId);

        // Then
        assertTrue(exists);
    }

    @Test
    void existsById_WithNonExistentFight_ShouldReturnFalse() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        boolean exists = fightRepository.existsById(nonExistentId);

        // Then
        assertFalse(exists);
    }

    private Monster createMonsterWithHealth(int health, int attack, int defence) {
        return FIXTURE_MONKEY.giveMeBuilder(Monster.class)
                .set("id", null)
                .set("health", health)
                .set("maxHealth", Math.max(health, 100))
                .set("attack", attack)
                .set("defence", defence)
                .sample();
    }

    private Fight createFight(List<Monster> teamA, List<Monster> teamB, FightStatus status) {
        return FIXTURE_MONKEY.giveMeBuilder(Fight.class)
                .set("teamA", teamA)
                .set("teamB", teamB)
                .set("activeMonsterA", teamA.getFirst().getId())
                .set("activeMonsterB", teamB.getFirst().getId())
                .set("status", status)
                .set("id", null)
                .sample();
    }
}
