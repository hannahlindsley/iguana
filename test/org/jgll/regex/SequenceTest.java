package org.jgll.regex;

import static org.junit.Assert.*;

import org.jgll.grammar.condition.RegularExpressionCondition;
import org.jgll.grammar.symbol.Character;
import org.jgll.grammar.symbol.Range;
import org.jgll.regex.automaton.Automaton;
import org.jgll.regex.automaton.RunnableAutomaton;
import org.jgll.util.Input;
import org.jgll.util.Visualization;
import org.junit.Before;
import org.junit.Test;

public class SequenceTest {
	
	private RegularExpression seq1;
	private RegularExpression seq2;
	private RegularExpression seq3;

	@Before
	public void init() {
		seq1 = new Sequence<>(new Character('a'), new Character('b'));		
		seq2 = new Sequence<>(new Range('a', 'z'), new Range('0', '9'));
		seq3 = new Sequence<>(new Range('a', 'z'), new Range('b', 'm'));
	}
	
	@Test
	public void test1() {
		Automaton nfa = seq1.toAutomaton();

		assertEquals(6, nfa.getCountStates());

		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		
		assertTrue(dfa.match(Input.fromString("ab")));
		assertFalse(dfa.match(Input.fromString("ac")));
		assertFalse(dfa.match(Input.fromString("da")));
	}
	
	
	@Test
	public void test2() {
		Automaton nfa = seq2.toAutomaton();

		assertEquals(6, nfa.getCountStates());

		RunnableAutomaton dfa = nfa.getRunnableAutomaton();
		
		assertTrue(dfa.match(Input.fromString("a0")));
		assertTrue(dfa.match(Input.fromString("a5")));
		assertTrue(dfa.match(Input.fromString("a9")));
		assertTrue(dfa.match(Input.fromString("c7")));
		assertTrue(dfa.match(Input.fromString("z0")));
		assertTrue(dfa.match(Input.fromString("z9")));
		
		assertFalse(dfa.match(Input.fromString("ac")));
		assertFalse(dfa.match(Input.fromString("da")));
	}
	
	/**
	 * Two character classes with overlapping ranges
	 */
	@Test
	public void test3() {
		Automaton nfa = seq3.toAutomaton();
		
		RunnableAutomaton matcher = nfa.getRunnableAutomaton();
		assertTrue(matcher.match(Input.fromString("dm")));
	}
	
	@Test
	public void test1WithPostCondition() {
		// [a][b] !>> [c]
		RegularExpression r = seq1.addCondition(RegularExpressionCondition.notFollow(new Character('c')));
		RunnableAutomaton dfa = r.toAutomaton().getRunnableAutomaton();

		assertEquals(-1, dfa.match(Input.fromString("abc"), 0));
	}
	
}
