package org.jgll.sppf;

import java.util.List;

import org.jgll.grammar.slot.GrammarSlot;
import org.jgll.parser.HashFunctions;


/**
 * 
 * A NonPackedNode is the abstract super class for nonterminal 
 * and intermediate symbol nodes.
 * 
 * 
 * @author Ali Afroozeh
 * 
 */

public abstract class NonPackedNode implements SPPFNode {
	
	protected final GrammarSlot slot;
	
	protected final int leftExtent;
	
	protected final int rightExtent;
	
	public NonPackedNode(GrammarSlot slot, int leftExtent, int rightExtent) {
		this.slot = slot;
		this.leftExtent = leftExtent;
		this.rightExtent = rightExtent;
	}

	public int getLeftExtent() {
		return leftExtent;
	}
	
	public int getRightExtent() {
		return rightExtent;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) 
			return true;
		
		if (!(obj instanceof NonPackedNode)) 
			return false;
		
		NonPackedNode other = (NonPackedNode) obj;
		
		return slot == other.slot && 
			   leftExtent == other.leftExtent && 
			   rightExtent == other.rightExtent;
	}

	@Override
	public int hashCode() {
		return HashFunctions.defaulFunction.hash(slot.getId(), leftExtent, rightExtent);
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %d, %d)", slot, leftExtent, rightExtent);
	}
	
	public abstract List<PackedNode> getChildren();
	
//    public static ExternalHashEquals<NonPackedNode> globalHashEquals(HashFunction f) {
//    	
//    	return new ExternalHashEquals<NonPackedNode>() {
//
//			@Override
//			public int hash(NonPackedNode n) {
//				return f.hash(n.slot.getId(), n.leftExtent, n.rightExtent);
//			}
//
//			@Override
//			public boolean equals(NonPackedNode n1, NonPackedNode n2) {
//				return  n1.slot == n2.slot && 
//                        n1.leftExtent  == n2.leftExtent && 
//                        n1.rightExtent == n2.rightExtent;
//			}
//		};
//    }
//    
//    public static ExternalHashEquals<NonPackedNode> distributedHashEquals(HashFunction f) {
//    	
//    	return new ExternalHashEquals<NonPackedNode>() {
//
//			@Override
//			public int hash(NonPackedNode n) {
//				return f.hash(n.getLeftExtent(), n.getRightExtent());
//			}
//
//			@Override
//			public boolean equals(NonPackedNode n1, NonPackedNode n2) {
//				return  n1.getLeftExtent()  == n2.getLeftExtent() && 
//                        n1.getRightExtent() == n2.getRightExtent();
//			}
//		};
//    }    
	
}
