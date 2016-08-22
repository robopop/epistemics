/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.common;

import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.deriver.matcher.CategoryMatchImpl;
import selemca.epistemics.mentalworld.engine.realitycheck.RealityCheck;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by henrizwols on 27-05-15.
 */
public class MockCategoryMatchBuilder {
    Concept concept = new Concept("concept", 0.8);
    Set<Concept> contributors = new HashSet<>();
    int nrOfRealisticContributors = 0;
    int nrOfUnrealisticContributors = 0;
    double matchScore = 0.5;

    public MockCategoryMatchBuilder withConcept(Concept concept) {
        this.concept = concept;
        return this;
    }

    public MockCategoryMatchBuilder withConcept(String conceptName) {
        return withConcept(new Concept(conceptName, 0.8));
    }

    public MockCategoryMatchBuilder addContributor(String name, double truthValue) {
        Concept concept = new Concept(name, truthValue);
        contributors.add(concept);
        return this;
    }

    public MockCategoryMatchBuilder addContributor(double truthValue) {
        Concept concept = new Concept("contributor" + contributors.size()+1, truthValue);
        contributors.add(concept);
        return this;
    }

    public MockCategoryMatchBuilder addRealisticContributor() {
        Concept concept = new Concept("realistic" + ++nrOfRealisticContributors, 0.75);
        contributors.add(concept);
        return this;
    }

    public MockCategoryMatchBuilder addUnRealisticContributor() {
        Concept concept = new Concept("unrealistic" + ++nrOfUnrealisticContributors, 0.25);
        contributors.add(concept);
        return this;
    }

    public MockCategoryMatchBuilder withMatchScore(double matchScore) {
        this.matchScore = matchScore;
        return this;
    }

    public CategoryMatch build() {
        RealityCheck realityCheck = new RealityCheck() {
            @Override
            public boolean isReality(double truthValue) {
                return truthValue > 0.4;
            }

            @Override
            public boolean isFiction(double truthValue) {
                return truthValue < 0.6;
            }
        };
        CategoryMatchImpl categoryMatch = new CategoryMatchImpl(concept, realityCheck);
        for (Concept contributor : contributors) {
            categoryMatch.addContribution(contributor, contributor.getTruthValue());
        }
        return categoryMatch;

//        return new CategoryMatch() {
//            @Override
//            public Concept getConcept() {
//                return concept;
//            }
//
//            @Override
//            public Set<Concept> getContributors() {
//                return contributors;
//            }
//
//            @Override
//            public double getMatchScore() {
//                return matchScore;
//            }
//
//            @Override
//            public double getContributorScore(Concept contributor) {
//                return contributor.getTruthValue();
//            }
//
//            @Override
//            public boolean withinReality(Concept contributor) {
//                return contributor.getName().startsWith("realistic");
//            }
//
//            @Override
//            public boolean withinFiction(Concept contributor) {
//                return contributor.getName().startsWith("unrealistic");
//            }
//        };
    }
}
