/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.category;

import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.repository.BeliefModelService;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.category.CategoryMatcher;
import selemca.epistemics.mentalworld.engine.factory.DeriverNodeFactory;
import selemca.epistemics.mentalworld.engine.node.CategoryMatchDeriverNode;
import selemca.epistemics.mentalworld.engine.node.DeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;
import selemca.epistemics.mentalworld.registry.CategoryMatcherRegistry;

import java.util.Optional;

/**
 * Created by henrizwols on 26-02-15.
 */
@Component
public class CategoryMatchDeriverNodeFactory implements DeriverNodeFactory<CategoryMatchDeriverNode> {
    private static final String CONFIGURATION_NAME = "categoryMatchDeriver.default";
    @Autowired
    private BeliefModelService beliefModelService;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private CategoryMatcherRegistry categoryMatcherRegistry;

    @Autowired
    private Configuration applicationSettings;

    @Override
    public Class<CategoryMatchDeriverNode> getDeriverNodeClass() {
        return CategoryMatchDeriverNode.class;
    }

    @Override
    public String getName() {
        return CONFIGURATION_NAME;
    }

    @Override
    public CategoryMatchDeriverNode createDeriverNode(WorkingMemory workingMemory, Graph<Concept, Association> beliefSystemGraph, MentalWorldEngine.Logger logger) {
        Optional<CategoryMatcher> categoryMatcherOptional = categoryMatcherRegistry.getImplementation();
        if (categoryMatcherOptional.isPresent()) {
            return new DefaultCategoryMatchDeriverNodeImpl(beliefModelService, beliefSystemGraph, workingMemory, categoryMatcherOptional.get(), logger, applicationSettings);
        } else {
            throw new IllegalStateException("No CategoryMatcher found. Failing");
        }
    }
}
