/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.beliefsystem.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.AssociationPK;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.data.entity.ConceptMeta;
import selemca.epistemics.mentalworld.beliefsystem.repository.*;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.beliefsystem.service.ImportService;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;

import static java.lang.String.format;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(BeliefSystemRestController.URL_PREFIX)
public class BeliefSystemRestController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    protected static final String URL_PREFIX = "/epistemics";
    protected static final String SERVLET_ASSOCIATION = "/association";
    protected static final String SERVLET_ASSOCIATION_META = "/association-meta";
    protected static final String SERVLET_CONCEPT = "/concept";
    protected static final String SERVLET_CONTEXT = "/context";
    protected static final String SERVLET_CONTEXTS = "/contexts";
    protected static final String SERVLET_CONCEPT_META = "/concept-meta";
    protected static final String SERVLET_BELIEF_SYSTEM = "/belief-system";
    protected static final String PARAM_CONCEPT_ID = "conceptId";
    protected static final String PARAM_CONCEPT_PART = "/{" + PARAM_CONCEPT_ID + "}";
    protected static final String PARAM_OTHER_CONCEPT_ID = "concept2Id";
    protected static final String PARAM_OTHER_CONCEPT_PART = "/{" + PARAM_OTHER_CONCEPT_ID + "}";
    protected static final String PARAM_META_ID = "metaId";
    protected static final String PARAM_META_PART = "/{" + PARAM_META_ID + "}";

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

    @Autowired
    private ImportService importService;

    /*
     * CONCEPT
     */
    @RequestMapping(value = SERVLET_CONCEPT, method = GET, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public List<Concept> listConcepts() {
        List<Concept> concepts = conceptRepository.findAll();
        LOG.info("Requesting concepts -> {}", concepts);
        return concepts;
    }

    @RequestMapping(value = SERVLET_CONCEPT + PARAM_CONCEPT_PART, method = GET, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public Concept getConcept(@PathVariable(PARAM_CONCEPT_ID) String conceptId) {
        Concept result = conceptRepository.findOne(conceptId)
                .orElseThrow(() -> new NotFoundException(format("Concept: %s", conceptId)));
        LOG.info("Get concept: {} -> {}", conceptId, result);
        return result;
    }

    @RequestMapping(value = SERVLET_CONCEPT + PARAM_CONCEPT_PART, method = PUT)
    @Consumes(MediaType.APPLICATION_JSON)
    public void editConcept(@PathVariable(PARAM_CONCEPT_ID) String conceptId, @RequestBody Concept concept) {
        if (!Objects.equals(conceptId, concept.getName())) {
            throw new BadRequestException(format("Concept ID does not match name: %s: %s", conceptId, concept.getName()));
        }
        LOG.info("Storing concept: {}", concept);
        conceptRepository.save(concept);
    }

    @RequestMapping(value = SERVLET_CONCEPT + PARAM_CONCEPT_PART, method = DELETE)
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeConcept(@PathVariable(PARAM_CONCEPT_ID) String conceptId) {
        conceptRepository.findOne(conceptId)
                .map(concept -> {
                    LOG.info("Deleting concept: {}", concept);
                    beliefModelService.cascadingDelete(concept);
                    return true;
                })
                .orElseThrow(() -> new NotFoundException(format("Concept: %s", conceptId)))
        ;
    }

    /*
     * CONCEPT META
     */
    @RequestMapping(value = SERVLET_CONCEPT_META, method = GET, produces = MediaType.APPLICATION_JSON)
    public List<ConceptMeta> listConceptMetas() {
        List<ConceptMeta> conceptMetas = conceptMetaRepository.findAll();
        LOG.info("Requesting concept meta -> {}", conceptMetas);
        return conceptMetas;
    }

    @RequestMapping(value = SERVLET_CONCEPT_META + PARAM_CONCEPT_PART, method = GET, produces = MediaType.APPLICATION_JSON)
    public List<ConceptMeta> listConceptMetas(@PathVariable(PARAM_CONCEPT_ID) String conceptId) {
        Concept concept = conceptRepository.findOne(conceptId)
                .orElseThrow(() -> new NotFoundException(format("Concept: %s", conceptId)));
        List<ConceptMeta> result = conceptMetaRepository.findByConcept(concept);
        LOG.info("Requesting concept meta -> {}", result);
        return result;
    }

    @RequestMapping(value = SERVLET_CONCEPT_META + PARAM_CONCEPT_PART + PARAM_META_PART, method = GET)
    @Produces(MediaType.APPLICATION_JSON)
    public ConceptMeta getConceptMeta(@PathVariable(PARAM_CONCEPT_ID) String conceptId, @PathVariable(PARAM_META_ID) Long metaId) {
        ConceptMeta result = getCurrentConceptMeta(conceptId, metaId);
        LOG.info("Get concept meta: ({}, {}) -> {}", new Object[]{conceptId, metaId, result});
        return result;
    }

    @RequestMapping(value = SERVLET_CONCEPT_META + PARAM_CONCEPT_PART, method = PUT)
    @Consumes(MediaType.APPLICATION_JSON)
    public void addConceptMeta(@PathVariable(PARAM_CONCEPT_ID) String conceptId, @RequestBody ConceptMeta conceptMeta) {
        if (conceptMeta.getId() != null) {
            throw new BadRequestException(format("With PUT, ID must omitted: %s", conceptMeta.getId()));
        }
        checkId(conceptMeta, conceptId);
        LOG.info("Storing concept meta: {}", conceptMeta);
        conceptMetaRepository.save(conceptMeta);
    }

    @RequestMapping(value = SERVLET_CONCEPT_META + PARAM_CONCEPT_PART + PARAM_META_PART, method = POST)
    @Consumes(MediaType.APPLICATION_JSON)
    public void editConceptMeta(@PathVariable(PARAM_CONCEPT_ID) String conceptId, @PathVariable(PARAM_META_ID) Long conceptMetaId, @RequestBody ConceptMeta conceptMeta) {
        getCurrentConceptMeta(conceptId, conceptMetaId);
        LOG.info("Storing concept meta: {}", conceptMeta);
        conceptMetaRepository.save(conceptMeta);
    }

    @RequestMapping(value = SERVLET_CONCEPT_META + PARAM_CONCEPT_PART + PARAM_META_PART, method = DELETE)
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeConceptMeta(@PathVariable(PARAM_CONCEPT_ID) String conceptId, @PathVariable(PARAM_META_ID) Long conceptMetaId) {
        ConceptMeta current = getCurrentConceptMeta(conceptId, conceptMetaId);
        LOG.info("Deleting concept meta: {}", current);
        conceptMetaRepository.delete(current);
    }

    private ConceptMeta getCurrentConceptMeta(String conceptId, Long metaId) {
        ConceptMeta current = conceptMetaRepository.findOne(metaId)
                .orElseThrow(() -> new NotFoundException(format("Concept meta: %s", metaId)));
        if (!Objects.equals(metaId, current.getId())) {
            throw new BadRequestException(format("Concept meta ID does not match ID: %s: %s", metaId, current.getId()));
        }
        checkId(current, conceptId);
        return current;
    }

    private void checkId(ConceptMeta conceptMeta, String conceptId) {
        if (!Objects.equals(conceptId, conceptMeta.getConcept().getName())) {
            throw new BadRequestException(format("Concept ID does not match name: %s: %s", conceptId, conceptMeta.getConcept().getName()));
        }
    }
    /*
     * ASSOCIATION
     */
    @RequestMapping(value = SERVLET_ASSOCIATION, method = GET, produces = MediaType.APPLICATION_JSON)
    public List<Association> listAssociations() {
        List<Association> result = associationRepository.findAll();
        LOG.info("Requesting associations -> {}", result);
        return result;
    }

    @RequestMapping(value = SERVLET_ASSOCIATION + PARAM_CONCEPT_PART, method = GET, produces = MediaType.APPLICATION_JSON)
    public List<Association> listAssociations(@PathVariable(PARAM_CONCEPT_ID) String conceptId) {
        LOG.info("{}: {}, {}", new Object[] { SERVLET_ASSOCIATION + PARAM_CONCEPT_PART, PARAM_CONCEPT_ID, conceptId });
        Concept concept = getConcept(conceptId);
        List<Association> result = new ArrayList<>();
        result.addAll(associationRepository.findByConcept1(concept));
        result.addAll(associationRepository.findByConcept2(concept));
        LOG.info("Requesting associations: {} -> {}", conceptId, result);
        return result;
    }

    @RequestMapping(value = SERVLET_ASSOCIATION + PARAM_CONCEPT_PART + PARAM_OTHER_CONCEPT_PART, method = GET)
    @Produces(MediaType.APPLICATION_JSON)
    public Association getAssociation(@PathVariable(PARAM_CONCEPT_ID) String conceptId, @PathVariable(PARAM_OTHER_CONCEPT_ID) String otherConceptId) {
        Association result = getCurrentAssociation(conceptId, otherConceptId);
        LOG.info("Get association: ({}, {}) -> {}", new Object[] { conceptId, otherConceptId, result });
        return result;
    }

    @RequestMapping(value = SERVLET_ASSOCIATION + PARAM_CONCEPT_PART + PARAM_OTHER_CONCEPT_PART, method = PUT)
    @Consumes(MediaType.APPLICATION_JSON)
    public void editAssociation(@PathVariable(PARAM_CONCEPT_ID) String conceptId, @PathVariable(PARAM_OTHER_CONCEPT_ID) String otherConceptId, @RequestBody Association association) {
        getCurrentAssociation(conceptId, otherConceptId);
        if (!Objects.equals(conceptId, association.getConcept1().getName())) {
            throw new BadRequestException(format("Concept ID does not match name: %s: %s", conceptId, association.getConcept1().getName()));
        }
        if (!Objects.equals(otherConceptId, association.getConcept2().getName())) {
            throw new BadRequestException(format("Concept ID does not match name: %s: %s", otherConceptId, association.getConcept2().getName()));
        }
        LOG.info("Storing association: {}", association);
        Optional<Association> associationOptional = beliefModelService.getAssociation(association.getConcept1(), association.getConcept2());
        Association associationPersist = associationOptional.orElse(association);
        associationPersist.setTruthValue(association.getTruthValue());
        associationRepository.save(associationPersist);
    }

    @RequestMapping(value = SERVLET_ASSOCIATION + PARAM_CONCEPT_PART + PARAM_OTHER_CONCEPT_PART, method = DELETE)
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeAssociation(@PathVariable(PARAM_CONCEPT_ID) String conceptId, @PathVariable(PARAM_OTHER_CONCEPT_ID) String otherConceptId) {
        Association association = getCurrentAssociation(conceptId, otherConceptId);
        LOG.info("Deleting association: {}", association);
        beliefModelService.getAssociation(association.getConcept1(), association.getConcept2()).ifPresent(associationRepository::delete);
    }

    private Association getCurrentAssociation(String conceptId, String otherConceptId) {
        AssociationPK associationPK = new AssociationPK(conceptId, otherConceptId);
        return associationRepository.findOne(associationPK)
                .orElseThrow(() -> new NotFoundException(format("Association: %s: %s", conceptId, otherConceptId)));
    }

    /*
     * ASSOCIATION META
     */
    @RequestMapping(value = SERVLET_ASSOCIATION_META, method = GET, produces = MediaType.APPLICATION_JSON)
    public List<AssociationMeta> listAssociationMetas() {
        List<AssociationMeta> result = associationMetaRepository.findAll();
        LOG.info("Requesting association meta -> {}", result);
        return result;
    }

    @RequestMapping(value = SERVLET_ASSOCIATION_META + PARAM_CONCEPT_PART + PARAM_OTHER_CONCEPT_PART, method = GET, produces = MediaType.APPLICATION_JSON)
    public List<AssociationMeta> listAssociationMetas(@PathVariable(PARAM_CONCEPT_ID) String conceptId, @PathVariable(PARAM_OTHER_CONCEPT_ID) String otherConceptId) {
        Concept concept = getConcept(conceptId);
        Concept otherConcept = getConcept(otherConceptId);
        List<AssociationMeta> result = new ArrayList<>();
        result.addAll(associationMetaRepository.findByConcept1AndConcept2(concept, otherConcept));
        result.addAll(associationMetaRepository.findByConcept1AndConcept2(otherConcept, concept));
        LOG.info("Requesting association meta: ({}, {}) -> {}", new Object[] { conceptId, otherConceptId, result });
        return result;
    }

    @RequestMapping(value = SERVLET_ASSOCIATION_META + PARAM_CONCEPT_PART + PARAM_OTHER_CONCEPT_PART + PARAM_META_PART, method = GET)
    public AssociationMeta getAssociationMeta(@PathVariable(PARAM_CONCEPT_ID) String conceptId, @PathVariable(PARAM_OTHER_CONCEPT_ID) String otherConceptId, @PathVariable(PARAM_META_ID) Long metaId) {
        AssociationMeta result = associationMetaRepository.findOne(metaId)
                .orElseThrow(() -> new NotFoundException(format("Association meta: %s", metaId)));
        checkIds(result, conceptId, otherConceptId);
        return result;
    }

    @RequestMapping(value = SERVLET_ASSOCIATION_META + PARAM_CONCEPT_PART + PARAM_OTHER_CONCEPT_PART, method = POST)
    @Consumes(MediaType.APPLICATION_JSON)
    public void addAssociationMeta(@PathVariable(PARAM_CONCEPT_ID) String conceptId, @PathVariable(PARAM_OTHER_CONCEPT_ID) String otherConceptId, @RequestBody AssociationMeta associationMeta) {
        checkIds(associationMeta, conceptId, otherConceptId);
        LOG.info("Storing association meta: {}", associationMeta);
        associationMetaRepository.save(associationMeta);
    }

    @RequestMapping(value = SERVLET_ASSOCIATION_META + PARAM_CONCEPT_PART + PARAM_OTHER_CONCEPT_PART + PARAM_META_PART, method = PUT)
    @Consumes(MediaType.APPLICATION_JSON)
    public void editAssociationMeta(@PathVariable(PARAM_CONCEPT_ID) String conceptId, @PathVariable(PARAM_OTHER_CONCEPT_ID) String otherConceptId, @PathVariable(PARAM_META_ID) Long metaId, @RequestBody AssociationMeta associationMeta) {
        getCurrentAssociationMeta(conceptId, otherConceptId, metaId);
        if (!Objects.equals(conceptId, associationMeta.getConcept1().getName())) {
            throw new BadRequestException(format("Concept ID does not match name: %s: %s", conceptId, associationMeta.getConcept1().getName()));
        }
        if (!Objects.equals(otherConceptId, associationMeta.getConcept2().getName())) {
            throw new BadRequestException(format("Concept ID does not match name: %s: %s", otherConceptId, associationMeta.getConcept2().getName()));
        }
        LOG.info("Storing association meta: {}", associationMeta);
        associationMetaRepository.save(associationMeta);
    }

    @RequestMapping(value = SERVLET_ASSOCIATION_META + PARAM_CONCEPT_PART + PARAM_OTHER_CONCEPT_PART + PARAM_META_PART, method = DELETE)
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeAssociationMeta(@PathVariable(PARAM_CONCEPT_ID) String conceptId, @PathVariable(PARAM_OTHER_CONCEPT_ID) String otherConceptId, @PathVariable(PARAM_META_ID) Long metaId) {
        AssociationMeta associationMeta = getCurrentAssociationMeta(conceptId, otherConceptId, metaId);
        getCurrentAssociationMeta(conceptId, otherConceptId, metaId);
        LOG.info("Deleting association meta: {}", associationMeta);
        associationMetaRepository.delete(associationMeta);
    }

    private AssociationMeta getCurrentAssociationMeta(String conceptId, String otherConceptId, Long metaId) {
        AssociationMeta current = associationMetaRepository.findOne(metaId)
                .orElseThrow(() -> new NotFoundException(format("Association meta: %s", metaId)));
        if (!Objects.equals(metaId, current.getId())) {
            throw new BadRequestException(format("Association meta ID does not match ID: %s: %s", metaId, current.getId()));
        }
        checkIds(current, conceptId, otherConceptId);
        return current;
    }

    private void checkIds(AssociationMeta associationMeta, String conceptId, String otherConceptId) {
        if (!Objects.equals(conceptId, associationMeta.getConcept1().getName())) {
            throw new BadRequestException(format("Concept ID does not match name: %s: %s", conceptId, associationMeta.getConcept1().getName()));
        }
        if (!Objects.equals(otherConceptId, associationMeta.getConcept2().getName())) {
            throw new BadRequestException(format("Concept ID does not match name: %s: %s", otherConceptId, associationMeta.getConcept2().getName()));
        }
    }

    /*
     * CONTEXT
     */
    @RequestMapping(value = SERVLET_CONTEXTS, method = GET, produces = MediaType.APPLICATION_JSON)
    public Collection<Concept> listContexts() {
        Set<Concept> contextConcepts = beliefModelService.listContextConcepts();
        LOG.info("Requesting context concepts -> {}", contextConcepts);
        return contextConcepts;
    }


    @RequestMapping(value = SERVLET_CONTEXT, method = GET)
    @Produces(MediaType.APPLICATION_JSON)
    public Concept getCurrentContext() {
        Concept context = beliefModelService.getContext().orElse(null);
        LOG.info("Requesting current context: {}", context);
        return context;
    }

    @RequestMapping(value = SERVLET_CONTEXT, method = PUT)
    @Consumes(MediaType.TEXT_PLAIN)
    public void setCurrentContext(@RequestBody String context) {
        LOG.info("Setting current context: {}", context);
        beliefModelService.setContext(context);
    }

    @RequestMapping(value = SERVLET_BELIEF_SYSTEM + "/test", method = POST)
    public void testImportBeliefSystem(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        LOG.info("Testing import of belief system");
        importService.importZipFile(multipartFile.getInputStream(), false);
    }

    @RequestMapping(value = SERVLET_BELIEF_SYSTEM, method = POST)
    public void importBeliefSystem(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        LOG.info("Importing belief system");
        importService.importZipFile(multipartFile.getInputStream(), true);
    }

    @RequestMapping(value = SERVLET_BELIEF_SYSTEM, method = DELETE)
    public void deleteBeliefSystem() {
        LOG.warn("Erasing entire belief system");
        beliefModelService.eraseAll();
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException exception) {
        LOG.error("IO exception", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException exception) {
        LOG.warn("Not found: {}", String.valueOf(exception));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException exception) {
        LOG.warn("Bad request: {}", String.valueOf(exception));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
