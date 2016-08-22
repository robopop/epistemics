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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static selemca.epistemics.mentalworld.engine.deriver.believedeviation.BelieverDeviationDeriverNodeSettingsProvider.BELIEVE_DEVIATION_CRITERION;

/**
 * Created by henrizwols on 27-02-15.
 */
public class DefaultBelieverDeviationDeriverNodeImpl implements BelieverDeviationDeriverNode {
    final double DEVIATION_CRITERION_DEFAULT = 0.4;

    private final WorkingMemory workingMemory;
    private final MentalWorldEngine.Logger logger;
    private final double criterion;

    private final List<Concept> willingToDeviateContributors = new ArrayList<>();
    private final List<Concept> unwillingToDeviateContributors = new ArrayList<>();

    public DefaultBelieverDeviationDeriverNodeImpl(WorkingMemory workingMemory, MentalWorldEngine.Logger logger, Configuration applicationSettings) {
        this.workingMemory = workingMemory;
        this.logger = logger;
        this.criterion = applicationSettings.getDouble(BELIEVE_DEVIATION_CRITERION, DEVIATION_CRITERION_DEFAULT);

        examineDeviation();
    }

    private void examineDeviation() {
        CategoryMatch categoryMatch = workingMemory.getCategoryMatch();
        Concept bestFit = categoryMatch.getConcept();
        for (Concept contribution : categoryMatch.getContributors()) {
            double truthValue = categoryMatch.getContributorScore(contribution);
            if (truthValue < criterion) {
                logger.debug(String.format("Unwilling to accept a relation between %s and %s (truth value is %s).", contribution, bestFit, truthValue));
                unwillingToDeviateContributors.add(contribution);
            } else {
                willingToDeviateContributors.add(contribution);
            }
        }
    }

    @Override
    public boolean isDeviationTolerant() {
        return unwillingToDeviateContributors.isEmpty();
    }

    @Override
    public Collection<Concept> getWillingToDeviateContributors() {
        return willingToDeviateContributors;
    }

    @Override
    public Collection<Concept> getUnwillingToDeviateContributors() {
        return unwillingToDeviateContributors;
    }
}
