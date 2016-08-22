/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.webapp.ui;

import selemca.epistemics.mentalworld.engine.MentalWorldEngine;

/**
 * Created by henrizwols on 05-02-15.
 */
public class StringBuilderLogger implements MentalWorldEngine.Logger {
    private final StringBuilder logArea = new StringBuilder();

    @Override
    public void debug(String message) {
        addLogMessage("   " + message);
    }

    @Override
    public void info(String message) {
        addLogMessage(message);
    }

    @Override
    public void warning(String message) {
        addLogMessage("Warning: " + message);
    }

    private void addLogMessage(String message) {
        logArea.append(message);
        logArea.append('\n');
    }

    public String getLogText() {
        return logArea.toString();
    }
}
