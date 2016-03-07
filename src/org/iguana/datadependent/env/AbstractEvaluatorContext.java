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

package org.iguana.datadependent.env;

import iguana.utils.input.Input;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractEvaluatorContext implements IEvaluatorContext {
	
	private final Input input;
	
	private Environment env;
	
	private Map<String, Object> global;
	
	public AbstractEvaluatorContext(Input input) {
		this.input = input;
	}
	
	@Override
	public Input getInput() {
		return this.input;
	}

	@Override
	public Environment getEnvironment() {
		return env;
	}

	@Override
	public void setEnvironment(Environment env) {
		this.env = env;
	}

    @Override
    public void declare(Object value) {
        env = env.declare(value);
    }

    @Override
    public void declare(Object... values) {
        env = env.declare(values);
    }

    @Override
    public void store(int i, Object value) {
        env = env.store(i, value);
    }

    @Override
    public Object lookup(int i) {
        return env.lookup(i);
    }

	@Override
	public void declareGlobal(String name, Object value) {
		if (global == null)
			global = new HashMap<>();

		global.put(name, value);
	}

	@Override
	public void declareGlobal(String[] names, Object[] values) {
		assert names.length == values.length;

		if (names.length == 0)
			return;

		if (global == null)
			global = new HashMap<>();

		int i = 0;
		while (i < names.length) {
			global.put(names[i], values[i]);
			i++;
		}
	}

	@Override
	public Object lookupGlobal(String name) {
		if (global == null)
			return null;

		return global.get(name);
	}

}
