package com.battler.Roaming.Fighter.testutils;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.jakarta.validation.plugin.JakartaValidationPlugin;

public class TestFixtures {

    public static final FixtureMonkey FIXTURE_MONKEY = FixtureMonkey.builder()
            .defaultNotNull(true)
            .plugin(new JakartaValidationPlugin())
            .build();
}