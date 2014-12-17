package org.jgll.util.visualization;

import org.jgll.grammar.GrammarSlotRegistry;
import org.jgll.sppf.DummyNode;
import org.jgll.sppf.IntermediateNode;
import org.jgll.sppf.NonterminalNode;
import org.jgll.sppf.PackedNode;
import org.jgll.sppf.SPPFNode;
import org.jgll.sppf.TerminalNode;

public abstract class ToDot {
	
	private GrammarSlotRegistry registry;
	
	public ToDot(GrammarSlotRegistry registry) {
		this.registry = registry;
	}
	
	protected String getId(SPPFNode node) {
		
		if(node instanceof NonterminalNode) {
			return "nontermianl_" + getNodeId(node);
		}
		else if (node instanceof IntermediateNode) {
			return "intermediate_" + getNodeId(node);
		}
		else if(node instanceof PackedNode) {
			SPPFNode parent = ((PackedNode) node).getParent();
			if(parent instanceof NonterminalNode) {
				return "packed_n_" + getNodeId(parent) + "_" + getNodeId((PackedNode)node);				
			} else {
				return "packed_i_" + getNodeId(parent) + "_" + getNodeId((PackedNode)node);
			}
		}
		else if(node instanceof DummyNode) {
			return "-1";
		} 
		else if(node instanceof TerminalNode) {
			return "token_" + getNodeId(node);
		}
		throw new RuntimeException("Node of type " +  node.getClass() + " could not be matched.");
	}
	
	protected String getNodeId(SPPFNode node) {
		return registry.getId(node.getGrammarSlot()) + "_" + node.getLeftExtent() + "_" + node.getRightExtent();
	}
	
	protected String getNodeId(PackedNode node) {
		return getNodeId(node.getParent()) + "_" + registry.getId(node.getGrammarSlot()) + "_" + node.getPivot();
	}
	
}
