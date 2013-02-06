/**
 * MyXYTaskDataset
 * http://www.jfree.org/phpBB2/viewtopic.php?f=3&t=26888&p=75060&hilit=xytaskdataset&sid=f0e3dc236378e81a832044df1b8ba13f
 */
package org.jfree.chart;

import java.util.*;

import org.jfree.chart.axis.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.gantt.*;
import org.jfree.data.time.*;
import org.jfree.data.xy.*;

/**
 * A dataset implementation that wraps a {@link TaskSeriesCollection} and presents it as an {@link IntervalXYDataset},
 * allowing a set of tasks to be displayed using an {@link XYBarRenderer} (and usually a {@link SymbolAxis}). This is a
 * very specialised dataset implementation ---before using it, you should take some time to understand the use-cases
 * that it is designed for.
 * 
 * @since 1.0.11
 */
public class MyXYTaskDataset extends XYTaskDataset {

	private static final long serialVersionUID = -8437432737098552565L;

	/** A flag that controls whether or not the resource is given by the serie or the index in a serie. */
	private boolean inverted = false;

	/**
	 * Creates a new dataset based on the supplied collection of tasks.
	 * 
	 * @param tasks
	 *            the underlying dataset (<code>null</code> not permitted).
	 */
	public MyXYTaskDataset(TaskSeriesCollection tasks) {
		super(tasks);
	}

	public final boolean isInverted() {
		return inverted;
	}

	public final void setInverted(boolean inverted) {
		this.inverted = inverted;
		fireDatasetChanged();
	}

	/**
	 * Returns the x-value (as a double primitive) for an item within a series.
	 * 
	 * @param series
	 *            the series index (zero-based).
	 * @param item
	 *            the item index (zero-based).
	 * 
	 * @return The value.
	 */
	@Override
	public double getXValue(int series, int item) {
		if (!isTransposed()) {
			return getIndexValue(series, item);
		} else {
			return getItemValue(series, item);
		}
	}

	/**
	 * Returns the starting date/time for the specified item (task) in the given series, measured in milliseconds since
	 * 1-Jan-1970 (as in java.util.Date).
	 * 
	 * @param series
	 *            the series index.
	 * @param item
	 *            the item (or task) index.
	 * 
	 * @return The start date/time.
	 */
	@Override
	public double getStartXValue(int series, int item) {
		if (!isTransposed()) {
			return getIndexStartValue(series, item);
		} else {
			return getItemStartValue(series, item);
		}
	}

	/**
	 * Returns the ending date/time for the specified item (task) in the given series, measured in milliseconds since
	 * 1-Jan-1970 (as in java.util.Date).
	 * 
	 * @param series
	 *            the series index.
	 * @param item
	 *            the item (or task) index.
	 * 
	 * @return The end date/time.
	 */
	@Override
	public double getEndXValue(int series, int item) {
		if (!isTransposed()) {
			return getIndexEndValue(series, item);
		} else {
			return getItemEndValue(series, item);
		}
	}

	/**
	 * Returns the y-value (as a double primitive) for an item within a series.
	 * 
	 * @param series
	 *            the series index (zero-based).
	 * @param item
	 *            the item index (zero-based).
	 * 
	 * @return The value.
	 */
	@Override
	public double getYValue(int series, int item) {
		if (!isTransposed()) {
			return getItemValue(series, item);
		} else {
			return getIndexValue(series, item);
		}
	}

	/**
	 * Returns the starting value of the y-interval for an item in the given series.
	 * 
	 * @param series
	 *            the series index.
	 * @param item
	 *            the item (or task) index.
	 * 
	 * @return The y-interval start.
	 */
	@Override
	public double getStartYValue(int series, int item) {
		if (!isTransposed()) {
			return getItemStartValue(series, item);
		} else {
			return getIndexStartValue(series, item);
		}
	}

	/**
	 * Returns the ending value of the y-interval for an item in the given series.
	 * 
	 * @param series
	 *            the series index.
	 * @param item
	 *            the item (or task) index.
	 * 
	 * @return The y-interval end.
	 */
	@Override
	public double getEndYValue(int series, int item) {
		if (!isTransposed()) {
			return getItemEndValue(series, item);
		} else {
			return getIndexEndValue(series, item);
		}
	}

	private double getIndexValue(int series, int item) {
		return isInverted() ? item : series;
	}

	private double getIndexStartValue(int series, int item) {
		return getIndexValue(series, item) - this.getSeriesWidth() / 2.0;
	}

	private double getIndexEndValue(int series, int item) {
		return getIndexValue(series, item) + this.getSeriesWidth() / 2.0;
	}

	private double getItemValue(int series, int item) {
		TaskSeries s = this.getTasks().getSeries(series);
		Task t = s.get(item);
		TimePeriod duration = t.getDuration();
		Date start = duration.getStart();
		Date end = duration.getEnd();
		return (start.getTime() + end.getTime()) / 2.0;
	}

	private double getItemStartValue(int series, int item) {
		TaskSeries s = this.getTasks().getSeries(series);
		Task t = s.get(item);
		TimePeriod duration = t.getDuration();
		Date start = duration.getStart();
		return start.getTime();
	}

	private double getItemEndValue(int series, int item) {
		TaskSeries s = this.getTasks().getSeries(series);
		Task t = s.get(item);
		TimePeriod duration = t.getDuration();
		Date end = duration.getEnd();
		return end.getTime();
	}

	/**
	 * Tests this dataset for equality with an arbitrary object.
	 * 
	 * @param obj
	 *            the object (<code>null</code> permitted).
	 * 
	 * @return A boolean.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof MyXYTaskDataset)) {
			return false;
		}
		MyXYTaskDataset that = (MyXYTaskDataset) obj;
		return super.equals(obj) && this.isInverted() == that.isInverted();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		MyXYTaskDataset clone = (MyXYTaskDataset) super.clone();
		clone.setInverted(this.isInverted());
		return clone;
	}
}
