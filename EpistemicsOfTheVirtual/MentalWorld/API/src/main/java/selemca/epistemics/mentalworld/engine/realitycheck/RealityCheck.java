/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.realitycheck;

/**
 * Created by henrizwols on 05-03-15.
 */
public interface RealityCheck {
    boolean isReality(double truthValue);
    boolean isFiction(double truthValue);
}
