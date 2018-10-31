package selemca.epistemics.mentalworld.engine.impl;

import org.apache.commons.configuration.Configuration;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.graph.ConceptGraph;
import selemca.epistemics.mentalworld.beliefsystem.graph.GraphBuilder;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.MentalWorldEngineState;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
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
import selemca.epistemics.mentalworld.engine.accept.Engine;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import static selemca.epistemics.mentalworld.engine.config.EngineConfig.BELIEF_SYSTEM_GRAPH;
import static selemca.epistemics.mentalworld.engine.impl.MentalWorldEngineSettingsProvider.MAXIMUM_TRAVERSALS;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.CATEGORY_MATCH;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.ENGINE_SETTINGS;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.NEW_CONTEXT;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.OBSERVATION_FEATURES;

class VirtualModelEngineState implements MentalWorldEngineState {
    private final MentalWorldEngineImpl engine;
    private final WorkingMemory workingMemory;
    private final MentalWorldEngine.Logger logger;
    private Map<Class<? extends DeriverNode>, DeriverNode> deriverNodeMap = new HashMap<>();
    private final Collection<String> triedConcepts = new ArrayList<>();
    private boolean observationAccepted = false;

    public VirtualModelEngineState(MentalWorldEngineImpl engine, Concept context, Set<String> observationFeatures, MentalWorldEngine.Logger logger) {
        this(engine, createWorkingMemory(context, observationFeatures, null), logger);
        createEngineSettings(engine.getApplicationSettings(), "engine", Engine.class)
                .map((engineSettings) -> {
                    workingMemory.set(ENGINE_SETTINGS, engineSettings);
                    return null;
                });
    }

    @SuppressWarnings("unused")
    public VirtualModelEngineState(MentalWorldEngineImpl engine, Concept context, Set<String> observationFeatures, Engine engineSettings, MentalWorldEngine.Logger logger) {
        this(engine, createWorkingMemory(context, observationFeatures, engineSettings), logger);
    }

    public VirtualModelEngineState(MentalWorldEngineImpl engine, WorkingMemory workingMemory, MentalWorldEngine.Logger logger) {
        this.engine = engine;
        this.workingMemory = workingMemory;
        this.logger = logger;
        workingMemory.set(BELIEF_SYSTEM_GRAPH, getGraph());
    }

    public MentalWorldEngine.Logger getLogger() {
        return logger;
    }

    private static WorkingMemory createWorkingMemory(Concept context, Set<String> observationFeatures, Engine engineSettings) {
        WorkingMemory workingMemory = new WorkingMemory();
        workingMemory.set(OBSERVATION_FEATURES, observationFeatures);
        workingMemory.set(ENGINE_SETTINGS, engineSettings);
        workingMemory.set(NEW_CONTEXT, context);
        return workingMemory;
    }

    public boolean isObservationAccepted() {
        return observationAccepted;
    }

    @Override
    public WorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    public void acceptObservation() {
        int categoriesTried = 0;
        int maximumTraversals = engine.getApplicationSettings().getInt(MAXIMUM_TRAVERSALS, MentalWorldEngineImpl.MAXIMUM_TRAVERSALS_DEFAULT);
        while (!observationAccepted && categoriesTried < maximumTraversals) {
            if (!triedConcepts.isEmpty()) {
                logger.info(String.format("That did not work. Try again. Now exclude %s", triedConcepts));
            }
            categoryMatch();
            categoriesTried++;
        }
    }

    private ConceptGraph getGraph() {
        Collection<Concept> concepts = engine.getConceptRepository().findAll();
        Collection<Association> associations = engine.getAssociationRepository().findAll();
        return new GraphBuilder(concepts, associations).build();
    }

    private void categoryMatch() {
        getDeriverNode(CategoryMatchDeriverNode.class).ifPresent(node -> {
            boolean match = node.categoryMatch(triedConcepts);
            CategoryMatch categoryMatch = workingMemory.get(CATEGORY_MATCH);
            if (match) {
                conformation();
            } else {
                if (categoryMatch != null) {
                    logger.debug("Imperfect match: " + categoryMatch);
                    contextMatch();
                } else {
                    logger.info("No match");
                }
            }
            if (categoryMatch != null && categoryMatch.getConcept() != null) {
                triedConcepts.add(categoryMatch.getConcept().getName());
            }
        });
   }

    private void conformation() {
        getDeriverNode(ConformationDeriverNode.class).ifPresent(ConformationDeriverNode::conformation);
        accept();
        reassurance();
    }

    private void reassurance() {
        getDeriverNode(ReassuranceDeriverNode.class).ifPresent(ReassuranceDeriverNode::reassurance);
    }

    private void contextMatch() {
        getDeriverNode(ContextMatchDeriverNode.class).ifPresent(node -> {
            if (node.contextMatch()) {
                believeDeviationTolerance();
            } else {
                if (workingMemory.get(NEW_CONTEXT) != null) {
                    declareContext();
                }
            }
        });
    }

    private void declareContext() {
        Concept context = workingMemory.get(NEW_CONTEXT);
        logger.info("New context: " + context);
        engine.getBeliefModelService().setContext(context.getName());
        categoryMatch();
    }

    private void believeDeviationTolerance() {
        getDeriverNode(BelieverDeviationDeriverNode.class).ifPresent(node -> {
            node.apply();
            if (node.isDeviationTolerant()) {
                CategoryMatch categoryMatch = workingMemory.get(CATEGORY_MATCH);
                epistemicAppraisal(categoryMatch.getConcept());
            } else {
                persistence();
            }
        });
    }

    private void epistemicAppraisal(Concept concept) {
        CategoryMatch categoryMatch = workingMemory.get(CATEGORY_MATCH);
        logger.info("Deviation tolerant. Lets examine concept " + categoryMatch.getConcept().getName());
        getDeriverNode(EpistemicAppraisalDeriverNode.class).ifPresent(node -> {
            node.apply();
            Iterable<Association> realisticContributions = node.getRealisticContributions();
            falsification(concept, realisticContributions);
            Iterable<Association> unrealisticContributions = node.getUnrealisticContributions();
            integratorDeviationTolerance(concept, unrealisticContributions);
        });
    }

    private void falsification(Concept concept, Iterable<Association> realisticContributions) {
        logger.info("Falsification: %s", concept);
        realisticContributions.forEach(this::insecurity);
        for (Association association : realisticContributions) {
            changeConcept(association, false);
        }
    }

    private void integratorDeviationTolerance(Concept concept, Iterable<Association> unrealisticContributions) {
        getDeriverNode(IntegratorDeviationDeriverNode.class).ifPresent(node -> {
            for (Association contribution : unrealisticContributions) {
                if (node.isWillingToDeviate(concept, contribution.getOtherConcept(concept))) {
                    metaphorProcessing(contribution);
                } else {
                    reject(contribution);
                }
            }
        });
    }

    private void metaphorProcessing(Association contribution) {
        engine.getMetaphorProcessorRegistry().getImplementation().ifPresent(metaphorProcessor -> {
            MetaphorProcessor.MetaphorAssesment metaphorAssesment = metaphorProcessor.assesRelation(contribution.getConcept1(), contribution.getConcept2());
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
        });
    }

    private void persistence() {
        getDeriverNode(PersistenceDeriverNode.class).ifPresent(PersistenceDeriverNode::persistence);
        reject();
        insecurity();
    }

    private void insecurity() {
        getDeriverNode(InsecurityDeriverNode.class).ifPresent(InsecurityDeriverNode::insecurity);
    }

    private void insecurity(Association association) {
        getDeriverNode(InsecurityDeriverNode.class).ifPresent(node -> node.insecurity(association));
    }

    private void changeConcept(Association association, boolean isMetaphor) {
        logger.info("Change concept " + association);
        getDeriverNode(ChangeConceptDeriverNode.class).ifPresent(node -> node.changeConcept(association, isMetaphor));
        observationAccepted = true;
    }

    private void accept() {
        CategoryMatch categoryMatch = workingMemory.get(CATEGORY_MATCH);
        Concept concept = categoryMatch.getConcept();
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

    public <D extends DeriverNode> Optional<D> getDeriverNode(Class<D> deliverNodeClass) {
        Optional<D> result = Optional.ofNullable(deriverNodeMap.get(deliverNodeClass)).map(deliverNodeClass::cast);

        if (!result.isPresent()) {
            result = engine.getDeriverNodeProviderRegistry().getDeriverNodeProvider(deliverNodeClass)
                .map(provider -> {
                    MentalWorldEngine.Logger nodeLogger = new PrefixLogger(logger, deliverNodeClass.getSimpleName() + ": ");
                    return provider.createDeriverNode(workingMemory, nodeLogger);
                })
                .map(node -> {
                    deriverNodeMap.put(deliverNodeClass, node);
                    return node;
                });
        }
        return result;
    }

    private <T> Optional<T> createEngineSettings(Configuration applicationSettings, String path, Class<T> type) {
        if (type == null || type == Void.class) {
            return Optional.empty();
        } else if (type == Double.class) {
            return getValue(applicationSettings, path, Configuration::getDouble, type);
        } else if (type == Integer.class) {
            return getValue(applicationSettings, path, Configuration::getInt, type);
        } else if (type == String.class) {
            return getValue(applicationSettings, path, Configuration::getString, type);
        } else {
            final T result;
            try {
                result = type.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                logger.warning(String.format("Could not create setting %s of type %s", path, type));
                return Optional.empty();
            }
            for (final Method method : type.getMethods()) {
                if (method.getParameterCount() == 1 && method.getName().startsWith("set")) {
                    String propertyName = method.getName().substring(3).toLowerCase();
                    Class<?> propertyType = method.getParameterTypes()[0];
                    String propertyPath = path + "." + propertyName;
                    createEngineSettings(applicationSettings, propertyPath, propertyType).map((value -> {
                        try {
                            method.invoke(result, value);
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            logger.warning(String.format("Could not set property %s to %s", propertyPath, value));
                        }
                        return null;
                    }));
                }
            }
            return Optional.of(result);
        }
    }

    private <T> Optional<T> getValue(Configuration configuration, String path, BiFunction<Configuration,String,?> getter, Class<T> type) {
        if (configuration.containsKey(path)) {
            return Optional.of(type.cast(getter.apply(configuration, path)));
        } else {
            return Optional.empty();
        }
    }
}
