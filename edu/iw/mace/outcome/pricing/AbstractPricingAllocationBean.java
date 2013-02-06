/*
 * Created on 08.01.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.iw.mace.outcome.pricing;

import edu.iw.mace.environment.Environment;
import edu.iw.mace.outcome.Outcome;

/**
 * @author bschnizler
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractPricingAllocationBean {

    /**
     * 
     * @uml.property name="environment"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    protected Environment environment = null;

    /**
     * 
     * @uml.property name="allocation"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    protected Outcome allocation = null;

	
	/**
	 *  
	 */
	public AbstractPricingAllocationBean(Environment environment,
			Outcome allocation) {
		this.environment = environment;
		this.allocation = allocation;
	}

    /**
     * @return Returns the allocation.
     * 
     * @uml.property name="allocation"
     */
    public Outcome getAllocation() {
        return allocation;
    }

    /**
     * @param allocation
     *            The allocation to set.
     * 
     * @uml.property name="allocation"
     */
    public void setAllocation(Outcome allocation) {
        this.allocation = allocation;
    }

    /**
     * @return Returns the environment.
     * 
     * @uml.property name="environment"
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * @param environment
     *            The environment to set.
     * 
     * @uml.property name="environment"
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}