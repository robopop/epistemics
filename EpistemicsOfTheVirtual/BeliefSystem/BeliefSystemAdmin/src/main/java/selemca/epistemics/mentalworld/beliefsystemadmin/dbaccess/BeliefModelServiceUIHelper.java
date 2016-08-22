package selemca.epistemics.mentalworld.beliefsystemadmin.dbaccess;

import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationMetaRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.BeliefModelService;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;
import selemca.epistemics.mentalworld.beliefsystemadmin.importexport.ZipExportStreamResourceFactory;
import selemca.epistemics.mentalworld.beliefsystemadmin.importexport.ZipUploadReceiverFactory;

import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BeliefModelServiceUIHelper implements Serializable {
    private final SpringContextHelper helper;

    public BeliefModelServiceUIHelper(ServletContext servletContext) {
        helper = new SpringContextHelper(servletContext);
    }

    public List<Concept> findConcepts(String stringFilter) {
        List<Concept> allConcepts = getConceptRepository().findAll();
        List<Concept> filtered = new ArrayList<>();
        for (Concept concept : allConcepts) {
            if (stringFilter == null || stringFilter.isEmpty() ||
                    concept.getName().contains(stringFilter)) {
                filtered.add(concept);
            }
        }
        return filtered;
    }

    public List<AssociationUIObject> findAssociations(String stringFilter) {
        List<AssociationUIObject> allAssociations = convertAll(getAssociationRepository().findAll());
        List<AssociationUIObject> filtered = new ArrayList<>();
        for (AssociationUIObject association : allAssociations) {
            if (stringFilter == null || stringFilter.isEmpty() ||
                    (association.getConcept1().contains(stringFilter) ||
                        association.getConcept2().contains(stringFilter))) {
                    filtered.add(association);
            }
        }
        return filtered;
    }

    public void save(AssociationUIObject associationUIObject, String relationType) {
        Association association = findMatch(associationUIObject);
        if (association == null) {
            association = createAssociation(associationUIObject);
        }
        association.setTruthValue(associationUIObject.getTruthValue());
        getAssociationRepository().save(association);

        if (relationType != null) {
            getBeliefModelService().setAssociationType(association.getConcept1(), association.getConcept2(), relationType);
        }
    }

    private Association createAssociation(AssociationUIObject associationUIObject) {
        Concept concept1 = retrieveOrStore(associationUIObject.getConcept1());
        Concept concept2 = retrieveOrStore(associationUIObject.getConcept2());
        // Store association's concepts in alphabetical order. Just for esthetics
        if (concept1.getName().compareTo(concept2.getName()) > 0) {
            Concept swapHelper = concept1;
            concept1 = concept2;
            concept2 = swapHelper;
        }
        return new Association(concept1, concept2, associationUIObject.getTruthValue());

    }

    public void delete(AssociationUIObject associationUIObject) {
        Association association = findMatch(associationUIObject);
        if (association != null) {
            getAssociationRepository().delete(association);
        }
    }

    public void wipeAll() {
        getAssociationRepository().deleteAll();
    }

    public void save(Concept concept, boolean isContext) {
        Concept persistedConcept = findMatch(concept);
        if (persistedConcept == null) {
            persistedConcept = new Concept(concept.getName(), 0.8);
        }
        persistedConcept.setTruthValue(concept.getTruthValue());
        getConceptRepository().save(persistedConcept);

        getBeliefModelService().setConceptContextState(persistedConcept, isContext);
    }

    public void delete(Concept concept) {
        getBeliefModelService().cascadingDelete(concept);
    }

    public boolean isContextConcept(Concept concept) {
        return getBeliefModelService().isContextConcept(concept);
    }

    private Concept retrieveOrStore(String conceptName) {
        ConceptRepository conceptRepository = getConceptRepository();
        Optional<Concept> conceptOptional = conceptRepository.findOne(conceptName);
        Concept concept = null;
        if (conceptOptional.isPresent()) {
            concept = conceptOptional.get();
        } else {
            concept = new Concept(conceptName, 0.8);
            conceptRepository.save(concept);
        }
        return concept;
    }

    private Association findMatch(AssociationUIObject associationUIObject) {
        Association foundAssociation = null;
        ConceptRepository conceptRepository = getConceptRepository();
        Optional<Concept> concept1Optional = conceptRepository.findOne(associationUIObject.getConcept1());
        Optional<Concept> concept2Optional = conceptRepository.findOne(associationUIObject.getConcept2());
        if (concept1Optional.isPresent() && concept2Optional.isPresent()) {
            Optional<Association> associationOptional = getBeliefModelService().getAssociation(concept1Optional.get(), concept2Optional.get());
            foundAssociation = associationOptional.orElse(null);
        }
        return foundAssociation;
    }

    private Concept findMatch(Concept concept) {
        return getConceptRepository().findOne(concept.getName()).orElse(null);
    }

    public String findRelationType(AssociationUIObject associationUIObject) {
        Association association = findMatch(associationUIObject);
        if (association != null) {
            return getBeliefModelService().getAssociationType(association.getConcept1(), association.getConcept2()).orElse(null);
        } else {
            return null;
        }
    }

    private List<AssociationUIObject> convertAll(List<Association> associations) {
        List<AssociationUIObject> result = new ArrayList<>();
        for (Association association : associations) {
            result.add(convert(association));
        }
        return result;
    }

    private AssociationUIObject convert(Association association) {
        AssociationUIObject associationUIObject = new AssociationUIObject();
        associationUIObject.setConcept1(association.getConcept1().getName());
        associationUIObject.setConcept2(association.getConcept2().getName());
        associationUIObject.setTruthValue(association.getTruthValue());
        return associationUIObject;
    }

    public AssociationRepository getAssociationRepository() {
        return (AssociationRepository) helper.getBean("associationRepository");
    }

    public AssociationMetaRepository getAssociationMetaRepository() {
        return (AssociationMetaRepository) helper.getBean("associationMetaRepository");
    }

    public BeliefModelService getBeliefModelService() {
        return (BeliefModelService) helper.getBean("beliefModelService");
    }

    public ConceptRepository getConceptRepository() {
        return (ConceptRepository) helper.getBean("conceptRepository");
    }

    public ZipExportStreamResourceFactory getZipExportStreamResourceFactory() {
        return (ZipExportStreamResourceFactory) helper.getBean("zipExportStreamResourceFactory");
    }

    public ZipUploadReceiverFactory getZipUploadReceiverFactory() {
        return (ZipUploadReceiverFactory) helper.getBean("zipUploadReceiverFactory");
    }
}
