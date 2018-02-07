/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.rest.controller;

import org.apache.commons.collections15.IteratorUtils;
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
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.MentalWorldEngineState;
import selemca.epistemics.mentalworld.engine.accept.Engine;
import selemca.epistemics.mentalworld.engine.accept.Request;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;
import selemca.epistemics.mentalworld.rest.util.LoggerImpl;

import javax.servlet.http.HttpSession;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.OBSERVATION_FEATURES;

@RestController
@RequestMapping(MentalWorldRestController.PATH_PREFIX)
public class MentalWorldRestController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final MentalWorldEngine.Logger LOGGER = new EngineLogger();
    protected static final String APPRAISALS_ATTRIBUTE = "APPRAISALS";
    protected static final String PATH_PREFIX = "/epistemics";
    protected static final String PATH_ACCEPT_OBSERVATION = "/accept-observation";
    protected static final String PATH_APPRAISAL = "/appraisal";
    protected static final String PATH_ID_NAME = "id";
    protected static final String PATH_ID_PART = "/{" + PATH_ID_NAME + "}";
    protected static final String CONCEPT_ID_PARAM = "conceptId";
    protected static final String PATH_ENGINE_SETTINGS = "/engine-settings";
    protected static final String PATH_OBSERVATION_FEATURES = "/observation-features";
    protected static final String PATH_NEW_CONTEXT = "/new-context";
    protected static final String PATH_LOG_MESSAGES = "/log-messages";

    @Autowired
    private MentalWorldEngine mentalWorldEngine;

    @Autowired
    private ConceptRepository conceptRepository;

    /*
     * CONCEPT
     */
    @RequestMapping(value = PATH_ACCEPT_OBSERVATION, method = POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    public boolean acceptObservation(@RequestBody Request request) {
        Set<String> observationFeatures = new HashSet<>(request.getFeatureList());
        Engine engineSettings = request.getEngineSettings();
        return mentalWorldEngine.acceptObservation(observationFeatures, engineSettings, LOGGER);
    }

    @RequestMapping(value = PATH_APPRAISAL, method = POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    public ResponseEntity<Void> createAppraisal(HttpSession httpSession, UriComponentsBuilder uriComponentsBuilder) {
        AppraisalsMap appraisals = getAppraisalsMap(httpSession);
        String appraisalId = UUID.randomUUID().toString();
        MentalWorldEngine.Logger logger = new LoggerImpl();
        appraisals.put(appraisalId, mentalWorldEngine.createState(logger));
        UriComponents uriComponents = uriComponentsBuilder.path(PATH_PREFIX + PATH_APPRAISAL + "/{id}").buildAndExpand(appraisalId);
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    /*  {
          "believeDeviation": {
            "criterion": 0.35
          },
          "changeConcept": {
            "newAssociationTruthValue": 0.5
          },
          "contextAssociationMaximumDistance": 1.14,
          "epistemicAppraisal": {
            "criterion": 0.3
          },
          "fiction": {
            "cutoff": 2,
            "deviation": 0.15,
            "mean": 0.25
          },
          "insecurity": {
            "converseToTarget": 0.45,
            "directAssociationModificationPercentage": 21
          },
          "integratorDeviation": {
            "criterion": 0.5
          },
          "maximumNumberOfTraversals": 2,
          "metaphor": {
            "intersectionMinimumSize": {
              "absolute": 2,
              "relative": 10
            },
            "intersectionMinimumSizeMixed": {
              "absolute": 2,
              "relative": 10
            },
            "vicinity": 0.5
          },
          "reality": {
            "cutoff": 2,
            "deviation": 0.15,
            "mean": 0.75
          },
          "reassurance": {
            "directAssociationModificationPercentage": 20,
            "indirectAssociationsModificationPercentage": 5
          }
        }
     */
    @RequestMapping(value = PATH_APPRAISAL + PATH_ID_PART + PATH_ENGINE_SETTINGS, method = POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void setEngineSettings(@PathVariable(PATH_ID_NAME) String appraisalId, @RequestBody Engine engineSettings, HttpSession httpSession) {
        MentalWorldEngineState engineState = getEngineState(appraisalId, httpSession);
        WorkingMemory workingMemory = engineState.getWorkingMemory();
        workingMemory.setEngineSettings(engineSettings);
    }

    @RequestMapping(value = PATH_APPRAISAL + PATH_ID_PART + PATH_ENGINE_SETTINGS, method = GET, produces = APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Engine getEngineSettings(@PathVariable(PATH_ID_NAME) String appraisalId, HttpSession httpSession) {
        MentalWorldEngineState engineState = getEngineState(appraisalId, httpSession);
        WorkingMemory workingMemory = engineState.getWorkingMemory();
        return workingMemory.getEngineSettings();
    }

    @RequestMapping(value = PATH_APPRAISAL + PATH_ID_PART + PATH_OBSERVATION_FEATURES, method = POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public void setObservationFeatures(@PathVariable(PATH_ID_NAME) String appraisalId, @RequestBody Set<String> observationFeatures, HttpSession httpSession) {
        MentalWorldEngineState engineState = getEngineState(appraisalId, httpSession);
        WorkingMemory workingMemory = engineState.getWorkingMemory();
        workingMemory.set(OBSERVATION_FEATURES, observationFeatures);
    }

    @RequestMapping(value = PATH_APPRAISAL + PATH_ID_PART + PATH_OBSERVATION_FEATURES, method = GET, produces = APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Iterable<String> getObservationFeatures(@PathVariable(PATH_ID_NAME) String appraisalId, HttpSession httpSession) {
        MentalWorldEngineState engineState = getEngineState(appraisalId, httpSession);
        WorkingMemory workingMemory = engineState.getWorkingMemory();
        return workingMemory.getAll(OBSERVATION_FEATURES);
    }

    @RequestMapping(value = PATH_APPRAISAL + PATH_ID_PART + PATH_NEW_CONTEXT, method = POST)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void setNewContext(@PathVariable(PATH_ID_NAME) String appraisalId, @RequestParam(CONCEPT_ID_PARAM) String conceptId, HttpSession httpSession) {
        MentalWorldEngineState engineState = getEngineState(appraisalId, httpSession);
        WorkingMemory workingMemory = engineState.getWorkingMemory();
        Concept concept = conceptRepository.findOne(conceptId).orElseThrow(() -> new NotFoundException(format("Concept: %s", conceptId)));
        workingMemory.setNewContext(concept);
    }

    @RequestMapping(value = PATH_APPRAISAL + PATH_ID_PART + PATH_NEW_CONTEXT, method = GET, produces = APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Concept getNewContext(@PathVariable(PATH_ID_NAME) String appraisalId, HttpSession httpSession) {
        MentalWorldEngineState engineState = getEngineState(appraisalId, httpSession);
        WorkingMemory workingMemory = engineState.getWorkingMemory();
        return workingMemory.getNewContext();
    }

    @RequestMapping(value = PATH_APPRAISAL + PATH_ID_PART + PATH_ACCEPT_OBSERVATION, method = POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    public boolean acceptObservation(@PathVariable(PATH_ID_NAME) String appraisalId, HttpSession httpSession) {
        MentalWorldEngineState engineState = getEngineState(appraisalId, httpSession);
        if (engineState == null) {
            throw new NotFoundException(format("Engine state: %s", appraisalId));
        }
        engineState.acceptObservation();
        return engineState.isObservationAccepted();
    }

    @RequestMapping(value = PATH_APPRAISAL + PATH_ID_PART + PATH_LOG_MESSAGES, method = GET, produces = APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<?> getLogMessages(@PathVariable(PATH_ID_NAME) String appraisalId, HttpSession httpSession) {
        MentalWorldEngineState engineState = getEngineState(appraisalId, httpSession);
        LoggerImpl logger = (LoggerImpl) engineState.getLogger();
        return IteratorUtils.toList(logger.iterator());
    }

    protected MentalWorldEngineState getEngineState(String appraisalId, HttpSession httpSession) {
        AppraisalsMap appraisals = getAppraisalsMap(httpSession);
        MentalWorldEngineState engineState = appraisals.get(appraisalId);
        if (engineState == null) {
            throw new NotFoundException(format("Engine state: %s", appraisalId));
        }
        return engineState;
    }

    protected AppraisalsMap getAppraisalsMap(HttpSession httpSession) {
        AppraisalsMap appraisals = (AppraisalsMap) httpSession.getAttribute(APPRAISALS_ATTRIBUTE);
        if (appraisals == null) {
            appraisals = new AppraisalsMap();
            httpSession.setAttribute(APPRAISALS_ATTRIBUTE, appraisals);
        }
        return appraisals;
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
