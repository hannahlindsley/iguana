/*
 * Copyright (c) 2015, Ali Afroozeh and Anastasia Izmaylova, Centrum Wiskunde & Informatica (CWI)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */

package org.iguana.datadependent.traversal;

import org.iguana.datadependent.ast.Expression;
import org.iguana.grammar.Grammar;
import org.iguana.grammar.exception.UndeclaredVariableException;
import org.iguana.grammar.symbol.*;
import org.iguana.grammar.transformation.EnvSymbolVisitor;

import java.util.*;

/**
 * Created by Anastasia Izmaylova
 */

/**
 * Description:
 *
 *     Performs the following steps:
 *
 *         - calculates uses of state variables in a nonterminal (across all the nonterminal's rules), plus transitive closure
 *           (this step determines which state variables should be passed to which nonterminal)
 *
 *         - calculates updates to state variables in a nonterminal, plus transitive closure
 *
 *         - calculates uses of state variables after a nonterminal across all the rules, plus transitive closure
 *           (the last two steps determine which state variables should be returned from a nonterminal)
 *
 *     Notes:
 *
 *         - Global variables CAN BE shadowed by local variables;
 */
public class StateVariableVisitor extends EnvSymbolVisitor {

    /*
     * variables    - global variables in the grammar
     * usesIn       - uses of global variables in a nonterminal (transitively closed); key: the nonterminal's name
     * updates      - updates to global variables in a nonterminal (transitively closed); key: the nonterminal's name
     * usesAfter    - uses of global variables after a nonterminal across all the grammar rules: key: the nonterminal's name
     * bindings     - variable bindings required after a nonterminal;
     *                variable bindings are associated with a rule and a grammar position in this rule
     *                if a rule does not use global variable, the corresponding inner list is EMPTY_LIST
     *
     * Notes: EBNF constructs are allowed at this step; EBNF constructs introduce inner scopes that can use and update
     *        variable of outer scopes; the uses and updates to global variables found inside an EBNF construct are
     *        added to the uses and updates of the nonterminal in which this EBNF construct appears
     */
    private Map<String, Object> variables;

    private Map<String, Set<String>> usesIn = new HashMap<>();
    private Map<String, Set<String>> updates = new HashMap<>();
    private Map<String, Set<String>> usesAfter = new HashMap<>();
    private Map<String, List<List<Set<String>>>> bindings = new HashMap<>();
    private Map<String, List<String>> returns = new HashMap<>();


    private Set<String> currUsesIn;
    private Set<String> currUpdates;

    private List<String> currNonterminals;
    private List<Set<String>> currUsesAfter;

    private static final Set<String> EMPTY_SET = new HashSet<>();
    private static final List<Set<String>> EMPTY_LIST = new ArrayList<>();

    /**
     * @param variables    - global variables
     * @param reachability - transitive closure of reachable nonterminals in the grammar
     */
    public void visit(Grammar grammar, Map<String, Object> variables, Map<String, Set<String>> reachability) {
        this.variables = variables;
        grammar.getRules().forEach(rule -> visit(rule.head(), rule.getBody()));
        usesIn = close(usesIn, reachability);
        updates = close(updates, reachability);
        usesAfter = close(usesAfter, reverse(reachability));
        updates.entrySet().forEach(e -> {
            Set<String> x = usesAfter.get(e.getKey());
            List<String> y = new ArrayList<>();
            if (x != null) {
                e.getValue().forEach(v -> { if (x.contains(v)) y.add(v); });
            }
            returns.put(e.getKey(), y);
        });
    }

    protected void visit(String head, List<Symbol> body) {
        currUsesIn = EMPTY_SET;
        currUpdates = EMPTY_SET;
        currNonterminals = new ArrayList<>();
        currUsesAfter = new ArrayList<>();

        body.forEach(this::visitSymbol);

        recordUsesIn(head);
        recordUpdates(head);

        boolean added = recordUsesAfter();

        List<List<Set<String>>> v;
        if ((v = bindings.get(head)) != null) {
            if (added)
                v.add(currUsesAfter);
        } else
            bindings.put(head, new ArrayList<>(Arrays.asList(added ? currUsesAfter : EMPTY_LIST)));
    }

    private void recordUsesIn(String head) {
        if (currUsesIn == EMPTY_SET) return;
        Set<String> v = usesIn.computeIfAbsent(head, key -> currUsesIn);
        if (v != currUsesIn)
            v.addAll(currUsesIn);
    }

    private void recordUpdates(String head) {
        if (currUpdates == EMPTY_SET) return;
        Set<String> v = updates.computeIfAbsent(head, key -> currUpdates);
        if (v != currUpdates)
            v.addAll(currUpdates);
    }

    private boolean recordUsesAfter() {
        Iterator<String> it1 = currNonterminals.iterator();
        Iterator<Set<String>> it2 = currUsesAfter.iterator();
        boolean noUses = true;
        while (it1.hasNext()) {
            String nt = it1.next();
            Set<String> v = it2.next();
            if (!v.isEmpty()) {
                noUses = false;
                Set<String> x = usesAfter.computeIfAbsent(nt, key -> v);
                if (x != v)
                    x.addAll(v);
            }
        }
        return !noUses;
    }

    protected Map<String, Set<String>> close(Map<String, Set<String>> map, Map<String, Set<String>> reachability) {
        Map<String, Set<String>> result = new HashMap<>();
        for (Map.Entry<String, Set<String>> e : map.entrySet()) {
            Set<String> s1 = new HashSet<>(e.getValue());
            Set<String> s2 = reachability.get(e.getKey());
            if (s2 != null && !s2.isEmpty()) {
                for (String nt : s2) {
                    Set<String> s3 = map.get(nt);
                    if (s3 != null)
                        s1.addAll(s3);
                }
            }
            result.put(e.getKey(), s1);
        }
        return result;
    }

    protected static Map<String, Set<String>> reverse(Map<String, Set<String>> reachability) {
        Map<String, Set<String>> result = new HashMap<>();
        reachability.entrySet()
                .forEach(e -> e.getValue()
                        .forEach(v -> result.computeIfAbsent(v, key -> new HashSet<>()).add(e.getKey())));
        return result;
    }

    @Override
    public Void visit(Expression.Name expression) {
        super.visit(expression);
        checkPossibleUse(expression.getName());
        return null;
    }

    @Override
    public Void visit(Expression.Assignment expression) {
        super.visit(expression);
        String id = expression.getId();
        if (!getEnv().containsKey(id) && variables.containsKey(id))
            currUpdates.add(id);
        return null;
    }

    @Override
    public Void visit(Expression.LeftExtent expression) {
        super.visit(expression);
        checkPossibleUse(expression.getLabel());
        return null;
    }

    @Override
    public Void visit(Expression.RightExtent expression) {
        super.visit(expression);
        checkPossibleUse(expression.getLabel());
        return null;
    }

    @Override
    public Void visit(Expression.Val expression) {
        super.visit(expression);
        checkPossibleUse(expression.getLabel());
        return null;
    }

    @Override
    public Void visit(Expression.Yield expression) {
        super.visit(expression);
        checkPossibleUse(expression.getLabel());
        return null;
    }

    @Override
    public Void visit(Nonterminal symbol) {
        super.visit(symbol);
        String name = symbol.getName();
        currNonterminals.add(name);
        currUsesAfter.add(new HashSet<>());
        return null;
    }

    @Override
    public void visitSymbol(Symbol symbol) {
        if (canDeclareVariable(symbol)) {
            if (symbol instanceof Nonterminal) {
                String variable = ((Nonterminal) symbol).getVariable();
                if (variable != null)
                    checkPossibleUse(variable);
            }
        }
        super.visitSymbol(symbol);
    }

    @Override
    protected void check(String name) {
        if (!(getEnv().containsKey(name) || variables.containsKey(name)))
            throw new UndeclaredVariableException(name);
    }

    protected void checkPossibleUse(String name) {
        if (!getEnv().containsKey(name) && variables.containsKey(name)) {
            currUsesIn.add(name);
            currUsesAfter.forEach(v -> v.add(name));
        }
    }

}
