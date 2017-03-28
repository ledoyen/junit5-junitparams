package com.github.ledoyen.junit5.junitparams;

import java.util.Optional;

import org.junit.platform.engine.ConfigurationParameters;

public class EmptyConfigurationParameters implements ConfigurationParameters {

    @Override
    public Optional<String> get(String key) {
        return Optional.empty();
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        return Optional.empty();
    }

    @Override
    public int size() {
        return 0;
    }
}
