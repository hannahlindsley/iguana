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

import org.iguana.grammar.Grammar;
import org.iguana.grammar.symbol.Nonterminal;
import org.iguana.grammar.symbol.Symbol;

import java.util.*;

/**
 * Created by Anastasia Izmaylova
 */

/*
 * In contrast to StateVariableVisitor, this class also computes reachability graph for nonterminals (in two ways);
 */
public class TransitiveStateVariableVisitor extends StateVariableVisitor {

    private String currHead;
    private Map<String, Set<String>> reachability = new HashMap<>();

    public void visit(Grammar grammar, Map<String, Object> variables) {
        visit(grammar, variables, reachability);
    }

    @Override
    protected void visit(String head, List<Symbol> body) {
        currHead = head;
        super.visit(head, body);
    }

    @Override
    protected void compute(Nonterminal symbol) {
        reachability.computeIfAbsent(currHead, key -> new HashSet<>()).add(symbol.getName());
    }

    @Override
    protected Map<String, Set<String>> close(Map<String, Set<String>> reachability) {
        Map<String, Set<String>> result = new HashMap<>(reachability);
        Map<String, Set<String>> x = reachability;
        while (!x.isEmpty()) {
            Map<String, Set<String>> y = x;
            x = new HashMap<>();
            for (Map.Entry<String, Set<String>> e : y.entrySet()) {
                Set<String> z = reachability.get(e.getKey());
                if (z != null && !z.isEmpty()) {
                    for (String v1 : e.getValue()) {
                        Set<String> w = result.computeIfAbsent(v1, key -> new HashSet<>());
                        for (String v2 : z) {
                            if (!w.contains(v2)) {
                                w.add(v2);
                                x.computeIfAbsent(v2, key -> new HashSet<>()).add(v1);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

}
