/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.realitycheck;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.realitycheck.RealityCheck;

import static selemca.epistemics.mentalworld.engine.deriver.realitycheck.RealityCheckSettingsProvider.*;

/**
 * Created by henrizwols on 05-03-15.
 */
@Component("realityCheckNormalDistribution")
public class RealityCheckNormalDistributionImpl implements RealityCheck {
    private static final double DISTRIBUTION_FICTION_MEAN_DEFAULT = 0.25;
    private static final double DISTRIBUTION_FICTION_DEVIATION_DEFAULT = 0.15;
    private static final int DISTRIBUTION_FICTION_CUTOFF_DEFAULT = 2;
    private static final double DISTRIBUTION_REALITY_MEAN_DEFAULT = 0.25;
    private static final double DISTRIBUTION_REALITY_DEVIATION_DEFAULT = 0.15;
    private static final int DISTRIBUTION_REALITY_CUTOFF_DEFAULT = 2;

    @Autowired
    private Configuration applicationSettings;

    @Override
    public boolean isReality(double truthValue) {
        double realityMean = applicationSettings.getDouble(DISTRIBUTION_REALITY_MEAN, DISTRIBUTION_REALITY_MEAN_DEFAULT);
        double realityDeviation = applicationSettings.getDouble(DISTRIBUTION_REALITY_DEVIATION, DISTRIBUTION_REALITY_DEVIATION_DEFAULT);
        int realityCutoff = applicationSettings.getInt(DISTRIBUTION_REALITY_CUTOFF, DISTRIBUTION_REALITY_CUTOFF_DEFAULT);
        // Ignore high half of distribution: true if truthValue > mean - 2*believedeviation
        return truthValue > realityMean - (realityCutoff*realityDeviation);
    }

    @Override
    public boolean isFiction(double truthValue) {
        double fictionMean = applicationSettings.getDouble(DISTRIBUTION_FICTION_MEAN, DISTRIBUTION_FICTION_MEAN_DEFAULT);
        double fictionDeviation = applicationSettings.getDouble(DISTRIBUTION_FICTION_DEVIATION, DISTRIBUTION_FICTION_DEVIATION_DEFAULT);
        int fictionCutoff = applicationSettings.getInt(DISTRIBUTION_FICTION_CUTOFF, DISTRIBUTION_FICTION_CUTOFF_DEFAULT);
        // Ignore lower half of distribution: true if truthValue < mean + 2*believedeviation
        return truthValue < fictionMean + (fictionCutoff*fictionDeviation);
    }
}
