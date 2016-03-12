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

package org.iguana.grammar.transformation;

import org.iguana.datadependent.ast.Expression;
import org.iguana.datadependent.ast.VariableDeclaration;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.exception.UndeclaredVariableException;
import org.iguana.grammar.symbol.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anastasia Izmaylova
 */

/**
 * Description:
 *
 *     - Shadowing of local variable is NOT allowed; also in inner scopes introduced by EBNF constructs;
 *
 *     - In x = l:A(args), if x is in the scope, it is reassigned a new value; otherwise,
 *       a new variable x is introduced into the scope; l is a new variable introduced into the scope;
 *
 *     - In x = e, a new value is reassigned to x;
 *
 *     - In var x and var x = e, a new variable is introduced into the scope;
 */
public class EnvSymbolVisitor implements DefaultSymbolVisitor {

    private final static Object DEFAULT = new Object();
    private Map<String, Object> env = new HashMap<>();

    protected EnvSymbolVisitor() {}

    public Map<String, Object> getEnv() {
        return env;
    }

    @Override
    public Void visit(Expression.Name expression) {
        check(expression.getName());
        return null;
    }

    @Override
    public Void visit(Expression.Assignment expression) {
        check(expression.getId());
        return null;
    }

    @Override
    public Void visit(Expression.LeftExtent expression) {
        check(java.lang.String.format(Expression.LeftExtent.format, expression.getLabel()));
        return null;
    }

    @Override
    public Void visit(Expression.RightExtent expression) {
        check(expression.getLabel());
        return null;
    }

    @Override
    public Void visit(Expression.Yield expression) {
        check(expression.getLabel());
        return null;
    }

    @Override
    public Void visit(Expression.Val expression) {
        check(expression.getLabel());
        return null;
    }

    @Override
    public Void visit(VariableDeclaration declaration) {
        DefaultSymbolVisitor.super.visit(declaration);
        declare(declaration.getName());
        return null;
    }

    @Override
    public Void visit(Block symbol) {
        Map<String, Object> env = this.env;
        DefaultSymbolVisitor.super.visit(symbol);
        this.env = env;
        return null;
    }

    @Override
    public Void visit(IfThen symbol) {
        Map<String, Object> env = this.env;
        DefaultSymbolVisitor.super.visit(symbol);
        this.env = env;
        return null;
    }

    @Override
    public Void visit(IfThenElse symbol) {
        symbol.getExpression().accept(this);
        Map<String, Object> env = this.env;
        visitSymbol(symbol.getThenPart());
        this.env = env;
        visitSymbol(symbol.getElsePart());
        this.env = env;
        return null;
    }

    @Override
    public Void visit(While symbol) {
        Map<String, Object> env = this.env;
        DefaultSymbolVisitor.super.visit(symbol);
        this.env = env;
        return null;
    }

    @Override
    public <E extends Symbol> Void visit(Alt<E> symbol) {
        Map<String, Object> env = this.env;
        symbol.getSymbols().forEach(s -> {
            visitSymbol(s);
            this.env = env;
        });
        return null;
    }

    @Override
    public Void visit(Opt symbol) {
        Map<String, Object> env = this.env;
        DefaultSymbolVisitor.super.visit(symbol);
        this.env = env;
        return null;
    }

    @Override
    public Void visit(Plus symbol) {
        Map<String, Object> env = this.env;
        DefaultSymbolVisitor.super.visit(symbol);
        this.env = env;
        return null;
    }

    @Override
    public <E extends Symbol> Void visit(Sequence<E> symbol) {
        Map<String, Object> env = this.env;
        DefaultSymbolVisitor.super.visit(symbol);
        this.env = env;
        return null;
    }

    @Override
    public Void visit(Star symbol) {
        Map<String, Object> env = this.env;
        DefaultSymbolVisitor.super.visit(symbol);
        this.env = env;
        return null;
    }

    @Override
    public void visitSymbol(Symbol symbol) {
        String label = symbol.getLabel();
        declare(java.lang.String.format(Expression.LeftExtent.format, label));
        for (Condition cond : symbol.getPreConditions()) cond.accept(this);
        symbol.accept(this);

        declare(label);

        if (canDeclareVariable(symbol)) {
            // TODO: Allow the other types of symbols to have a variable
            if (symbol instanceof Nonterminal) {
                String variable = ((Nonterminal) symbol).getVariable();
                if (variable != null && !env.containsKey(variable))
                    declare(variable);
            }
        }

        for (Condition cond : symbol.getPostConditions()) cond.accept(this);
    }

    protected void declare(String name) {
        if (env.containsKey(name))
            throw new RuntimeException("Redeclaration of a local variable: " + name);
        env = new HashMap<>(env);
        env.put(name, DEFAULT);
    }

    protected void check(String name) {
        if (!env.containsKey(name))
            throw new UndeclaredVariableException(name);
    }

    protected boolean canDeclareVariable(Symbol symbol) {
        if (symbol instanceof Nonterminal
                || symbol instanceof Alt<?> || symbol instanceof Opt || symbol instanceof Plus
                || symbol instanceof Sequence<?> || symbol instanceof Star)
            return true;
        return false;
    }

}
