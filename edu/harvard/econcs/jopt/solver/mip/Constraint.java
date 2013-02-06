/*
 * Copyright (c) 2005
 *	The President and Fellows of Harvard College.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE UNIVERSITY AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE UNIVERSITY OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package edu.harvard.econcs.jopt.solver.mip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import edu.harvard.econcs.jopt.solver.MIPException;

/**
 * @author sultan
 * 
 * General Representation of a MIP constraint.
 */
public class Constraint implements Serializable, Cloneable {
	private static final long serialVersionUID = 456346456l;

	private Collection exprTerms = new ArrayList();
	private double constant;
	private CompareType type;
	private int id = -1;
	
	/**
	 * @param constTerm
	 * @param type
	 */
	public Constraint(CompareType type, double constant) {
		MIP.checkMax(constant);
		this.constant = constant;
		this.type = type;
		this.exprTerms = new ArrayList();
	}
	
	/**
	 * @return Returns the constTerm.
	 */
	public double getConstant() {
		return constant;
	}
	
	/**
	 * @return Returns the type.
	 */
	public CompareType getType() {
		return type;
	}
	
	/**
	 * 
	 * @param term
	 */
	public void addTerm(Term term) {
		exprTerms.add(term);
	}
	
	public void addTerm(Variable var, double coefficient) {
	    addTerm(new Term(var, coefficient));
	}
	
	/**
	 * 
	 * @return Returns the exprTerms.
	 */
	public Iterator iterator() {
		return exprTerms.iterator();
	}
	
	public int size() {
		return exprTerms.size();
	}
	
	public String toString() {
		return "Constraint {" + exprTerms + getType() + getConstant() + "}";
	}
	
	public Iterator sortedIterator() {
		Term[] sorted = (Term[])exprTerms.toArray(new Term[exprTerms.size()]);
		Arrays.sort(sorted, new Comparator(){
			public int compare(Object o1, Object o2) {
				return ((Term)o1).getVarName().compareTo(((Term)o2).getVarName());
			}});
		return Arrays.asList(sorted).iterator();
	}
	
	public String prettyString() {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (Iterator iter = sortedIterator(); iter.hasNext(); ) {
			Term t = (Term)iter.next();
			if (t.getCoefficient() >= 0) {
				if (!first) {
					sb.append(" + ");
				}
			} else {
				sb.append(" - ");
			}
			sb.append(Math.abs(t.getCoefficient())).append(" ").append(t.getVarName());
			first = false;
		}
		sb.append(getType()).append(getConstant());
		return sb.toString();
	}
	
	/**
	 * @param constant The constant to set.
	 */
	public void setConstant(double constant) {
		this.constant = constant;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Constraint ret = (Constraint)super.clone();
		ret.exprTerms = new ArrayList();
		for (Iterator iter = iterator(); iter.hasNext(); ) {
			ret.exprTerms.add(((Term)iter.next()).typedClone());
		}
		return ret;
	}
	
	public Constraint typedClone() {
		try {
			return (Constraint)clone();
		} catch (CloneNotSupportedException e) {
			throw new MIPException("Problem in clone", e);
		}
	}
	
	public boolean equals(Object arg0) {
		return exprTerms.equals(arg0);
	}
	public int hashCode() {
		return exprTerms.hashCode();
	}
	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	protected void setId(int id) {
		this.id = id;
	}
}
