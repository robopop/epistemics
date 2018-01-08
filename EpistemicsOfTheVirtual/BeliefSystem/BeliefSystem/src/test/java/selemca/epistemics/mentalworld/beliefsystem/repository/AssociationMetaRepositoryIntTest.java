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
import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.config.BeliefSystemConfig;
import selemca.epistemics.mentalworld.beliefsystem.config.BeliefSystemTestConfig;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BeliefSystemTestConfig.class, BeliefSystemConfig.class})
@Transactional
public class AssociationMetaRepositoryIntTest {
    private static final String CONCEPT1_NAME = "concept1";
    private static final String CONCEPT2_NAME = "concept2";
    private static final String META_RELATION = "relationType";
    private static final String META_VALUE = "l";
    private static final String META_VALUE_UPDATED = "f";

    @Autowired
    ConceptRepository conceptRepository;

    @Autowired
    AssociationMetaRepository associationMetaRepository;

    @Test
    public void testCRUD() {
        Concept concept1 = new Concept(CONCEPT1_NAME, 0.8);
        Concept concept2 = new Concept(CONCEPT2_NAME, 0.8);
        // First of all this test can only test correctly is the concept to be added is not already
        // in the database
        Assert.assertEquals(0, associationMetaRepository.findByConcept1AndConcept2(concept1, concept2).size());

        conceptRepository.save(concept1);
        conceptRepository.save(concept2);
        AssociationMeta associationMeta = new AssociationMeta(concept1, concept2, META_RELATION, META_VALUE);

        // Create
        AssociationMeta savedAssociationMeta = associationMetaRepository.save(associationMeta);
        Assert.assertEquals(concept1, savedAssociationMeta.getConcept1());
        Assert.assertEquals(concept2, savedAssociationMeta.getConcept2());
        Assert.assertEquals(META_RELATION, savedAssociationMeta.getRelation());
        Assert.assertEquals(META_VALUE, savedAssociationMeta.getValue());

        // Read
        List<AssociationMeta> readAssociationMetaList = associationMetaRepository.findByConcept1AndConcept2(concept1, concept2);
        Assert.assertEquals(1, readAssociationMetaList.size());
        AssociationMeta readAssociationMeta = readAssociationMetaList.get(0);
        Assert.assertEquals(concept1, readAssociationMeta.getConcept1());
        Assert.assertEquals(concept2, readAssociationMeta.getConcept2());
        Assert.assertEquals(META_RELATION, readAssociationMeta.getRelation());
        Assert.assertEquals(META_VALUE, readAssociationMeta.getValue());

        // Update
        savedAssociationMeta.setValue(META_VALUE_UPDATED);
        savedAssociationMeta = associationMetaRepository.save(savedAssociationMeta);
        Assert.assertEquals(concept1, savedAssociationMeta.getConcept1());
        Assert.assertEquals(concept2, savedAssociationMeta.getConcept2());
        Assert.assertEquals(META_RELATION, savedAssociationMeta.getRelation());
        Assert.assertEquals(META_VALUE_UPDATED, savedAssociationMeta.getValue());

        // Read again
        readAssociationMetaList = associationMetaRepository.findByConcept1AndConcept2(concept1, concept2);
        Assert.assertEquals(1, readAssociationMetaList.size());
        readAssociationMeta = readAssociationMetaList.get(0);
        Assert.assertEquals(concept1, readAssociationMeta.getConcept1());
        Assert.assertEquals(concept2, readAssociationMeta.getConcept2());
        Assert.assertEquals(META_RELATION, readAssociationMeta.getRelation());
        Assert.assertEquals(META_VALUE_UPDATED, readAssociationMeta.getValue());

        // Delete
        associationMetaRepository.delete(savedAssociationMeta);
        Assert.assertEquals(0, associationMetaRepository.findByConcept1AndConcept2(concept1, concept2).size());
    }
}
