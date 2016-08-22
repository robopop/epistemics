/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.category.CategoryMatcher;
import selemca.epistemics.mentalworld.registry.config.RegistryKeys;

import java.util.Map;

/**
 * Created by henrizwols on 05-03-15.
 */
@Component
public class CategoryMatcherRegistry extends AbstractPluginRegistry<CategoryMatcher> {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public CategoryMatcherRegistry(Map<String, CategoryMatcher> implementations) {
        super(RegistryKeys.CATEGORY_MATCH_IMPLEMENTATION, CategoryMatcher.class, implementations);
    }
}
