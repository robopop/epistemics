/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.appraisal;

import org.apache.commons.configuration.Configuration;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.node.EpistemicAppraisalDeriverNode;
import selemca.epistemics.mentalworld.engine.realitycheck.RealityCheck;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

import static selemca.epistemics.mentalworld.engine.deriver.appraisal.EpistemicAppraisalDeriverNodeSettingsProvider.ACCEPT_AS_REALISTIC_CRITERION;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.REALISTIC_CONTRIBUTIONS;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.UNREALISTIC_CONTRIBUTIONS;

public class DefaultEpistemicAppraisalDeriverNode implements EpistemicAppraisalDeriverNode {
    final double ACCEPT_AS_REALISTIC_CRITERION_DEFAULT = 0.4;

    private final WorkingMemory workingMemory;
    private final MentalWorldEngine.Logger logger;
    private final RealityCheck realityCheck;
    private final double criterion;
    private final Concept concept;

    public DefaultEpistemicAppraisalDeriverNode(WorkingMemory workingMemory, MentalWorldEngine.Logger logger, RealityCheck realityCheck, Configuration applicationSettings) {
        this.workingMemory = workingMemory;
        this.logger = logger;
        this.realityCheck = realityCheck;
        this.criterion = applicationSettings.getDouble(ACCEPT_AS_REALISTIC_CRITERION, ACCEPT_AS_REALISTIC_CRITERION_DEFAULT);

        this.concept = workingMemory.getCategoryMatch().getConcept();
        run();
    }

    private void run() {
        CategoryMatch categoryMatch = workingMemory.getCategoryMatch();
        for (Concept contributor : categoryMatch.getContributors()) {
            double truthValue = categoryMatch.getContributorScore(contributor);
            Association contribution = new Association(concept, contributor, truthValue);
            if (realityCheck.isReality(truthValue)) {
                logger.debug(String.format("%s realistic", contribution));
                REALISTIC_CONTRIBUTIONS.add(workingMemory, contribution);
            } else {
                // Re-examine unrealistic contribution: if it exceeds criterion, regard realistic after all.
                if (contribution.getTruthValue() > criterion) {
                    logger.debug(String.format("%s unrealistic, however it exceeds criterion %s, thus realistic", contribution, criterion));

                    REALISTIC_CONTRIBUTIONS.add(workingMemory, contribution);
                } else {
                    logger.debug(String.format("%s unrealistic. It does not exceed criterion %s.", contribution, criterion));
                    UNREALISTIC_CONTRIBUTIONS.add(workingMemory, contribution);
                }
            }
        }
    }

    @Override
    public Concept getCategory() {
        return concept;
    }

    @Override
    public Iterable<Association> getRealisticContributions() {
        return REALISTIC_CONTRIBUTIONS.get(workingMemory);
    }

    @Override
    public Iterable<Association> getUnrealisticContributions() {
        return UNREALISTIC_CONTRIBUTIONS.get(workingMemory);
    }
}
