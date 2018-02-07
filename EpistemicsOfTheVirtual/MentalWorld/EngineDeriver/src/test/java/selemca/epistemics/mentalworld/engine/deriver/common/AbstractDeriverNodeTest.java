/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.common;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.mockito.Mock;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.graph.GraphBuilder;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.realitycheck.RealityCheck;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

import java.util.List;
import java.util.Set;

import static org.mockito.AdditionalMatchers.geq;
import static org.mockito.AdditionalMatchers.leq;
import static org.mockito.Mockito.when;
import static selemca.epistemics.mentalworld.engine.config.EngineConfig.BELIEF_SYSTEM_GRAPH;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.OBSERVATION_FEATURES;

public abstract class AbstractDeriverNodeTest {
    protected WorkingMemory workingMemory = new WorkingMemory();
    protected CategoryMatch categoryMatch;
    protected SampleBeliefSystem sampleBeliefSystem = new SampleBeliefSystem();
    protected BeliefModelService beliefModelService;

    @Mock
    protected MentalWorldEngine.Logger logger;

    @Mock
    protected RealityCheck realityCheck;

    protected Configuration applicationSettings = new BaseConfiguration();

    protected void initRealityCheck() {
        when(realityCheck.isReality(geq(0.4))).thenReturn(true);
        when(realityCheck.isFiction(leq(0.6))).thenReturn(true);
    }

    protected void initBeliefSystem() {
        List<Concept> concepts = sampleBeliefSystem.asConceptRepository().findAll();
        List<Association> associations = sampleBeliefSystem.asAssociationRepository().findAll();
        BELIEF_SYSTEM_GRAPH.add(workingMemory, new GraphBuilder(concepts, associations).build());
        beliefModelService = sampleBeliefSystem.asBeliefModelService();
    }

    protected void initCategoryMatch(double... contributorTruthValues) {
        MockCategoryMatchBuilder builder = new MockCategoryMatchBuilder();
        for (double truthValue : contributorTruthValues) {
            builder.addContributor(truthValue);
        }
        categoryMatch = builder.build();
        workingMemory.setCategoryMatch(categoryMatch);

    }

    protected void initObservationFeatures(Set<String> observationFeatures) {
        OBSERVATION_FEATURES.addAll(workingMemory, observationFeatures);
    }

    protected void initCategoryMatch(CategoryMatch categoryMatch) {
        workingMemory.setCategoryMatch(categoryMatch);
    }
}
