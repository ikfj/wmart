package edu.iw.mace.environment;

import edu.iw.utils.MathUtils;

/**
 * Represents rime range including time slots
 * 
 * 2009/03/15
 * 
 * @author Ikki Fujiwara, NII
 * @version 1.2
 */
public class TimeRange {

	private int minTimeSlot;
	private int maxTimeSlot;

	/**
	 * Empty constructor
	 */
	public TimeRange() {
		this(MathUtils.INF_NUMBER, -1);
	}

	/**
	 * Constructor
	 * 
	 * @param minTimeSlot
	 * @param maxTimeSlot
	 */
	public TimeRange(int minTimeSlot, int maxTimeSlot) {
		this.minTimeSlot = minTimeSlot;
		this.maxTimeSlot = maxTimeSlot;
	}

	public void addTimeSlot(int timeSlot) throws MaceException {
		if (timeSlot < 0 || timeSlot >= MathUtils.INF_NUMBER) {
			throw new MaceException("TimeSlot should be between 0 and "
					+ (MathUtils.INF_NUMBER - 1));
		}
		if (timeSlot < minTimeSlot) {
			minTimeSlot = timeSlot;
		}
		if (timeSlot > maxTimeSlot) {
			maxTimeSlot = timeSlot;
		}
	}
	
	public void addTimeRange(int minTimeSlot, int maxTimeSlot) throws MaceException {
		addTimeSlot(minTimeSlot);
		addTimeSlot(maxTimeSlot);
	}

	/**
	 * @return the minTimeSlot
	 */
	public int getMinTimeSlot() {
		return minTimeSlot;
	}

	/**
	 * @param minTimeSlot the minTimeSlot to set
	 */
	public void setMinTimeSlot(int minTimeSlot) {
		this.minTimeSlot = minTimeSlot;
	}

	/**
	 * @return the maxTimeSlot
	 */
	public int getMaxTimeSlot() {
		return maxTimeSlot;
	}

	/**
	 * @param maxTimeSlot the maxTimeSlot to set
	 */
	public void setMaxTimeSlot(int maxTimeSlot) {
		this.maxTimeSlot = maxTimeSlot;
	}

	/*
	 * (”ñ Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = new String();
		str = "TimeRange(" + minTimeSlot + "," + maxTimeSlot + ")";
		return str;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TimeRange tr = new TimeRange();
		System.out.println(tr);
		try {
			tr.addTimeSlot(10);
			System.out.println(tr);
			tr.addTimeSlot(40);
			System.out.println(tr);
			tr.addTimeSlot(15);
			System.out.println(tr);
		} catch (MaceException e) {
			e.printStackTrace();
		}
	}
}
