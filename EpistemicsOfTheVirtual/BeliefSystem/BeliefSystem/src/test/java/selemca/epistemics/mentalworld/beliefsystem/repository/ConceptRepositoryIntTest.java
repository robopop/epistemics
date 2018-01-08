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
import selemca.epistemics.mentalworld.beliefsystem.config.BeliefSystemConfig;
import selemca.epistemics.mentalworld.beliefsystem.config.BeliefSystemTestConfig;

import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BeliefSystemTestConfig.class, BeliefSystemConfig.class})
@Transactional
public class ConceptRepositoryIntTest {
    private static final String TEST_ID = "unittest";
    private static final double TEST_TV = 0.7;
    private static final double TEST_UPDATED_TV = 0.6;

    @Autowired
    ConceptRepository conceptRepository;

    @Test
    public void testCRUD() {
        // First of all this test can only test correctly is the concept to be added is not already
        // in the database
        Assert.assertFalse(conceptRepository.findOne(TEST_ID).isPresent());

        Concept concept = new Concept(TEST_ID, TEST_TV);

        // Create
        Concept savedConcept = conceptRepository.save(concept);
        Assert.assertEquals(TEST_ID, savedConcept.getName());
        Assert.assertEquals(TEST_TV, savedConcept.getTruthValue(), 0.001);

        // Read
        Optional<Concept> readOptional = conceptRepository.findOne(TEST_ID);
        Assert.assertTrue(readOptional.isPresent());
        Assert.assertEquals(TEST_ID, readOptional.get().getName());
        Assert.assertEquals(TEST_TV, readOptional.get().getTruthValue(), 0.001);

        // Update
        savedConcept.setTruthValue(TEST_UPDATED_TV);
        savedConcept = conceptRepository.save(savedConcept);
        Assert.assertEquals(TEST_ID, savedConcept.getName());
        Assert.assertEquals(TEST_UPDATED_TV, savedConcept.getTruthValue(), 0.001);

        // Read again
        readOptional = conceptRepository.findOne(TEST_ID);
        Assert.assertTrue(readOptional.isPresent());
        Assert.assertEquals(TEST_ID, readOptional.get().getName());
        Assert.assertEquals(TEST_UPDATED_TV, readOptional.get().getTruthValue(), 0.001);

        // Delete
        conceptRepository.delete(savedConcept);
        Assert.assertFalse(conceptRepository.findOne(TEST_ID).isPresent());
    }

    /**
     * Test writing a new concept with PK already present.
     * This should overwrite the existing record.
     * The difference with the update above is that the update will read the concept, change it
     * and write that instance.
     */
    @Test
    public void testOverwriteExisiting() {
        // First of all this test can only test correctly is the concept to be added is not already
        // in the database
        Assert.assertFalse(conceptRepository.findOne(TEST_ID).isPresent());

        Concept concept = new Concept(TEST_ID, TEST_TV);
        conceptRepository.save(concept);

        // Write new concept
        Concept copy = new Concept(TEST_ID,TEST_UPDATED_TV);
        conceptRepository.save(copy);

        // Read again
        Optional<Concept> readOptional = conceptRepository.findOne(TEST_ID);
        Assert.assertTrue(readOptional.isPresent());
        Assert.assertEquals(TEST_ID, readOptional.get().getName());
        Assert.assertEquals(TEST_UPDATED_TV, readOptional.get().getTruthValue(), 0.001);
    }
}
