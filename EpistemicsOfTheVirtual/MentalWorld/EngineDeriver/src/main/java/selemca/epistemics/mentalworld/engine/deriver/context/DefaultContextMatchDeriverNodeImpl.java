/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.context;

import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.configuration.Configuration;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.deriver.util.GraphUtil;
import selemca.epistemics.mentalworld.engine.node.ContextMatchDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

import java.util.Collection;
import java.util.Optional;

import static selemca.epistemics.mentalworld.engine.deriver.context.ContextDeriverNodeSettingsProvider.CONTEXT_ASSOCIATION_MAXIMUM_DISTANCE;

public class DefaultContextMatchDeriverNodeImpl implements ContextMatchDeriverNode {
    final double CONTEXT_ASSOCIATION_MAXIMUM_DISTANCE_DEFAULT = 1.0;

    private final WorkingMemory workingMemory;
    private final MentalWorldEngine.Logger logger;
    private final BeliefModelService beliefModelService;
    private final Graph<Concept, Association> beliefSystemGraph;
    private double contextAssociationMaximumDistance;

    public DefaultContextMatchDeriverNodeImpl(BeliefModelService beliefModelService, Graph<Concept, Association> beliefSystemGraph, WorkingMemory workingMemory, MentalWorldEngine.Logger logger, Configuration applicationSettings) {
        this.workingMemory = workingMemory;
        this.logger = logger;
        this.beliefModelService = beliefModelService;
        this.beliefSystemGraph = beliefSystemGraph;
        contextAssociationMaximumDistance = applicationSettings.getDouble(CONTEXT_ASSOCIATION_MAXIMUM_DISTANCE, CONTEXT_ASSOCIATION_MAXIMUM_DISTANCE_DEFAULT);
    }

    @Override
    public boolean contextMatch() {
        CategoryMatch categoryMatch = workingMemory.getCategoryMatch();
        boolean contextMatch = withinCurrentContext(categoryMatch.getConcept());

        logger.debug(String.format("Category %s %sin current context", categoryMatch.getConcept().getName(), contextMatch ? "" : "not "));
        if (!contextMatch) {
            Concept context = findBestMatchingContext(categoryMatch.getConcept());
            double distanceToConcept = new GraphUtil().getDistance(beliefSystemGraph, context, categoryMatch.getConcept());
            System.out.println(String.format("Nearest context: %s  distance: %s", context, distanceToConcept));
            if (distanceToConcept <= contextAssociationMaximumDistance) {
                workingMemory.setNewContext(context);
                logger.debug(String.format("Propose new context: %s (distance %s)", context.getName(), distanceToConcept));
            } else {
                logger.debug("I don't know a suitable context");
            }
        }
        return contextMatch;
    }

    public Concept findBestMatchingContext(Concept concept) {
        Collection<Concept> contexts = beliefModelService.listContextConcepts();
        GraphUtil graphUtil = new GraphUtil();

        Concept nearestContext = null;
        double minumumDistance = Double.MAX_VALUE;
        for (Concept context : contexts) {
            double distance = graphUtil.getDistance(beliefSystemGraph, concept, context);
            if (distance < minumumDistance) {
                nearestContext = context;
                minumumDistance = distance;
            }
        }
        return nearestContext;
    }

    private boolean withinCurrentContext(Concept category) {
        boolean withinContext = false;
        Optional<Concept> contextOptional = beliefModelService.getContext();
        if (contextOptional.isPresent()) {
            double distance = new GraphUtil().getDistance(beliefSystemGraph, contextOptional.get(), category);
            withinContext = (distance <= contextAssociationMaximumDistance);
        }
        return withinContext;
    }
}
