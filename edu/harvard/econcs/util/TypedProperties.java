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
package edu.harvard.econcs.util;

import java.util.Properties;

/**
 * Special type of properties object that knows how to cast to
 * primitive types.
 * 
 * @author Last modified by $Author: jeffsh $
 * @version $Revision: 1.4 $ on $Date: 2005/10/17 18:49:45 $
 * @since Mar 15, 2004
 **/
public class TypedProperties extends Properties {
	
	private static final long serialVersionUID = 3761403118801139760L;
	private static Log log = new Log(TypedProperties.class);
	
	public String getString(String name, String def){
		return getProperty(name,def);
	}
	
	public int getInt(String name, int def){
		try{
			String val=getProperty(name);
			if(val==null)
				return def;
			return Integer.parseInt(val);
		}catch(NumberFormatException nfe){
			log.warn("Expected integer in property: "+name);
		}
		return def;
	}
	
	public long getLong(String name, long def){
		try{
			String val=getProperty(name);
			if(val==null)
				return def;
			return Long.parseLong(val);
		}catch(NumberFormatException nfe){
			log.warn("Expected long in property: "+name);
		}
		return def;
	}
	
	/*
	 * While this code is likely correct, I don't want people using floats at all.
	 * Use double instead.
	 *
	public float getFloat(String name, float def){
		try{
			String val=getProperty(name);
			if(val==null)
				return def;
			return Float.parseFloat(val);
		}catch(NumberFormatException nfe){
			log.warn("Expected float in property: "+name);
		}
		return def;
	}
	*/
	
	public double getDouble(String name, double def){
		try{
			String val=getProperty(name);
			if(val==null)
				return def;
			return Double.parseDouble(val);
		}catch(NumberFormatException nfe){
			log.warn("Expected double in property: "+name);
		}
		return def;
	}
	
	public boolean getBoolean(String name, boolean def){
		String val=getProperty(name);
		if(val==null)
			return def;
		val=val.toLowerCase();
		if(val.equals("1")||
				val.equals("y")||
				val.equals("yes")||
				val.equals("t")||
				val.equals("true"))
			return true;
		else if(val.equals("0")||
				val.equals("n")||
				val.equals("no")||
				val.equals("f")||
				val.equals("false"))
			return false;
		else
			log.warn("Expected boolean in property: "+name);
		return def;
	}
	
	public int getEnum(String name, String[] enumArray, int def){
		String val=getProperty(name);
		if(val!=null){
			for(int i=0;i<enumArray.length;i++){
				if(enumArray[i].equals(val))
					return i;
			}
		}
		return def;
	}	
}
