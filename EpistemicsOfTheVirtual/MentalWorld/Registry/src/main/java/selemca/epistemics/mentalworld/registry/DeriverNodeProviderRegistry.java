/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.registry;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.factory.DeriverNodeFactory;
import selemca.epistemics.mentalworld.engine.node.*;
import selemca.epistemics.mentalworld.registry.config.RegistryKeys;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by henrizwols on 05-03-15.
 */
@Component
public class DeriverNodeProviderRegistry {
    @Autowired
    private Configuration applicationSettings;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private List<DeriverNodeFactory> deriverNodeFactories;


    private final Map<Class<? extends DeriverNode>, String> deriverNodeConfigurationKeys = new HashMap<>();
    {
        deriverNodeConfigurationKeys.put(CategoryMatchDeriverNode.class, RegistryKeys.DERIVER_CATEGORY_MATCH);
        deriverNodeConfigurationKeys.put(ConformationDeriverNode.class, RegistryKeys.DERIVER_CONFORMATION);
        deriverNodeConfigurationKeys.put(ContextMatchDeriverNode.class, RegistryKeys.DERIVER_CONTEXTMATCH);
        deriverNodeConfigurationKeys.put(BelieverDeviationDeriverNode.class, RegistryKeys.DERIVER_BELIEVER_DEVIATION);
        deriverNodeConfigurationKeys.put(IntegratorDeviationDeriverNode.class, RegistryKeys.DERIVER_INTEGRATOR_DEVIATION);
        deriverNodeConfigurationKeys.put(InsecurityDeriverNode.class, RegistryKeys.DERIVER_INSECURITY);
        deriverNodeConfigurationKeys.put(PersistenceDeriverNode.class, RegistryKeys.DERIVER_PERSISTENCE);
        deriverNodeConfigurationKeys.put(ReassuranceDeriverNode.class, RegistryKeys.DERIVER_REASSURANCE);
        deriverNodeConfigurationKeys.put(EpistemicAppraisalDeriverNode.class, RegistryKeys.DERIVER_APPRAISAL);
        deriverNodeConfigurationKeys.put(ChangeConceptDeriverNode.class, RegistryKeys.DERIVER_CHANGE_CONCEPT);
    }

    @PostConstruct
    public void shout() {
        StringBuilder message = new StringBuilder("Registered DeriverNodeProviders: ");
        for (DeriverNodeFactory<?> deriverNodeFactory : deriverNodeFactories) {
            message.append(deriverNodeFactory.getName());
            message.append(", ");
        }
        java.util.logging.Logger.getLogger(getClass().getSimpleName()).info(message.toString());
    }

    public Set<String> getAllImplementationsNames(Class<? extends DeriverNode> deriverNodeClass) {
        Set<String> result = new HashSet<>();
        for (DeriverNodeFactory<?> deriverNodeFactory : getAllImplementations(deriverNodeClass)) {
            result.add(deriverNodeFactory.getName());
        }

        return result;
    }

    private Set<DeriverNodeFactory> getAllImplementations(Class<? extends DeriverNode> deriverNodeClass) {
        Set<DeriverNodeFactory> result = new HashSet<>();
        for (DeriverNodeFactory<?> deriverNodeFactory : deriverNodeFactories) {
            if (deriverNodeClass.equals(deriverNodeFactory.getDeriverNodeClass())) {
                result.add(deriverNodeFactory);
            }
        }

        return result;
    }

    public Optional<DeriverNodeFactory<?>> getDeriverNodeProvider(Class<? extends DeriverNode> deriverNodeClass) {
        String deriverNodeConfigurationKey = deriverNodeConfigurationKeys.get(deriverNodeClass);
        String configuredImplementation = applicationSettings.getString(deriverNodeConfigurationKey);

        DeriverNodeFactory<?> result = null;
        Set<DeriverNodeFactory> implementations = getAllImplementations(deriverNodeClass);
        if (configuredImplementation != null) {
            for (DeriverNodeFactory<?> deriverNodeFactory : implementations) {
                if (deriverNodeFactory.getName().equals(configuredImplementation)) {
                    result = deriverNodeFactory;
                }
            }
            if (result == null) {
                String message = String.format("Configured implementation %s for node %s not found.", configuredImplementation, deriverNodeClass.getSimpleName());
                Logger.getLogger(getClass().getSimpleName()).severe(message);
            }
        } else {
            if (implementations.size() > 0) {
                result = implementations.iterator().next();
                String message = String.format("%s implementation not configured. Set server setting %s to specify implementation.\nUsing available implementation: %s", deriverNodeClass.getSimpleName(), deriverNodeConfigurationKey, result.getName());
                Logger.getLogger(getClass().getSimpleName()).info(message);
            } else {
                String message = String.format("No implementation found for deriver node %s.", deriverNodeClass.getSimpleName());
                Logger.getLogger(getClass().getSimpleName()).severe(message);
            }
        }
        return Optional.ofNullable(result);
    }
}
