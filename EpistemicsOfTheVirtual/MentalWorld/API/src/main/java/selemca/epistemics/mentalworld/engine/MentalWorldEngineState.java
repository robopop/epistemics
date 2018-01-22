package selemca.epistemics.mentalworld.engine;

import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

public interface MentalWorldEngineState {
    WorkingMemory getWorkingMemory();
    void acceptObservation();
    boolean isObservationAccepted();
    MentalWorldEngine.Logger getLogger();
}
