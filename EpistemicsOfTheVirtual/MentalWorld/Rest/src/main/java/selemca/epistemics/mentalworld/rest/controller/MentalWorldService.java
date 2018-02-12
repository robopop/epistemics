package selemca.epistemics.mentalworld.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.rest.util.LoggerImpl;

import java.util.UUID;

@Component("mentalWorldService")
public class MentalWorldService {

    @Autowired
    private MentalWorldEngine mentalWorldEngine;

    public String createAppraisal(AppraisalsMap appraisals) {
        String appraisalId = UUID.randomUUID().toString();
        MentalWorldEngine.Logger logger = new LoggerImpl();
        appraisals.put(appraisalId, mentalWorldEngine.createState(logger));
        return appraisalId;
    }
}
