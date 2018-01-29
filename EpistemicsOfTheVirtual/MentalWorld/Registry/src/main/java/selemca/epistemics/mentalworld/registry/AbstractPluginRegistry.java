/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.registry;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import selemca.epistemics.mentalworld.registry.config.RegistryKeys;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public abstract class AbstractPluginRegistry<T> {
    private final RegistryKeys configurationKey;
    private final Class<? extends T> addOnClass;
    private final Map<String, T> implementations;

    @Autowired
    private Configuration applicationSettings;

    public AbstractPluginRegistry(RegistryKeys configurationKey, Class<? extends T> addOnClass, Map<String, T> implementations) {
        this.configurationKey = configurationKey;
        this.addOnClass = addOnClass;
        this.implementations = implementations;
    }

    public Set<String> getAllImplementationsNames() {
        return new HashSet<>(implementations.keySet());
    }

    public Optional<T> getImplementation() {
        String configuredImplementation = applicationSettings.getString(configurationKey.getKey());
        T implementation = null;
        if (configuredImplementation != null) {
            implementation = implementations.get(configuredImplementation);
            if (implementation == null) {
                String message = String.format("No %s implementation found with configured name %s.%n", addOnClass.getSimpleName(), configuredImplementation);
                message += String.format("Configure %s to be one of: %s", configurationKey, implementations.keySet());
                java.util.logging.Logger.getLogger(getClass().getSimpleName()).severe(message);
                return Optional.empty();
            }
        } else {
            if (!implementations.isEmpty()) {
                Map.Entry<String, T> anyImplementationEntry = implementations.entrySet().iterator().next();
                implementation = anyImplementationEntry.getValue();
                String message = String.format("%s implementation not configured. Set server setting %s to specify implementation.\nUsing available implementation: %s", addOnClass.getSimpleName(), configurationKey, anyImplementationEntry.getKey());
                Logger.getLogger(getClass().getSimpleName()).info(message);
            } else {
                String message = String.format("No implementation found for deriver node %s.", addOnClass.getSimpleName());
                Logger.getLogger(getClass().getSimpleName()).severe(message);
            }
        }
        return Optional.ofNullable(implementation);
    }
}
