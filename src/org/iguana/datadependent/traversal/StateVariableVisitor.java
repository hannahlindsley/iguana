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
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Rule;
import org.iguana.grammar.symbol.Symbol;
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

    private final Map<String, Object> variables;

    private final Map<String, Set<String>> usesIn = new HashMap<>();    // per nonterminal
    private final Map<String, Set<String>> updates = new HashMap<>();   // per nonterminal
    private final Map<String, Set<String>> usesAfter = new HashMap<>(); // per nonterminal

    private Set<String> currUsesIn;
    private Set<String> currUpdates;
    private Set<String> currUsesAfter;
    private List<String> currNonts;

    public StateVariableVisitor(Map<String, Object> variables) {
        this.variables = variables;
    }

    public void visit(Grammar grammar) {

    }

    public void visit(Rule rule) {
        visit(rule.head(), rule.getBody());
    }

    private void visit(String head, List<Symbol> body) {
        currUsesIn = usesIn.computeIfAbsent(head, key -> new HashSet<>());
        currUpdates = updates.computeIfAbsent(head, key -> new HashSet<>());
        currNonts = new ArrayList<>();
        body.forEach(this::visitSymbol);
    }

    @Override
    public Void visit(Expression.Name expression) {
        super.visit(expression);
        addUse(expression.getName());
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
        addUse(expression.getLabel());
        return null;
    }

    @Override
    public Void visit(Expression.RightExtent expression) {
        super.visit(expression);
        addUse(expression.getLabel());
        return null;
    }

    @Override
    public Void visit(Expression.Val expression) {
        super.visit(expression);
        addUse(expression.getLabel());
        return null;
    }

    @Override
    public Void visit(Expression.Yield expression) {
        super.visit(expression);
        addUse(expression.getLabel());
        return null;
    }

    @Override
    public Void visit(Nonterminal symbol) {
        super.visit(symbol);
        String name = symbol.getName();
        currUsesAfter = usesAfter.computeIfAbsent(name, key -> new HashSet<>());
        currNonts.add(name);
        return null;
    }

    @Override
    protected void check(String name) {
        if (!(getEnv().containsKey(name) || variables.containsKey(name)))
            throw new UndeclaredVariableException(name);
    }

    private void addUse(String name) {
        if (!getEnv().containsKey(name) && variables.containsKey(name)) {
            currUsesIn.add(name);
            currUsesAfter.add(name);
        }
    }

}
