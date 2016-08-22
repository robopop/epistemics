/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.engine.deriver.common.AbstractDeriverNodeTest;
import selemca.epistemics.mentalworld.engine.deriver.common.MockCategoryMatchBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class DefaultContextMatchDeriverNodeImplTest extends AbstractDeriverNodeTest {
    final String MAXIMUM_DISTANCE = "engine.contextAssociationMaximumDistance";

    DefaultContextMatchDeriverNodeImpl classUnderTest;

    @Mock
    AssociationRepository associationRepository;



    @Before
    public void init() {
        initRealityCheck();
        initBeliefSystem();
        applicationSettings.setProperty(MAXIMUM_DISTANCE, 1.0);

        Set<String> observedFeatures = new HashSet<>(Arrays.asList("2 legs", "beak", "feathers", "swimming", "webbed toes"));
        workingMemory.setObservationFeatures(observedFeatures);

        MockCategoryMatchBuilder builder = new MockCategoryMatchBuilder();
        Optional<Concept> duck = sampleBeliefSystem.asConceptRepository().findOne("duck");
        builder.withConcept(duck.get());
        for (String feature : observedFeatures) {
            builder.addContributor(feature, 0.7);
        }
        workingMemory.setCategoryMatch(builder.build());
    }

    /**
     * Given that the category match has defered duck for observation 2 legs, beak, feathers, swimming and webbed toes (see init),
     * and the current context is nature, test that this is an appropriate context.
     */
    @Test
    public void testContextMatchCurrentCorrect() {
        beliefModelService.setContext("nature");
        classUnderTest = new DefaultContextMatchDeriverNodeImpl(beliefModelService, beliefModelGraph, workingMemory, logger, applicationSettings);

        Assert.assertTrue(classUnderTest.contextMatch());
    }

    /**
     * Given that the category match has defered duck for observation 2 legs, beak, feathers, swimming and webbed toes (see init),
     * and the current context is metro, test that this is not an appropriate context.
     * Furthermore, test that a more appropriate context nature is set as new context.
     */
    @Test
    public void testContextMatchCurrentWrong() {
        beliefModelService.setContext("metro");
        classUnderTest = new DefaultContextMatchDeriverNodeImpl(beliefModelService, beliefModelGraph, workingMemory, logger, applicationSettings);

        Assert.assertFalse(classUnderTest.contextMatch());
        Assert.assertEquals("nature", workingMemory.getNewContext().getName());
    }

}
