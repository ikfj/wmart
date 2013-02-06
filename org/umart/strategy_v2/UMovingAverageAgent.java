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

package org.umart.strategy_v2;

import java.util.*;

/**
 * �敨���i��p����ړ����σG�[�W�F���g�N���X
 * @author isao
 *
 */
public class UMovingAverageAgent extends UAgent {
	
	/** �Z���̐ߐ��̃f�t�H���g�l */
	public static final int DEFAULT_SHORT_TERM = 8;
	
	/** �����̐ߐ��̃f�t�H���g�l */
	public static final int DEFAULT_MEDIUM_TERM = 16;
  
  /** �������ʂ̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_QUANT = 50;

  /** �������ʂ̍ŏ��l�̃f�t�H���g�l */
  public static final int DEFAULT_MIN_QUANT = 10;

  /** ��/���|�W�V�����̍ő�l�̃f�t�H���g�l */
  public static final int DEFAULT_MAX_POSITION = 300;

  
	/** �Z���̐ߐ� */
	private int fShortTerm = DEFAULT_SHORT_TERM;
	
	/** �����̐ߐ� */
	private int fMediumTerm = DEFAULT_MEDIUM_TERM;

  /** �������ʂ̍ő�l */
  private int fMaxQuant = DEFAULT_MAX_QUANT;

  /** �������ʂ̍ŏ��l */
  private int fMinQuant = DEFAULT_MIN_QUANT;

  /** ��/���|�W�V�����̍ő�l */
  private int fMaxPosition = DEFAULT_MAX_POSITION;

  
  /** �Z���̐ߐ��̃v���p�e�B��(ShortTerm) */
  public static final String SHORT_TERM_KEY = "ShortTerm";

  /** �����̐ߐ��̃v���p�e�B��(MediumTerm) */
  public static final String MEDIUM_TERM_KEY = "MediumTerm";
  
  /** �������ʂ̍ő�l�̃v���p�e�B��(MaxQuant) */
	public static final String MAX_QUANT_KEY = "MaxQuant";

	/** �������ʂ̍ŏ��l�̃v���p�e�B��(MinQuant) */
	public static final String MIN_QUANT_KEY = "MinQuant";
	
	/** ��/���|�W�V�����̍ő�l�̃v���p�e�B��(MaxPosition) */
	public static final String MAX_POSITION_KEY = "MaxPosition";
  

  /** 1�ߑO�ɂ�����Z���ړ����ϒl */
	private double fPreviousShortTermMovingAverage = UOrderForm.INVALID_PRICE;
	
	/** 1�ߑO�ɂ����钆���ړ����ϒl */
	private double fPreviousMediumTermMovingAverage = UOrderForm.INVALID_PRICE;

	
  /**
   * �R���X�g���N�^
   * @param loginName ���O�C����
   * @param passwd �p�X���[�h
   * @param realName ����
   * @param seed �����̎�
   */
	public UMovingAverageAgent(String loginName, String passwd, String realName, int seed) {
		super(loginName, passwd, realName, seed);
	}

	/**
	 * �Z���̐ߐ���Ԃ��D
	 * @return �Z���̐ߐ�
	 */
	public int getShortTerm() {
		return fShortTerm;
	}
	
	/**
	 * �����̐ߐ���Ԃ��D
	 * @return �����̐ߐ�
	 */
	public int getMediumTerm() {
		return fMediumTerm;
	}

	/**
	 * �ŏ��������ʂ�Ԃ��D
	 * @return �ŏ���������
	 */
	public int getMinQuant() {
		return fMinQuant;
	}

	/**
	 * �ő咍�����ʂ�Ԃ��D
	 * @return �ő咍������
	 */
	public int getMaxQuant() {
		return fMaxQuant;
	}

	/**
	 * �ő�|�W�V������Ԃ��D
	 * @return �ő�|�W�V����
	 */
	public int getMaxPosition() {
		return fMaxPosition;
	}

  /**
   * �����[���쐬����D
   * �f�t�H���g�ł́u�������Ȃ��v�����[��Ԃ������Ȃ̂ŁC�q�N���X�ŕK���I�[�o�[���C�h���邱�ƁD
   * @param day ��
   * @param session ��
   * @param �������
   * @param noOfSessionsPerDay 1���̐ߐ�
   * @param spotPrices �������i�n��DspotPrices[0]����spotPrices[119]�܂ł�120�ߕ��̃f�[�^���i�[����Ă���DspotPrices[119]�����߂̉��i�ł���D�������C���i���������Ă��Ȃ��ꍇ�C-1�������Ă���̂Œ��ӂ��邱�ƁD
   * @param futurePrices �������i�n��DspotPrices[0]����spotPrices[119]�܂ł�120�ߕ��̃f�[�^���i�[����Ă���DspotPrices[119]�����߂̉��i�ł���D�������C���i���������Ă��Ȃ��ꍇ�C-1�������Ă���̂Œ��ӂ��邱�ƁD�܂��C����J�n�߂��O�͌������i���i�[����Ă���D
   * @param position �|�W�V�����D���Ȃ�Δ����z��(�����O�E�|�W�V����)�C���Ȃ�Δ���z���i�V���[�g�E�|�W�V�����j��\���D
   * @param money �����c���D�^��long�ł��邱�Ƃɒ��ӁD
   * @return UOrderForm[] �����[�̔z�� 
   */
  public UOrderForm[] makeOrderForms(int day, int session, 
                                      int maxDays, int noOfSessionsPerDay, 
                                      int[] spotPrices, int[] futuresPrices, 
                                      int position, long money) {
  	Random rand = getRandom();
  	UOrderForm[] forms = new UOrderForm [1];
  	forms[0] = new UOrderForm();
  	int[] prices = futuresPrices;
    forms[0].setBuySell(chooseAction(prices));
    println("");
    print("day=" + day + ", session=" + session
          + ", futures=" + prices[prices.length - 1] 
          + ", shortTerm=" + fPreviousShortTermMovingAverage
          + ", mediumTerm=" + fPreviousMediumTermMovingAverage);
    if (forms[0].getBuySell() == UOrderForm.NONE) {
    	return forms;
    }
    if (forms[0].getBuySell() == UOrderForm.BUY) {
      if (position > fMaxPosition) {
        forms[0].setBuySell(UOrderForm.NONE);
        return forms;
      }
    } else if (forms[0].getBuySell() == UOrderForm.SELL) {
      if (position < -fMaxPosition) {
        forms[0].setBuySell(UOrderForm.NONE);
        return forms;
      }
    }
    forms[0].setPrice(determinePrice(forms[0].getBuySell(), prices));
    forms[0].setQuantity(fMinQuant + rand.nextInt(fMaxQuant - fMinQuant + 1));
    print(" => " + forms[0].getBuySellByString() + ", price=" + forms[0].getPrice()
           + ", quantity=" + forms[0].getQuantity()); 
    return forms;
  }
  
  /**
   * �������i�����肵�ĕԂ��D
   * @param action �����敪
   * @param prices ���i�n��D�������Cprices[prices.length]�����߂ł��邱�ƁD
   * @return �������i
   */
  private int determinePrice(int action, int[] prices) {
  	int price = UOrderForm.INVALID_PRICE;
  	int widthOfPrice = Math.abs(prices[prices.length - 1] - prices[prices.length - 2]);
  	if (action == UOrderForm.BUY) {
      price = prices[prices.length - 1] + widthOfPrice + (int)((double)widthOfPrice / 4.0 * getRandom().nextGaussian());
    } else if (action == UOrderForm.SELL) {
      price = prices[prices.length - 1] - widthOfPrice + (int)((double)widthOfPrice / 4.0 * getRandom().nextGaussian());
    }
  	if (price < 0) {
  		price = 1;
  	}
  	return price;
  }
  
  /**
   * �����敪��I��ŕԂ��D
   * @param prices ���i�n��D�������Cprices[prices.length]�����߂ł��邱�ƁD
   * @return �����敪
   */
  private int chooseAction(int[] prices) {
  	int action = UOrderForm.NONE;
  	double shortTermMovingAverage = calculateMovingAverage(prices, fShortTerm);
  	double mediumTermMovingAverage = calculateMovingAverage(prices, fMediumTerm);
  	if (fPreviousShortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& fPreviousMediumTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& shortTermMovingAverage != UOrderForm.INVALID_PRICE
  			&& mediumTermMovingAverage != UOrderForm.INVALID_PRICE) {
  		if (fPreviousShortTermMovingAverage < fPreviousMediumTermMovingAverage
  				&& mediumTermMovingAverage < shortTermMovingAverage) {
  			action = UOrderForm.BUY;
  		} else if (fPreviousMediumTermMovingAverage < fPreviousShortTermMovingAverage
  				        && shortTermMovingAverage < mediumTermMovingAverage) {
  			action = UOrderForm.SELL;
  		}
  	}
  	fPreviousShortTermMovingAverage = shortTermMovingAverage;
  	fPreviousMediumTermMovingAverage = mediumTermMovingAverage;
  	return action;
  }
  
  /**
   * ���߂���term�ߕ��̉��i�n��̈ړ����ϒl���v�Z���ĕԂ��D
   * �������C���i���������Ă��Ȃ��ꍇ�́CUOrderForm.INVALID_PRICE (=-1)��Ԃ��D
   * @param prices ���i�n��D�������Cprices[prices.length - 1]�����߂ƂȂ��Ă��邱�ƁD
   * @param term �ړ����ς��Ƃ����
   * @return ���߂���term�ߕ��̉��i�n��̈ړ����ϒl
   */
  private double calculateMovingAverage(int[] prices, int term) {
  	double sum = 0.0;
  	for (int i = 0; i < term; ++i) {
  		if (prices[prices.length - 1 - i] < 0) {
  			return (double)UOrderForm.INVALID_PRICE;
  		}
  		sum += (double)prices[prices.length - 1 - i];
  	}
  	return sum / (double)term;
  }

  /**
   * �G�[�W�F���g�̃V�X�e���p�����[�^��ݒ肷��D
   * @param args �V�X�e���p�����[�^
   */
  public void setParameters(String[] args) {
    super.setParameters(args);
    for (int i = 0; i < args.length; ++i) {
      StringTokenizer st = new StringTokenizer(args[i], "= ");
      String key = st.nextToken();
      String value = st.nextToken();
      if (key.equals(UMovingAverageAgent.SHORT_TERM_KEY)) {
        fShortTerm = Integer.parseInt(value);
        println("ShortTerm has been changed to " + fShortTerm);
      } else if (key.equals(UMovingAverageAgent.MEDIUM_TERM_KEY)) {
      	fMediumTerm = Integer.parseInt(value);
      	println("MediumTerm has been changed to " + fMediumTerm);
      } else if (key.equals(UMovingAverageAgent.MIN_QUANT_KEY)) {
        fMinQuant = Integer.parseInt(value);
        println("MinQuant has been changed to " + fMinQuant);
      } else if (key.equals(UMovingAverageAgent.MAX_QUANT_KEY)) {
        fMaxQuant = Integer.parseInt(value);
        println("MaxQuant has been changed to " + fMaxQuant);
      } else if (key.equals(UMovingAverageAgent.MAX_POSITION_KEY)) {
        fMaxPosition = Integer.parseInt(value);
        println("MaxPosition has been changed to " + fMaxPosition);
      } else {
      	println("Unknown parameter:" + key + " in RandomStrategy.setParameters");
      }
    }
  }

}