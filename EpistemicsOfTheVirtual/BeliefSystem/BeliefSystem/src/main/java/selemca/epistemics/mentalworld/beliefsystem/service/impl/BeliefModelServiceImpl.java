/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.*;
import selemca.epistemics.mentalworld.beliefsystem.repository.*;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("beliefModelService")
@Primary
public class BeliefModelServiceImpl implements BeliefModelService {

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private AssociationMetaRepository associationMetaRepository;

    @Autowired
    private ConceptMetaRepository conceptMetaRepository;

    @Autowired
    private OwnStateRepository ownStateRepository;

    @Override
    public void cascadingDelete(Concept concept) {
        listAssociations(concept).forEach(associationRepository::delete);
        conceptMetaRepository.findByConcept(concept).forEach(conceptMetaRepository::delete);
        getOwnStateValue(CONTEXT_PROPERTY).filter(value -> value.equals(concept.getName())).ifPresent(ignore -> setContext(null));
        conceptRepository.delete(concept);
    }

    @Override
    public Set<Association> listAssociations(Concept concept) {
        Set<Association> result = new HashSet<>();

        result.addAll(associationRepository.findByConcept1(concept));
        result.addAll(associationRepository.findByConcept2(concept));
        return result;
    }

    @Override
    public Optional<Association> getAssociation(Concept concept1, Concept concept2) {
        Optional<Association> associationOptional = associationRepository.findByConcept1AndConcept2(concept1, concept2);
        if (!associationOptional.isPresent()) {
            associationOptional = associationRepository.findByConcept1AndConcept2(concept2, concept1);
        }
        return associationOptional;
    }

    @Override
    public void fullSave(Association association) {
        // Find persisted Association to update
        Association newAssociation = getAssociation(association.getConcept1(), association.getConcept2())
            .map(existing -> {
                existing.setTruthValue(association.getTruthValue());
                return existing;
            })
            .orElseGet(() -> {
                // Store Concept 1 is not present
                String concept1Id = association.getConcept1().getName();
                if (!conceptRepository.findOne(concept1Id).isPresent()) {
                    association.setConcept1(conceptRepository.save(new Concept(concept1Id, 0.8)));
                }
                // Store Concept 2 is not present
                String concept2Id = association.getConcept2().getName();
                if (!conceptRepository.findOne(concept2Id).isPresent()) {
                    association.setConcept2(conceptRepository.save(new Concept(concept2Id, 0.8)));
                }
                return orderAssociation(association);
            });
        associationRepository.save(newAssociation);
    }

    private Association orderAssociation(Association association) {
        Concept concept1 = association.getConcept1();
        Concept concept2 = association.getConcept2();
        if (concept1.getName().compareTo(concept2.getName()) > 0) {
            concept1 = association.getConcept2();
            concept2 = association.getConcept1();
        }
        return new Association(concept1, concept2, association.getTruthValue());
    }
    @Override
    public List<AssociationMeta> getAssociationMeta(Concept concept1, Concept concept2) {
        List<AssociationMeta> result = new ArrayList<>();

        result.addAll(associationMetaRepository.findByConcept1AndConcept2(concept1, concept2));
        result.addAll(associationMetaRepository.findByConcept1AndConcept2(concept2, concept1));
        return result;
    }

    @Override
    public Optional<String> getAssociationType(Concept concept1, Concept concept2) {
        return getAssociationRelationType(concept1, concept2).map(AssociationMeta::getValue);
    }

    private Optional<AssociationMeta> getAssociationRelationType(Concept concept1, Concept concept2) {
        AssociationMeta result = null;
        for (AssociationMeta associationMeta : getAssociationMeta(concept1, concept2)) {
            if (RELATION_TYPE.equals(associationMeta.getRelation())) {
                result = associationMeta;
            }
        }
        return Optional.ofNullable(result);
    }

    @Override
    public void setAssociationType(Concept concept1, Concept concept2, String relationType) {
        getAssociationRelationType(concept1, concept2).ifPresent(associationMetaRepository::delete);

        AssociationMeta associationMeta = new AssociationMeta(concept1, concept2, RELATION_TYPE, relationType);
        associationMetaRepository.save(associationMeta);
    }

    @Override
    public Set<Concept> listContextConcepts() {
        List<ConceptMeta> conceptMetas = conceptMetaRepository.findByRelationAndValue(CONCEPTMETA_RELATION_KINDOF, CONCEPTMETA_VALUE_CONTEXT);
        return conceptMetas.stream().map(ConceptMeta::getConcept).collect(Collectors.toSet());
    }

    @Override
    public boolean isContextConcept(Concept concept) {
        return conceptMetaRepository.findByConceptAndRelationAndValue(concept, CONCEPTMETA_RELATION_KINDOF, CONCEPTMETA_VALUE_CONTEXT).isPresent();
    }

    @Override
    public void setConceptContextState(Concept concept, boolean isContext) {
        Optional<ConceptMeta> contextConceptMetaOptional = conceptMetaRepository.findByConceptAndRelationAndValue(concept, CONCEPTMETA_RELATION_KINDOF, CONCEPTMETA_VALUE_CONTEXT);
        if (isContext) {
            // conceptMeta should be present
            if (!contextConceptMetaOptional.isPresent()) {
                ConceptMeta contextConceptMeta = new ConceptMeta(concept, CONCEPTMETA_RELATION_KINDOF, CONCEPTMETA_VALUE_CONTEXT);
                conceptMetaRepository.save(contextConceptMeta);
            }
        } else {
            // conceptMeta should not be present
            contextConceptMetaOptional.ifPresent(conceptMetaRepository::delete);
        }
    }

//    private Optional<ConceptMeta> findContextConceptMeta(Concept concept) {
//        ConceptMeta contextConceptMeta = null;
//        for (ConceptMeta conceptMeta : conceptMetaRepository.findByConcept(concept)) {
//            if (conceptMeta.getRelation().equals(CONCEPTMETA_RELATION_KINDOF) &&
//                    conceptMeta.getValue().equals(CONCEPTMETA_VALUE_CONTEXT)) {
//                contextConceptMeta = conceptMeta;
//                break;
//            }
//        }
//        return Optional.ofNullable(contextConceptMeta);
//    }

    @Override
    public void setContext(String context) {
        Optional<OwnState> ownStatePropertyOptional = ownStateRepository.findOne(CONTEXT_PROPERTY);
        OwnState ownStateProperty = ownStatePropertyOptional.orElse(new OwnState(CONTEXT_PROPERTY));
        ownStateProperty.setValue(context);
        ownStateRepository.save(ownStateProperty);
    }

    @Override
    public Optional<Concept> getContext() {
        return getOwnStateValue(CONTEXT_PROPERTY).flatMap((Function<String,Optional<Concept>>) conceptRepository::findOne);
    }

    private Optional<String> getOwnStateValue(String property) {
        return ownStateRepository.findOne(property).map(OwnState::getValue);
    }

    @Override
    public void eraseAll() {
        associationMetaRepository.deleteAll();
        associationRepository.deleteAll();
        ownStateRepository.deleteAll();
        conceptMetaRepository.deleteAll();
        conceptRepository.deleteAll();
    }
}
