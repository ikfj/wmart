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
 methods. It is applied not to the futures but spot prices. The limit price
 is set randomly around the latest futures price, and quantity of the
 order is set randomly within a prescribed range. Position of the agent
 is also considered in decision making.
 */

package org.umart.strategy;

import java.util.*;

public class SRsiStrategy extends Strategy {

  /* Instance variables that keep values during all the periods. */

  private Random random; // random number sequence.
  private final int widthOfPrice = 20; // Standard deviation of limit price decided randomly.
  private final int maxQuant = 50; // Maximum quantity of order.
  private final int minQuant = 15; // Minimal quantity of order.
  private final int referenceTerm = 10; // Time Window for measuring

  // upward and downward price
  // changes.
  private final int maxPosition = 300; // Limit of position that allows

  // order that increases it.
  private final double edge = 0.3; // Edge band value of RSI method.

  /* Constructor for Agent Initialization */

  public SRsiStrategy(int seed) {
    /* If you need some initialization of e.g., instance variables
       write here.
     */

    random = new Random(seed); // Initialization of random sequence.
  }

  /* Method of making order. If you submit a single order in one period,
     keep it as it is. If you want to submit several orders in one period,
     prepare methods other than getOrder and repeat orderRequest.
   */

  public void action(int[] spotPrices, int[] futurePrices, int pos, long money,
                     int restDay) {
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

  public Order getOrder(int[] spotPrices, int[] futurePrices, int pos,
                        long money, int restDay) {

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

    // Measure and accumulate upward and downward changes of spot prices
    int oldPrice = -1;
    int scnt = spotPrices.length - referenceTerm; // go back scnt by referenceTerm
    while (true) { // Find the well defined oldest price in the time window
      if (scnt == spotPrices.length) { // All the prices are invalid, no order is made
        order.buysell = Order.NONE;
        return order;
      }
      oldPrice = spotPrices[scnt];
      if (oldPrice >= 0) {
        break;
      }
      ++scnt;
    }
    int upSum = 0; // accumulated upward price change
    int downSum = 0; // accumulated downward price change
    while (true) {
      if (scnt == spotPrices.length) {
        break;
      }
      if (spotPrices[scnt] >= 0) { // If price is valid, get up/downward price change
        if (spotPrices[scnt] - oldPrice > 0) {
          upSum += spotPrices[scnt] - oldPrice;
        } else if (spotPrices[scnt] - oldPrice < 0) {
          downSum += oldPrice - spotPrices[scnt];
        }
        oldPrice = spotPrices[scnt];
      }
      ++scnt;
    }
    if (upSum + downSum == 0) { // If there is no price change in the
      // time window, no order is made.
      order.buysell = Order.NONE;
      return order;
    }

    // Calculate RSI for spot prices
    double rsi = (double) upSum / (double) (upSum + downSum);

    // Decide buy or sell based on RSI
    if (rsi < edge) {
      order.buysell = Order.BUY; // Make a buy order because upward change
      // is maturing.
    } else if (rsi > 1.0 - edge) {
      order.buysell = Order.SELL; // Make a sell order because downward change
      // is maturing.
    } else {
      order.buysell = Order.NONE; //
      return order;
    }

    // Cancel decision if it may increase absolute value of the position
    if (order.buysell == Order.BUY) {
      if (pos > maxPosition) {
        order.buysell = Order.NONE;
        return order;
      }
    } else if (order.buysell == Order.SELL) {
      if (pos < -maxPosition) {
        order.buysell = Order.NONE;
        return order;
      }
    }

    int prevPrice = getLatestPrice(spotPrices);
    while (true) {
      order.price = prevPrice + (int) (widthOfPrice * random.nextGaussian());
      if (order.price > 0) {
        break;
      }
    } // Add normal random number around prevPrice (the latest spot price)
    // with the standard deviation widthOfPrice.
    // If it gets negative, repeat again.

    // Decide quantity of order between [minQuant, maxQuant] randomly.
    order.quant = minQuant + random.nextInt(maxQuant - minQuant + 1);
    // Message
    message(Order.buySellToString(order.buysell) + ", price = " + order.price +
            ", volume = " + order.quant + " (RSI of Spot = " + rsi + " )");
    return order;
  }

}
