package edu.harvard.econcs.util.csv;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import edu.harvard.econcs.util.Log;

/**
 * A Class to support CSV file writing
 * 
 * @author Last modified by $Author: jeffsh $
 * @version $Revision: 1.3 $ on $Date: 2005/05/16 23:27:29 $
 * @since May 10, 2004
 **/
public class CSVWriter {

    private static Log log = new Log(CSVWriter.class);
    
	private PrintStream ostream;
	private List data = new ArrayList();
	private CSVFormat format;
	
	public CSVWriter(CSVFormat format, File outputfile) {
		this.format = format;
		try {
			boolean append = false;
			ostream = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputfile, append)));
		} catch (FileNotFoundException e) {
			log.error("Could not open output file: " + outputfile);
		}
		writeLine(format.getColumns());
		initData();
	}
	
	public CSVWriter(CSVFormat format, String outputDir, String fileName) {
		this.format = format;
		File outputfile = new File(outputDir, fileName);
		try {
			boolean append = false;
			ostream = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputfile, append)));
		} catch (FileNotFoundException e) {
			throw new CSVException("Could not open output file: " + outputfile);
		}
		writeLine(format.getColumns());
		initData();
	}
	
	public void flush() {
		ostream.flush();
	}
	
	public void close() {
		ostream.close();
	}
 
	public void add(String column, String d) {
		int idx = format.getIndex(column);
		data.set(idx, d);
	}
	
	public void add(String column, boolean b) {
		add(column, (b?"true":"false"));
	}
	
	public void add(String column, int i) {
		add(column, Integer.toString(i));
	}

	public void add(String column, double f) {
		add(column, Double.toString(f));
	}

	public void lineFinished() {
		String[] l =(String[])data.toArray(new String[data.size()]);
		writeLine(l);
		initData();
	}

	private void initData() {
		data.clear();
		for (int i=0; i < format.getNumColumns(); i++) {
			data.add("");
		}		
	}
	
	private void writeLine(String[] d) {
	    if (ostream != null) {
			//Don't bother with escaping for now:
			for (int i=0; i<d.length; i++) {
				if (d[i].indexOf('"') != -1) {
					// TODO: Correctly handle quotes by escaping
					throw new CSVException("String for CSVWriter cannot currently contain quotes: " + d[i]);
				} else if (d[i].indexOf(',')!=-1) {
					ostream.print('"');
					ostream.print(d[i]);
					ostream.print('"');
				} else {
					ostream.print(d[i]);
				}
				if(i==d.length-1) {
					ostream.println();
				} else{
					ostream.print(",");
				}
			}
			ostream.flush();
	    }
	}
}
