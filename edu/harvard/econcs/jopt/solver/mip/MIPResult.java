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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.harvard.econcs.jopt.solver.IMIPResult;

/**
 * The results of running CPLEX.
 * 
 * @author Last modified by $Author: jeffsh $
 * @version $Revision: 1.5 $ on $Date: 2005/10/17 18:49:44 $
 * @since Apr 12, 2004
 **/
public class MIPResult implements IMIPResult {
    private static final long serialVersionUID = 176452143213L;
	private double objectiveValue;
	private Map values = new HashMap();
	private Map constraintidstoDuals = new HashMap();
	
	public MIPResult(double objectiveValue, Map values, Map constraintidsToDuals) {
		this.objectiveValue = objectiveValue;
		this.values = values;
		this.constraintidstoDuals = constraintidsToDuals;
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.econcs.jopt.solver.IMIPResult#getObjectiveValue()
	 */
	public double getObjectiveValue() {
		return objectiveValue;
	}
	/* (non-Javadoc)
	 * @see edu.harvard.econcs.jopt.solver.IMIPResult#getValues()
	 */
	public Map getValues() {
		return values;
	}
	
	public double getDual(int constraintId) {
		return ((Double) constraintidstoDuals.get(new Integer(constraintId))).doubleValue();
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("MIPResult: \n").append(objectiveValue).append("\n");
		sb.append("Variables: \n");
		int max = 50;
		int zeroVars = 0;
		for (Iterator i = values.keySet().iterator(); i.hasNext(); ) {
			String var = (String) i.next();			
			Double value = (Double) values.get(var);
			if (value.doubleValue() == 0) {
				zeroVars++;
				continue;
			}
			sb.append(var);
			if (var.length() < max) {
				for (int j = 0; j < max-var.length(); j++) {
					sb.append(" ");
				}
			}
			sb.append(value);
				sb.append("\n");
		}
		sb.append("Remaining " + zeroVars + " variables (of " + values.size() + " are 0).\n");
		sb.append("\nObjective Value: ");
		sb.append(this.objectiveValue);
		sb.append("\n");
		return sb.toString();
	}
	
	/*
	public String toString() {
		StringBuffer rc = new String("");
		Map vals = this.getValues();
		for(Iterator i = vals.keySet().iterator(); i.hasNext();)
		{
			String var = (String)i.next();
			rc += var + ": " + vals.get(var) + "\n";
		}
		return rc;
	}
	*/
}
