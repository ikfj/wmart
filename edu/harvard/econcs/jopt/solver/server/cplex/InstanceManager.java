package edu.harvard.econcs.jopt.solver.server.cplex;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;

import java.util.HashSet;
import java.util.Set;

import edu.harvard.econcs.jopt.solver.MIPException;
import edu.harvard.econcs.util.Log;

public class InstanceManager {
	private static Log log = new Log(InstanceManager.class);

	private static int numSimultaneous = 10;
	private static InstanceManager instance = new InstanceManager();
	
	private Set available = new HashSet();
	private Set inUse = new HashSet();
	
	private InstanceManager() {}

	public static InstanceManager getInstance() {
		return instance;
	}
	
	public static void setNumSimultaneous(int numSimultaneous) {
		InstanceManager.numSimultaneous = numSimultaneous;
	}

	public synchronized IloCplex checkOutCplex() {
		while (notAvailable()) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		IloCplex cplex = getCplex();
		inUse.add(cplex);
		return cplex;
	}
	
	private boolean notAvailable() {
		return inUse.size() >= numSimultaneous;
	}

	private IloCplex getCplex() {
		for(int i=0; i<100; i++) {
			if (!available.isEmpty()) {
				IloCplex ret = (IloCplex)available.iterator().next();
				available.remove(ret);
				return ret;
			}
			if(available.size()+inUse.size()<numSimultaneous) {
				IloCplex ret = createCplex();
				if (ret!=null) {
					return ret;
				}
			}
		}
		throw new MIPException("Could not obtain cplex instance");
	}
	
	private IloCplex createCplex() {
		for (int i=0; i<10; i++) {
			try {
				IloCplex cplex;
				cplex = new IloCplex();
				return cplex;
			} catch (IloException ex) {
				if(i<9) {
					log.warn("Could not get CPLEX instance, retrying", ex);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					log.warn("Could not get CPLEX instance, giving up", ex);
					return null;
				}
			}
		}
		return null;
	}
	
	public synchronized void checkInCplex(IloCplex cplex) {
		if (cplex == null) {
			return;
		}
		try {
			cplex.clearModel();
		} catch (IloException e) {
			log.error("Exception clearing model: " + e.getMessage(), e);
			cplex.end();
			inUse.remove(cplex);
			this.notify();
			return;
		}
		inUse.remove(cplex);
		available.add(cplex);
		this.notify();
	}
}
