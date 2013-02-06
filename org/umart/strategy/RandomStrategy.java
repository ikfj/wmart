/*
 Copyright (c) 1999 Hiroshi Sato
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
 RandomStrategy.java                    2002/01/31
 A class describing an agent's org.umart.strategy, which buys or sells randomly.
 The limit price is set randomly around the latest futures price, and
 quantity of the order is set randomly within a prescribed range.
 Position of the agent is also considered in decision making.
 */

package org.umart.strategy;

import java.util.*;

public class RandomStrategy extends Strategy {

  /** Default value of width of price on order */
  public static final int DEFAULT_WIDTH_OF_PRICE = 20;

  /** Default value of maximum order quantity */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** Default value of minimum order quantity */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** Default valued of maximum (long/short) position */
  public static final int DEFAULT_MAX_POSITION = 300;

  /** Default value of price used for the case where no price information from the market is valid */
  public static final int DEFAULT_NOMINAL_PRICE = 3000;

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

  public RandomStrategy(int seed) {
    fRandom = new Random(seed);
  }

  public Order getOrder(int[] spotPrices, int[] futurePrices,
                        int pos, long money, int restDay) {
    Order order = new Order();
    // Scan a well defined latest futures price. If it is not available,
    // a nominal value is used.
    int prevPrice = getLatestPrice(futurePrices);
    if (prevPrice == -1) {
      prevPrice = getLatestPrice(spotPrices);
    }
    if (prevPrice == -1) {
      prevPrice = fNominalPrice;
      // Submit a random order with a random volume and a random price
      // around the latest futures price.
    }
    order.buysell = fRandom.nextInt(2) + 1;
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
    while (true) {
      order.price = prevPrice + (int) (fWidthOfPrice * fRandom.nextGaussian());
      if (order.price > 0) {
        break;
      }
    }
    order.quant = fMinQuant + fRandom.nextInt(fMaxQuant - fMinQuant + 1);
    // Message
    message(Order.buySellToString(order.buysell) + ", price = " + order.price
            + ", volume = " + order.quant + " (prevPrice = " + prevPrice
            + "  )");
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
      } else {
        message("Unknown parameter:" + key + " in RandomStrategy.setParameters");
      }
    }
  }

}
