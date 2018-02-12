package selemca.epistemics.mentalworld.engine;

import selemca.epistemics.mentalworld.engine.node.DeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

import java.util.Optional;

public interface MentalWorldEngineState {
    WorkingMemory getWorkingMemory();
    <D extends DeriverNode> Optional<D> getDeriverNode(Class<D> deliverNodeClass);
    void acceptObservation();
    boolean isObservationAccepted();
    MentalWorldEngine.Logger getLogger();
}
