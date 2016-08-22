/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.impl;

import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.graph.GraphBuilder;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.BeliefModelService;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.factory.DeriverNodeFactory;
import selemca.epistemics.mentalworld.engine.metaphor.MetaphorProcessor;
import selemca.epistemics.mentalworld.engine.node.*;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;
import selemca.epistemics.mentalworld.registry.DeriverNodeProviderRegistry;
import selemca.epistemics.mentalworld.registry.MetaphorProcessorRegistry;

import java.util.*;
import static selemca.epistemics.mentalworld.engine.impl.MentalWorldEngineSettingsProvider.MAXIMUM_TRAVERSALS;

@Component("mentalWorldEngine")
public class MentalWorldEngineImpl implements MentalWorldEngine {
    public static final String SUBJECT_NAME = "subject";
    public static final int MAXIMUM_TRAVERSALS_DEFAULT = 1;


    @Autowired
    DeriverNodeProviderRegistry deriverNodeProviderRegistry;

    @Autowired
    MetaphorProcessorRegistry metaphorProcessorRegistry;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private BeliefModelService beliefModelService;

    @Autowired
    private Configuration applicationSettings;

    @Override
    public void acceptObservation(Set<String> observationFeatures, Logger logger) {
        Optional<Concept> contextOptional = beliefModelService.getContext();
        if (contextOptional.isPresent()) {
            VirtualModelEngineState virtualModelEngineState = new VirtualModelEngineState(contextOptional.get(), observationFeatures, logger);

            int categoriesTried = 0;
            int maximumTraversals = applicationSettings.getInt(MAXIMUM_TRAVERSALS, MAXIMUM_TRAVERSALS_DEFAULT);
            while (!virtualModelEngineState.observationAccepted && categoriesTried < maximumTraversals) {
                if (!virtualModelEngineState.triedConcepts.isEmpty()) {
                    logger.info(String.format("That did not work. Try again. Now exclude %s", virtualModelEngineState.triedConcepts));
                }
                virtualModelEngineState.categoryMatch();
                categoriesTried++;
            }

        } else {
            logger.info("There is no context. We are mentally blind");
        }
    }


    private class VirtualModelEngineState {
        private final WorkingMemory workingMemory;
        private final Logger logger;
        private Map<Class<? extends DeriverNode>, DeriverNode> deriverNodeMap = new HashMap<>();
        private final Graph<Concept, Association> beliefSystemGraph;
        private final Collection<String> triedConcepts = new ArrayList<>();
        private boolean observationAccepted = false;

        public VirtualModelEngineState(Concept context, Set<String> observationFeatures, Logger logger) {
            this.workingMemory = new WorkingMemory();
            workingMemory.setObservationFeatures(observationFeatures);
            this.logger = logger;
            beliefSystemGraph = getGraph();
        }

        private Graph<Concept, Association> getGraph() {
            Collection<Concept> concepts = conceptRepository.findAll();
            Collection<Association> associations = associationRepository.findAll();
            return new GraphBuilder(concepts, associations).build();
        }

        private void categoryMatch() {
            Optional<DeriverNode> deriverNodeOptional = getDeliverNode(CategoryMatchDeriverNode.class);
            if (deriverNodeOptional.isPresent()) {
                CategoryMatchDeriverNode categoryMatchDeriverNode = (CategoryMatchDeriverNode) deriverNodeOptional.get();
                if (categoryMatchDeriverNode.categoryMatch(triedConcepts)) {
                    conformation();
                } else {
                    CategoryMatch categoryMatch = workingMemory.getCategoryMatch();
                    if (categoryMatch != null) {
                        logger.debug("Imperfect match: " + categoryMatch);
                        contextMatch();
                    } else {
                        logger.info("No match");
                    }
                }
                CategoryMatch categoryMatch = workingMemory.getCategoryMatch();
                if (categoryMatch != null && categoryMatch.getConcept() != null) {
                    triedConcepts.add(categoryMatch.getConcept().getName());
                }
            }
       }

        private void conformation() {
            Optional<DeriverNode> deriverNodeOptional = getDeliverNode(ConformationDeriverNode.class);
            if (deriverNodeOptional.isPresent()) {
                ((ConformationDeriverNode) deriverNodeOptional.get()).conformation();
            }
            accept();
            reassurance();
        }

        private void reassurance() {
            Optional<DeriverNode> deriverNodeOptional = getDeliverNode(ReassuranceDeriverNode.class);
            if (deriverNodeOptional.isPresent()) {
                ((ReassuranceDeriverNode) deriverNodeOptional.get()).reassurance();
            }
        }

        private void contextMatch() {
            Optional<DeriverNode> deriverNodeOptional = getDeliverNode(ContextMatchDeriverNode.class);
            if (deriverNodeOptional.isPresent()) {
                ContextMatchDeriverNode contextMatchDeriverNode = (ContextMatchDeriverNode) deriverNodeOptional.get();
                if (contextMatchDeriverNode.contextMatch()) {
                    believeDeviationTolerance();
                } else {
                    if (workingMemory.getNewContext() != null) {
                        declareContext();
                    }
                }
            }
        }

        private void declareContext() {
            Concept context = workingMemory.getNewContext();
            logger.info("New context: " + context);
            beliefModelService.setContext(context.getName());
            categoryMatch();
        }

        private void believeDeviationTolerance() {
            Optional<DeriverNode> deriverNodeOptional = getDeliverNode(BelieverDeviationDeriverNode.class);
            if (deriverNodeOptional.isPresent()) {
                BelieverDeviationDeriverNode believerDeviationDeriverNode = (BelieverDeviationDeriverNode) deriverNodeOptional.get();
                if (believerDeviationDeriverNode.isDeviationTolerant()) {
                    epistemicAppraisal(workingMemory.getCategoryMatch().getConcept());
                } else {
                    persistence();
                }
            }
        }

        private void epistemicAppraisal(Concept concept) {
            logger.info("Deviation tolerant. Lets examine concept " + workingMemory.getCategoryMatch().getConcept().getName());
            Optional<DeriverNode> deriverNodeOptional = getDeliverNode(EpistemicAppraisalDeriverNode.class);
            if (deriverNodeOptional.isPresent()) {
                EpistemicAppraisalDeriverNode epistemicAppraisalDeriverNode = (EpistemicAppraisalDeriverNode) deriverNodeOptional.get();
                Collection<Association> realisticContributions = epistemicAppraisalDeriverNode.getRealisticContributions();
                falsification(concept, realisticContributions);
                Collection<Association> unrealisticContributions = epistemicAppraisalDeriverNode.getUnrealisticContributions();
                integratorDeviationTolerance(concept, unrealisticContributions);
            }
        }

        private void falsification(Concept concept, Collection<Association> realisticContributions) {
            logger.info("Falsification");
            realisticContributions.forEach(this::insecurity);
            for (Association association : realisticContributions) {
                changeConcept(association, false);
            }
        }

        private void integratorDeviationTolerance(Concept concept, Collection<Association> unrealisticContributions) {
            Optional<DeriverNode> deriverNodeOptional = getDeliverNode(IntegratorDeviationDeriverNode.class);
            if (deriverNodeOptional.isPresent()) {
                IntegratorDeviationDeriverNode integratorDeviationDeriverNode = (IntegratorDeviationDeriverNode) deriverNodeOptional.get();
                for (Association contribution : unrealisticContributions) {
                    if (integratorDeviationDeriverNode.isWillingToDeviate(concept, contribution.getOtherConcept(concept))) {
                        metaphorProcessing(contribution);
                    } else {
                        reject(contribution);
                    }
                }
            }
        }

        private void metaphorProcessing(Association contribution) {
            Optional<MetaphorProcessor> metaphorProcessorOptional = metaphorProcessorRegistry.getImplementation();
            if (metaphorProcessorOptional.isPresent()) {
                MetaphorProcessor.MetaphorAssesment metaphorAssesment = metaphorProcessorOptional.get().assesRelation(contribution.getConcept1(), contribution.getConcept2());
                switch (metaphorAssesment) {
                    case ANOMALY:
                        insecurity(contribution);
                        break;
                    case LITERAL:
                        changeConcept(contribution, false);
                        break;
                    case METAPHOR:
                        changeConcept(contribution, true);
                        break;
                }
            }

        }

        private void persistence() {
            Optional<DeriverNode> deriverNodeOptional = getDeliverNode(PersistenceDeriverNode.class);
            if (deriverNodeOptional.isPresent()) {
                ((PersistenceDeriverNode) deriverNodeOptional.get()).persistence();
            }
            reject();
            insecurity();
        }

        private void insecurity() {
            Optional<DeriverNode> deriverNodeOptional = getDeliverNode(InsecurityDeriverNode.class);
            if (deriverNodeOptional.isPresent()) {
                ((InsecurityDeriverNode) deriverNodeOptional.get()).insecurity();
            }
        }

        private void insecurity(Association association) {
            Optional<DeriverNode> deriverNodeOptional = getDeliverNode(InsecurityDeriverNode.class);
            if (deriverNodeOptional.isPresent()) {
                ((InsecurityDeriverNode) deriverNodeOptional.get()).insecurity(association);
            }
        }

        private void changeConcept(Association association, boolean isMetaphor) {
            logger.info("Change concept " + association);
            Optional<DeriverNode> deriverNodeOptional = getDeliverNode(ChangeConceptDeriverNode.class);
            if (deriverNodeOptional.isPresent()) {
                ((ChangeConceptDeriverNode) deriverNodeOptional.get()).changeConcept(association, isMetaphor);
            }
            observationAccepted = true;
        }

        private void accept() {
            Concept concept = workingMemory.getCategoryMatch().getConcept();
            logger.info("Matching concept found: " + concept.getName());
            logger.info("Observation accepted");
            observationAccepted = true;
        }

        private void reject() {
            logger.info("Observation rejected");
        }

        private void reject(Association association) {
            logger.info(String.format("Rejecting a relation between %s and %s", association.getConcept1(), association.getConcept2()));
        }

        private Optional<DeriverNode> getDeliverNode(Class<? extends DeriverNode> deliverNodeClass) {
            DeriverNode result = deriverNodeMap.get(deliverNodeClass);

            if (result == null) {
                Optional<DeriverNodeFactory<?>> deriverNodeProviderOptional = deriverNodeProviderRegistry.getDeriverNodeProvider(deliverNodeClass);
                if (deriverNodeProviderOptional.isPresent()) {
                    Logger nodeLogger = new PrefixLogger(logger, deliverNodeClass.getSimpleName() + ": ");
                    result = deriverNodeProviderOptional.get().createDeriverNode(workingMemory, beliefSystemGraph, nodeLogger);
                    if (result != null) {
                        deriverNodeMap.put(deliverNodeClass, result);
                    }
                }
            }
            return Optional.ofNullable(result);
        }

    }

    private static class PrefixLogger implements Logger {
        private final Logger delegate;
        private final String prefix;

        public PrefixLogger(Logger delegate, String prefix) {
            this.delegate = delegate;
            this.prefix = prefix;
        }

        @Override
        public void debug(String message) {
            delegate.debug(prefixMessage(message));
        }

        @Override
        public void info(String message) {
            delegate.info(prefixMessage(message));
        }

        @Override
        public void warning(String message) {
            delegate.warning(prefixMessage(message));
        }
        private String prefixMessage(String message) {
            return prefix + message;
        }
    }
}
