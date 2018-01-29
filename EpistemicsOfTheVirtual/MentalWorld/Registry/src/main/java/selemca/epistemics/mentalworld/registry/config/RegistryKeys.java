/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.registry.config;

public enum RegistryKeys {
    DERIVER_CATEGORY_MATCH("engine.categoryMatchDeriver"),
    DERIVER_CONFORMATION("engine.conformationDeriver"),
    DERIVER_CONTEXTMATCH("engine.contextMatchDeriver"),
    DERIVER_BELIEVER_DEVIATION("engine.believerDeviationDeriver"),
    DERIVER_INTEGRATOR_DEVIATION("engine.integratorDeviationDeriver"),
    DERIVER_INSECURITY("engine.insecurityDeriver"),
    DERIVER_PERSISTENCE("engine.persistenceDeriver"),
    DERIVER_REASSURANCE("engine.reassuranceDeriver"),
    REALITY_CHECK_IMPLEMENTATION("engine.realityCheck.implementation"),
    CATEGORY_MATCH_IMPLEMENTATION("engine.categoryMatch.implementation"),
    METAPHOR_PROSESSOR_IMPLEMENTATION("engine.metaphorProcessing.implementation"),
    DERIVER_APPRAISAL("engine.epistemicAppraisalDeriver"),
    DERIVER_CHANGE_CONCEPT("engine.changeConceptDeriver"),
    ;

    private final String key;

    private RegistryKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
