/*
 * Copyright (c) 2015, CWI
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

package org.iguana.grammar.symbol;

import org.iguana.traversal.ISymbolVisitor;

public class Ignore extends AbstractSymbol {
	
private static final long serialVersionUID = 1L;
	
	private final Symbol symbol;

	Ignore(Builder builder) {
		super(builder);
		this.symbol = builder.symbol;
	}
	
	public static Ignore ignore(Symbol symbol) {
		return builder(symbol).build();
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	@Override
	public String getConstructorCode() {
		return "Ignore.builder(" + symbol.getConstructorCode() + ")" 
								  + super.getConstructorCode()
								  + ".build()";
	}

	@Override
	public Builder copyBuilder() {
		return new Builder(this);
	}
	
	@Override
	public int size() {
		return symbol.size();
	}
	
	@Override
	public String toString() {
		return String.format("ignore %s", symbol.toString());
	}
	
	@Override
	public String toString(int j) {
		return String.format("ignore %s", symbol.toString(j));
	}
	
	public static Builder builder(Symbol symbol) {
		return new Builder(symbol);
	}
	
	public static class Builder extends SymbolBuilder<Ignore> {
		
		private final Symbol symbol;

		public Builder(Ignore ignore) {
			super(ignore);
			this.symbol = ignore.symbol;
		}
		
		public Builder(Symbol symbol) {
			super(String.format("ignore %s", symbol.toString()));
			this.symbol = symbol;
		}

		@Override
		public Ignore build() {
			return new Ignore(this);
		}
		
	}

	@Override
	public <T> T accept(ISymbolVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
