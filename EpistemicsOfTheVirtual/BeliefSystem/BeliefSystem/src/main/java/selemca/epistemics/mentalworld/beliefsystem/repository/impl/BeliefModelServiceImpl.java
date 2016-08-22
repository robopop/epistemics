/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import selemca.epistemics.data.entity.*;
import selemca.epistemics.mentalworld.beliefsystem.repository.*;

import java.util.*;

@Component("beliefModelService")
@Primary
public class BeliefModelServiceImpl implements BeliefModelService {
    private static final String CONTEXT_PROPERTY = "context";
    private static final String CONCEPTMETA_RELATION_KINDOF = "kind_of";
    private static final String CONCEPTMETA_VALUE_CONTEXT = "context";
    private static final String RELATION_TYPE = "relationType";

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
        for (Association association : listAssociations(concept)) {
            associationRepository.delete(association);
        }
        for (ConceptMeta meta : conceptMetaRepository.findByConcept(concept)) {
            conceptMetaRepository.delete(meta);
        }
        Optional<String> ownStateValueOptional = getOwnStateValue(CONTEXT_PROPERTY);
        if (ownStateValueOptional.isPresent() && ownStateValueOptional.get().equals(concept.getName())) {
            setContext((String)null);
        }
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
        Optional<Association> associationOptional = associationRepository.findByConcept1AndConcept2(association.getConcept1(), association.getConcept2());
        if (!associationOptional.isPresent()) {
            associationOptional = associationRepository.findByConcept1AndConcept2(association.getConcept2(), association.getConcept1());
        }
        if (associationOptional.isPresent()) {
            associationOptional.get().setTruthValue(association.getTruthValue());
            associationRepository.save(associationOptional.get());
        } else {
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
            association = orderAssociation(association);
            associationRepository.save(association);
        }
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
        String relationType = null;
        Optional<AssociationMeta> associationMetaOptional = getAssociationRelationType(concept1, concept2);
        if (associationMetaOptional.isPresent()) {
            relationType = associationMetaOptional.get().getValue();
        }
        return Optional.ofNullable(relationType);
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
        Optional<AssociationMeta> associationMetaOptional = getAssociationRelationType(concept1, concept2);
        if (associationMetaOptional.isPresent()) {
            associationMetaRepository.delete(associationMetaOptional.get());
        }

        AssociationMeta associationMeta = new AssociationMeta(concept1, concept2, RELATION_TYPE, relationType);
        associationMetaRepository.save(associationMeta);
    }

    @Override
    public Set<Concept> listContextConcepts() {
        List<ConceptMeta> conceptMetas = conceptMetaRepository.findByRelationAndValue(CONCEPTMETA_RELATION_KINDOF, CONCEPTMETA_VALUE_CONTEXT);
        Set<Concept> concepts = new HashSet<>();
        for (ConceptMeta conceptMeta : conceptMetas) {
            concepts.add(conceptMeta.getConcept());
        }
        return concepts;
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
            if (contextConceptMetaOptional.isPresent()) {
                conceptMetaRepository.delete(contextConceptMetaOptional.get());
            }
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
        Optional<String> ownStateValueOptional = getOwnStateValue(CONTEXT_PROPERTY);
        Optional<Concept> context = Optional.empty();
        Optional<String> stateValueOptional = ownStateValueOptional;
        if (stateValueOptional.isPresent()) {
            context = conceptRepository.findOne(stateValueOptional.get());
        }
        return context;
    }

    private Optional<String> getOwnStateValue(String property) {
        Optional<OwnState> ownStatePropertyOptional = ownStateRepository.findOne(property);
        String value = null;
        if (ownStatePropertyOptional.isPresent()) {
            value = ownStatePropertyOptional.get().getValue();
        }
        return Optional.ofNullable(value);
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
