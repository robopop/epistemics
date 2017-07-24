/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.accept.Engine;
import selemca.epistemics.mentalworld.engine.accept.Request;

import java.util.HashSet;
import java.util.Set;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(MentalWorldRestController.URL_PREFIX)
public class MentalWorldRestController {
    private static final MentalWorldEngine.Logger LOGGER = new EngineLogger();
    protected static final String URL_PREFIX = "/epistemics";
    protected static final String ACCEPT_OBSERVATION = "/accept-observation";

    @Autowired
    private MentalWorldEngine mentalWorldEngine;

    /*
     * CONCEPT
     */
    @RequestMapping(value = ACCEPT_OBSERVATION, method = POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    public boolean acceptObservation(@RequestBody Request request) {
        Set<String> observationFeatures = new HashSet<>(request.getFeatureList());
        Engine engineSettings = request.getEngineSettings();
        return mentalWorldEngine.acceptObservation(observationFeatures, engineSettings, LOGGER);
    }
}
