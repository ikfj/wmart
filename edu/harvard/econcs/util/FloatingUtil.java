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

 /** 
 * Mipinator.MaxValue is 29 bits. Java double representation is 52 bits for the mantissa.
 * This leaves 23 bits for the floating point part, at worst. So at worst, this means
 * that values this huge have a (2^-23)/2 = 0.000000059604644775390625 representation error.
 * With an epsilon of .001f, this means that we can have ~16,000 operations with max error and
 * still be ok. This seems like a lot and probably fine for the exchange.
 * 
 * @author Benjamin Lubin; Last modified by $Author: blubin $
 * @version $Revision: 1.9 $ on $Date: 2006/08/23 20:17:58 $
 * @since Dec 2, 2004
 **/
public class FloatingUtil {
	
	/** 0.001953125 == (1/512) == 1/(2^-9) -> power of two representable cleanly in floating point. */
	//public static double EPSILON = 0.001953125;
    public static double EPSILON = 1e-6;
	
	public static boolean equal(double a, double b) {
		return Math.abs(a-b) <= EPSILON;		
	}

	public static boolean equal(double a, double b, double eps) {
		return Math.abs(a-b) <= eps;		
	}

	public static boolean greater(double a, double b) {
        return a > b + EPSILON; 
	}
	
	public static boolean greater(double a, double b, double eps) {
        return a > b + eps; 
	}
	
	public static boolean less(double a, double b) {
		return a < b - EPSILON;
	}

	public static boolean less(double a, double b, double eps) {
		return a < b - eps;
	}

	public static boolean greaterEqual(double a, double b) {
        return a >= b - EPSILON;
	}
	
	public static boolean greaterEqual(double a, double b, double eps) {
        return a >= b - eps;
	}

	public static boolean lessEqual(double a, double b) {
		return a <= b + EPSILON;
	}

	public static boolean lessEqual(double a, double b, double eps) {
		return a <= b + eps;
	}

}