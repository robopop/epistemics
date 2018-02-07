/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.integratordeviation;

import org.apache.commons.configuration.Configuration;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.node.IntegratorDeviationDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

import static selemca.epistemics.mentalworld.engine.deriver.integratordeviation.IntegratorDeviationDeriverNodeSettingsProvider.INTEGRATOR_DEVIATION_CRITERION;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.CATEGORY_MATCH;

public class DefaultIntegratorDeviationDeriverNodeImpl implements IntegratorDeviationDeriverNode {
    final double DEVIATION_CRITERION_DEFAULT = 0.4;

    private final WorkingMemory workingMemory;
    private final MentalWorldEngine.Logger logger;
    private final double criterion;

    public DefaultIntegratorDeviationDeriverNodeImpl(WorkingMemory workingMemory, MentalWorldEngine.Logger logger, Configuration applicationSettings) {
        this.workingMemory = workingMemory;
        this.logger = logger;
        this.criterion = applicationSettings.getDouble(INTEGRATOR_DEVIATION_CRITERION, DEVIATION_CRITERION_DEFAULT);
    }

    @Override
    public boolean isWillingToDeviate(Concept concept, Concept contribution) {
        CategoryMatch categoryMatch = workingMemory.get(CATEGORY_MATCH);
        return categoryMatch.getContributorScore(contribution) >= criterion;
    }
}
