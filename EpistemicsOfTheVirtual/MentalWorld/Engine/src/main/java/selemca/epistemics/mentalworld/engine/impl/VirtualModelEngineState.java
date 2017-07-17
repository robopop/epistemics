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
import selemca.epistemics.mentalworld.engine.node.BelieverDeviationDeriverNode;
import selemca.epistemics.mentalworld.engine.node.CategoryMatchDeriverNode;
import selemca.epistemics.mentalworld.engine.node.ChangeConceptDeriverNode;
import selemca.epistemics.mentalworld.engine.node.ConformationDeriverNode;
import selemca.epistemics.mentalworld.engine.node.ContextMatchDeriverNode;
import selemca.epistemics.mentalworld.engine.node.DeriverNode;
import selemca.epistemics.mentalworld.engine.node.EpistemicAppraisalDeriverNode;
import selemca.epistemics.mentalworld.engine.node.InsecurityDeriverNode;
import selemca.epistemics.mentalworld.engine.node.IntegratorDeviationDeriverNode;
import selemca.epistemics.mentalworld.engine.node.PersistenceDeriverNode;
import selemca.epistemics.mentalworld.engine.node.ReassuranceDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;
import selemca.epistemics.mentalworld.registry.DeriverNodeProviderRegistry;
import selemca.epistemics.mentalworld.registry.MetaphorProcessorRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static selemca.epistemics.mentalworld.engine.impl.MentalWorldEngineSettingsProvider.MAXIMUM_TRAVERSALS;

@Component
class VirtualModelEngineState {

    @Autowired
    private BeliefModelService beliefModelService;

    @Autowired
    MetaphorProcessorRegistry metaphorProcessorRegistry;

    @Autowired
    DeriverNodeProviderRegistry deriverNodeProviderRegistry;

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private Configuration applicationSettings;

    private final WorkingMemory workingMemory;
    private final MentalWorldEngine.Logger logger;
    private Map<Class<? extends DeriverNode>, DeriverNode> deriverNodeMap = new HashMap<>();
    private final Graph<Concept, Association> beliefSystemGraph;
    private final Collection<String> triedConcepts = new ArrayList<>();
    private boolean observationAccepted = false;

    public VirtualModelEngineState(Concept context, Set<String> observationFeatures, MentalWorldEngine.Logger logger) {
        this.workingMemory = new WorkingMemory();
        workingMemory.setObservationFeatures(observationFeatures);
        this.logger = logger;
        beliefSystemGraph = getGraph();
    }

    protected void acceptObservation() {
        int categoriesTried = 0;
        int maximumTraversals = applicationSettings.getInt(MAXIMUM_TRAVERSALS, MentalWorldEngineImpl.MAXIMUM_TRAVERSALS_DEFAULT);
        while (!observationAccepted && categoriesTried < maximumTraversals) {
            if (!triedConcepts.isEmpty()) {
                logger.info(String.format("That did not work. Try again. Now exclude %s", triedConcepts));
            }
            categoryMatch();
            categoriesTried++;
        }
    }

    private Graph<Concept, Association> getGraph() {
        Collection<Concept> concepts = conceptRepository.findAll();
        Collection<Association> associations = associationRepository.findAll();
        return new GraphBuilder(concepts, associations).build();
    }

    private void categoryMatch() {
        Optional<DeriverNode> deriverNodeOptional = getDeriverNode(CategoryMatchDeriverNode.class);
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
        Optional<DeriverNode> deriverNodeOptional = getDeriverNode(ConformationDeriverNode.class);
        if (deriverNodeOptional.isPresent()) {
            ((ConformationDeriverNode) deriverNodeOptional.get()).conformation();
        }
        accept();
        reassurance();
    }

    private void reassurance() {
        Optional<DeriverNode> deriverNodeOptional = getDeriverNode(ReassuranceDeriverNode.class);
        if (deriverNodeOptional.isPresent()) {
            ((ReassuranceDeriverNode) deriverNodeOptional.get()).reassurance();
        }
    }

    private void contextMatch() {
        Optional<DeriverNode> deriverNodeOptional = getDeriverNode(ContextMatchDeriverNode.class);
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
        Optional<DeriverNode> deriverNodeOptional = getDeriverNode(BelieverDeviationDeriverNode.class);
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
        Optional<DeriverNode> deriverNodeOptional = getDeriverNode(EpistemicAppraisalDeriverNode.class);
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
        Optional<DeriverNode> deriverNodeOptional = getDeriverNode(IntegratorDeviationDeriverNode.class);
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
        Optional<DeriverNode> deriverNodeOptional = getDeriverNode(PersistenceDeriverNode.class);
        if (deriverNodeOptional.isPresent()) {
            ((PersistenceDeriverNode) deriverNodeOptional.get()).persistence();
        }
        reject();
        insecurity();
    }

    private void insecurity() {
        Optional<DeriverNode> deriverNodeOptional = getDeriverNode(InsecurityDeriverNode.class);
        if (deriverNodeOptional.isPresent()) {
            ((InsecurityDeriverNode) deriverNodeOptional.get()).insecurity();
        }
    }

    private void insecurity(Association association) {
        Optional<DeriverNode> deriverNodeOptional = getDeriverNode(InsecurityDeriverNode.class);
        if (deriverNodeOptional.isPresent()) {
            ((InsecurityDeriverNode) deriverNodeOptional.get()).insecurity(association);
        }
    }

    private void changeConcept(Association association, boolean isMetaphor) {
        logger.info("Change concept " + association);
        Optional<DeriverNode> deriverNodeOptional = getDeriverNode(ChangeConceptDeriverNode.class);
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

    private Optional<DeriverNode> getDeriverNode(Class<? extends DeriverNode> deliverNodeClass) {
        DeriverNode result = deriverNodeMap.get(deliverNodeClass);

        if (result == null) {
            Optional<DeriverNodeFactory<?>> deriverNodeProviderOptional = deriverNodeProviderRegistry.getDeriverNodeProvider(deliverNodeClass);
            if (deriverNodeProviderOptional.isPresent()) {
                MentalWorldEngine.Logger nodeLogger = new PrefixLogger(logger, deliverNodeClass.getSimpleName() + ": ");
                result = deriverNodeProviderOptional.get().createDeriverNode(workingMemory, beliefSystemGraph, nodeLogger);
                if (result != null) {
                    deriverNodeMap.put(deliverNodeClass, result);
                }
            }
        }
        return Optional.ofNullable(result);
    }

}
