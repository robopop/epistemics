/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.metaphorprocessing;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.beliefsystem.service.WeightedBeliefModelService;
import selemca.epistemics.mentalworld.engine.metaphor.MetaphorProcessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MetaphorProcessorImplTest {
    private static final String VICINITY_TRESHOLT = "engine.metaphor.vicinity";
    private static final double VICINITY_TRESHOLT_DEFAULT = 0.5;
    private static final String RELATION_TYPE = "relationType";
    private static final String DEFAULT_RELATION_TYPE = "engine.metaphor.relationTypeDefault";
    private static final String INTERSECTION_MINUMUM_SIZE_ABSOLUTE = "engine.metaphor.intersectionMinimumSize.absolute";
    private static final String INTERSECTION_MINUMUM_SIZE_RELATIVE = "engine.metaphor.intersectionMinimumSize.relative";
    private static final String MIXED_INTERSECTION_MINUMUM_SIZE_ABSOLUTE = "engine.metaphor.intersectionMixedMinimumSize.absolute";
    private static final String MIXED_INTERSECTION_MINUMUM_SIZE_RELATIVE = "engine.metaphor.intersectionMixedMinimumSize.relative";

    @Spy
    private Configuration applicationSettings = new BaseConfiguration();

    @Mock
    private BeliefModelService beliefModelService;

    @Mock
    private WeightedBeliefModelService weightedBeliefModelService;

    @InjectMocks
    MetaphorProcessorImpl classUnderTest;

    @Test
    public void testAnomaly() {
        Concept rose = new Concept("rose", 0.8);
        Set<Concept> roseProperties = createConcepts("red", "leafs", "thorns", "pain", "beauty", "trunk");
        Concept beaver = new Concept("beaver", 0.8);
        Set<Concept> beaverProperties = createConcepts("fur", "4 legs", "breast feeding");

        when(weightedBeliefModelService.listAssociationConcepts(rose, VICINITY_TRESHOLT_DEFAULT)).thenReturn(roseProperties);
        when(weightedBeliefModelService.listAssociationConcepts(beaver, VICINITY_TRESHOLT_DEFAULT)).thenReturn(beaverProperties);

        applyMetadata("rose", "red", "l");
        applyMetadata("rose", "leafs", "l");
        applyMetadata("rose", "thorns", "l");
        applyMetadata("rose", "pain", "l");
        applyMetadata("rose", "beauty", "f");
        applyMetadata("rose", "trunk", "l");

        applyMetadata("beaver", "fur", "l");
        applyMetadata("beaver", "4 legs", "l");
        applyMetadata("beaver", "breast feeding", "l");

        MetaphorProcessor.MetaphorAssesment metaphorAssesment = classUnderTest.assesRelation(rose, beaver);
        Assert.assertEquals(MetaphorProcessor.MetaphorAssesment.ANOMALY, metaphorAssesment);
    }

    @Test
    public void testLiteral() {
        Concept rose = new Concept("rose", 0.8);
        Set<Concept> roseProperties = createConcepts("red", "leafs", "thorns", "pain", "beauty", "trunk");
        Concept flower = new Concept("flower", 0.8);
        Set<Concept> flowerProperties = createConcepts("leafs", "green", "trunk");

        when(weightedBeliefModelService.listAssociationConcepts(rose, VICINITY_TRESHOLT_DEFAULT)).thenReturn(roseProperties);
        when(weightedBeliefModelService.listAssociationConcepts(flower, VICINITY_TRESHOLT_DEFAULT)).thenReturn(flowerProperties);

        applyMetadata("rose", "red", "l");
        applyMetadata("rose", "leafs", "l");
        applyMetadata("rose", "thorns", "l");
        applyMetadata("rose", "pain", "l");
        applyMetadata("rose", "beauty", "f");
        applyMetadata("rose", "trunk", "l");

        applyMetadata("flower", "leafs", "l");
        applyMetadata("flower", "green", "l");
        applyMetadata("flower", "trunk", "l");
        applyMetadata("flower", "birth", "f");

        MetaphorProcessor.MetaphorAssesment metaphorAssesment = classUnderTest.assesRelation(rose, flower);
        Assert.assertEquals(MetaphorProcessor.MetaphorAssesment.LITERAL, metaphorAssesment);
    }

    @Test
    public void testMetaphor() {
        Concept rose = new Concept("rose", 0.8);
        Set<Concept> roseProperties = createConcepts("red", "leafs", "thorns", "pain", "beauty", "trunk");
        Concept love = new Concept("love", 0.8);
        Set<Concept> loveProperties = createConcepts("red", "pain", "emotions", "beauty");

        when(weightedBeliefModelService.listAssociationConcepts(rose, VICINITY_TRESHOLT_DEFAULT)).thenReturn(roseProperties);
        when(weightedBeliefModelService.listAssociationConcepts(love, VICINITY_TRESHOLT_DEFAULT)).thenReturn(loveProperties);

        applyMetadata("rose", "red", "l");
        applyMetadata("rose", "leafs", "l");
        applyMetadata("rose", "thorns", "l");
        applyMetadata("rose", "pain", "l");
        applyMetadata("rose", "beauty", "f");
        applyMetadata("rose", "trunk", "l");

        applyMetadata("love", "red", "f");
        applyMetadata("love", "pain", "l");
        applyMetadata("love", "emotion", "l");
        applyMetadata("love", "beauty", "l");

        MetaphorProcessor.MetaphorAssesment metaphorAssesment = classUnderTest.assesRelation(rose, love);
        Assert.assertEquals(MetaphorProcessor.MetaphorAssesment.METAPHOR, metaphorAssesment);
    }

    private void applyMetadata(String concept1Name, String concept2Name, String relationType) {
        Concept concept1 = createConcept(concept1Name);
        Concept concept2 = createConcept(concept2Name);

        AssociationMeta associationMeta = new AssociationMeta(concept1, concept2, RELATION_TYPE, relationType);
        List<AssociationMeta> associationMetaList = new ArrayList<>();
        associationMetaList.add(associationMeta);
        when(beliefModelService.getAssociationMeta(concept1, concept2)).thenReturn(associationMetaList);
        when(beliefModelService.getAssociationMeta(concept2, concept1)).thenReturn(associationMetaList);
    }

    private Set<Concept> createConcepts(String... names) {
        Set<Concept> concepts = new HashSet<>();
        for (String name : names) {
            concepts.add(createConcept(name));
        }
        return concepts;
    }

    private Concept createConcept(String name) {
        return new Concept(name, 0.8);
    }
}
