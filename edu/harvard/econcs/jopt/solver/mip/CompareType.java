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

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Enum of types of comparisons that can be made.
 * 
 * @author Last modified by $Author: jeffsh $
 * @version $Revision: 1.3 $ on $Date: 2005/10/17 18:49:44 $
 * @since Apr 14, 2004
 **/
public class CompareType implements Serializable {
	
	public static final CompareType LEQ = new CompareType(0);
	public static final CompareType EQ     = new CompareType(1);
	public static final CompareType GEQ   = new CompareType(2);

	private static final long serialVersionUID = 200404141900l;
	private int type;

	private CompareType(int type) {
		this.type = type;
	}

	public String toString() {
        switch(type) {
        	case 0:
        		return " <= ";
        	case 1:
        		return " == ";
        	case 2:
        		return " >= ";
        }
        return "Unknown:ERROR";
	}
	
	/** Make serialization work. **/
	private Object readResolve () throws ObjectStreamException
    {
        switch(type) {
        	case 0:
        		return LEQ;
        	case 1:
        		return EQ;
        	case 2:
        		return GEQ;
        }
        throw new InvalidObjectException("Unknown type: " + type);
    }
	
	public boolean equals(Object obj) {
		return obj != null && 
		       obj.getClass() ==  CompareType.class &&
			   ((CompareType)obj).type == type;
	}

	public int hashCode() {
		return type;
	}
}
