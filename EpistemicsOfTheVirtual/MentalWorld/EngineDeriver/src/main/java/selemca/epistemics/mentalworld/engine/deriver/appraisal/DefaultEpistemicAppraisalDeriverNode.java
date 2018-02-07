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
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.CATEGORY_MATCH;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.REALISTIC_CONTRIBUTIONS;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.UNREALISTIC_CONTRIBUTIONS;

public class DefaultEpistemicAppraisalDeriverNode implements EpistemicAppraisalDeriverNode {
    final double ACCEPT_AS_REALISTIC_CRITERION_DEFAULT = 0.4;

    private final WorkingMemory workingMemory;
    private final MentalWorldEngine.Logger logger;
    private final RealityCheck realityCheck;
    private final double criterion;

    public DefaultEpistemicAppraisalDeriverNode(WorkingMemory workingMemory, MentalWorldEngine.Logger logger, RealityCheck realityCheck, Configuration applicationSettings) {
        this.workingMemory = workingMemory;
        this.logger = logger;
        this.realityCheck = realityCheck;
        this.criterion = applicationSettings.getDouble(ACCEPT_AS_REALISTIC_CRITERION, ACCEPT_AS_REALISTIC_CRITERION_DEFAULT);
    }

    @Override
    public void apply() {
        CategoryMatch categoryMatch = workingMemory.get(CATEGORY_MATCH);
        Concept concept = categoryMatch.getConcept();
        for (Concept contributor : categoryMatch.getContributors()) {
            double truthValue = categoryMatch.getContributorScore(contributor);
            Association contribution = new Association(concept, contributor, truthValue);
            if (realityCheck.isReality(truthValue)) {
                logger.debug(String.format("%s realistic", contribution));
                workingMemory.add(REALISTIC_CONTRIBUTIONS, contribution);
            } else {
                // Re-examine unrealistic contribution: if it exceeds criterion, regard realistic after all.
                if (contribution.getTruthValue() > criterion) {
                    logger.debug(String.format("%s unrealistic, however it exceeds criterion %s, thus realistic", contribution, criterion));

                    workingMemory.add(REALISTIC_CONTRIBUTIONS, contribution);
                } else {
                    logger.debug(String.format("%s unrealistic. It does not exceed criterion %s.", contribution, criterion));
                    workingMemory.add(UNREALISTIC_CONTRIBUTIONS, contribution);
                }
            }
        }
    }

    @Override
    public Concept getCategory() {
        return workingMemory.get(CATEGORY_MATCH).getConcept();
    }

    @Override
    public Iterable<Association> getRealisticContributions() {
        return workingMemory.getAll(REALISTIC_CONTRIBUTIONS);
    }

    @Override
    public Iterable<Association> getUnrealisticContributions() {
        return workingMemory.getAll(UNREALISTIC_CONTRIBUTIONS);
    }
}
