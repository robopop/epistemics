package selemca.epistemics.mentalworld.webapp.wordnet;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

@Component("wordnetAccess")
public class WordnetAccessRDFImpl implements WordnetAccess {
    private static final String WORDNET_FILE = "wordnet_2_0.rdf";
    private static final String WORD_PREFIX = "word-";

    @Autowired
    private File selemcaHome;

    private List<Resource> wordResources = null;
    private SortedSet<String> words = null;

    @Override
    public synchronized List<Resource> listWordResources() {
        if (wordResources == null) {
            readWordResources();
        }
        return wordResources;
    }

    private void readWordResources() {
        List<Resource> wordResources = new ArrayList<>();

        Model wordnetModel = readModel();
        for (ResIterator resources = wordnetModel.listSubjects(); resources.hasNext(); ) {

            Resource resource = resources.nextResource();
            String localName = resource.getLocalName();
            if (localName.startsWith(WORD_PREFIX)) {
                wordResources.add(resource);
            }
        }
        this.wordResources = Collections.unmodifiableList(wordResources);
    }

    private Model readModel() {
        Model model = ModelFactory.createDefaultModel();

        File wordnetFile = new File(selemcaHome, WORDNET_FILE);
        if (wordnetFile.canRead()) {
            model.read(wordnetFile.getAbsolutePath());
        } else {
            Logger.getLogger(getClass().getSimpleName()).warning("Wordnet file not found at location: " + wordnetFile);
        }

        return model;
    }

    @Override
    public synchronized SortedSet<String> listWords() {
        if (words == null) {
            buildWordSet();
        }

        return words;
    }

    private void buildWordSet() {
        SortedSet<String> words = new TreeSet<>();
        for (Resource resource : listWordResources()) {
            String localName = resource.getLocalName();
            words.add(localName.substring(WORD_PREFIX.length()));
        }
        this.words = Collections.unmodifiableSortedSet(words);
    }
}
