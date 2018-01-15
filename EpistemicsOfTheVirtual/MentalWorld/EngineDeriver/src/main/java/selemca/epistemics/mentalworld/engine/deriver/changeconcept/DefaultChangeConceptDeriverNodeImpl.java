/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.changeconcept;

import org.apache.commons.configuration.Configuration;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.node.ChangeConceptDeriverNode;
import selemca.epistemics.mentalworld.engine.workingmemory.WorkingMemory;

import java.util.Optional;

import static selemca.epistemics.mentalworld.engine.deriver.changeconcept.ChangeConceptDeriverNodeSettingsProvider.NEW_ASSOCIATION_TRUTH_VALUE;

public class DefaultChangeConceptDeriverNodeImpl implements ChangeConceptDeriverNode {
    final double NEW_ASSOSIATION_TRUTH_VALUE_DEFAULT = 0.5;

    private final WorkingMemory workingMemory;
    private final MentalWorldEngine.Logger logger;
    private final double newAssociationTruthValue;
    private final AssociationRepository associationRepository;
    private final BeliefModelService beliefModelService;

    public DefaultChangeConceptDeriverNodeImpl(WorkingMemory workingMemory, MentalWorldEngine.Logger logger, AssociationRepository associationRepository, BeliefModelService beliefModelService, Configuration applicationSettings) {
        this.workingMemory = workingMemory;
        this.logger = logger;
        this.associationRepository = associationRepository;
        this.beliefModelService = beliefModelService;
        newAssociationTruthValue = applicationSettings.getDouble(NEW_ASSOCIATION_TRUTH_VALUE, NEW_ASSOSIATION_TRUTH_VALUE_DEFAULT);
    }

    @Override
    public void changeConcept(Association association, boolean isMetaphor) {
        if (getTruthValue(association) < newAssociationTruthValue) {
            logger.debug(String.format("Changed relation %s to %s", association, newAssociationTruthValue));
            beliefModelService.fullSave(association);
        }
        String relationType = isMetaphor ? "f" : "l";
        beliefModelService.setAssociationType(association.getConcept1(), association.getConcept2(),relationType);
    }

    private double getTruthValue(Association association) {
        Optional<Association> existingAssociationOptional = beliefModelService.getAssociation(association.getConcept1(), association.getConcept2());
        return existingAssociationOptional.isPresent() ? existingAssociationOptional.get().getTruthValue() : 0;
    }
}
