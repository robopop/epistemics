/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.integratordeviation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.deriver.common.AbstractDeriverNodeTest;

@RunWith(MockitoJUnitRunner.class)
public class DefaultIntegratorDeviationDeriverNodeImplTest extends AbstractDeriverNodeTest {
    final String WILLING_TO_DEVIATE = "engine.integratorDeviation.criterion";

    DefaultIntegratorDeviationDeriverNodeImpl classUnderTest;

    @Before
    public void init() {
        initRealityCheck();
        initCategoryMatch(0.4);
    }

    @Test
    public void testIsWillingToDeviateLowCriterion() {
        applicationSettings.setProperty(WILLING_TO_DEVIATE, 0.25);

        classUnderTest = new DefaultIntegratorDeviationDeriverNodeImpl(workingMemory, logger, applicationSettings);
        CategoryMatch categoryMatch = getCategoryMatch();
        Concept contributor = categoryMatch.getContributors().iterator().next();
        Assert.assertTrue(classUnderTest.isWillingToDeviate(categoryMatch.getConcept(), contributor));
    }

    @Test
    public void testIsWillingToDeviateHighCriterion() {
        applicationSettings.setProperty(WILLING_TO_DEVIATE, 0.5);

        classUnderTest = new DefaultIntegratorDeviationDeriverNodeImpl(workingMemory, logger, applicationSettings);
        CategoryMatch categoryMatch = getCategoryMatch();
        Concept contributor = categoryMatch.getContributors().iterator().next();
        Assert.assertFalse(classUnderTest.isWillingToDeviate(categoryMatch.getConcept(), contributor));
    }
}
