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
import org.iguana.datadependent.ast.Statement;
import org.iguana.datadependent.ast.VariableDeclaration;
import org.iguana.datadependent.traversal.IAbstractASTVisitor;
import org.iguana.grammar.condition.Condition;
import org.iguana.grammar.condition.DataDependentCondition;
import org.iguana.grammar.condition.PositionalCondition;
import org.iguana.grammar.condition.RegularExpressionCondition;
import org.iguana.grammar.symbol.*;
import org.iguana.traversal.IConditionVisitor;
import org.iguana.traversal.ISymbolVisitor;

import java.util.Arrays;

/**
 * Created by Anastasia Izmaylova
 */
public class DefaultSymbolVisitor implements ISymbolVisitor<Void>, IAbstractASTVisitor<Void>, IConditionVisitor<Void> {
    @Override
    public Void visit(Expression.Boolean expression) {
        return null;
    }

    @Override
    public Void visit(Expression.Integer expression) {
        return null;
    }

    @Override
    public Void visit(Expression.Real expression) {
        return null;
    }

    @Override
    public Void visit(Expression.String expression) {
        return null;
    }

    @Override
    public Void visit(Expression.Tuple expression) {
        for (Expression e : expression.getElements()) e.accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.Name expression) {
        return null;
    }

    @Override
    public Void visit(Expression.Call expression) {
        for (Expression e : expression.getArguments()) e.accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.Assignment expression) {
        expression.getExpression().accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.LShiftANDEqZero expression) {
        expression.getLhs().accept(this);
        expression.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.OrIndent expression) {
        expression.getIndex().accept(this);
        expression.getIndent().accept(this);
        expression.getFirst().accept(this);
        expression.getLExt().accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.AndIndent expression) {
        expression.getIndex().accept(this);
        expression.getFirst().accept(this);
        expression.getLExt().accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.Or expression) {
        expression.getLhs().accept(this);
        expression.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.And expression) {
        expression.getLhs().accept(this);
        expression.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.Less expression) {
        expression.getLhs().accept(this);
        expression.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.LessThanEqual expression) {
        expression.getLhs().accept(this);
        expression.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.Greater expression) {
        expression.getLhs().accept(this);
        expression.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.GreaterThanEqual expression) {
        expression.getLhs().accept(this);
        expression.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.Equal expression) {
        expression.getLhs().accept(this);
        expression.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.NotEqual expression) {
        expression.getLhs().accept(this);
        expression.getRhs().accept(this);
        return null;
    }

    @Override
    public Void visit(Expression.LeftExtent expression) {
        return null;
    }

    @Override
    public Void visit(Expression.RightExtent expression) {
        return null;
    }

    @Override
    public Void visit(Expression.Yield expression) {
        return null;
    }

    @Override
    public Void visit(Expression.Val expression) {
        return null;
    }

    @Override
    public Void visit(Expression.EndOfFile expression) {
        return null;
    }

    @Override
    public Void visit(Expression.IfThenElse expression) {
        expression.getCondition().accept(this);
        expression.getThenPart().accept(this);
        expression.getElsePart().accept(this);
        return null;
    }

    @Override
    public Void visit(VariableDeclaration declaration) {
        Expression e = declaration.getExpression();
        if (e != null)
            e.accept(this);
        return null;
    }

    @Override
    public Void visit(Statement.Expression statement) {
        statement.getExpression().accept(this);
        return null;
    }

    @Override
    public Void visit(Statement.VariableDeclaration statement) {
        statement.getDeclaration().accept(this);
        return null;
    }

    @Override
    public Void visit(DataDependentCondition condition) {
        condition.getExpression().accept(this);
        return null;
    }

    @Override
    public Void visit(PositionalCondition condition) {
        return null;
    }

    @Override
    public Void visit(RegularExpressionCondition condition) {
        return null;
    }

    @Override
    public Void visit(Align symbol) {
        visitSymbol(symbol.getSymbol());
        return null;
    }

    @Override
    public Void visit(Block symbol) {
        Arrays.asList(symbol.getSymbols()).forEach(this::visitSymbol);
        return null;
    }

    @Override
    public Void visit(Code symbol) {
        visitSymbol(symbol.getSymbol());
        for (Statement stat : symbol.getStatements()) stat.accept(this);
        return null;
    }

    @Override
    public Void visit(Conditional symbol) {
        visitSymbol(symbol.getSymbol());
        symbol.getExpression().accept(this);
        return null;
    }

    @Override
    public Void visit(IfThen symbol) {
        symbol.getExpression().accept(this);
        visitSymbol(symbol.getThenPart());
        return null;
    }

    @Override
    public Void visit(IfThenElse symbol) {
        symbol.getExpression().accept(this);
        visitSymbol(symbol.getThenPart());
        visitSymbol(symbol.getElsePart());
        return null;
    }

    @Override
    public Void visit(Ignore symbol) {
        visitSymbol(symbol.getSymbol());
        return null;
    }

    @Override
    public Void visit(Nonterminal symbol) {
        for (Expression e : symbol.getArguments()) e.accept(this);
        return null;
    }

    @Override
    public Void visit(Offside symbol) {
        visitSymbol(symbol.getSymbol());
        return null;
    }

    @Override
    public Void visit(Terminal symbol) {
        return null;
    }

    @Override
    public Void visit(While symbol) {
        symbol.getExpression().accept(this);
        visitSymbol(symbol.getBody());
        return null;
    }

    @Override
    public Void visit(Return symbol) {
        symbol.getExpression().accept(this);
        return null;
    }

    @Override
    public <E extends Symbol> Void visit(Alt<E> symbol) {
        symbol.getSymbols().forEach(this::visitSymbol);
        return null;
    }

    @Override
    public Void visit(Opt symbol) {
        visitSymbol(symbol.getSymbol());
        return null;
    }

    @Override
    public Void visit(Plus symbol) {
        visitSymbol(symbol.getSymbol());
        return null;
    }

    @Override
    public <E extends Symbol> Void visit(Sequence<E> symbol) {
        symbol.getSymbols().forEach(this::visitSymbol);
        return null;
    }

    @Override
    public Void visit(Star symbol) {
        visitSymbol(symbol.getSymbol());
        return null;
    }

    protected void visitSymbol(Symbol symbol) {
        for (Condition cond : symbol.getPreConditions()) cond.accept(this);
        symbol.accept(this);
        for (Condition cond : symbol.getPostConditions()) cond.accept(this);
    }
}
