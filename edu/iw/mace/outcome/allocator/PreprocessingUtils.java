/*
 * jCase - Java Combinatorial Auction Simulator 
 * Copyright (C) 2004-2006 Bjoern Schnizler, University of Karlsruhe (TH)
 * http://www.iw.uni-karlsruhe.de/jcase
 *
 * Parts of this work are funded by the European Union
 * under the IST project CATNETS (http://www.catnets.org/)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or any later version.
 * See the GNU General Public License for more details.
 *
 * This code comes WITHOUT ANY WARRANTY
 */

package edu.iw.mace.outcome.allocator;

import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import edu.iw.mace.environment.Bundle;
import edu.iw.mace.environment.BundlePool;
import edu.iw.mace.environment.Good;
import edu.iw.mace.environment.GoodPool;
import edu.iw.mace.environment.MaceException;
import edu.iw.mace.order.AbstractBundleOrder;
import edu.iw.mace.order.AbstractOrder;
import edu.iw.mace.order.AbstractOrderPool;
import edu.iw.mace.order.OrderBook;
import edu.iw.utils.patterns.Entity;

/**
 * This class provides algorithms which are applied to "simplify" a given order
 * set. After that, "easier" problems can be solved by the winner determination
 * implementation.
 * 
 * Created: 14.11.2005
 * 
 * @author Björn Schnizler, University of Karlsruhe (TH)
 * @version 1.0
 */

public class PreprocessingUtils {

	private static Logger log = Logger.getLogger(PreprocessingUtils.class);

	/**
	 * Identifies disjunctive sets in a given orderbook. Suppose three bids with
	 * "A", "AB", "C" can be divided into "A","AB" (order book 1) and "C" (order
	 * book 2)
	 * 
	 * @param orderBook
	 *            An orderbook
	 * @return Vector containing GoodPools. These Goodpools include the
	 *         dependent goods.
	 */
	public static Vector<GoodPool> getDisjunctiveSets(OrderBook orderBook)
			throws MaceException {
		Vector<GoodPool> result = new Vector<GoodPool>();
		// Find the sets for the buyer orderbook
		log.debug("Trying to find disjunctive sets for the buyers");
		parseOrderPool(result, orderBook.getBuyerOrderPool());
		// Find the sets for the seller orderbook
		log.debug("Trying to find disjunctive sets for the sellers");
		parseOrderPool(result, orderBook.getSellerOrderPool());

		return result;
	}

	/**
	 * Parses a given orderpool and adds goods to dependent buckets (HashMaps)
	 * 
	 * @param result
	 *            Vector containing the existing good pools. Each vector
	 *            contains goods that are disjunctive, i.e. that can be split
	 *            into several order books
	 * @param orderPool
	 */
	private static void parseOrderPool(Vector<GoodPool> result,
			AbstractOrderPool orderPool) throws MaceException {
		// Parse orders
		for (Iterator<Entity> orderPoolIterator = orderPool
				.getValueIterator(); orderPoolIterator.hasNext();) {
			AbstractOrder order = (AbstractOrder) orderPoolIterator.next();
			// Iterate over all atomic bids
			for (Iterator<AbstractBundleOrder> iterator = order
					.getBundleOrders().values().iterator(); iterator.hasNext();) {
				AbstractBundleOrder bundleOrder = iterator.next();
				Bundle bundle = bundleOrder.getBundle();
				Vector<GoodPool> buckets = new Vector<GoodPool>();
				// For each good, find a bucket in which it will be stored
				// The intuition is that different buckets represent different
				// (and disjunctive) order books
				for (int c = 0; c < bundle.size(); c++) {
					// Get the bucket
					GoodPool bucket = getBucket(result, bundle.getGood(c));
					if (bucket != null) {
						buckets.add(bucket);
					} else {
						bucket = new GoodPool();
						bucket.addGood(bundle.getGood(c));
						buckets.add(bucket);
						result.add(bucket);
					}
				}
				// If only one bucket is found, add the bundle (goods) to that
				if (buckets.size() == 1) {
					addBundleToBucket(buckets.get(0), bundle);
				} else {
					// If more than one bucket is found, merge the buckets into
					// a new one
					GoodPool newBucket = new GoodPool();
					// Merge buckets
					for (int c = 0; c < buckets.size(); c++) {
						GoodPool resultBucket = buckets.get(c);
						newBucket.addEntityPool(resultBucket);
						result.remove(resultBucket);
					}
					result.add(newBucket);
					// Add the bundle to the new bucket
					addBundleToBucket(newBucket, bundle);
				}
			}
		}
	}

	/**
	 * Adds all goods of a given bundle to a given bucket
	 * 
	 * @param bucket
	 *            Destination bucket
	 * @param bundle
	 *            Bundles including goods to be added
	 */
	private static void addBundleToBucket(GoodPool bucket, Bundle bundle)
			throws MaceException {
		for (int a = 0; a < bundle.size(); a++) {
			// Add the good - if it is not already in the bucket
			if (!bucket.contains(bundle.getGood(a))) {
				bucket.addGood(bundle.getGood(a));
			}
		}
	}

	/**
	 * Returns the bucket in which a given good is stored
	 * 
	 * @param buckets
	 *            Existing maps
	 * @param good
	 *            Good
	 * @return GoodPool (as a bucket) containing the good, or <NULL> if no map
	 *         is found.
	 */
	private static GoodPool getBucket(Vector buckets, Good good) {
		int bucketSize = buckets.size();
		for (int a = 0; a < bucketSize; a++) {
			GoodPool bucket = (GoodPool) buckets.get(a);
			if (bucket.contains(good)) {
				return bucket;
			}
		}
		return null;
	}

	/**
	 * Stores "relevant" bundles of a given orderPool into a bundle pool. A
	 * bundle is relevant if there is a bid on it.
	 * 
	 * @param destination
	 *            Destination bundlePool
	 * @param orderPool
	 *            Orderpool to parse
	 */
	public static void findRelevantBundles(BundlePool destination,
			AbstractOrderPool orderPool) throws MaceException {
		for (Iterator<Entity> orderPoolIterator = orderPool
				.getValueIterator(); orderPoolIterator.hasNext();) {
			AbstractOrder order = (AbstractOrder) orderPoolIterator.next();
			// Iterate over all atomic bids
			for (Iterator<AbstractBundleOrder> iterator = order
					.getBundleOrders().values().iterator(); iterator.hasNext();) {
				AbstractBundleOrder bundleOrder = iterator.next();
				if (!destination.contains(bundleOrder.getBundle())) {
					destination.addBundle(bundleOrder.getBundle());
				}
			}
		}
	}

}
