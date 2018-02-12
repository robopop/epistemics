package selemca.epistemics.mentalworld.engine.deriver.declarecontext;

import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.node.DeclareContextDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

import static selemca.epistemics.mentalworld.engine.workingmemory.AttributeKind.NEW_CONTEXT;

public class DefaultDeclareContextDeriverNodeImpl implements DeclareContextDeriverNode {
    private final BeliefModelService beliefModelService;
    private final WorkingMemory workingMemory;
    private final MentalWorldEngine.Logger logger;

    public DefaultDeclareContextDeriverNodeImpl(BeliefModelService beliefModelService, WorkingMemory workingMemory, MentalWorldEngine.Logger logger) {
        this.beliefModelService = beliefModelService;
        this.workingMemory = workingMemory;
        this.logger = logger;
    }

    @Override
    public void apply() {
        logger.info("Declare context");
        Concept context = workingMemory.get(NEW_CONTEXT);
        logger.info("New context: " + context);
        beliefModelService.setContext(context.getName());
    }
}
