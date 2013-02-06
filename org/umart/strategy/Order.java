package org.umart.strategy;

/**
 * @author user
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Order {

  public static final int SELL = 1;
  public static final int BUY = 2;
  public static final int NONE = 0;

  public int price;
  public int quant;
  public int buysell; // 1: sell, 2: buy, 0: none

  public static String buySellToString(int buySell) {
    if (buySell == SELL) {
      return "Sell";
    } else if (buySell == BUY) {
      return "Buy";
    } else if (buySell == NONE) {
      return "None";
    } else {
      return "Others?";
    }
  }

}
