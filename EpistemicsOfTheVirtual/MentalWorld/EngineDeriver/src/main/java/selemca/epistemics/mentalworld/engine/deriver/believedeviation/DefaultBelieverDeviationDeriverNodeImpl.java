/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.believedeviation;

import org.apache.commons.configuration.Configuration;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.node.BelieverDeviationDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

import static selemca.epistemics.mentalworld.engine.deriver.believedeviation.BelieverDeviationDeriverNodeSettingsProvider.BELIEVE_DEVIATION_CRITERION;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.CATEGORY_MATCH;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.UNWILLING_TO_DEVIATE_CONTRIBUTORS;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.WILLING_TO_DEVIATE_CONTRIBUTORS;

public class DefaultBelieverDeviationDeriverNodeImpl implements BelieverDeviationDeriverNode {
    final double DEVIATION_CRITERION_DEFAULT = 0.4;

    private final WorkingMemory workingMemory;
    private final MentalWorldEngine.Logger logger;
    private final double criterion;

    public DefaultBelieverDeviationDeriverNodeImpl(WorkingMemory workingMemory, MentalWorldEngine.Logger logger, Configuration applicationSettings) {
        this.workingMemory = workingMemory;
        this.logger = logger;
        this.criterion = applicationSettings.getDouble(BELIEVE_DEVIATION_CRITERION, DEVIATION_CRITERION_DEFAULT);

        examineDeviation();
    }

    private void examineDeviation() {
        CategoryMatch categoryMatch = workingMemory.get(CATEGORY_MATCH);
        Concept bestFit = categoryMatch.getConcept();
        for (Concept contribution : categoryMatch.getContributors()) {
            double truthValue = categoryMatch.getContributorScore(contribution);
            if (truthValue < criterion) {
                logger.debug(String.format("Unwilling to accept a relation between %s and %s (truth value is %s).", contribution, bestFit, truthValue));
                workingMemory.add(UNWILLING_TO_DEVIATE_CONTRIBUTORS, contribution);
            } else {
                workingMemory.add(WILLING_TO_DEVIATE_CONTRIBUTORS, contribution);
            }
        }
    }

    @Override
    public boolean isDeviationTolerant() {
        return !getUnwillingToDeviateContributors().iterator().hasNext();
    }

    @Override
    public Iterable<Concept> getWillingToDeviateContributors() {
        return workingMemory.getAll(WILLING_TO_DEVIATE_CONTRIBUTORS);
    }

    @Override
    public Iterable<Concept> getUnwillingToDeviateContributors() {
        return workingMemory.getAll(UNWILLING_TO_DEVIATE_CONTRIBUTORS);
    }
}
