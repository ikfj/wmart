/*
 * Created on Feb 26, 2005
 *
 */
package edu.harvard.econcs.util.csv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CSVFormat {

	//column names to zero indexed column number
	public Map columns = new HashMap();
	
	public CSVFormat() {
		//
	}

	public CSVFormat(String[] format) {
		for (int i=0; i<format.length; i++) {
			addColumn(format[i]);
		}
	}
	
	public void addColumn(String name) {
		if (columns.keySet().contains(name)) {
			throw new CSVException("Format already contains column: " + name);
		}
		int n = columns.size();
		columns.put(name, new Integer(n));
	}
	
	//zero indexed
	public int getIndex(String column) {
		Integer i = (Integer)columns.get(column);
		if (i==null) {
			throw new CSVException("Unknown Column " + column +": " + columns);
		}
		return i.intValue();
	}
	
	public String[] getColumns() {
		String[] ret = new String[columns.size()];
		for(Iterator iter = columns.keySet().iterator(); iter.hasNext(); ) {
			String col = (String)iter.next();
			int c = ((Integer)columns.get(col)).intValue();
			ret[c]=col;
		}
		return ret;
	}
	
	public int getNumColumns() {
		return columns.size();
	}
}
