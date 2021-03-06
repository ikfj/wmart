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


package edu.harvard.econcs.jopt.solver.server;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import edu.harvard.econcs.jopt.solver.IMIPSolver;
import edu.harvard.econcs.jopt.solver.MIPException;
import edu.harvard.econcs.util.Log;

/**
 * 
 * 
 * @author Last modified by $Author: jeffsh $
 * @version $Revision: 1.3 $ on $Date: 2005/10/17 18:49:44 $
 * @since Apr 12, 2004
 **/
public class SolverServer extends UnicastRemoteObject implements ISolverServer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3977583593201872945L;
	private static Log log = new Log(SolverServer.class);
	private Class solverClass;
	
	/**
	 * Create a new Server
	 * @param port
	 * @param solverClass
	 */
	public static void createServer(int port, Class solverClass) throws MIPException {
		try {
			log.main("Binding server to port: " + port);
			Registry localreg = LocateRegistry.createRegistry(port);
			SolverServer server = new SolverServer(port, solverClass);
			localreg.bind(NAME, server);
		} catch (AccessException e) {
			throw new MIPException("Access", e);
		} catch (AlreadyBoundException e) {
			throw new MIPException("AlreadyBound",e);
		} catch (RemoteException e) {
			throw new MIPException("RemoteException",e);
		}
	}
	
	/**
	 * @param solverClass -- what class to use to create new instances 
	 * of the solver.
	 * @throws RemoteException
	 */
	protected SolverServer(int port, Class solverClass) throws RemoteException {
		super(port);
		this.solverClass = solverClass;
	}

	/**
	 * @see edu.harvard.econcs.jopt.solver.server.ISolverServer#getSolver()
	 */
	public IRemoteMIPSolver getSolver() throws RemoteException {
		log.main("Creating a new Solver Instance");
		IMIPSolver solver = null;
		try {
			solver = (IMIPSolver)solverClass.newInstance();
		} catch (InstantiationException e) {
			throw new MIPException("Could not create solver intance", e);
		} catch (IllegalAccessException e) {
			throw new MIPException("Could not create solver intance", e);
		}
		return new RemoteMIPSolver(solver);
	}
}
