/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.believedeviation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import selemca.epistemics.mentalworld.engine.deriver.common.AbstractDeriverNodeTest;

import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.size;

@RunWith(MockitoJUnitRunner.class)
public class DefaultBelieverDeviationDeriverNodeImplTest extends AbstractDeriverNodeTest {
    final String DEVIATION_CRITERION = "engine.believeDeviation.criterion";

    DefaultBelieverDeviationDeriverNodeImpl classUnderTest;

    @Before
    public void init() {
        initRealityCheck();
        initCategoryMatch(0.1, 0.3, 0.5, 0.7, 0.9);
    }

    @Test
    public void testIsDeviationTolerantLowCriterion() {
        applicationSettings.setProperty(DEVIATION_CRITERION, 0.2);
        classUnderTest = new DefaultBelieverDeviationDeriverNodeImpl(workingMemory, logger, applicationSettings);
        Assert.assertFalse(classUnderTest.isDeviationTolerant());
    }

    @Test
    public void testIsDeviationTolerantHighCriterion() {
        applicationSettings.setProperty(DEVIATION_CRITERION, 0.8);
        classUnderTest = new DefaultBelieverDeviationDeriverNodeImpl(workingMemory, logger, applicationSettings);
        Assert.assertFalse(classUnderTest.isDeviationTolerant());
    }

    @Test
    public void testGetWillingToDeviateContributorsLowCriterion() {
        applicationSettings.setProperty(DEVIATION_CRITERION, 0.2);
        classUnderTest = new DefaultBelieverDeviationDeriverNodeImpl(workingMemory, logger, applicationSettings);
        Assert.assertEquals(4, size(classUnderTest.getWillingToDeviateContributors()));
    }

    @Test
    public void testGetWillingToDeviateContributorsHighCriterion() {
        applicationSettings.setProperty(DEVIATION_CRITERION, 0.8);
        classUnderTest = new DefaultBelieverDeviationDeriverNodeImpl(workingMemory, logger, applicationSettings);
        Assert.assertEquals(1, size(classUnderTest.getWillingToDeviateContributors()));
    }

    @Test
    public void testGetUnwillingToDeviateContributorsLowCriterion() {
        applicationSettings.setProperty(DEVIATION_CRITERION, 0.2);
        classUnderTest = new DefaultBelieverDeviationDeriverNodeImpl(workingMemory, logger, applicationSettings);
        Assert.assertEquals(1, size(classUnderTest.getUnwillingToDeviateContributors()));
    }

    @Test
    public void testGetUnwillingToDeviateContributorsHighCriterion() {
        applicationSettings.setProperty(DEVIATION_CRITERION, 0.8);
        classUnderTest = new DefaultBelieverDeviationDeriverNodeImpl(workingMemory, logger, applicationSettings);
        Assert.assertEquals(4, size(classUnderTest.getUnwillingToDeviateContributors()));
    }

}
