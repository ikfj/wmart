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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.MIPException;
import edu.harvard.econcs.jopt.solver.SolveParam;

/**
 * Basic implementation of a mip.
 * 
 * @author Last modified by $Author: blubin $
 * @version $Revision: 1.18 $ on $Date: 2006/08/24 18:31:53 $ 
 * @since April 12, 2004
 **/
public class MIP implements IMIP, Serializable, Cloneable {
    //private static final Log log = new Log(MIP.class);
    
    private static final long serialVersionUID = 1152496442L;
    
	/** Never give mip a number bigger than this, FOR ANY reason
	 	This should never be greater than 1E9 per https://support.ilog.com/public/products/faq.cfm?Product=CPLEX&FAQ=75&CFID=96044&CFTOKEN=40609383&jsessionid=92301560341115251663761 
	 **/
	public static final int MAX_VALUE = 536870910;
	
	private Map vars = new HashMap();
	private Map constraints = new HashMap();
	private Collection objectiveTerms = new ArrayList();
	private boolean isMax;
	 	
	private Map solveParams = new HashMap();
	private Map proposedValuesForVars = null;
	
	public MIP() {
		resetDefaultSolveParams();
	}
	
	/* (non-Javadoc)
	 * @see edu.harvard.econcs.jopt.solver.IMIP#getVars()
	 */
	public Map getVars() {
		return Collections.unmodifiableMap(vars);
	}

	/* (non-Javadoc)
	 * @see edu.harvard.econcs.jopt.solver.IMIP#getVar(int)
	 */
	public Variable getVar(String name) {
		return (Variable)vars.get(name);
	}

	/* (non-Javadoc)
	 * @see edu.harvard.econcs.jopt.solver.IMIP#add(edu.harvard.econcs.jopt.solver.mip.Variable)
	 */
	public String add(Variable var) {
		String name = var.getName();
		if (name == null) {
			throw new MIPException("Invalid variable name");
		} else if (vars.get(name) != null) {
			//FUTURE: This is fine for now
		    // jeffsh: actually, not an error? But this code should be very general.
		    // so I think we treat this as an error and push it back to caller.
		    //log.debug("JS: Name->Var already exists. Ok to re-use previous reference?");		    
			throw new MIPException("Tried to overwrite existing var: " + name);
		}
		vars.put(name, var);
		return name;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.econcs.jopt.solver.IMIP#getNumVars()
	 */
	public int getNumVars() {
		return vars.size();
	}
	
///////	

	private void checkProposedHashMap() {
		if (proposedValuesForVars == null) {
			proposedValuesForVars = new HashMap();
		}
	}
	
	/** Sets proposed value for this variable */
	public void proposeValue(Variable var, double value) {
		checkProposedHashMap();
		proposedValuesForVars.put(var, new Double(value));
	}
	
	/** Sets proposed value for this variable */
	public void proposeValue(Variable var, int value) {
		checkProposedHashMap();
		if (!((value == 0) && (getBooleanSolveParam(SolveParam.ZERO_MISSING_PROPOSED, new Boolean(false)) == true))) {
			// optimization to cut down serialization size
			proposedValuesForVars.put(var, new Integer(value));
		}
	}

	/** Sets proposed value for this variable */
	public void proposeValue(Variable var, boolean value) {
		checkProposedHashMap();
		if (!((value == false) && (getBooleanSolveParam(SolveParam.ZERO_MISSING_PROPOSED, new Boolean(false)) == true))) {
			// optimization to cut down serialization size			
			proposedValuesForVars.put(var, new Boolean(value));
		}
	}

	/** Removes proposed value for this variable */
	public void removeProposedValue(Variable var) {
		if (proposedValuesForVars == null) {
			return;
		}
		proposedValuesForVars.remove(var);
		if (proposedValuesForVars.isEmpty()) {
			proposedValuesForVars = null;
		}
	}
	
	/** Clears proposed value for this variable */
	public void clearProposedValues() {
		proposedValuesForVars = null;
	}

	/** useful for copying proposed var/vals to a new MIP */
	public Set getVarsWithProposedValues() {
		if(proposedValuesForVars == null) {
			return Collections.EMPTY_SET;
		}
		return Collections.unmodifiableSet(proposedValuesForVars.keySet());
	}
	
	public int getProposedIntValue(Variable var) {
		Object i = proposedValuesForVars.get(var);
		if (i == null) {
			throw new MIPException("Variable " + var + " has no proposed value");
		}
		if (!(i instanceof Integer)) {
			throw new MIPException("Variable " + var + " not an Integer");
		}
		return ((Integer)i).intValue();
	}
	
	public double getProposedDoubleValue(Variable var) {
		Object i = proposedValuesForVars.get(var);
		if (i == null) {
			throw new MIPException("Variable " + var + " has no proposed value");
		}
		if (!(i instanceof Double)) {
			throw new MIPException("Variable " + var + " not a Double");
		}
		return ((Double)i).doubleValue();
	}


	public boolean getProposedBooleanValue(Variable var) {
		Object i = proposedValuesForVars.get(var);
		if (i == null) {
			throw new MIPException("Variable " + var + " has no proposed value");
		}
		if (!(i instanceof Boolean)) {
			throw new MIPException("Variable " + var + " not an Boolean");
		}
		return ((Boolean)i).booleanValue();
	}
				

	/* (non-Javadoc)
	 * @see edu.harvard.econcs.jopt.solver.IMIP#getConstraints()
	 */
	public Iterator getConstraints() {
		return constraints.values().iterator();
	}

	/* (non-Javadoc)
	 * @see edu.harvard.econcs.jopt.solver.IMIP#add(edu.harvard.econcs.jopt.solver.mip.Constraint)
	 */
	public int add(Constraint constraint, int constraintId) {
		constraints.put(new Integer(constraintId), constraint);
		constraint.setId(constraintId);
		return constraintId;
	}

	/* (non-Javadoc)
	 * @see edu.harvard.econcs.jopt.solver.IMIP#getNumConstraints()
	 */
	public int getNumConstraints() {
		return constraints.size();
	}

	/* (non-Javadoc)
	 * @see edu.harvard.econcs.jopt.solver.IMIP#getObjectiveTerms()
	 */
	public Iterator getObjectiveTerms() {
		return objectiveTerms.iterator();
	}

	/* (non-Javadoc)
	 * @see edu.harvard.econcs.jopt.solver.IMIP#addObjectTerm(edu.harvard.econcs.jopt.solver.mip.Term)
	 */
	public void addObjectiveTerm(Term term) {
		objectiveTerms.add(term);
	}

	public void addObjectiveTerm(Variable var, double coefficient) {
		Term term = new Term(var, coefficient);
		addObjectiveTerm(term);
	}	
	
	public boolean isObjectiveMax() {
		return isMax;
	}
	
	public boolean isObjectiveMin() {
		return !isMax;
	}
	
	public void setObjectiveMax(boolean isMax) {
		this.isMax = isMax;
	}
	
	/*
	public String toString() {
		return "MIP {" + vars + ", " + constraints + ", " + 
		       objectiveTerms + ", " + isMax + "}";
	}
	*/
	
	public String toString () {
		String rc = new String("");
		rc += "Vars:\n";
		Map vars = this.getVars();
		for(Iterator i = vars.keySet().iterator(); i.hasNext();)
		{
			String var = (String)i.next();
			rc += "    " + vars.get(var).toString() + "\n";
		}
		
		rc += "Constraints:\n";
		for(Iterator i = this.getConstraints(); i.hasNext();)
		{
			Constraint cons = (Constraint)i.next();
			rc += "    " + cons + "\n";
		}
		rc += "Objective Function: " + (this.isMax ? "Max" : "Min") + "[ ";
		for(Iterator i = this.getObjectiveTerms(); i.hasNext();)
		{
			Term t = (Term) i.next();
			rc += t;
			if (i.hasNext()) {
				rc += " + ";
			}
		}
		rc += " ]";
		return rc;
	}
	

	
	public void remove(int constraintId) {
		if (constraints.remove(new Integer(constraintId)) == null) {
			throw new MIPException("Tried to remove constraint that does not exist");
		}
	}
	
	public void remove(Variable var) {
		String name = var.getName();
		if (name == null) {
			throw new MIPException("Invalid variable name");
		}
		if (vars.remove(name) == null) {
			throw new MIPException("Tried to remove constraint that does not exist");
		}
	}
	public void removeObjectiveTerm(Term term) {
		if (!objectiveTerms.remove(term)) {
			throw new MIPException("Tried to remove constraint that does not exist");
		}
	}
	
	
	protected Object clone() throws CloneNotSupportedException {
		MIP ret = (MIP)super.clone();
		ret.constraints = new HashMap();
		for (Iterator iter = constraints.keySet().iterator(); iter.hasNext(); ) {
			Integer id = (Integer) iter.next();
			ret.constraints.put(id, ((Constraint)constraints.get(id)).typedClone());
		}
		
		ret.objectiveTerms = new ArrayList();
		for (Iterator iter = objectiveTerms.iterator(); iter.hasNext(); ) {
			ret.objectiveTerms.add(((Term)iter.next()).typedClone());
		}
		
		ret.vars = new HashMap();
		for (Iterator iter = vars.keySet().iterator(); iter.hasNext(); ) {
			String name = (String)iter.next();
			Variable val = (Variable)vars.get(name);
			ret.vars.put(name, val.typedClone());
		}
		
		ret.proposedValuesForVars = new HashMap();
		for (Iterator iter = proposedValuesForVars.keySet().iterator(); iter.hasNext(); ) {
			Variable v = (Variable)iter.next();
			Object val = proposedValuesForVars.get(v);
			ret.proposedValuesForVars.put(ret.getVar(v.getName()), val);
		}
		ret.solveParams = new HashMap(solveParams);
		
		return ret;
	}
	
	public IMIP typedClone() {
		try {
			return (MIP)clone();
		} catch (CloneNotSupportedException e) {
			throw new MIPException("Problem in clone", e);
		}
	}

	public Constraint getConstraint(int constraintId) {
		return (Constraint) constraints.get(new Integer(constraintId));
	}
	
	/**
	 * Throws exeption if n is greater than MIP.MAX_VALUE
	 * @param n
	 */
	static void checkMax(double n) {
		if (n > MIP.MAX_VALUE) {
			throw new MIPException("Value (" + n + ") must be less than MIP.MAX_VALUE: " + MIP.MAX_VALUE);
		}
	}
	
	//Parameters:
	
	public Object getSolveParam(SolveParam param) {
		return solveParams.get(param);
	}

	public int getIntSolveParam(SolveParam param, Integer defaultValue) {
		Object o = getSolveParam(param);
		if (o == null) {
			if (defaultValue == null) {
				throw new MIPException("Parameter not specified: " + param);
			} else {
				return defaultValue.intValue();
			}				
		}
		if (o instanceof Integer) {
			return ((Integer)o).intValue();
		}
		throw new MIPException("Parameter " + param + " not an Integer: " + o);
	}
	public int getIntSolveParam(SolveParam param, int defaultValue) {
		return getIntSolveParam(param, new Integer(defaultValue));
	}
	public int getIntSolveParam(SolveParam param) {
		return getIntSolveParam(param, null);
	}

	public double getDoubleSolveParam(SolveParam param, Double defaultValue) {
		Object o = getSolveParam(param);
		if (o == null) {
			if (defaultValue == null) {
				throw new MIPException("Parameter not specified: " + param);
			} else {
				return defaultValue.doubleValue();
			}				
		}
		if (o instanceof Double) {
			return ((Double)o).doubleValue();
		}
		throw new MIPException("Parameter " + param + " not a Double: " + o);		
	}
	public double getDoubleSolveParam(SolveParam param, double defaultValue) {
		return getDoubleSolveParam(param, new Double(defaultValue));
	}
	public double getDoubleSolveParam(SolveParam param) {
		return getDoubleSolveParam(param, null);
	}
	
	public boolean getBooleanSolveParam(SolveParam param, Boolean defaultValue) {
		Object o = getSolveParam(param);
		if (o == null) {
			if (defaultValue == null) {
				throw new MIPException("Parameter not specified: " + param);
			} else {
				return defaultValue.booleanValue();
			}				
		}
		if (o instanceof Boolean) {
			return ((Boolean)o).booleanValue();
		}
		throw new MIPException("Parameter " + param + " not a Boolean: " + o);		
	}
	public boolean getBooleanSolveParam(SolveParam param, boolean defaultValue) {
		return getBooleanSolveParam(param, new Boolean(defaultValue));
	}
	public boolean getBooleanSolveParam(SolveParam param) {
		return getBooleanSolveParam(param, null);
	}
		
	public String getStringSolveParam(SolveParam param, String defaultValue) {
		Object o = getSolveParam(param);
		if (o == null) {
			if (defaultValue == null) {
				throw new MIPException("Parameter not specified: " + param);
			} else {
				return defaultValue;
			}				
		}
		if (o instanceof String) {
			return (String)o;
		}
		throw new MIPException("Parameter " + param + " not a String: " + o);
	}
	
	public String getStringSolveParam(SolveParam param) {
		return getStringSolveParam(param, null);
	}	
	
	public void setSolveParam(SolveParam param, int value) {
		if (!param.isInteger()) {
			throw new MIPException("Parameter " + param + " is a " + param.getTypeDescription() + ", not Integer");
		}
		solveParams.put(param, new Integer(value));
	}
	
	public void setSolveParam(SolveParam param, double value) {
		if (!param.isDouble()) {
			throw new MIPException("Parameter " + param + " is a " + param.getTypeDescription() + ", not Double");
		}
		solveParams.put(param, new Double(value));
	}
	
	public void setSolveParam(SolveParam param, boolean value) {
		if (!param.isBoolean()) {
			throw new MIPException("Parameter " + param + " is a " + param.getTypeDescription() + ", not Boolean");
		}
		solveParams.put(param, new Boolean(value));		
	}
	
	public void setSolveParam(SolveParam param, String value) {
		if (!param.isString()) {
			throw new MIPException("Parameter " + param + " is a " + param.getTypeDescription() + ", not String");
		}
		solveParams.put(param, value);
	}
	
	public Set getSpecefiedSolveParams() {
		return Collections.unmodifiableSet(solveParams.keySet());
	}
	
	public void clearSolveParams() {
		solveParams.clear();
	}
	
	/**
	 * Resets CPLEX/JOpt parameters to:
	 * Wall clock, 10 minute timelimit, strict IIS calculation, no-output problem file, and zero missing proposed variables.
	 */
	public void resetDefaultSolveParams() {
		setSolveParam(SolveParam.CLOCK_TYPE, 2); //1 CPU, 2 wall clock
		setSolveParam(SolveParam.TIME_LIMIT, 600d);//10 minutes.

		// When we seed values of MIPs, this make all missing int/bool variables == 0. 
		setSolveParam(SolveParam.ZERO_MISSING_PROPOSED, true);
		setSolveParam(SolveParam.PROBLEM_FILE, "");
		setSolveParam(SolveParam.CONSTRAINT_BACKOFF_LIMIT, .1d);
		setSolveParam(SolveParam.DISPLAY_OUTPUT, true);
		setSolveParam(SolveParam.CALCULATE_CONFLICT_SET, true);
	}
}
