/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.appraisal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import selemca.epistemics.mentalworld.engine.deriver.common.AbstractDeriverNodeTest;

import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.size;

@RunWith(MockitoJUnitRunner.class)
public class DefaultEpistemicAppraisalDeriverNodeTest extends AbstractDeriverNodeTest {
    final String ACCEPT_AS_REALISTIC_CRITERION = "engine.epistemicAppraisal.criterion";

    DefaultEpistemicAppraisalDeriverNode classUnderTest;

    @Before
    public void init() {
        initRealityCheck();
        initCategoryMatch(0.25, 0.75);
    }

    @Test
    public void testGetCategory() {
        classUnderTest = new DefaultEpistemicAppraisalDeriverNode(workingMemory, logger, realityCheck, applicationSettings);
        Assert.assertSame(categoryMatch.getConcept(), classUnderTest.getCategory());
    }

    @Test
    public void testGetRealisticContributionsLowCriterion() {
        applicationSettings.setProperty(ACCEPT_AS_REALISTIC_CRITERION, 0.2);
        classUnderTest = new DefaultEpistemicAppraisalDeriverNode(workingMemory, logger, realityCheck, applicationSettings);

        Assert.assertEquals(2, size(classUnderTest.getRealisticContributions()));
        Assert.assertEquals(0, size(classUnderTest.getUnrealisticContributions()));
    }

    @Test
    public void testGetRealisticContributionsHighCriterion() {
        applicationSettings.setProperty(ACCEPT_AS_REALISTIC_CRITERION, 0.8);
        classUnderTest = new DefaultEpistemicAppraisalDeriverNode(workingMemory, logger, realityCheck, applicationSettings);

        Assert.assertEquals(1, size(classUnderTest.getRealisticContributions()));
        Assert.assertEquals(1, size(classUnderTest.getUnrealisticContributions()));
    }
}
