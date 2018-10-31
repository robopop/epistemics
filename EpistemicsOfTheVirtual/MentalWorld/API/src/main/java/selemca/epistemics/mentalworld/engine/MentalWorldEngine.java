/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine;

import selemca.epistemics.mentalworld.engine.accept.Engine;

import java.util.Set;

public interface MentalWorldEngine {

    void acceptObservation(Set<String> observationFeatures, Logger logger);

    boolean acceptObservation(Set<String> observationFeatures, Engine engineSettings, Logger logger);

    MentalWorldEngineState createState(Logger logger);

    interface Logger {

        void debug(String message);
        default void debug(String message, Object first, Object... more) {
            debug(format(message, first, more));
        }

        void info(String message);
        default void info(String message, Object first, Object... more) {
            info(format(message, first, more));
        }

        void warning(String message);
        default void warning(String message, Object first, Object... more) {
            warning(format(message, first, more));
        }

        default String format(String template, Object first, Object... more) {
            if (more.length < 1) {
                return String.format(template, first);
            } else {
                Object[] parameters = new Object[more.length + 1];
                parameters[0] = first;
                System.arraycopy(more, 0, parameters, 1, more.length);
                return String.format(template, parameters);
            }
        }
    }
}
