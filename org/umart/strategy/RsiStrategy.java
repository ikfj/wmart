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
 Relative Strength Index (RSI) method, one of major technical analysis
 methods. The limit price is set randomly around the latest futures
 price, and quantity of the order is set randomly within a prescribed
 range. Position of the agent is also considered in decision making.
 */

package org.umart.strategy;

import java.util.*;

public class RsiStrategy extends Strategy {

  /** Default value of width of price on order */
  public static final int DEFAULT_WIDTH_OF_PRICE = 20;

  /** Default value of maximum order quantity */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** Default value of minimum order quantity */
  public static final int DEFAULT_MIN_QUANT = 15;

  /** Default valued of maximum (long/short) position */
  public static final int DEFAULT_MAX_POSITION = 300;

  /** Default value of price used for the case where no price information from the market is valid */
  public static final int DEFAULT_NOMINAL_PRICE = 3000;

  /** Default value of reference term used for RSI calculation */
  public static final int DEFAULT_REFERENCE_TERM = 10;

  /** Default value of edge band */
  public static final double DEFAULT_EDGE_BAND = 0.3;

  /** Random number generator */
  private Random fRandom;

  /** Width of the price on order */
  private int fWidthOfPrice = DEFAULT_WIDTH_OF_PRICE;

  /** Maximum order quantity */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** Minimum order quantity */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** Maximum (long/short) position */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  /** Price used for the case where no price information from the market is valid */
  private int fNominalPrice = DEFAULT_NOMINAL_PRICE;

  /** Reference term used for RSI calculation */
  private int fReferenceTerm = DEFAULT_REFERENCE_TERM;

  /** Edge band */
  private double fEdgeBand = DEFAULT_EDGE_BAND;

  /* Constructor for Agent Initialization */
  public RsiStrategy(int seed) {
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
    Order order;
    /* Class order has instance variables as follows
       order.price: limit price,
       order.quant: quantity,
       order.buysell: no order(0), sell(1) or buy(2)
     */
    /* decide 'order' (here we use method getOrder described below),
       and call method orderRequest to submit order.
       If you want to submit several orders, repeat such procedure
     */
    order = getOrder(spotPrices, futurePrices, pos, money, restDay);
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
     restDay: Number of remaining transaction to the closing of the market.  */
    Order order = new Order(); // Object to return values.
    // Measure and accumulate upward and downward price changes
    int oldPrice = -1;
    int fcnt = futurePrices.length - fReferenceTerm;
    // go back fcnt by referenceTerm
    while (true) { // Find the well defined oldest price in the time window
      if (fcnt == futurePrices.length) {
        // All the prices are invalid, no order is made
        order.buysell = Order.NONE;
        return order;
      }
      oldPrice = futurePrices[fcnt];
      if (oldPrice >= 0) {
        break;
      }
      ++fcnt;
    }
    int upSum = 0; // accumulated upward price change
    int downSum = 0; // accumulated downward price change
    while (true) {
      if (fcnt == futurePrices.length) {
        break;
      }
      if (futurePrices[fcnt] >= 0) {
        // If price is valid, get up/downward price change
        if (futurePrices[fcnt] - oldPrice > 0) {
          upSum += futurePrices[fcnt] - oldPrice;
        } else if (futurePrices[fcnt] - oldPrice < 0) {
          downSum += oldPrice - futurePrices[fcnt];
        }
        oldPrice = futurePrices[fcnt];
      }
      ++fcnt;
    }
    if (upSum + downSum == 0) { // If there is no price change in the
      // time window, no order is made.
      order.buysell = Order.NONE;
      return order;
    }
    // Calculate RSI
    double rsi = (double) upSum / (double) (upSum + downSum);
    // Decide buy or sell based on RSI
    if (rsi < fEdgeBand) {
      order.buysell = Order.BUY;
      // Make a buy order because upward change
      // is maturing.
    } else if (rsi > 1.0 - fEdgeBand) {
      order.buysell = Order.SELL;
      // Make a sell order because downward change
      // is maturing.
    } else {
      order.buysell = Order.NONE; //
      return order;
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
    int prevPrice = getLatestPrice(futurePrices);
    while (true) {
      order.price = prevPrice + (int) (fWidthOfPrice * fRandom.nextGaussian());
      if (order.price > 0) {
        break;
      }
    } // Add normal random number around prevPrice
    // with the standard deviation widthOfPrice.
    // If it gets negative, repeat again.
    // Decide quantity of order between [minQuant, maxQuant] randomly.
    order.quant = fMinQuant + fRandom.nextInt(fMaxQuant - fMinQuant + 1);
    // Message
    message(Order.buySellToString(order.buysell)
            + ", price = " + order.price
            + ", volume = " + order.quant
            + " (RSI = " + rsi + " )");
    return order;
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
      if (key.equals("WidthOfPrice")) {
        fWidthOfPrice = Integer.parseInt(value);
      } else if (key.equals("MinQuant")) {
        fMinQuant = Integer.parseInt(value);
      } else if (key.equals("MaxQuant")) {
        fMaxQuant = Integer.parseInt(value);
      } else if (key.equals("MaxPosition")) {
        fMaxPosition = Integer.parseInt(value);
      } else if (key.equals("ReferenceTerm")) {
        fReferenceTerm = Integer.parseInt(value);
      } else if (key.equals("EdgeBand")) {
        fEdgeBand = Double.parseDouble(value);
      } else {
        message("Unknown parameter:" + key + " in RsiStrategy.setParameters");
      }
    }
  }

}
