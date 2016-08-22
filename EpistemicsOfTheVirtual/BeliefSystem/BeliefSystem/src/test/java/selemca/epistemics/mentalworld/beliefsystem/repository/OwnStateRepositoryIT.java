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
import selemca.epistemics.data.entity.OwnState;
import selemca.epistemics.mentalworld.beliefsystem.config.BeliefSystemConfig;

import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BeliefSystemTestConfig.class, BeliefSystemConfig.class})
@Transactional
public class OwnStateRepositoryIT {
    private static final String TEST_KEY = "key";
    private static final String TEST_VALUE = "value";
    private static final String TEST_VALUE_UPDATED = "changed";

    @Autowired
    OwnStateRepository ownStateRepository;

    @Test
    public void testCRUD() {
        // First of all this test can only test correctly is the concept to be added is not already
        // in the database
        Assert.assertFalse(ownStateRepository.findOne(TEST_KEY).isPresent());

        OwnState ownState = new OwnState(TEST_KEY, TEST_VALUE);

        // Create
        OwnState savedOwnState = ownStateRepository.save(ownState);
        Assert.assertEquals(TEST_KEY, savedOwnState.getProperty());
        Assert.assertEquals(TEST_VALUE, savedOwnState.getValue());

        // Read
        Optional<OwnState> readOwnStateOptional = ownStateRepository.findOne(TEST_KEY);
        Assert.assertTrue(readOwnStateOptional.isPresent());
        Assert.assertEquals(TEST_KEY, readOwnStateOptional.get().getProperty());
        Assert.assertEquals(TEST_VALUE, readOwnStateOptional.get().getValue());

        // Update
        savedOwnState.setValue(TEST_VALUE_UPDATED);
        savedOwnState = ownStateRepository.save(savedOwnState);
        Assert.assertEquals(TEST_KEY, savedOwnState.getProperty());
        Assert.assertEquals(TEST_VALUE_UPDATED, savedOwnState.getValue());

        // Read again
        readOwnStateOptional = ownStateRepository.findOne(TEST_KEY);
        Assert.assertTrue(readOwnStateOptional.isPresent());
        Assert.assertEquals(TEST_KEY, readOwnStateOptional.get().getProperty());
        Assert.assertEquals(TEST_VALUE_UPDATED, readOwnStateOptional.get().getValue());

        // Delete
        ownStateRepository.delete(savedOwnState);
        Assert.assertFalse(ownStateRepository.findOne(TEST_KEY).isPresent());
    }

    /**
     * Test writing a new ownState with PK already present.
     * This should overwrite the existing record.
     * The difference with the update above is that the update will read the ownState, change it
     * and write that instance.
     */
    @Test
    public void testOverwriteExisiting() {
        // First of all this test can only test correctly is the concept to be added is not already
        // in the database
        Assert.assertFalse(ownStateRepository.findOne(TEST_KEY).isPresent());

        OwnState ownState = new OwnState(TEST_KEY, TEST_VALUE);
        ownStateRepository.save(ownState);

        // Write new concept
        OwnState copy = new OwnState(TEST_KEY, TEST_VALUE_UPDATED);
        ownStateRepository.save(copy);

        // Read again
        Optional<OwnState> readOwnStateOptional = ownStateRepository.findOne(TEST_KEY);
        Assert.assertTrue(readOwnStateOptional.isPresent());
        Assert.assertEquals(TEST_KEY, readOwnStateOptional.get().getProperty());
        Assert.assertEquals(TEST_VALUE_UPDATED, readOwnStateOptional.get().getValue());
    }
}
