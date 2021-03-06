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


package edu.harvard.econcs.jopt.solver.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import edu.harvard.econcs.jopt.solver.IMIP;
import edu.harvard.econcs.jopt.solver.IMIPResult;
import edu.harvard.econcs.jopt.solver.IMIPSolver;
import edu.harvard.econcs.jopt.solver.MIPException;
import edu.harvard.econcs.jopt.solver.server.IRemoteMIPSolver;
import edu.harvard.econcs.jopt.solver.server.ISolverServer;
import edu.harvard.econcs.util.Log;
import edu.harvard.econcs.util.TypedProperties;

/**
 * Local class that can be used to solve a MIP.  Finds a remote Solver to actually do the
 * calculation.
 * 
 * @author Last modified by $Author: jeffsh $
 * @version $Revision: 1.8 $ on $Date: 2005/10/17 18:49:45 $
 * @since Apr 12, 2004
 **/
public class SolverClient implements IMIPSolver{
	public static String DEFAULT_SOLVER = 
		"edu.harvard.econcs.jopt.solver.server.lpsolve.LPSolveMIPSolver";
//		"edu.harvard.econcs.jopt.solver.server.cplex.CPlexMIPSolver";
	public static String SOLVER_CPLEX="edu.harvard.econcs.jopt.solver.server.cplex.CPlexMIPSolver";
	public static String SOLVER_LPSOLVE51="edu.harvard.econcs.jopt.solver.server.lpsolve.LPSolve51MIPSolver";
	public static String SOLVER_LPSOLVE55="edu.harvard.econcs.jopt.solver.server.lpsolve.LPSolve55MIPSolver";
	
	protected static Log log = new Log(SolverClient.class);
	protected IMIPSolver solver;
		
	public SolverClient(TypedProperties props) {
		this(getSolverForProps(props));
	}
	
	/** Creates a default local solver **/
	public SolverClient() {
		this(getLocalSolver(DEFAULT_SOLVER));
	}
	
	/** Creates the given local solver **/
	public SolverClient(String className) {
		this(getLocalSolver(className));
	}

	/** 
	 * Creates a client using the server at the given host and port.
	 * This server can be either a a server or a load balancer.
	 **/
	public SolverClient(String host, int port) {
		this(getRemoteSolver(host, port));
	}

	/**
	 * create a client using the given solver explicitly.
	 **/
	public SolverClient(IMIPSolver solver) {
		this.solver = solver;
	}
		
	public String getName() {
		if (solver instanceof ClientSolver) {
			return ((ClientSolver)solver).getName();
		}
		return "Local";
	}
	
	public String getHost() {
		if (solver instanceof ClientSolver) {
			return ((ClientSolver)solver).getHost();
		}
		return null;
	}
	
	public int getPort() {
		if (solver instanceof ClientSolver) {
			return ((ClientSolver)solver).getPort();
		}
		return -1;
	}
	
	/**
	 * Solve the given mip
	 **/
	public IMIPResult solve(IMIP mip) throws MIPException {
		if (log.isDebugEnabled()) {
			log.debug(mip.toString());
		}
		return solver.solve(mip);
	}
		
	/**
	 * Solve the given serialized mip
	 **/
	public IMIPResult solve(byte[] serializedMip) {
		if (solver instanceof ClientSolver) {
			//If its a client solver, it knows how to solve SerializedMIPs
			//itself
			return ((ClientSolver)solver).solve(serializedMip);
		}
		//Otherwise, we need to deserialize for our solver:
		ObjectInputStream ois;
		IMIP mipObj = null;
		try {
			log.trace("Begin de-serialization of " + serializedMip.length);
			long time = System.currentTimeMillis();
			ois = new ObjectInputStream(new ByteArrayInputStream(serializedMip));
			mipObj = (IMIP)ois.readObject();
			time = System.currentTimeMillis() - time;
			log.trace("Finished de-serialiation in " + time + " millis.");
		} catch (IOException e) {
			throw new MIPException("Serialization error", e);
		} catch (ClassNotFoundException e) {
			throw new MIPException("Serialization error", e);			
		}
		
		// For scalability experiment
		//long time = System.currentTimeMillis();
		IMIPResult ret = solver.solve(mipObj);
		//time = System.currentTimeMillis() - time;
		//putData(time + "");*/
		
		return ret;
	}

	protected static IMIPSolver getSolverForProps(TypedProperties props) {
		if (props.getBoolean("SOLVE_LOCAL", false)) {
			log.info("Using local solver");
			return getLocalSolver(props.getString("solver", DEFAULT_SOLVER));
		} else {
			String host = props.getString("server", "econcs.eecs.harvard.edu");
			int port = props.getInt("port", 2000);
			log.info("Using remote solver: " + host + ":" + port);
			return getRemoteSolver(host, port);
		}
	}
	
	/**
	 * Get a local solver
	 */
	protected static IMIPSolver getLocalSolver(String className) {
		Class cl = null;
		try {
			cl = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new MIPException("Could not create local MIPSolver");
		}
		IMIPSolver s;
		try {
			s = (IMIPSolver)cl.newInstance();
		} catch (InstantiationException e1) {
			throw new MIPException("Could not create local CPlexMIPSolver");
		} catch (IllegalAccessException e1) {
			throw new MIPException("Could not create local CPlexMIPSolver");
		}
		return s;
	}
	
	protected static IMIPSolver getRemoteSolver(String host, int port) {
		return new ClientSolver(host, port);
	}

	protected static class ClientSolver implements IMIPSolver {
		private IRemoteMIPSolver solver;
		private String host;
		private int port;

		protected ClientSolver(String host, int port) {
			this.host = host;
			this.port = port;
			try {
				this.solver = getServer(host, port).getSolver();
			} catch (RemoteException e) {
				throw new MIPException("Could not create remote solver", e);
			}
		}
		
		public String getHost() {
			return host;
		}
		
		public int getPort() {
			return port;
		}
		
		public String getName() {
			return getHost() + ":"+getPort();
		}
		
		public IMIPResult solve(IMIP mip) throws MIPException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
			try {
				long time = System.currentTimeMillis();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(mip);
				time = System.currentTimeMillis() - time;
				log.trace("Serialized MIP in " + time + " millis.");
			} catch (IOException e) {
				throw new MIPException("Serialization error", e);
			}
			IMIPResult ret=null;
			long time = System.currentTimeMillis();
			ret = solve(baos.toByteArray());
			time = System.currentTimeMillis() - time;
			if (log.isDebugEnabled())
				log.debug(ret.toString());
			log.trace("Remote server solved MIP in " + time + " millis.");
			
			return ret;
		}
		
		protected static ISolverServer getServer(String host, int port) {
			log.info("Contacting Server for remote solver: " + host + ":" + port);
			//Throwable t = new Throwable();
			//t.printStackTrace();
			try {
				return (ISolverServer)Naming.lookup("//"+host+":"+port+"/"+ISolverServer.NAME);
			} catch (MalformedURLException e) {
				log.error("bad URL", e);
				throw new MIPException("Could not contact server" + e.getMessage());
			} catch (RemoteException e) {
				log.error("Can't contact server", e);
				throw new MIPException("Could not contact server: " + e.getMessage());
			} catch (NotBoundException e) {
				log.error("Can't contact server", e);
				throw new MIPException("Could not contact server" + e.getMessage());
			}
		}
		
		protected IMIPResult solve(byte[] serializedMip) {
			try {
				return solver.solve(serializedMip);
			} catch (RemoteException e) {
				throw new MIPException("Exception while contacting remote solver", e);
			}
		}
	}
	

}
