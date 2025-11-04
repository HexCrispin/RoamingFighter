package com.battler.Roaming.Fighter;

import com.battler.Roaming.Fighter.monster.MonsterService;
import com.battler.Roaming.Fighter.player.PlayerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SuppressWarnings("unused")
class RoamingFighterApplicationTests {

	@Autowired
	private PlayerService playerService;

	@Autowired
	private MonsterService monsterService;

	@Test
	void contextLoads() {
		// This test verifies that the Spring application context loads successfully
		// with all beans properly wired. If the context fails to load, this test will fail.
	}

	@Test
	void keyServicesAreLoaded() {
		// Verify that key service beans are available in the context
		assertThat(playerService).isNotNull();
		assertThat(monsterService).isNotNull();
	}

}
