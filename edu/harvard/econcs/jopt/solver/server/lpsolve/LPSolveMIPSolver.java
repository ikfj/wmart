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

package edu.harvard.econcs.jopt.solver.server.lpsolve;

import java.util.*;

import lpsolve.*;
import edu.harvard.econcs.jopt.solver.*;
import edu.harvard.econcs.jopt.solver.mip.*;
import edu.harvard.econcs.jopt.solver.server.*;

/**
 * A Class for solving MIPs based on the LPSolve solver.
 * 
 * @author Benjamin Lubin; Last modified by $Author: blubin $
 * @version $Revision: 1.5 $ on $Date: 2006/01/13 19:52:44 $
 * @since Jan 4, 2005
 **/
public class LPSolveMIPSolver implements IMIPSolver {

	// private static Log log = new Log(LPSolveMIPSolver.class);
	private static final String fileName = "mipInstance.txt";
	private static final long TIME_LIMIT = 60000;

	private static boolean debug = false;

	{
		// Load the necessary System libraries:
		// java.library.path must point to lib directory...
		// System.loadLibrary("lpsolve51");
		// System.loadLibrary("lpsolve51j");
	}

	public IMIPResult solve(IMIP mip) throws MIPException {
		try {
			Map objTerms = getObjTerms(mip);
			List activeVars = getActiveVars(mip);
			// Create a problem with (constraints, rows):
			LpSolve solver = LpSolve.makeLp(0, activeVars.size());
			solver.setAddRowmode(true);
			solver.setTimeout(TIME_LIMIT);

			// define objective:

			if (mip.isObjectiveMax()) {
				solver.setMaxim();
			}
			if (mip.isObjectiveMin()) {
				solver.setMinim();
			}

			double[] obj = new double[activeVars.size() + 1];
			for (int i = 0; i < activeVars.size(); i++) {
				Variable v = (Variable) activeVars.get(i);
				Term t = ((Term) objTerms.get(v.getName()));
				obj[i + 1] = t == null ? 0 : t.getCoefficient();
				solver.setColName(i + 1, v.getName());
			}
			solver.setObjFn(obj);

			// setup variables:
			for (int i = 0; i < activeVars.size(); i++) {
				Variable v = (Variable) activeVars.get(i);
				VarType t = v.getType();
				if (t == VarType.BOOLEAN) {
					solver.setBinary(i + 1, true);
				}
				if (t == VarType.DOUBLE) {
					solver.setBounds(i + 1, v.getLowerBound(), v.getUpperBound());
				}
				if (t == VarType.INT) {
					solver.setBounds(i + 1, v.getLowerBound(), v.getUpperBound());
					solver.setInt(i + 1, true);
				}
			}

			// add constraints
			List constraints = getConstraints(mip);
			for (int i = 0; i < constraints.size(); i++) {
				Constraint c = (Constraint) constraints.get(i);
				List terms = getTerms(mip, activeVars, c);
				double[] row = new double[activeVars.size() + 1];
				for (int j = 0; j < activeVars.size(); j++) {
					Term t = (Term) terms.get(j);
					row[j + 1] = t == null ? 0 : t.getCoefficient();
				}
				solver.addConstraint(row, getType(c.getType()), c.getConstant());
				// solver.setRowName(i+1, c.toString());
			}

			solver.setDebug(debug);

			// solve the problem
			solver.solve();

			if (debug) {
				// write out the formulation:
				solver.writeLp(fileName);
				solver.printLp();
			}

			// Fill the results:
			Map values = new HashMap();
			double[] vars = solver.getPtrVariables();
			for (int i = 0; i < activeVars.size(); i++) {
				Variable v = (Variable) activeVars.get(i);
				values.put(v.getName(), new Double(vars[i]));
			}
			Map duals = new HashMap();
			double[] dualVars = solver.getPtrDualSolution();
			for (int i = 0; i < constraints.size(); i++) {
				Constraint c = (Constraint) constraints.get(i);
				duals.put(new Integer(c.getId()), new Double(dualVars[i + 1]));
			}

			MIPResult ret = new MIPResult(solver.getObjective(), values, duals);

			// print solution
			/*
			 * System.out.println("Value of objective function: " + solver.getObjective()); double[]
			 * var =
			 * solver.getPtrVariables(); for (int i = 0; i < var.length; i++) {
			 * System.out.println("Value of var[" + i +
			 * "] = " + var[i]); }
			 */

			// delete the problem and free memory
			solver.deleteLp();

			return ret;
		} catch (LpSolveException e) {
			throw new MIPException("Exception solving MIP: " + e);
		}
	}

	private List getActiveVars(IMIP mip) {
		List ret = new ArrayList(mip.getNumVars());
		for (Iterator iter = mip.getVars().values().iterator(); iter.hasNext();) {
			Variable v = (Variable) iter.next();
			if (!v.ignore()) {
				ret.add(v);
			}
		}
		return ret;
	}

	private Map getObjTerms(IMIP mip) {
		Map ret = new HashMap();
		for (Iterator iter = mip.getObjectiveTerms(); iter.hasNext();) {
			Term t = (Term) iter.next();
			Variable v = mip.getVar(t.getVarName());
			if (!v.ignore()) {
				ret.put(t.getVarName(), t);
			}
		}
		return ret;
	}

	private List getConstraints(IMIP mip) {
		List ret = new ArrayList(mip.getNumConstraints());
		for (Iterator iter = mip.getConstraints(); iter.hasNext();) {
			ret.add(iter.next());
		}
		return ret;
	}

	private List getTerms(IMIP mip, List activeVars, Constraint c) {
		List ret = new ArrayList();
		Map varToTerm = new HashMap();
		for (Iterator iter = c.iterator(); iter.hasNext();) {
			Term t = (Term) iter.next();
			varToTerm.put(mip.getVar(t.getVarName()), t);
		}
		for (int i = 0; i < activeVars.size(); i++) {
			ret.add(varToTerm.get(activeVars.get(i)));
		}
		return ret;
	}

	private int getType(CompareType type) {
		if (type == CompareType.EQ) {
			return LpSolve.EQ;
		}
		if (type == CompareType.GEQ) {
			return LpSolve.GE;
		}
		if (type == CompareType.LEQ) {
			return LpSolve.LE;
		}
		throw new RuntimeException("Unknown type: " + type);
	}

	public static void main(String argv[]) {
		if (argv.length != 1) {
			System.err
				.println("Usage: edu.harvard.econcs.jopt.solver.server.cplex.LPSolveMIPSolver <port>");
			System.exit(1);
		}
		int port = Integer.parseInt(argv[0]);
		SolverServer.createServer(port, LPSolveMIPSolver.class);
	}
}
