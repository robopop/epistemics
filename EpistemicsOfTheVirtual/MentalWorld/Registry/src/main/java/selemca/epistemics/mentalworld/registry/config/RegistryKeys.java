/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.registry.config;

public interface RegistryKeys {

    final String DERIVER_CATEGORY_MATCH = "engine.categoryMatchDeriver";
    final String DERIVER_CONFORMATION = "engine.conformationDeriver";
    final String DERIVER_CONTEXTMATCH = "engine.contextMatchDeriver";
    final String DERIVER_BELIEVER_DEVIATION = "engine.believerDeviationDeriver";
    final String DERIVER_INTEGRATOR_DEVIATION = "engine.integratorDeviationDeriver";
    final String DERIVER_INSECURITY = "engine.insecurityDeriver";
    final String DERIVER_PERSISTENCE = "engine.persistenceDeriver";
    final String DERIVER_REASSURANCE = "engine.reassuranceDeriver";
    final String REALITY_CHECK_IMPLEMENTATION = "engine.realityCheck.implementation";
    final String CATEGORY_MATCH_IMPLEMENTATION = "engine.categoryMatch.implementation";
    final String METAPHOR_PROSESSOR_IMPLEMENTATION = "engine.metaphorProcessing.implementation";
    final String DERIVER_APPRAISAL = "engine.epistemicAppraisalDeriver";
    final String DERIVER_CHANGE_CONCEPT = "engine.changeConceptDeriver";
}
