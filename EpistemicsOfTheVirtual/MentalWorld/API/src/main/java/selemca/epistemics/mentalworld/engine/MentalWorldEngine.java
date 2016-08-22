/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine;

import java.util.Set;

/**
 * Created by henrizwols on 24-02-15.
 */
public interface MentalWorldEngine {
    void acceptObservation(Set<String> observationFeatures, Logger logger);

    public interface Logger {

        public void debug(String message);

        public void info(String message);

        public void warning(String message);
    }
}
