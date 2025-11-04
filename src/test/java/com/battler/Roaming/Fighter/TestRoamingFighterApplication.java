package com.battler.Roaming.Fighter;

import org.springframework.boot.SpringApplication;

public class TestRoamingFighterApplication {

	public static void main(String[] args) {
		SpringApplication.from(RoamingFighterApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
