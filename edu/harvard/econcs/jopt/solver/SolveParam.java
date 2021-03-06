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


package edu.harvard.econcs.jopt.solver;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * These parameters control how JOpt operates, and how the backend solver operates.
 * You can control a number of parameters. Not all of these will be supported by
 * your chosen backend solver, unfortunently. But we try to map these settings
 * into relevant solver settings. See each setting to see if they are applicable
 * to CPLEX, LPSOLVE, or ALLSOLVERS
 * 
 * @author Benjamin Lubin; Last modified by $Author: blubin $
 * @version $Revision: 1.14 $ on $Date: 2006/08/30 00:53:53 $
 * @since Apr 19, 2005
 **/
public class SolveParam implements Serializable {
	
	/* These are types for our parameters */
	private static final byte INT = 0;
	private static final byte DOUBLE = 1;
	private static final byte BOOLEAN = 2;
	private static final byte STRING = 3;

	// ENUMS go here:
	/////////////////
	
	/**Measure timing using CPU=1, wall clock=2 [CPLEX]**/
	public static final SolveParam CLOCK_TYPE = new SolveParam(0, INT, "ClockType");
	
	/**Maximum time to run solver before returning current best in seconds [CPLEX]**/
	public static final SolveParam TIME_LIMIT = new SolveParam(1, DOUBLE, "TimeLimit");
	
	/**Amount of barrier progress information to be displayed [CPLEX]**/
	public static final SolveParam BARRIER_DISPLAY = new SolveParam(2, INT, "BarrierDisplay");

	/**
	 * Min objective value for maximization problems (specified to assist cutting).
	 * Set this too high --> No Feasible Solution [CPLEX]
	 **/
	public static final SolveParam MIN_OBJ_VALUE = new SolveParam(3, DOUBLE, "MinObjValue");
	
	/**
	 * Max objective value for minimization problems (specified to assist cutting).
	 * Set this too low --> No Feasible Solution [CPLEX]
	 **/
	public static final SolveParam MAX_OBJ_VALUE = new SolveParam(4, DOUBLE, "MaxObjValue");

	/**Optimality Tolerance [CPLEX] **/
	public static final SolveParam OBJ_TOLERANCE = new SolveParam(5, DOUBLE, "ObjTolerance");
	
	/**Optimization stops when current solution within this parameter of LP relaxiation [CPLEX]**/
	public static final SolveParam ABSOLUTE_OBJ_GAP = new SolveParam(6, DOUBLE, "AbsoluteObjGap");

	/**Optimization stops when current solution within this percentage of LP relaxiation [CPLEX]**/
	public static final SolveParam RELATIVE_OBJ_GAP = new SolveParam(7, DOUBLE, "RelativeObjGap");
	
	/**How close a double must be to an int to be considered an int [CPLEX]**/
	public static final SolveParam ABSOLUTE_INT_GAP = new SolveParam(8, DOUBLE, "AbsoluteIntGap");
	
	/**Degree to which variables may violate their bounds [CPLEX]**/
	public static final SolveParam ABSOLUTE_VAR_BOUND_GAP = new SolveParam(9, DOUBLE, "AbsoluteVarBoundGap");
		
	/**Which optimization algorithm to use. [CPLEX]**/
	public static final SolveParam LP_OPTIMIZATION_ALG = new SolveParam(10, INT, "LPOptimizationAlg");

	/**Specify what to display when solving a MIP [CPLEX]<p>
	 * 
	 * From the Cplex 8.1 Documentation (see that documentation for better context):<p>
	 * 
		0 No display<br>
		1 Display integer feasible solutions<br>
		2 Display nodes under CPX_PARAM_MIPINTERVAL<br>
		3 Same as 2 with information on node cuts<br>
		4 Same as 3 with LP subproblem information at root<br>
		5 Same as 4 with LP subproblem information at
		nodes<br>
		Default: 2<br>
		Description: MIP node log display information.<br>
		Determines what CPLEX reports to the screen during mixed integer optimization. The amount of information displayed increases with
		increasing values of this parameter. A setting of 0 causes no node log to be displayed until the optimal solution is found. A setting of
		1 displays an entry for each integer feasible solution found. Each entry contains the objective function value, the node count, the
		number of unexplored nodes in the tree, and the current optimality gap. A setting of 2 also generates an entry for every nth node
		(where n is the setting of the MIP INTERVAL parameter). A setting of 3 additionally generates an entry for every nth node giving the
		number of cuts added to the problem for the previous INTERVAL nodes. A setting of 4 additionally generates entries for the LP root
		relaxation according to the 'SET SIMPLEX DISPLAY' setting. A setting of 5 additionally generates entries for the LP subproblems,
		also according to the 'SET SIMPLEX DISPLAY' setting.
	 * 
	 * **/
	public static final SolveParam MIP_DISPLAY = new SolveParam(11, INT, "MIPDisplay");
	
	/**
	 * Optimality vs. Feasibility heuristic for MIP solving.
	 * 0=balanced, 1=feasibility, 2=optimality, 3=moving best bound. [CPLEX]
	 **/
	public static final SolveParam MIP_EMPHASIS = new SolveParam(12, INT, "MIPEmphasis");

	/**Check starting values for feasibility? [CPLEX]**/
	public static final SolveParam CHECK_INIT_VALUE_FEASIBILITY = new SolveParam(13, BOOLEAN, "CheckInitValueFeasibility");
	
	/**For Minimization problems, stop when this value has been exceeded [CPLEX]**/
	public static final SolveParam MIN_OBJ_THRESHOLD = new SolveParam(14, DOUBLE, "MinObjThreshold");
	
	/**For Maximization problems, stop when this value has been exceeded [CPLEX]**/
	public static final SolveParam MAX_OBJ_THRESHOLD = new SolveParam(15, DOUBLE, "MaxObjThreshold");

	/**Working directory for working files for the optimzer [CPLEX]**/
	public static final SolveParam WORK_DIR = new SolveParam(16, STRING, "WorkDir");
	
	/**Threads to use in solving a MIP [CPLEX]**/
	public static final SolveParam THREADS = new SolveParam(17, INT, "Threads");
		
	// Internal variables
	/////////////////////
	/** Name of mipInstance file - set to empty string if you don't want to write one out [CPLEX]**/
	public static final SolveParam PROBLEM_FILE = new SolveParam(101, STRING, "ProblemFile", true);	
	
	/** If given a set of proposed starting values, should we set missing values to be zero [CPLEX]**/
	public static final SolveParam ZERO_MISSING_PROPOSED = new SolveParam(102, BOOLEAN, "ZeroMissingProposed", true);

	/** Maximum amount to backoff and retry solving mip with looser constraint tolerance. CPlex max is .1, .1 is default.  typically either .1 or 0 **/
	public static final SolveParam CONSTRAINT_BACKOFF_LIMIT = new SolveParam(103, DOUBLE, "ConstraintBackoffLimit", true);
	
	/** Should we attempt to determine conflcit set? **/
	public static final SolveParam CALCULATE_CONFLICT_SET = new SolveParam(104, BOOLEAN, "CalculateConflictSet", true);
	
	/** Display output? **/
	public static final SolveParam DISPLAY_OUTPUT = new SolveParam(105, BOOLEAN, "DisplayOutput", true);

	//Other stuff below:
	////////////////////
	
	private static final long serialVersionUID = 200505191821l;
	
	private int enumUID;
	private String name;
	private byte type;
	
	/** if set, isInternal signfifies that this is a JOpt parameter; doesn't control solver. */
	private boolean isInternal;

	private SolveParam(int enumUID, byte type, String name, boolean isInternal) {
		this.enumUID = enumUID;
		this.name = name;
		this.type = type;
		this.isInternal = isInternal;
	}	
	
	private SolveParam(int enumUID, byte type, String name) {
		this(enumUID, type, name, false);
	}

	/** Make serialization work. **/
	private Object readResolve () throws ObjectStreamException
    {
        switch(enumUID) {
        	case 0:
        		return CLOCK_TYPE;    
        	case 1:
        		return TIME_LIMIT;
        	case 2:
        		return BARRIER_DISPLAY;
        	case 3:
        		return MIN_OBJ_VALUE;
        	case 4:
        		return MAX_OBJ_VALUE;
        	case 5:
        		return OBJ_TOLERANCE;        	
        	case 6:
        		return ABSOLUTE_OBJ_GAP;
        	case 7:
        		return RELATIVE_OBJ_GAP;
        	case 8:
        		return ABSOLUTE_INT_GAP;
        	case 9:
        		return ABSOLUTE_VAR_BOUND_GAP;
        	case 10:
        		return LP_OPTIMIZATION_ALG;
        	case 11:
        		return MIP_DISPLAY;
        	case 12:
        		return MIP_EMPHASIS;
        	case 13:
        		return CHECK_INIT_VALUE_FEASIBILITY;
        	case 14:
        		return MIN_OBJ_THRESHOLD;
        	case 15:
        		return MAX_OBJ_THRESHOLD;
        	case 16:
        		return WORK_DIR;
        	case 17:
        		return THREADS;
        	case 101:
				return PROBLEM_FILE;
        	case 102:
				return ZERO_MISSING_PROPOSED;
        	case 103:
        		return CONSTRAINT_BACKOFF_LIMIT;
        	case 104:
        		return CALCULATE_CONFLICT_SET;
        	case 105:
        		return DISPLAY_OUTPUT;
        }
        throw new InvalidObjectException("Unknown enum: " + enumUID);
    }
	
	public String toString() {
		return name;
	}
	
	public boolean isInteger() {
		return type == INT;
	}

	public boolean isDouble() {
		return type == DOUBLE;
	}

	public boolean isBoolean() {
		return type == BOOLEAN;
	}

	public boolean isString() {
		return type == STRING;
	}

	public boolean isInternal() {
		return isInternal == true;
	}	
	
	public String getTypeDescription() {
		switch(type) {
			case INT:
				return "Integer";
			case DOUBLE:
				return "Double";
			case BOOLEAN:
				return "Boolean";
			case STRING:
				return "String";
			default:
				return "Unknown type: " + type;
		}
	}
	
	public boolean equals(Object obj) {
		return obj != null && 
		       obj.getClass().equals(SolveParam.class) &&
			   ((SolveParam)obj).enumUID == enumUID;
	}

	public int hashCode() {
		return enumUID;
	}

}
