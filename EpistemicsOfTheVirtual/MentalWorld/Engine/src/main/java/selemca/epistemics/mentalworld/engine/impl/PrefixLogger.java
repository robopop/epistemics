package selemca.epistemics.mentalworld.engine.impl;

import selemca.epistemics.mentalworld.engine.MentalWorldEngine;

public class PrefixLogger implements MentalWorldEngine.Logger {
    private final MentalWorldEngine.Logger delegate;
    private final String prefix;

    public PrefixLogger(MentalWorldEngine.Logger delegate, String prefix) {
        this.delegate = delegate;
        this.prefix = prefix;
    }

    @Override
    public void debug(String message) {
        delegate.debug(prefixMessage(message));
    }

    @Override
    public void info(String message) {
        delegate.info(prefixMessage(message));
    }

    @Override
    public void warning(String message) {
        delegate.warning(prefixMessage(message));
    }
    private String prefixMessage(String message) {
        return prefix + message;
    }
}
