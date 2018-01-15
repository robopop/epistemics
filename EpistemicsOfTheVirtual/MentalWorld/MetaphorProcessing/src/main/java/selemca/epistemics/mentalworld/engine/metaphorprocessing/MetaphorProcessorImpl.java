/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.metaphorprocessing;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.beliefsystem.service.WeightedBeliefModelService;
import selemca.epistemics.mentalworld.engine.metaphor.MetaphorProcessor;

import java.util.*;
import java.util.logging.Logger;
import static selemca.epistemics.mentalworld.engine.metaphorprocessing.MetaphorProcessingSettingsProvider.*;

@Component("metaphorProcessor.default")
public class MetaphorProcessorImpl implements MetaphorProcessor {
    private static final double VICINITY_TRESHOLT_DEFAULT = 0.5;
    private static final String RELATION_TYPE = "relationType";
    private static final String RELATION_TYPE_LITERAL = "l";
    private static final String RELATION_TYPE_FIGURATIVE = "f";
    private static final int INTERSECTION_MINUMUM_SIZE_ABSOLUTE_DEFAULT = 2;
    private static final int INTERSECTION_MINUMUM_SIZE_RELATIVE_DEFAULT = 10;
    private static final int MIXED_INTERSECTION_MINUMUM_SIZE_ABSOLUTE_DEFAULT = 2;
    private static final int MIXED_INTERSECTION_MINUMUM_SIZE_RELATIVE_DEFAULT = 10;

    @Autowired
    private Configuration applicationSettings;

    @Autowired
    private BeliefModelService beliefModelService;

    @Autowired
    private WeightedBeliefModelService weightedBeliefModelService;

    private enum RelationType {
        LITERAL, FIGURATIVE
    }

    @Override
    public MetaphorAssesment assesRelation(Concept concept1, Concept concept2) {
        double vicinityThresholt = applicationSettings.getDouble(VICINITY_TRESHOLT, VICINITY_TRESHOLT_DEFAULT);

        Set<Concept> concept1Relations = weightedBeliefModelService.listAssociationConcepts(concept1, vicinityThresholt);
        Set<Concept> concept2Relations = weightedBeliefModelService.listAssociationConcepts(concept2, vicinityThresholt);

        Set<Concept> union = new HashSet<>(concept1Relations);
        union.addAll(concept2Relations);

        if (union.isEmpty()) {
            return MetaphorAssesment.ANOMALY;
        }

        Set<Concept> intersection = intersection(concept1Relations, concept2Relations);
        Set<Concept> literalIntersection = literalIntersection(concept1, concept2, intersection);
        Set<Concept> mixedIntersection = mixedIntersection(concept1, concept2, intersection);

        int intersectionMinimumSizeAbsolute = applicationSettings.getInt(INTERSECTION_MINIMUM_SIZE_ABSOLUTE, INTERSECTION_MINUMUM_SIZE_ABSOLUTE_DEFAULT);
        double intersectionMinimumSizePercentage = applicationSettings.getInt(INTERSECTION_MINIMUM_SIZE_RELATIVE, INTERSECTION_MINUMUM_SIZE_RELATIVE_DEFAULT);
        int mixedIntersectionMinimumSizeAbsolute = applicationSettings.getInt(MIXED_INTERSECTION_MINIMUM_SIZE_ABSOLUTE, MIXED_INTERSECTION_MINUMUM_SIZE_ABSOLUTE_DEFAULT);
        double mixedIntersectionMinimumSizePercentage = applicationSettings.getInt(MIXED_INTERSECTION_MINIMUM_SIZE_RELATIVE, MIXED_INTERSECTION_MINUMUM_SIZE_RELATIVE_DEFAULT);

        double intersectionSizeRelative = (double) intersection.size() / union.size();
        double intersectionMinimumSizeRelative = intersectionMinimumSizePercentage / 100.0;
        boolean intersectionLargeEnough = intersection.size() >= intersectionMinimumSizeAbsolute && intersectionSizeRelative >= intersectionMinimumSizeRelative;
        if (!intersectionLargeEnough) {
            return MetaphorAssesment.ANOMALY;
        } else {
            double mixedIntersectionSizeRelative = (double) mixedIntersection.size() / intersection.size();
            double mixedIntersectionMinimumSizeRelative = mixedIntersectionMinimumSizePercentage / 100.0;
            boolean mixedIntersectionLargeEnough = mixedIntersection.size() >=  mixedIntersectionMinimumSizeAbsolute && mixedIntersectionSizeRelative >= mixedIntersectionMinimumSizeRelative;
            if (!mixedIntersectionLargeEnough) {
                return MetaphorAssesment.LITERAL;
            } else {
                return MetaphorAssesment.METAPHOR;
            }
        }
    }

    private Map<Concept, RelationType> findRelationTypes(Concept concept, Set<Concept> relations) {
        Map<Concept, RelationType> relationTypeMap = new HashMap<>();
        for (Concept relation : relations) {
            relationTypeMap.put(relation, findRelationType(concept, relation));
        }
        return relationTypeMap;
    }

    private RelationType findRelationType(Concept concept, Concept relation) {
        RelationType relationType = findConfiguredRelationType(concept, relation);
        if (relationType == null) {
            String defaultRelationType = applicationSettings.getString(DEFAULT_RELATION_TYPE, RELATION_TYPE_LITERAL);
            if (RELATION_TYPE_FIGURATIVE.equals(defaultRelationType)) {
                relationType = RelationType.FIGURATIVE;
            } else if (RELATION_TYPE_LITERAL.equals(defaultRelationType)) {
                relationType = RelationType.LITERAL;
            }
        }
        return relationType;
    }

    private RelationType findConfiguredRelationType(Concept concept, Concept relation) {
        RelationType type = null;
        List<AssociationMeta> associationMetaList = beliefModelService.getAssociationMeta(concept, relation);
        for (AssociationMeta associationMeta : associationMetaList) {
            if (RELATION_TYPE.equals(associationMeta.getRelation())) {
                String typeValue = associationMeta.getValue();
                switch (typeValue) {
                    case RELATION_TYPE_FIGURATIVE:
                        type = RelationType.FIGURATIVE;
                        break;
                    case RELATION_TYPE_LITERAL:
                        type = RelationType.LITERAL;
                        break;
                    default:
                        Logger.getLogger(getClass().getSimpleName()).info(String.format("Configured relation type '%s' between %s and %s not known. Valid values are 'f' and 'l'", typeValue, concept, relation));
                        break;
                }
            }
        }
        return type;
    }

    private Set<Concept> intersection(Set<Concept> concept1Relations, Set<Concept> concept2Relations) {
        Set<Concept> intersection = new HashSet<>();
        for (Concept concept : concept1Relations) {
            if (concept2Relations.contains(concept)) {
                intersection.add(concept);
            }
        }
        return intersection;
    }

    private Set<Concept> literalIntersection(Concept concept1, Concept concept2, Set<Concept> relations) {
        Set<Concept> filteredConcepts = new HashSet<>();
        for (Concept relation : relations) {
            RelationType concept1RelationType = findRelationType(concept1, relation);
            RelationType concept2RelationType = findRelationType(concept2, relation);
            if (concept1RelationType == RelationType.LITERAL && concept2RelationType == RelationType.LITERAL) {
                filteredConcepts.add(relation);
            }
        }
        return filteredConcepts;
    }

    private Set<Concept> mixedIntersection(Concept concept1, Concept concept2, Set<Concept> relations) {
        Set<Concept> filteredConcepts = new HashSet<>();
        for (Concept relation : relations) {
            RelationType concept1RelationType = findRelationType(concept1, relation);
            RelationType concept2RelationType = findRelationType(concept2, relation);
            if (concept1RelationType != null && concept2RelationType != null && concept1RelationType != concept2RelationType) {
                filteredConcepts.add(relation);
            }
        }
        return filteredConcepts;
    }

}
