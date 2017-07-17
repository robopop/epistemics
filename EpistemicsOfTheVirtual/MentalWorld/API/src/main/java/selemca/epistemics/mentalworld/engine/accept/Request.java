package selemca.epistemics.mentalworld.engine.accept;

import java.util.List;

public class Request {
    private List<String> featureList;
    private Engine engineSettings;

    public List<String> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<String> featureList) {
        this.featureList = featureList;
    }

    public Engine getEngineSettings() {
        return engineSettings;
    }

    public void setEngineSettings(Engine engineSettings) {
        this.engineSettings = engineSettings;
    }
}
