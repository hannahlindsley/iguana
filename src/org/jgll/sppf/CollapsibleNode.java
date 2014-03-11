package org.jgll.sppf;



/**
 * 
 * A collapsible node is a nonterminal symbol node that will be replaced
 * by its children after the parse tree is built. This node can be used,
 * for example, to represent nonterminals as part of the left-factorization
 * process. 
 * 
 * @author Ali Afroozeh
 *
 */
public class CollapsibleNode extends NonterminalSymbolNode {

	public CollapsibleNode(int nonterminalId, int numberOfAlternatives, int leftExtent, int rightExtent) {
		super(nonterminalId, numberOfAlternatives, leftExtent, rightExtent);
	}

}
