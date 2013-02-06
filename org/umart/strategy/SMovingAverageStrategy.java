/*
 Copyright (c) 2000 Rikiya Fukumoto
 Copyright (c) 2002 U-Mart Project
 Permission is hereby granted, free of charge, to any person obtaining
 a copy of this software and associated documentation files (the
 "Software"), to deal in the Software without restriction, including
 without limitation the rights to use, copy, modify, merge, publish,
 distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to
 the following conditions:
 The above copyright notice and this permission notice shall be
 included in all copies or substantial portions of the Software.
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ---------------------------------------------------------------------
 A class describing an agent's org.umart.strategy, which buys or sells based on
 difference between the latest price and a moving average of the prices
 of futures. Position of the agent is also considered in decision making.
 The limit price is set randomly around the latest futures price, and
 quantity of the order is set randomly within a prescribed range.
 */

package org.umart.strategy;

import java.util.*;

public class SMovingAverageStrategy extends Strategy {

  /** Default value of maximum order quantity */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** Default value of minimum order quantity */
  public static final int DEFAULT_MIN_QUANT = 15;

  /** Default valued of maximum (long/short) position */
  public static final int DEFAULT_MAX_POSITION = 30;

  /** Default value of price used for the case where no price information from the market is valid */
  public static final int DEFAULT_NOMINAL_PRICE = 3000;

  /** Default value of reference term for short term moving average */
  public static final int DEFAULT_SHORT_REFERENCE_TERM = 10;

  /** Default value of reference term for medium term moving average */
  public static final int DEFAULT_MEDIUM_REFERENCE_TERM = 20;

  /** Random number generator */
  private Random fRandom;

  /** Maximum order quantity */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** Minimum order quantity */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** Maximum (long/short) position */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** Price used for the case where no price information from the market is valid */
  private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

  /** Reference term for short term moving average */
  private int fShortReferenceTerm = DEFAULT_SHORT_REFERENCE_TERM;

  /** Reference term for medium term moving average */
  private int fMediumReferenceTerm = DEFAULT_MEDIUM_REFERENCE_TERM;

  /* Constructor for Agent Initialization */
  public SMovingAverageStrategy(int seed) {
    /* If you need some initialization of e.g., instance variables
       write here.
     */

    fRandom = new Random(seed); // Initialization of random sequence.
  }

  /* Method of making order. If you submit a single order in one period,
     keep it as it is. If you want to submit several orders in one period,
     prepare methods other than getOrder and repeat orderRequest.
   */

  public void action(int[] spotPrices, int[] futurePrices,
                     int pos, long money, int restDay) {
    /* Class order has instance variables as follows
       order.price: limit price,
       order.quant: quantity,
       order.buysell: no order(0), sell(1) or buy(2)
     */
    /* decide 'order' (here we use method getOrder described below),
       and call method orderRequest to submit order.
       If you want to submit several orders, repeat such procedure
     */
    Order order = getOrder(spotPrices, futurePrices, pos, money, restDay);
    orderRequest(order);
  }

  /* Method of making single order. Implement your trading org.umart.strategy rewriting
     This method */
  public Order getOrder(int[] spotPrices, int[] futurePrices,
                        int pos, long money, int restDay) {
    /* Information available for trading:
       spotPrices[]: Time series of spot prices. It provides 120 elements from spotPrices[0]
       to spotPrices[119].  The element spotPrices[119] is the latest.
       futurePrices[]: Time series of futures price. It provides 60 elements from futurePrices[0]
       to futurePrices[59]. The element futurePrices[59] is the latest.
       Before opening the market, same values with spot prices are given.
       If no contract is made in the market, value -1 is given.
     pos: Position of the agent. Positive is buying position. Negative is selling.
       money: Available cash. Note that type 'long' is used because of needed precision.
       restDay: Number of remaining transaction to the closing of the market.
     */
    Order order = new Order(); // Object to return values.
    /* Calculate each moving Average of Spot Prices */
    double shortTermMA = calculateMovingAverage(spotPrices, fShortReferenceTerm,
                                                0);
    //
    if (shortTermMA < 0.0) {
      order.buysell = Order.NONE;
      return order;
    }
    double mediumTermMA = calculateMovingAverage(spotPrices,
                                                 fMediumReferenceTerm, 0);
    //
    if (mediumTermMA < 0.0) {
      order.buysell = Order.NONE;
      return order;
    }
    double previousShortTermMA = calculateMovingAverage(spotPrices,
        fShortReferenceTerm, 1);
    //
    if (previousShortTermMA < 0.0) {
      order.buysell = Order.NONE;
      return order;
    }
    double previousMediumTermMA = calculateMovingAverage(spotPrices,
        fMediumReferenceTerm, 1);
    //
    if (previousMediumTermMA < 0.0) {
      order.buysell = Order.NONE;
      return order;
    }
    // Decide BUY or SELL
    if (shortTermMA == previousShortTermMA) {
      order.buysell = Order.NONE;
      return order;
    } else if (previousShortTermMA < shortTermMA) {
      if (previousMediumTermMA > previousShortTermMA &&
          mediumTermMA > shortTermMA) {
        order.buysell = Order.NONE;
        return order;
      } else if (previousMediumTermMA < previousShortTermMA &&
                 mediumTermMA < shortTermMA) {
        order.buysell = Order.NONE;
        return order;
      } else {
        order.buysell = Order.BUY;
      }
    } else if (previousShortTermMA > shortTermMA) {
      if (previousMediumTermMA > previousShortTermMA &&
          mediumTermMA > shortTermMA) {
        order.buysell = Order.NONE;
        return order;
      } else if (previousMediumTermMA < previousShortTermMA &&
                 mediumTermMA < shortTermMA) {
        order.buysell = Order.NONE;
        return order;
      } else {
        order.buysell = Order.SELL;
      }
    }
    // Cancel decision if it may increase absolute value of the position
    if (order.buysell == Order.BUY) {
      if (pos > fMaxPosition) {
        order.buysell = Order.NONE;
        return order;
      }
    } else if (order.buysell == Order.SELL) {
      if (pos < -fMaxPosition) {
        order.buysell = Order.NONE;
        return order;
      }
    }
    /* Decide limit price */
    int latestPrice = getLatestPrice(spotPrices);
    boolean flag = false;
    int previousLatestPrice = -1;
    for (int i = spotPrices.length - 1; i >= 0; --i) {
      if (spotPrices[i] >= 0 && flag == false) {
        flag = true;
      } else if (spotPrices[i] >= 0 && flag == true) {
        previousLatestPrice = spotPrices[i];
        break;
      }
    }
    int widthOfPrice = Math.abs(latestPrice - previousLatestPrice);
    while (true) {
      if (order.buysell == Order.BUY) {
        order.price = latestPrice + widthOfPrice +
            (int) (widthOfPrice / 4 * fRandom.nextGaussian());
      } else if (order.buysell == Order.SELL) {
        order.price = latestPrice - widthOfPrice +
            (int) (widthOfPrice / 4 * fRandom.nextGaussian());
      }
      if (order.price > 0) {
        break;
      }
    } // Add normal random number around prevPrice
    // with the standard deviation a quarter of widthOfPrice.
    // If it gets negative, repeat again.
    // Decide quantity of order between [minQuant, maxQuant] randomly.
    order.quant = fMinQuant + fRandom.nextInt(fMaxQuant - fMinQuant + 1);
    // Message
    message(Order.buySellToString(order.buysell)
            + ", price = " + order.price
            + ", volume = " + order.quant
            + ", short term moving average = " + shortTermMA
            + ", medium term moving average = " + mediumTermMA
            + ", previous short term moving average = " + previousShortTermMA
            + ", previous medium term moving average = " + previousMediumTermMA);
    return order;
  }

  /* Calculate short term moving Average of Spots Prices */
  private double calculateMovingAverage(int[] prices, int referenceTerm,
                                        int startOfIndex) {
    double movingAverage = 0.0; //
    int count = 0; // consider only valid prices
    // Sum up spots prices
    for (int i = 0; i < referenceTerm; ++i) {
      // skip if the price is negative that means no contract
      if (prices[prices.length - 1 - startOfIndex - i] > 0) {
        movingAverage += prices[prices.length - 1 - startOfIndex - i];
        count++;
      }
    }
    if (count == 0) { // Make no order if all the prices are invalid
      return -1.0;
    }
    movingAverage = (int) (movingAverage / (double) count);
    return movingAverage;
  }

  /* (non-Javadoc)
   * @see org.umart.strategy.UBaseStrategy#setParameters(java.lang.String[])
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    for (int i = 0; i < args.length; ++i) {
      StringTokenizer st = new StringTokenizer(args[i], "= ");
      String key = st.nextToken();
      String value = st.nextToken();
      if (key.equals("MinQuant")) {
        fMinQuant = Integer.parseInt(value);
      } else if (key.equals("MaxQuant")) {
        fMaxQuant = Integer.parseInt(value);
      } else if (key.equals("MaxPosition")) {
        fMaxPosition = Integer.parseInt(value);
      } else if (key.equals("ShortReferenceTerm")) {
        fShortReferenceTerm = Integer.parseInt(value);
      } else if (key.equals("MediumReferenceTerm")) {
        fMediumReferenceTerm = Integer.parseInt(value);
      } else {
        message("Unknown parameter:" + key +
                " in SMovingAverageStrategy.setParameters");
      }
    }
  }

}
