package selemca.epistemics.mentalworld.engine.setting.engine;

public class Metaphor {
    private MinimumSize intersectionMinimumSize;
    private MinimumSize intersectionMinimumSizeMixed;
    private Double vicinity;

    public MinimumSize getIntersectionMinimumSize() {
        return intersectionMinimumSize;
    }

    public void setIntersectionMinimumSize(MinimumSize intersectionMinimumSize) {
        this.intersectionMinimumSize = intersectionMinimumSize;
    }

    public MinimumSize getIntersectionMinimumSizeMixed() {
        return intersectionMinimumSizeMixed;
    }

    public void setIntersectionMinimumSizeMixed(MinimumSize intersectionMinimumSizeMixed) {
        this.intersectionMinimumSizeMixed = intersectionMinimumSizeMixed;
    }

    public Double getVicinity() {
        return vicinity;
    }

    public void setVicinity(Double vicinity) {
        this.vicinity = vicinity;
    }
}
