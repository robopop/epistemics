/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.registry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.metaphor.MetaphorProcessor;
import selemca.epistemics.mentalworld.registry.config.RegistryKeys;

import java.util.Map;

@Component
public class MetaphorProcessorRegistry extends AbstractPluginRegistry<MetaphorProcessor> {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public MetaphorProcessorRegistry(Map<String, MetaphorProcessor> implementations) {
        super(RegistryKeys.METAPHOR_PROSESSOR_IMPLEMENTATION, MetaphorProcessor.class, implementations);
    }
}
