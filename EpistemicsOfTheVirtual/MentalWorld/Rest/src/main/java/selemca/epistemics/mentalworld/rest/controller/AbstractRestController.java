package selemca.epistemics.mentalworld.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import selemca.epistemics.mentalworld.engine.MentalWorldEngineState;

import javax.servlet.http.HttpSession;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import java.lang.invoke.MethodHandles;

import static java.lang.String.format;

public class AbstractRestController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    protected static final String APPRAISALS_ATTRIBUTE = "APPRAISALS";
    protected static final String PATH_ID_NAME = "id";
    protected static final String PATH_ID_PART = "/{" + PATH_ID_NAME + "}";

    protected AppraisalsMap getAppraisalsMap(HttpSession httpSession) {
        AppraisalsMap appraisals = (AppraisalsMap) httpSession.getAttribute(APPRAISALS_ATTRIBUTE);
        if (appraisals == null) {
            appraisals = new AppraisalsMap();
            httpSession.setAttribute(APPRAISALS_ATTRIBUTE, appraisals);
        }
        return appraisals;
    }

    protected MentalWorldEngineState getEngineState(String appraisalId, HttpSession httpSession) {
        AppraisalsMap appraisals = getAppraisalsMap(httpSession);
        MentalWorldEngineState engineState = appraisals.get(appraisalId);
        if (engineState == null) {
            throw new NotFoundException(format("Engine state: %s", appraisalId));
        }
        return engineState;
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
