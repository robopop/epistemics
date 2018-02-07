/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.matcher;

import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.realitycheck.RealityCheck;

import java.util.*;

public class CategoryMatchImpl implements CategoryMatch {
    private final Concept concept;
    private final RealityCheck realityCheck;

    private Map<Concept, Double> contributions = new HashMap<>();

    public CategoryMatchImpl(Concept concept, RealityCheck realityCheck) {
        this.concept = concept;
        this.realityCheck = realityCheck;
    }

    public void addContribution(Concept contributor, double truthValue) {
        contributions.put(contributor, truthValue);
    }

    public void addContributions(Map<Concept, Double> contributions) {
        this.contributions.putAll(contributions);
    }

    public Concept getConcept() {
        return concept;
    }

    public Set<Concept> getContributors() {
        return new HashSet<>(contributions.keySet());
    }

    public double getMatchScore() {
        double inverseScore = 1.0;
        for (Double truthValue : contributions.values()) {
            inverseScore *= (1.0 - truthValue);
        }
        return 1.0 - inverseScore;
    }

    public double getContributorScore(Concept contributor) {
        Double score = contributions.get(contributor);
        return score == null ? 0 : score;
    }

    public boolean withinReality(Concept contributor) {
        return realityCheck.isReality(getContributorScore(contributor));
    }

    public boolean withinFiction(Concept contributor) {
        return realityCheck.isFiction(getContributorScore(contributor));
    }

    public String toString() {
        List<String> contributorsWithinReality = contributorsWithinReality();
        List<String> contributorsOutsideReality = contributorsOutsideReality();
        return String.format("Match: %s  Helping: %s  Not helping: %s", concept.getName(), contributorsWithinReality, contributorsOutsideReality);
    }

    private List<String> contributorsWithinReality() {
        List<String> result = new ArrayList<>();
        for (Concept contributor : contributions.keySet()) {
            if (withinReality(contributor)) {
                result.add(contributor.getName());
            }
        }
        return result;
    }

    private List<String> contributorsOutsideReality() {
        List<String> result = new ArrayList<>();
        for (Concept contributor : contributions.keySet()) {
            if (!withinReality(contributor)) {
                result.add(contributor.getName());
            }
        }
        return result;
    }
}
