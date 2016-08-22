/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.matcher;

import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.graph.GraphBuilder;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.deriver.common.SampleBeliefSystem;
import selemca.epistemics.mentalworld.engine.realitycheck.RealityCheck;
import selemca.epistemics.mentalworld.registry.RealityCheckRegistry;

import java.util.*;

import static org.mockito.AdditionalMatchers.geq;
import static org.mockito.AdditionalMatchers.leq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CategoryMatcherImplTest {

    Configuration applicationSettings = new BaseConfiguration();

    @Mock
    MentalWorldEngine.Logger logger;

    @Mock
    RealityCheck realityCheck;
    @Mock
    RealityCheckRegistry realityCheckRegistry;

    @InjectMocks
    CategoryMatcherImpl classUnderTest;

    SampleBeliefSystem sampleBeliefSystem = new SampleBeliefSystem();
    Graph<Concept, Association> beliefModelGraph;

    @Before
    public void init() {
        when(realityCheckRegistry.getImplementation()).thenReturn(Optional.of(realityCheck));
        when(realityCheck.isReality(geq(0.4))).thenReturn(true);
        when(realityCheck.isFiction(leq(0.6))).thenReturn(true);

        List<Concept> concepts = sampleBeliefSystem.asConceptRepository().findAll();
        List<Association> associations = sampleBeliefSystem.asAssociationRepository().findAll();
        beliefModelGraph = new GraphBuilder(concepts, associations).build();
    }

    @Test
    public void testFindMatchBird() {
        Set<String> observedFeatures = new HashSet<>(Arrays.asList("2 legs", "beak", "feathers", "flying", "webbed toes"));

        Optional<CategoryMatch> matchOptional = classUnderTest.findMatch(beliefModelGraph, observedFeatures, logger);
        Assert.assertTrue(matchOptional.isPresent());
        CategoryMatch match = matchOptional.get();
        Assert.assertEquals("bird", match.getConcept().getName());

        Set<String> contributorNames = extractNames(match.getContributors());
        for (String observed : observedFeatures) {
            Assert.assertTrue(observed, contributorNames.contains(observed));
        }
    }

    @Test
    public void testFindMatchClown() {
        Set<String> observedFeatures = new HashSet<>(Arrays.asList("colorful wig", "grime", "red nose", "wig"));

        Optional<CategoryMatch> matchOptional = classUnderTest.findMatch(beliefModelGraph, observedFeatures, logger);
        Assert.assertTrue(matchOptional.isPresent());
        CategoryMatch match = matchOptional.get();
        Assert.assertEquals("clown", match.getConcept().getName());

        Set<String> contributorNames = extractNames(match.getContributors());
        for (String observed : observedFeatures) {
            Assert.assertTrue(observed, contributorNames.contains(observed));
        }
    }

    private Set<String> extractNames(Set<Concept> contributors) {
        Set<String> names = new HashSet<>();
        for (Concept contrubitor : contributors) {
            names.add(contrubitor.getName());
        }
        return names;
    }

}
