/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.common;

import selemca.epistemics.data.entity.Association;
import selemca.epistemics.data.entity.AssociationMeta;
import selemca.epistemics.data.entity.AssociationPK;
import selemca.epistemics.data.entity.Concept;
import selemca.epistemics.mentalworld.beliefsystem.repository.AssociationRepository;
import selemca.epistemics.mentalworld.beliefsystem.repository.BeliefModelService;
import selemca.epistemics.mentalworld.beliefsystem.repository.ConceptRepository;

import java.util.*;

/**
 * Some sample data for unit tests
 */
public class SampleBeliefSystem {
    private final List<Concept> concepts = new ArrayList<>();
    private final List<Association> associations = new ArrayList<>();
    private final List<Concept> contexts = new ArrayList<>();
    private Concept currentContext;

    public SampleBeliefSystem() {
        Concept twoLegs = new Concept("2 legs", 0.8);
        concepts.add(twoLegs);
        Concept fourLegs = new Concept("4 legs", 0.8);
        concepts.add(fourLegs);
        Concept animal = new Concept("animal", 0.8);
        concepts.add(animal);
        Concept beak = new Concept("beak", 0.8);
        concepts.add(beak);
        Concept beaver = new Concept("beaver", 0.8);
        concepts.add(beaver);
        Concept bird = new Concept("bird", 0.8);
        concepts.add(bird);
        Concept breast_feeding = new Concept("breast feeding", 0.8);
        concepts.add(breast_feeding);
        Concept circus = new Concept("circus", 0.8);
        concepts.add(circus);
        Concept clown = new Concept("clown", 0.8);
        concepts.add(clown);
        Concept colorful_wig = new Concept("colorful wig", 0.8);
        concepts.add(colorful_wig);
        Concept duck = new Concept("duck", 0.8);
        concepts.add(duck);
        Concept eggs = new Concept("eggs", 0.8);
        concepts.add(eggs);
        Concept flat_tail = new Concept("flat tail", 0.8);
        concepts.add(flat_tail);
        Concept feathers = new Concept("feathers", 0.8);
        concepts.add(feathers);
        Concept flying = new Concept("flying", 0.8);
        concepts.add(flying);
        Concept fur = new Concept("fur", 0.8);
        concepts.add(fur);
        Concept grime = new Concept("grime", 0.8);
        concepts.add(grime);
        Concept lapwing = new Concept("lapwing", 0.8);
        concepts.add(lapwing);
        Concept legs = new Concept("legs", 0.8);
        concepts.add(legs);
        Concept mammal = new Concept("mammal", 0.8);
        concepts.add(mammal);
        Concept man = new Concept("man", 0.8);
        concepts.add(man);
        Concept metro = new Concept("metro", 0.8);
        concepts.add(metro);
        Concept nature = new Concept("nature", 0.8);
        concepts.add(nature);
        Concept person = new Concept("person", 0.8);
        concepts.add(person);
        Concept red_nose = new Concept("red nose", 0.8);
        concepts.add(red_nose);
        Concept swimming = new Concept("swimming", 0.8);
        concepts.add(swimming);
        Concept walking = new Concept("walking", 0.8);
        concepts.add(walking);
        Concept webbed_toes = new Concept("webbed toes", 0.8);
        concepts.add(webbed_toes);
        Concept wig = new Concept("wig", 0.8);
        concepts.add(wig);
        Concept wings = new Concept("wings", 0.8);
        concepts.add(wings);

        associations.add(new Association(twoLegs, bird, 0.7));
        associations.add(new Association(twoLegs, legs, 0.7));
        associations.add(new Association(fourLegs, legs, 0.7));
        associations.add(new Association(fourLegs, mammal, 0.8));
        associations.add(new Association(animal, bird, 0.7));
        associations.add(new Association(animal, mammal, 0.6));
        associations.add(new Association(animal, nature, 0.8));
        associations.add(new Association(beak, bird, 0.8));
        associations.add(new Association(beaver, flat_tail, 0.8));
        associations.add(new Association(beak, mammal, 0.1));
        associations.add(new Association(bird, duck, 0.7));
        associations.add(new Association(bird, eggs, 0.8));
        associations.add(new Association(bird, flying, 0.9));
        associations.add(new Association(bird, fur, 0.1));
        associations.add(new Association(bird, lapwing, 0.7));
        associations.add(new Association(bird, mammal, 0.1));
        associations.add(new Association(bird, walking, 0.56));
        associations.add(new Association(bird, webbed_toes, 0.75));
        associations.add(new Association(bird, wings, 0.8));
        associations.add(new Association(breast_feeding, mammal, 0.8));
        associations.add(new Association(circus, clown, 0.85));
        associations.add(new Association(clown, colorful_wig, 0.8));
        associations.add(new Association(clown, grime, 0.8));
        associations.add(new Association(clown, person, 0.7));
        associations.add(new Association(clown, red_nose, 0.8));
        associations.add(new Association(duck, swimming, 0.9));
        associations.add(new Association(flying, wings, 0.85));
        associations.add(new Association(fur, mammal, 0.8));
        associations.add(new Association(mammal, person, 0.6));
        associations.add(new Association(mammal, swimming, 0.53));
        associations.add(new Association(mammal, webbed_toes, 0.1));
        associations.add(new Association(man, person, 0.7));
        associations.add(new Association(metro, person, 0.75));

        contexts.add(circus);
        contexts.add(metro);
        contexts.add(nature);
        currentContext = nature;
    }

    public ConceptRepository asConceptRepository() {
        return new AsConceptRepository();
    }

    public AssociationRepository asAssociationRepository() {
        return new AsAssociationRepository();
    }

    public BeliefModelService asBeliefModelService() {
        return new AsBeliefModelService();
    }

    public class AsConceptRepository implements ConceptRepository {
        @Override
        public void delete(Concept deleted) {
        }

        @Override
        public void deleteAll() {
        }

        @Override
        public List<Concept> findAll() {
            return concepts;
        }

        @Override
        public Optional<Concept> findOne(String s) {
            Concept result = null;
            for (Concept concept : concepts) {
                if (concept.getName().equals(s)) {
                    result = concept;
                }
            }
            return Optional.ofNullable(result);
        }

        @Override
        public Concept save(Concept persisted) {
            return null;
        }
    }

    public class AsAssociationRepository implements AssociationRepository {
        @Override
        public List<Association> findByConcept1(Concept concept) {
            List<Association> result = new ArrayList<>();
            for (Association association : associations) {
                if (association.getConcept1().equals(concept)) {
                    result.add(association);
                }
            }
            return result;
        }

        @Override
        public List<Association> findByConcept2(Concept concept) {
            List<Association> result = new ArrayList<>();
            for (Association association : associations) {
                if (association.getConcept2().equals(concept)) {
                    result.add(association);
                }
            }
            return result;
        }

        @Override
        public Optional<Association> findByConcept1AndConcept2(Concept concept1, Concept concept2) {
            Association result = null;
            for (Association association : associations) {
                if (association.getConcept1().equals(concept1) && association.getConcept2().equals(concept2)) {
                    result = association;
                }
            }
            return Optional.ofNullable(result);
        }

        @Override
        public void delete(Association deleted) {
        }

        @Override
        public void deleteAll() {
        }

        @Override
        public List<Association> findAll() {
            return associations;
        }

        @Override
        public Optional<Association> findOne(AssociationPK associationPK) {
            Association result = null;
            for (Association association : associations) {
                if (association.getConcept1().getName().equals(associationPK.getConcept1()) && association.getConcept2().getName().equals(associationPK)) {
                    result = association;
                }
            }
            return Optional.ofNullable(result);
        }

        @Override
        public Association save(Association persisted) {
            return persisted;
        }
    }

    public class AsBeliefModelService implements BeliefModelService {
        @Override
        public void cascadingDelete(Concept concept) {

        }

        @Override
        public Set<Association> listAssociations(Concept concept) {
            Set<Association> result = new HashSet<>();
            for (Association association : associations) {
                if (association.getConcept1().equals(concept) || association.getConcept2().equals(concept)) {
                    result.add(association);
                }
            }
            return result;
        }

        @Override
        public Optional<Association> getAssociation(Concept concept1, Concept concept2) {
            Association result = null;
            for (Association association : associations) {
                if (association.getConcept1().equals(concept1) && association.getConcept2().equals(concept2)) {
                    result = association;
                }
            }
            return Optional.ofNullable(result);
        }

        @Override
        public void fullSave(Association association) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<AssociationMeta> getAssociationMeta(Concept concept1, Concept concept2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<String> getAssociationType(Concept concept1, Concept concept2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAssociationType(Concept concept1, Concept concept2, String relationType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setContext(String context) {
            for (Concept concept : contexts) {
                if (concept.getName().equals(context)) {
                    currentContext = concept;
                }
            }
        }

        @Override
        public boolean isContextConcept(Concept concept) {
            return Arrays.asList(new String[]{"circus", "metro", "nature"}).contains(concept.getName());
        }

        @Override
        public void setConceptContextState(Concept concept, boolean isContext) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Concept> getContext() {
            return Optional.ofNullable(currentContext);
        }

        @Override
        public Set<Concept> listContextConcepts() {
            return new HashSet<>(contexts);
        }

        @Override
        public void eraseAll() {
            throw new UnsupportedOperationException();
        }
    }
}
