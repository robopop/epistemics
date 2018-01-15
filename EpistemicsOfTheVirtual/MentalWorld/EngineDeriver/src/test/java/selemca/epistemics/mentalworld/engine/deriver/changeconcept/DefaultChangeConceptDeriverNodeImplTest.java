/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.changeconcept;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.service.BeliefModelService;
import selemca.epistemics.mentalworld.engine.deriver.common.AbstractDeriverNodeTest;

import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultChangeConceptDeriverNodeImplTest extends AbstractDeriverNodeTest {
    final String NEW_TRUTHVALUE = "engine.changeconcept.newAssociationTruthValue";

    DefaultChangeConceptDeriverNodeImpl classUnderTest;


    @Mock
    BeliefModelService beliefModelService;

    @Mock
    AssociationRepository associationRepository;


    @Before
    public void init() {
        initRealityCheck();
    }

    @Test
    public void testChangeConceptExistingHigher() {
        Concept concept1 = new Concept("concept1", 0.8);
        Concept concept2 = new Concept("concept2", 0.8);
        Association existingAssociation = new Association(concept1, concept2, 0.8);

        when(beliefModelService.getAssociation(concept1, concept2)).thenReturn(Optional.of(existingAssociation));

        classUnderTest = new DefaultChangeConceptDeriverNodeImpl(workingMemory, logger, associationRepository, beliefModelService, applicationSettings);
        Association newAssociation = new Association(concept1, concept2, 0.7);
        classUnderTest.changeConcept(newAssociation, false);
        verify(associationRepository, never()).save(any());
    }

    @Test
    public void testChangeConceptExistingLower() {
        Concept concept1 = new Concept("concept1", 0.8);
        Concept concept2 = new Concept("concept2", 0.8);
        Association existingAssociation = new Association(concept1, concept2, 0.4);

        when(beliefModelService.getAssociation(concept1, concept2)).thenReturn(Optional.of(existingAssociation));

        classUnderTest = new DefaultChangeConceptDeriverNodeImpl(workingMemory, logger, associationRepository, beliefModelService, applicationSettings);
        Association newAssociation = new Association(concept1, concept2, 0.7);
        classUnderTest.changeConcept(newAssociation, false);
        verify(beliefModelService).fullSave(eq(newAssociation));
    }

    @Test
    public void testChangeConceptNew() {
        Concept concept1 = new Concept("concept1", 0.8);
        Concept concept2 = new Concept("concept2", 0.8);

        when(beliefModelService.getAssociation(concept1, concept2)).thenReturn(Optional.empty());

        classUnderTest = new DefaultChangeConceptDeriverNodeImpl(workingMemory, logger, associationRepository, beliefModelService, applicationSettings);
        Association newAssociation = new Association(concept1, concept2, 0.7);
        classUnderTest.changeConcept(newAssociation, false);
        verify(beliefModelService).fullSave(eq(newAssociation));
    }

}
