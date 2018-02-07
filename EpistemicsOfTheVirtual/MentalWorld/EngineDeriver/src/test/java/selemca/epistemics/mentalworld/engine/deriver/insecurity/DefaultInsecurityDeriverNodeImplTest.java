/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.insecurity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.engine.category.CategoryMatch;
import selemca.epistemics.mentalworld.engine.deriver.common.AbstractDeriverNodeTest;
import selemca.epistemics.mentalworld.engine.deriver.common.MockCategoryMatchBuilder;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultInsecurityDeriverNodeImplTest extends AbstractDeriverNodeTest {
    final String DIRECT_MODIFICATION = "engine.insecurityDirectAssociationModificationPercentage";
    final String CONVERSE_TO = "engine.insecurityConverseToTarget";

    @Mock
    AssociationRepository associationRepository;

    DefaultInsecurityDeriverNodeImpl classUnderTest;


    @Before
    public void init() {
        CategoryMatch categoryMatch = new MockCategoryMatchBuilder()
                .withConcept("duck")
                .addContributor("swimming", 0.0)
                .build();
        initCategoryMatch(categoryMatch);
        initBeliefSystem();

        applicationSettings.setProperty(DIRECT_MODIFICATION, 20);
        applicationSettings.setProperty(CONVERSE_TO, 0.5);

    }

    @Test
    public void testInsecurity() {
        //  Truthvalue for contribution is taken from sample belief system: 0.9

        classUnderTest = new DefaultInsecurityDeriverNodeImpl(workingMemory, logger, sampleBeliefSystem.asConceptRepository(), associationRepository, applicationSettings);
        classUnderTest.insecurity();

        ArgumentCaptor<Association> argument = ArgumentCaptor.forClass(Association.class);
        verify(associationRepository).save(argument.capture());
        Association savedAssociation = argument.getValue();
        Assert.assertTrue(savedAssociation.getTruthValue() < 0.9);
    }

    @Test
    public void testInsecuritySpecific() {

        classUnderTest = new DefaultInsecurityDeriverNodeImpl(workingMemory, logger, sampleBeliefSystem.asConceptRepository(), associationRepository, applicationSettings);
        Concept bird = new Concept("bird", 0.8);
        Concept eggs = new Concept("eggs", 0.8);
        classUnderTest.insecurity(new Association(bird, eggs, 0.0)); // Truthvalue taken from sample belief system: 0.8

        ArgumentCaptor<Association> argument = ArgumentCaptor.forClass(Association.class);
        verify(associationRepository).save(argument.capture());
        Association savedAssociation = argument.getValue();
        Assert.assertTrue(savedAssociation.getTruthValue() < 0.8);
    }
}
