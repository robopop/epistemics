/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.node;

/**
 * Epistemics of the virtual node: right context
 * Checks whether found match is whithin context and proposes better context.
 */
public interface ContextMatchDeriverNode extends DecisionNode {
    /**
     * Checks wether the found match is whithin context. If so returns true. If not finds better context
     * for match, stores it in working memory and returns false.
     */
    boolean contextMatch();
}
