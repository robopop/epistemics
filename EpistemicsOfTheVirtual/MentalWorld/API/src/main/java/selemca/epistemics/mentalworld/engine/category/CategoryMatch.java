/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.category;

import selemca.epistemics.data.entity.Concept;

import java.util.Set;

/**
 * Created by henrizwols on 27-05-15.
 */
public interface CategoryMatch {
    Concept getConcept();

    Set<Concept> getContributors();

    double getMatchScore();

    double getContributorScore(Concept contributor);

    boolean withinReality(Concept contributor);

    boolean withinFiction(Concept contributor);

}
