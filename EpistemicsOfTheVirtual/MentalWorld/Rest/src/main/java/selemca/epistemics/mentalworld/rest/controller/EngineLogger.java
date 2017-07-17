package selemca.epistemics.mentalworld.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;

public class EngineLogger implements MentalWorldEngine.Logger {
    private final Logger LOGGER = LoggerFactory.getLogger(EngineLogger.class);

    @Override
    public void debug(String message) {
        LOGGER.debug(message);
    }

    @Override
    public void info(String message) {
        LOGGER.info(message);
    }

    @Override
    public void warning(String message) {
        LOGGER.warn(message);
    }
}
