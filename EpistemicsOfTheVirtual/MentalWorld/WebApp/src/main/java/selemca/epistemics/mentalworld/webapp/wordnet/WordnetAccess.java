package selemca.epistemics.mentalworld.webapp.wordnet;

import com.hp.hpl.jena.rdf.model.Resource;

import java.util.List;
import java.util.SortedSet;

public interface WordnetAccess {
    List<Resource> listWordResources();

    SortedSet<String> listWords();
}
