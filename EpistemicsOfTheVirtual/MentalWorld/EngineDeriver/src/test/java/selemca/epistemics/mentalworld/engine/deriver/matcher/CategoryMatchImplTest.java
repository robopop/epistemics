/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.matcher;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.engine.realitycheck.RealityCheck;

import java.util.Set;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CategoryMatchImplTest {
    @Mock
    RealityCheck realityCheck;

    CategoryMatchImpl classUnderTest;

    @Before
    public void init() {
        classUnderTest = new CategoryMatchImpl(createConcept("duck"), realityCheck);
        classUnderTest.addContribution(createConcept("swimming"), 0.7);
        classUnderTest.addContribution(createConcept("beak"), 0.6);
        classUnderTest.addContribution(createConcept("eggs"), 0.75);
        classUnderTest.addContribution(createConcept("fur"), 0.15);
    }

    @Test
    public void testGetConcept() {
        Assert.assertEquals(createConcept("duck"), classUnderTest.getConcept());
    }

    @Test
    public void testGetContributors() {
        Set<Concept> contributors = classUnderTest.getContributors();
        Assert.assertTrue(contributors.contains(createConcept("swimming")));
        Assert.assertTrue(contributors.contains(createConcept("beak")));
        Assert.assertTrue(contributors.contains(createConcept("eggs")));
        Assert.assertTrue(contributors.contains(createConcept("fur")));
    }

    @Test
    public void testGetMatchScore() {
        Assert.assertEquals(0.9745, classUnderTest.getMatchScore(), 0.001);
    }

    @Test
    public void testGetContributorScore() {
        Assert.assertEquals(0.7, classUnderTest.getContributorScore(createConcept("swimming")), 0.001);
        Assert.assertEquals(0.6, classUnderTest.getContributorScore(createConcept("beak")), 0.001);
        Assert.assertEquals(0.75, classUnderTest.getContributorScore(createConcept("eggs")), 0.001);
        Assert.assertEquals(0.15, classUnderTest.getContributorScore(createConcept("fur")), 0.001);
    }

    @Test
    public void testWithinReality() {
        classUnderTest.withinReality(createConcept("swimming"));
        verify(realityCheck).isReality(0.7);
    }

    @Test
    public void testWithinFiction() {
        classUnderTest.withinFiction(createConcept("swimming"));
        verify(realityCheck).isFiction(0.7);
    }

    private Concept createConcept(String name) {
        return new Concept(name, 0.8);
    }
}
