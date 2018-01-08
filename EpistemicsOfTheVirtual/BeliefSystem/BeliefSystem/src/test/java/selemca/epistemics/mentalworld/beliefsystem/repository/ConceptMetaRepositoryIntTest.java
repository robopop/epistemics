/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.data.entity.ConceptMeta;
import selemca.epistemics.mentalworld.beliefsystem.config.BeliefSystemConfig;
import selemca.epistemics.mentalworld.beliefsystem.config.BeliefSystemTestConfig;

import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BeliefSystemTestConfig.class, BeliefSystemConfig.class})
@Transactional
public class ConceptMetaRepositoryIntTest {
    private static final String TEST_ID = "unittest";
    private static final double TEST_TV = 0.7;
    private static final String META_RELATION = "kindof";
    private static final String META_VALUE = "test";
    private static final String META_RELATION_UPDATE = "isa";

    @Autowired
    ConceptRepository conceptRepository;

    @Autowired
    ConceptMetaRepository conceptMetaRepository;

    @Test
    public void testCRUD() {
        Concept concept = new Concept(TEST_ID, TEST_TV);
        ConceptMeta conceptMeta = new ConceptMeta(concept, META_RELATION, META_VALUE);

        // Create
        conceptRepository.save(concept);
        ConceptMeta savedConceptMeta = conceptMetaRepository.save(conceptMeta);
        Assert.assertEquals(TEST_ID, savedConceptMeta.getConcept().getName());
        Assert.assertEquals(TEST_TV, savedConceptMeta.getConcept().getTruthValue(), 0.001);
        Assert.assertEquals(META_RELATION, savedConceptMeta.getRelation());
        Assert.assertEquals(META_VALUE, savedConceptMeta.getValue());
        long savedConceptMetaId = savedConceptMeta.getId();

        // Read
        Optional<ConceptMeta> readOptional = conceptMetaRepository.findOne(savedConceptMetaId);
        Assert.assertTrue(readOptional.isPresent());
        ConceptMeta readConceptMeta = readOptional.get();
        Assert.assertEquals(TEST_ID, readConceptMeta.getConcept().getName());
        Assert.assertEquals(TEST_TV, readConceptMeta.getConcept().getTruthValue(), 0.001);
        Assert.assertEquals(META_RELATION, readConceptMeta.getRelation());
        Assert.assertEquals(META_VALUE, readConceptMeta.getValue());

        // Update
        savedConceptMeta.setRelation(META_RELATION_UPDATE);
        savedConceptMeta = conceptMetaRepository.save(savedConceptMeta);
        Assert.assertEquals(META_RELATION_UPDATE, savedConceptMeta.getRelation());

        // Read again
        readOptional = conceptMetaRepository.findOne(savedConceptMetaId);
        Assert.assertTrue(readOptional.isPresent());
        readConceptMeta = readOptional.get();
        Assert.assertEquals(TEST_ID, readConceptMeta.getConcept().getName());
        Assert.assertEquals(TEST_TV, readConceptMeta.getConcept().getTruthValue(), 0.001);
        Assert.assertEquals(META_RELATION_UPDATE, readConceptMeta.getRelation());
        Assert.assertEquals(META_VALUE, readConceptMeta.getValue());

        // Delete
        conceptMetaRepository.delete(savedConceptMeta);
        Assert.assertFalse(conceptMetaRepository.findOne(savedConceptMetaId).isPresent());
    }
}
