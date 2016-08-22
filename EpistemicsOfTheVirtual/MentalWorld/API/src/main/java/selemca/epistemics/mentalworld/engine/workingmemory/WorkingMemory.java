/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.workingmemory;

import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;

import java.util.Set;

/**
 * Created by henrizwols on 26-02-15.
 */
public class WorkingMemory {
    private Set<String> observationFeatures;
    private CategoryMatch categoryMatch = null;
    private Concept newContext;

    public Set<String> getObservationFeatures() {
        return observationFeatures;
    }

    public void setObservationFeatures(Set<String> observationFeatures) {
        this.observationFeatures = observationFeatures;
    }

    public CategoryMatch getCategoryMatch() {
        return categoryMatch;
    }

    public void setCategoryMatch(CategoryMatch categoryMatch) {
        this.categoryMatch = categoryMatch;
    }

    public Concept getNewContext() {
        return newContext;
    }

    public void setNewContext(Concept newContext) {
        this.newContext = newContext;
    }
}
