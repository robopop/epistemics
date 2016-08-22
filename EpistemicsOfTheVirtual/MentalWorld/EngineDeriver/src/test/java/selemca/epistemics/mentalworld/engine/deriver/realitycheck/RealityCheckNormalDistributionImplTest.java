/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.realitycheck;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RealityCheckNormalDistributionImplTest {
    final String DISTRIBUTION_FICTION_MEAN = "engine.fiction.mean";
    final String DISTRIBUTION_FICTION_DEVIATION = "engine.fiction.deviation";
    final String DISTRIBUTION_FICTION_CUTOFF = "engine.fiction.cutoff";
    final String DISTRIBUTION_REALITY_MEAN = "engine.reality.mean";
    final String DISTRIBUTION_REALITY_DEVIATION = "engine.reality.deviation";
    final String DISTRIBUTION_REALITY_CUTOFF = "engine.reality.cutoff";

    @Spy
    Configuration applicationSettings = new BaseConfiguration();

    @InjectMocks
    RealityCheckNormalDistributionImpl classUnderTest;

    @Before
    public void init() {
        applicationSettings.setProperty(DISTRIBUTION_FICTION_MEAN, 0.25);
        applicationSettings.setProperty(DISTRIBUTION_FICTION_DEVIATION, 0.15);
        applicationSettings.setProperty(DISTRIBUTION_FICTION_CUTOFF, 2);
        applicationSettings.setProperty(DISTRIBUTION_REALITY_MEAN, 0.75);
        applicationSettings.setProperty(DISTRIBUTION_REALITY_DEVIATION, 0.15);
        applicationSettings.setProperty(DISTRIBUTION_REALITY_CUTOFF, 3);

    }

    @Test
    public void testIsReality() {
        Assert.assertTrue(classUnderTest.isReality(0.75));
        Assert.assertTrue(classUnderTest.isReality(0.45));
        Assert.assertTrue(classUnderTest.isReality(0.31));
        Assert.assertFalse(classUnderTest.isReality(0.29));
        Assert.assertTrue(classUnderTest.isReality(1.0));
    }

    @Test
    public void testIsFiction() {
        Assert.assertTrue(classUnderTest.isFiction(0.25));
        Assert.assertTrue(classUnderTest.isFiction(0.54));
        Assert.assertFalse(classUnderTest.isFiction(0.56));
        Assert.assertTrue(classUnderTest.isFiction(0.0));
    }
}
