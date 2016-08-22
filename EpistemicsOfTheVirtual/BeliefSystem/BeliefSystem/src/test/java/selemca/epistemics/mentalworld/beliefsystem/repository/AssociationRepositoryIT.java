/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.repository;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.config.BeliefSystemConfig;

import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BeliefSystemTestConfig.class, BeliefSystemConfig.class})
@Transactional
public class AssociationRepositoryIT {
    private static final String CONCEPT1_NAME = "concept1";
    private static final String CONCEPT2_NAME = "concept2";
    private static final double TEST_TV = 0.7;
    private static final double TEST_UPDATED_TV = 0.6;

    @Autowired
    ConceptRepository conceptRepository;

    @Autowired
    AssociationRepository associationRepository;

    @Test
    public void testCRUD() {
        Concept concept1 = new Concept(CONCEPT1_NAME, 0.8);
        Concept concept2 = new Concept(CONCEPT2_NAME, 0.8);
        // First of all this test can only test correctly is the concept to be added is not already
        // in the database
        Assert.assertFalse(associationRepository.findByConcept1AndConcept2(concept1, concept2).isPresent());

        conceptRepository.save(concept1);
        conceptRepository.save(concept2);
        Association association = new Association(concept1, concept2, TEST_TV);

        // Create
        Association savedAssociation = associationRepository.save(association);
        Assert.assertEquals(concept1, savedAssociation.getConcept1());
        Assert.assertEquals(concept2, savedAssociation.getConcept2());
        Assert.assertEquals(TEST_TV, savedAssociation.getTruthValue(), 0.001);

        // Read
        Optional<Association> readAssociationOptional = associationRepository.findByConcept1AndConcept2(concept1, concept2);
        Assert.assertTrue(readAssociationOptional.isPresent());
        Association readAssociation = readAssociationOptional.get();
        Assert.assertEquals(concept1, readAssociation.getConcept1());
        Assert.assertEquals(concept2, readAssociation.getConcept2());
        Assert.assertEquals(TEST_TV, readAssociation.getTruthValue(), 0.001);

        // Update
        savedAssociation.setTruthValue(TEST_UPDATED_TV);
        savedAssociation = associationRepository.save(savedAssociation);
        Assert.assertEquals(concept1, savedAssociation.getConcept1());
        Assert.assertEquals(concept2, savedAssociation.getConcept2());
        Assert.assertEquals(TEST_UPDATED_TV, savedAssociation.getTruthValue(), 0.001);

        // Read again
        readAssociationOptional = associationRepository.findByConcept1AndConcept2(concept1, concept2);
        Assert.assertTrue(readAssociationOptional.isPresent());
        readAssociation = readAssociationOptional.get();
        Assert.assertEquals(concept1, readAssociation.getConcept1());
        Assert.assertEquals(concept2, readAssociation.getConcept2());
        Assert.assertEquals(TEST_UPDATED_TV, readAssociation.getTruthValue(), 0.001);

        // Delete
        associationRepository.delete(savedAssociation);
        Assert.assertFalse(associationRepository.findByConcept1AndConcept2(concept1, concept2).isPresent());
    }

    /**
     * Test writing a new association with PK already present.
     * This should overwrite the existing record.
     * The difference with the update above is that the update will read the association, change it
     * and write that instance.
     */
    @Test
    @Ignore("Apparently, this is not possible")
    public void testOverwriteExisiting() {
        Concept concept1 = new Concept(CONCEPT1_NAME, 0.8);
        Concept concept2 = new Concept(CONCEPT2_NAME, 0.8);
        // First of all this test can only test correctly is the concept to be added is not already
        // in the database
        Assert.assertFalse(associationRepository.findByConcept1AndConcept2(concept1, concept2).isPresent());

        conceptRepository.save(concept1);
        conceptRepository.save(concept2);
        Association association = new Association(concept1, concept2, TEST_TV);
        associationRepository.save(association);

        // Write new association
        Association copy = new Association(concept1, concept2, TEST_UPDATED_TV);
        associationRepository.save(copy);

        // Read again
        Optional<Association> readAssociationOptional = associationRepository.findByConcept1AndConcept2(concept1, concept2);
        Assert.assertTrue(readAssociationOptional.isPresent());
        Association readAssociation = readAssociationOptional.get();
        Assert.assertEquals(concept1, readAssociation.getConcept1());
        Assert.assertEquals(concept2, readAssociation.getConcept2());
        Assert.assertEquals(TEST_UPDATED_TV, readAssociation.getTruthValue(), 0.001);
    }
}
