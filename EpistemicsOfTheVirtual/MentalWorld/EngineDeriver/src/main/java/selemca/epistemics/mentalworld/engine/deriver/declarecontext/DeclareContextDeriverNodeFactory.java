package selemca.epistemics.mentalworld.engine.deriver.declarecontext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.factory.DeriverNodeFactory;
import selemca.epistemics.mentalworld.engine.node.DeclareContextDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

@Component
public class DeclareContextDeriverNodeFactory implements DeriverNodeFactory<DeclareContextDeriverNode> {
    private static final String CONFIGURATION_NAME = "declareContextDeriver.default";

    @Autowired
    private BeliefModelService beliefModelService;

    @Override
    public Class<DeclareContextDeriverNode> getDeriverNodeClass() {
        return DeclareContextDeriverNode.class;
    }

    @Override
    public String getName() {
        return CONFIGURATION_NAME;
    }

    @Override
    public DeclareContextDeriverNode createDeriverNode(WorkingMemory workingMemory, MentalWorldEngine.Logger logger) {
        return new DefaultDeclareContextDeriverNodeImpl(beliefModelService, workingMemory, logger);
    }
}
