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

package org.iguana.traversal.idea;

import org.iguana.grammar.Grammar;
import iguana.regex.*;

import java.util.*;

/**
 * Created by Anastasia Izmaylova on 28/09/15.
 */

public class IdeaIDEGenerator {

    public void generate(Grammar grammar, String language, String extension, String path) {

        path = path.endsWith("/")? path : path + "/";

        Set<String> tokenTypes = new HashSet<>();
        Map<String, RegularExpression> terminals = new LinkedHashMap<>();

        GenerateBasicFiles.generate(language, extension, path);
        new CollectRegularExpressions(terminals).collect(grammar);
        new GenerateJFlex(language, path, terminals, tokenTypes).generate();
        GenerateBasicHighlighter.generate(language, path, tokenTypes);
        GenerateElements.generate(grammar.getRules(), language, path);
        GenerateParser.generate(language, path);
        GenerateTermBuilder.generate(language, path);
    }

}
