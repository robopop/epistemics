/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.beliefsystem.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.data.entity.ConceptMeta;
import selemca.epistemics.mentalworld.beliefsystem.repository.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping(BeliefSystemRestController.URL_PREFIX)
public class BeliefSystemRestController {
    protected static final String URL_PREFIX = "/epistemics";
    protected static final String SERVLET_ASSOCIATION = "/association";
    protected static final String SERVLET_ASSOCIATIONMETA = "/associationmeta";
    protected static final String SERVLET_CONCEPT = "/concept";
    protected static final String SERVLET_CONTEXT = "/context";
    protected static final String SERVLET_CONTEXTS = "/contexts";
    protected static final String SERVLET_CONCEPTMETA = "/conceptmeta";
    protected static final String PARAM_CONCEPT_ID = "conceptId";

    @Autowired
    private ConceptRepository conceptRepository;

    @Autowired
    private ConceptMetaRepository conceptMetaRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private AssociationMetaRepository associationMetaRepository;

    @Autowired
    private BeliefModelService beliefModelService;

    /*
     * CONCEPT
     */
    @RequestMapping(value = SERVLET_CONCEPT, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public List<Concept> listConcepts(@PathParam(PARAM_CONCEPT_ID) String conceptId) {
        List<Concept> concepts = new ArrayList<>();
        if (conceptId != null) {
            Optional<Concept> conceptOptional = conceptRepository.findOne(conceptId);
            if (conceptOptional.isPresent()) {
                concepts.add(conceptOptional.get());
            }
        } else {
            concepts.addAll(conceptRepository.findAll());
        }
        Logger.getLogger(getClass().getSimpleName()).info(String.format("Requesting concepts -> %s", concepts));
        return concepts;
    }

    @RequestMapping(value = SERVLET_CONCEPT, method = RequestMethod.PUT)
    @Consumes(MediaType.APPLICATION_JSON)
    public void editConcept(@RequestBody Concept concept) {
        Logger.getLogger(getClass().getSimpleName()).info("Storing concept: " + concept);
        conceptRepository.save(concept);
    }

    @RequestMapping(value = SERVLET_CONCEPT, method = RequestMethod.DELETE)
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeConcept(@RequestBody Concept concept) {
        Logger.getLogger(getClass().getSimpleName()).info("Deleting concept: " + concept);
        beliefModelService.cascadingDelete(concept);
    }

    /*
     * CONCEPT META
     */
    @RequestMapping(value = SERVLET_CONCEPTMETA, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<ConceptMeta> listConceptMetas(@PathParam(PARAM_CONCEPT_ID) String conceptId) {
        List<ConceptMeta> conceptMetas = new ArrayList<>();
        if (conceptId != null) {
            Optional<Concept> conceptOptional = conceptRepository.findOne(conceptId);
            if (conceptOptional.isPresent()) {
                conceptMetas.addAll(conceptMetaRepository.findByConcept(conceptOptional.get()));
            }
        } else {
            conceptMetas.addAll(conceptMetaRepository.findAll());
        }
        Logger.getLogger(getClass().getSimpleName()).info(String.format("Requesting concept meta -> %s", conceptMetas));
        return conceptMetas;
    }

    @RequestMapping(value = SERVLET_CONCEPTMETA, method = RequestMethod.PUT)
    @Consumes(MediaType.APPLICATION_JSON)
    public void editConceptMeta(@RequestBody ConceptMeta conceptMeta) {
        Logger.getLogger(getClass().getSimpleName()).info("Storing concept meta: " + conceptMeta);
        conceptMetaRepository.save(conceptMeta);
    }

    @RequestMapping(value = SERVLET_CONCEPTMETA, method = RequestMethod.DELETE)
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeConcept(@RequestBody ConceptMeta conceptMeta) {
        Logger.getLogger(getClass().getSimpleName()).info("Deleting concept meta: " + conceptMeta);
        conceptMetaRepository.delete(conceptMeta);
    }

    /*
     * ASSOCIATION
     */
    @RequestMapping(value = SERVLET_ASSOCIATION, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<Association> listAssociations(@PathParam(PARAM_CONCEPT_ID) String conceptId) {
        List<Association> result = new ArrayList<>();
        if (conceptId != null) {
            Optional<Concept> conceptOptional = conceptRepository.findOne(conceptId);
            if (conceptOptional.isPresent()) {
                Concept concept = conceptOptional.get();
                result.addAll(associationRepository.findByConcept1(concept));
                result.addAll(associationRepository.findByConcept2(concept));
            }
        } else {
            result.addAll(associationRepository.findAll());
        }
        Logger.getLogger(getClass().getSimpleName()).info(String.format("Requesting ui -> %s", result));
        return result;
    }

    @RequestMapping(value = SERVLET_ASSOCIATION, method = RequestMethod.PUT)
    @Consumes(MediaType.APPLICATION_JSON)
    public void editAssociation(@RequestBody Association association) {
        Logger.getLogger(getClass().getSimpleName()).info("Storing association: " + association);
        Optional<Association> associationOptional = beliefModelService.getAssociation(association.getConcept1(), association.getConcept2());
        Association associationPersist = associationOptional.orElse(association);
        associationPersist.setTruthValue(association.getTruthValue());
        associationRepository.save(associationPersist);
    }

    @RequestMapping(value = SERVLET_ASSOCIATION, method = RequestMethod.DELETE)
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeAssociation(@RequestBody Association association) {
        Logger.getLogger(getClass().getSimpleName()).info("Deleting association: " + association);
        Optional<Association> associationOptional = beliefModelService.getAssociation(association.getConcept1(), association.getConcept2());
        if (associationOptional.isPresent()) {
            associationRepository.delete(associationOptional.get());
        }
    }

    /*
     * ASSOCIATION META
     */
    @RequestMapping(value = SERVLET_ASSOCIATIONMETA, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<AssociationMeta> listAssociationMetas(@PathParam(PARAM_CONCEPT_ID + "1") String concept1Id, @PathParam(PARAM_CONCEPT_ID + "2") String concept2Id) {
        List<AssociationMeta> result = new ArrayList<>();
        if (concept1Id != null && concept2Id != null) {
            Optional<Concept> concept1Optional = conceptRepository.findOne(concept1Id);
            Optional<Concept> concept2Optional = conceptRepository.findOne(concept2Id);
            if (concept1Optional.isPresent() && concept2Optional.isPresent()) {
                Concept concept1 = concept1Optional.get();
                Concept concept2 = concept2Optional.get();
                result.addAll(associationMetaRepository.findByConcept1AndConcept2(concept1, concept2));
                result.addAll(associationMetaRepository.findByConcept1AndConcept2(concept2, concept1));
            }
        } else {
            result.addAll(associationMetaRepository.findAll());
        }
        Logger.getLogger(getClass().getSimpleName()).info(String.format("Requesting association meta -> %s", result));
        return result;
    }

    @RequestMapping(value = SERVLET_ASSOCIATIONMETA, method = RequestMethod.PUT)
    @Consumes(MediaType.APPLICATION_JSON)
    public void editAssociationMeta(@RequestBody AssociationMeta associationMeta) {
        Logger.getLogger(getClass().getSimpleName()).info("Storing association meta: " + associationMeta);
        associationMetaRepository.save(associationMeta);
    }

    @RequestMapping(value = SERVLET_ASSOCIATIONMETA, method = RequestMethod.DELETE)
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeAssociationMeta(@RequestBody AssociationMeta associationMeta) {
        Logger.getLogger(getClass().getSimpleName()).info("Deleting association meta: " + associationMeta);
        associationMetaRepository.delete(associationMeta);
    }

    /*
     * CONTEXT
     */
    @RequestMapping(value = SERVLET_CONTEXTS, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public Collection<Concept> listContexts() {
        Set<Concept> contextConcepts = beliefModelService.listContextConcepts();
        Logger.getLogger(getClass().getSimpleName()).info(String.format("Requesting context concepts -> %s", contextConcepts));
        return contextConcepts;
    }


    @RequestMapping(value = SERVLET_CONTEXT, method = RequestMethod.GET)
    @Produces(MediaType.APPLICATION_JSON)
    public Concept getCurrentContext() {
        Concept context = beliefModelService.getContext().orElse(null);
        Logger.getLogger(getClass().getSimpleName()).info("Requesting current context: " + context);
        return context;
    }

    @RequestMapping(value = SERVLET_CONTEXT, method = RequestMethod.PUT)
    @Consumes(MediaType.TEXT_PLAIN)
    public void setCurrentContext(@RequestBody String context) {
        Logger.getLogger(getClass().getSimpleName()).info("Setting current context: " + context);
        beliefModelService.setContext(context);
    }

}
