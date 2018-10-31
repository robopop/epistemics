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
import selemca.epistemics.mentalworld.registry.config.RegistryKey;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.logging.Logger;

@Component
public class DeriverNodeProviderRegistry {
    @Autowired
    private Configuration applicationSettings;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private List<DeriverNodeFactory> deriverNodeFactories;


    private final Map<Class<? extends DeriverNode>, RegistryKey> deriverNodeConfigurationKeys = new HashMap<>();
    {
        deriverNodeConfigurationKeys.put(CategoryMatchDeriverNode.class, RegistryKey.DERIVER_CATEGORY_MATCH);
        deriverNodeConfigurationKeys.put(ConformationDeriverNode.class, RegistryKey.DERIVER_CONFORMATION);
        deriverNodeConfigurationKeys.put(ContextMatchDeriverNode.class, RegistryKey.DERIVER_CONTEXTMATCH);
        deriverNodeConfigurationKeys.put(BelieverDeviationDeriverNode.class, RegistryKey.DERIVER_BELIEVER_DEVIATION);
        deriverNodeConfigurationKeys.put(IntegratorDeviationDeriverNode.class, RegistryKey.DERIVER_INTEGRATOR_DEVIATION);
        deriverNodeConfigurationKeys.put(InsecurityDeriverNode.class, RegistryKey.DERIVER_INSECURITY);
        deriverNodeConfigurationKeys.put(PersistenceDeriverNode.class, RegistryKey.DERIVER_PERSISTENCE);
        deriverNodeConfigurationKeys.put(ReassuranceDeriverNode.class, RegistryKey.DERIVER_REASSURANCE);
        deriverNodeConfigurationKeys.put(EpistemicAppraisalDeriverNode.class, RegistryKey.DERIVER_APPRAISAL);
        deriverNodeConfigurationKeys.put(ChangeConceptDeriverNode.class, RegistryKey.DERIVER_CHANGE_CONCEPT);
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

    @SuppressWarnings("unchecked")
    private <D extends DeriverNode> Set<DeriverNodeFactory<D>> getAllImplementations(Class<D> deriverNodeClass) {
        Set<DeriverNodeFactory<D>> result = new HashSet<>();
        for (DeriverNodeFactory<?> deriverNodeFactory : deriverNodeFactories) {
            if (deriverNodeClass.equals(deriverNodeFactory.getDeriverNodeClass())) {
                result.add((DeriverNodeFactory<D>) deriverNodeFactory);
            }
        }

        return result;
    }

    public <D extends DeriverNode> Optional<DeriverNodeFactory<D>> getDeriverNodeProvider(Class<D> deriverNodeClass) {
        RegistryKey deriverNodeConfigurationKey = deriverNodeConfigurationKeys.get(deriverNodeClass);
        String configuredImplementation = deriverNodeConfigurationKey == null ? null : applicationSettings.getString(deriverNodeConfigurationKey.getKey());

        DeriverNodeFactory<D> result = null;
        Set<DeriverNodeFactory<D>> implementations = getAllImplementations(deriverNodeClass);
        if (configuredImplementation != null) {
            for (DeriverNodeFactory<D> deriverNodeFactory : implementations) {
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
