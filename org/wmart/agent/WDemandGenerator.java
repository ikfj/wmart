package org.wmart.agent;

import java.io.*;
import java.util.*;

/**
 * 需要ファイルを生成する
 * 
 * @author ikki
 * 
 */
public class WDemandGenerator {

	/** 1日のスロット数 */
	private int fSessionsPerDay;
	/** 運用日数 */
	private int fMaxDate;
	/** 先物取引対象日数 */
	private int fDaysForward;
	/** 先物取引対象スロット数 */
	private int fSlotsForward;
	/** ワークフロー最大長 */
	private int fMaxLength;
	/** ワークフロー最大幅 (並列タスク数) */
	private int fMaxWidth;
	/** 商品名リスト */
	private String[] fGoods;
	/** タスク最低単価 */
	private int fMinUnitPrice;
	/** タスク最高単価 */
	private int fMaxUnitPrice;
	/** タスク最大数量 */
	private int fMaxVolume;
	/** 利用頻度 (1日あたりワークフロー数の期待値) */
	private int fFreq;
	/** 乱数 */
	private Random fRandom;
	/** 生成された総需要量 */
	private int fResultVolume;
	/** 生成された発注金額 */
	private int fResultPrice;
	/** 生成されたワークフロー数 */
	private int fResultCount;
	/** 生成されたワークフロー全体長の分布 */
	private int[] fResultCountPerLength;
	/** 想定する供給量 (1日あたり全商品合計) */
	private int fSupplyVolumePerDay;
	/** 長さ分布グラフを表示 */
	private boolean fShowChart = false;

	/**
	 * メイン
	 * 
	 */
	public static void main(String[] args) {
		// 運用日数
		int maxDate = 30;
		// 先物取引対象日数
		int daysForward = 7;
		// 利用頻度
		int[] freqs = { 1, 3, 5, 7, 10, 14, 20, 30, 40, 50 };
		// バリエーション数
		int numSeeds = 10;
		// 出力先フォルダ名
		String outputDir = "demand";

		String goods = "A;B;C;D;E";
		int minUnitPrice = 2;
		int maxUnitPrice = 10;
		int maxVolume = 100;

		int i = 1;
		int n = freqs.length * numSeeds;
		System.out.println(String.format("Start to generate %d files", n));
		for (int q = 0; q < freqs.length; q++) {
			for (int seed = 0; seed < numSeeds; seed++) {
				int freq = freqs[q];
				String filename = String.format("%s%sx%03df%02dq%03dv%02d.csv", outputDir,
					File.separator, maxDate, daysForward, freq, seed);
				WDemandGenerator dg = new WDemandGenerator(seed, maxDate, daysForward, goods,
					minUnitPrice, maxUnitPrice, maxVolume, freq);
				System.out.print(String.format("(%3d/%3d) ", i, n));
				dg.generateDemandFile(filename);
				i++;
			}
		}
		System.out.println("Finished!");
	}

	/**
	 * コンストラクタ
	 */
	public WDemandGenerator(int seed, int maxDate, int daysForward, String goods, int minUnitPrice,
		int maxUnitPrice, int maxVolume, int freq) {
		fSessionsPerDay = 24;
		fMaxDate = maxDate;
		fDaysForward = daysForward;
		fSlotsForward = fSessionsPerDay * fDaysForward;
		fGoods = goods.split(";");
		fMinUnitPrice = minUnitPrice;
		fMaxUnitPrice = maxUnitPrice;
		fMaxVolume = maxVolume;
		fFreq = freq;
		fRandom = new Random(seed);
		// ワークフロー最大長は1日。ただし先物取引対象日数の半分を上回らない
		fMaxLength = fSessionsPerDay;
		if (fMaxLength > fSlotsForward / 2) {
			fMaxLength = fSlotsForward / 2;
		}
		// ワークフロー最大幅は 商品数 - 2
		fMaxWidth = fGoods.length - 2;
		// 統計情報
		fResultVolume = 0;
		fResultPrice = 0;
		fResultCount = 0;
		fResultCountPerLength = new int[fMaxLength];
		fSupplyVolumePerDay = 100 * fSessionsPerDay * fGoods.length;
	}

	/**
	 * 需要ファイルの長さを見る
	 * 
	 * @throws FileNotFoundException
	 */
	private void lengthsOfDemands(String filename) {
		HashMap<Integer, Integer> aaa = new HashMap<Integer, Integer>();
		// 1節あたりワークフロー数の期待値
		double avgOrders = (double) fFreq / fSessionsPerDay;
		// 1節あたりワークフロー数の最大値は期待値の2倍。ただし 3 を下回らない
		int maxOrders = (int) Math.ceil(avgOrders * 2);
		if (maxOrders < 3) {
			maxOrders = 3;
		}
		int sum = 0;
		int count = 0;
		// 最初の daysForward 日は先物発注余裕として空ける
		for (int day = fDaysForward + 1; day <= fMaxDate; day++) {
			for (int session = 1; session <= fSessionsPerDay; session++) {
				int numOrders = poissonDistributed(avgOrders, maxOrders);
				for (int i = 0; i < numOrders; i++) {
					double lambda = lambdaForProbabilityAt(fSessionsPerDay / 2, 0.05); // 全体の5%が12時間
					int length = 2 + exponentialDistributed(lambda, fMaxLength - 2); // ワークフロー全体長
					System.out.println(length);
					if (!aaa.containsKey(length)) {
						aaa.put(length, 0);
					}
					aaa.put(length, aaa.get(length) + 1);
					count++;
				}
				// 最終日は maxLength だけ余裕を残して終了
				if (day >= fMaxDate && session >= fSessionsPerDay - fMaxLength + 1) {
					break;
				}

			}
		}
		for (Iterator iterator = aaa.keySet().iterator(); iterator.hasNext();) {
			Integer len = (Integer) iterator.next();
			System.out.println(String.format("%d\t%d", len, aaa.get(len)));
		}
		System.out.println(String.format("count = %d", count));
	}

	/**
	 * 需要ファイルを生成する
	 * 
	 * @throws FileNotFoundException
	 */
	private void generateDemandFile(String filename) {
		System.out.print(String.format("Generating %s ...", filename));
		// 1節あたりワークフロー数の期待値
		double avgOrders = (double) fFreq / fSessionsPerDay;
		// 1節あたりワークフロー数の最大値は期待値の2倍。ただし 3 を下回らない
		int maxOrders = (int) Math.ceil(avgOrders * 2);
		if (maxOrders < 3) {
			maxOrders = 3;
		}
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(filename, false)); // 上書き
			// 最初の daysForward 日は先物発注余裕として空ける
			for (int day = fDaysForward + 1; day <= fMaxDate; day++) {
				for (int session = 1; session <= fSessionsPerDay; session++) {
					int numOrders = poissonDistributed(avgOrders, maxOrders);
					for (int i = 0; i < numOrders; i++) {
						pw.println(generateDemandLine(day, session));
					}
					// 最終日は maxLength だけ余裕を残して終了
					if (day >= fMaxDate && session >= fSessionsPerDay - fMaxLength + 1) {
						break;
					}
				}
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 結果表示
		int validDays = fMaxDate - fDaysForward - 1; // 売り相当日数
		float ratio = (float) fResultVolume / (fSupplyVolumePerDay * validDays);
		System.out.print(String.format(" Count=%4d, Ratio=%3.2f", fResultCount, ratio));
		if (fShowChart) {
			System.out.println();
			for (int i = 0; i < fResultCountPerLength.length; i++) {
				StringBuilder bar = new StringBuilder();
				for (int j = 0; j < fResultCountPerLength[i]; j++) {
					bar.append("*");
				}
				System.out.println(String.format("%4d|%s %d", i + 1, bar.toString(),
					fResultCountPerLength[i]));
			}
		}
		System.out.println(" [  OK  ]");
	}

	/**
	 * 1件の需要を生成する
	 * 
	 * <pre>
	 * (BEGIN) ┬→ task[0]   ┐
	 *         ├→  ...      ┤
	 *         └→ task[n-2] ┴→ task[n-1] ─→ (END)
	 * </pre>
	 * 
	 * <p>
	 * n個のタスクからなるワークフロー。<br />
	 * 全体の長さは 2 ～ maxLength の範囲で指数分布。それをランダムな長さに2分割し、前半を task[0]～task[n-2] に、後半を task[n-1] に割り当てる。<br />
	 * 商品はタスクごとに異なるn種類をランダムに選択。<br />
	 * 単価は全タスク同じで minUnitPrice ～ maxUnitPrice の範囲でランダム。<br />
	 * 量は各タスクごとに 1 ～ maxVolume の範囲でランダム。
	 * </p>
	 * 
	 * @param day
	 *            利用開始日
	 * @param session
	 *            利用開始節
	 * @return "先物発注日/節,利用開始日/節,入札価格,注文明細"
	 */
	private String generateDemandLine(int day, int session) {
		int slot = (day - 1) * fSessionsPerDay + session; // 利用開始スロット番号
		int margin = uniformDistributed(2, fDaysForward); // 先物発注余裕
		int fwdDay = day - margin; // 先物発注日
		int fwdSession = fSessionsPerDay; // 先物発注節

		int n = uniformDistributed(2, 5); // タスク数
		String[] goods = choose(n, fGoods); // 各タスクの商品名
		int[] volumes = uniform(n, 1, fMaxVolume); // 各タスクの資源量
		int[] lengths = new int[n]; // 各タスクの長さ
		int[] atimes = new int[n]; // 各タスクの開始時刻
		int[] dtimes = new int[n]; // 各タスクの終了時刻
		double[] uprices = new double[n]; // 各タスクの単価

		// 各タスクの長さを決める
		double lambda = lambdaForProbabilityAt(fSessionsPerDay / 2, 0.05); // 全体の5%が12時間
		int length = 2 + exponentialDistributed(lambda, fMaxLength - 2); // ワークフロー全体長
		int[] len2 = uniformDivide(2, length); // 全体長を2分割
		for (int i = 0; i <= n - 2; i++) {
			lengths[i] = len2[0];
			atimes[i] = slot;
			dtimes[i] = atimes[0] + lengths[0] - 1;
		}
		lengths[n - 1] = len2[1];
		atimes[n - 1] = dtimes[n - 2] + 1;
		dtimes[n - 1] = atimes[n - 1] + lengths[n - 1] - 1;

		// 単価は全タスク共通とする
		double uprice = uniformDistributed((double) fMinUnitPrice, (double) fMaxUnitPrice);
		double sum = 0.0;
		for (int i = 0; i < n; i++) {
			uprices[i] = uprice;
			sum += uprices[i] * volumes[i] * lengths[i];
		}
		int price = Math.round((float) sum);

		// 統計情報を記録
		for (int i = 0; i < n; i++) {
			fResultVolume += volumes[i] * lengths[i];
		}
		fResultPrice += price;
		fResultCount++;
		fResultCountPerLength[length - 1]++;

		// 文字列化して返す
		StringBuilder line = new StringBuilder();
		line.append(String.format("%d/%d,%d/%d,%d,", fwdDay, fwdSession, day, session, price));
		for (int i = 0; i < n; i++) {
			line.append(String.format("%s:%d:%d:%d:%d;", goods[i], volumes[i], atimes[i],
				dtimes[i], lengths[i]));
		}
		line.append(",");
		for (int i = 0; i < n; i++) {
			line.append(String.format("%.6f;", uprices[i]));
		}
		return line.toString();
	}

	/**
	 * 1件の需要を生成する
	 * <p>
	 * 前タスク(1個) → 中間タスク(1～maxwidth個並列) → 後タスク(1個) からなるワークフロー。<br />
	 * 中間タスク同士は時間余裕あり。全タスクの延べ時間が maxlength 以内となる。
	 * </p>
	 * 
	 * @param day
	 *            基準日
	 * @param session
	 *            基準節
	 * @return "先物発注日/節,現物発注日/節,入札価格,注文明細"
	 */
	private String generateDemandLine1(int day, int session) {
		int fwdDay = day; // 先物としての発注日
		int fwdSession = fSessionsPerDay; // 先物としての発注節
		int sptDay = day + fDaysForward; // 現物としての発注日
		int sptSession = session; // 現物としての発注節

		int n = 2 + decayDistributed(0.5, fMaxWidth); // ワークフローを構成するタスク数
		// [0] = 前タスク
		// [1]～[n-2] = 中間タスク
		// [n-1] = 後タスク
		String[] goods = choose(n, fGoods); // 各タスクの商品名
		int[] volumes = uniform(n, 1, fMaxVolume); // 各タスクの資源量
		int[] lengths = uniformDivide(n, fMaxLength); // 各タスクの長さ
		int[] atimes = new int[n]; // 各タスクの開始時刻
		int[] dtimes = new int[n]; // 各タスクの終了時刻
		double[] uprices = new double[n]; // 各タスクの単価
		// int margin = uniformDistributed(x - fSessionsPerDay + 1, x);
		atimes[0] = (sptDay - 1) * fSessionsPerDay + sptSession;// 予約は受付開始後すぐに
		dtimes[0] = atimes[0] + lengths[0] - 1;
		int maxMidLength = max(Arrays.copyOfRange(lengths, 1, n - 1)); // 中間タスクの中で最長のもの
		for (int i = 1; i <= n - 2; i++) {
			atimes[i] = dtimes[0] + 1;
			dtimes[i] = atimes[i] + maxMidLength - 1;
		}
		atimes[n - 1] = dtimes[0] + maxMidLength + 1;
		dtimes[n - 1] = atimes[n - 1] + lengths[n - 1] - 1;
		// 単価は全タスク共通とする
		double uprice = uniformDistributed((double) fMinUnitPrice, (double) fMaxUnitPrice);
		double sum = 0.0;
		for (int i = 0; i < n; i++) {
			uprices[i] = uprice;
			sum += uprices[i] * volumes[i] * lengths[i];
		}
		int price = Math.round((float) sum);
		// 文字列化
		StringBuilder line = new StringBuilder();
		line.append(String.format("%d/%d,%d/%d,%d,", fwdDay, fwdSession, sptDay, sptSession, price));
		for (int i = 0; i < n; i++) {
			line.append(String.format("%s:%d:%d:%d:%d;", goods[i], volumes[i], atimes[i],
				dtimes[i], lengths[i]));
		}
		line.append(",");
		for (int i = 0; i < n; i++) {
			line.append(String.format("%.6f;", uprices[i]));
		}
		return line.toString();
	}

	/**
	 * 与えられた長さを分割する
	 * 
	 * @param size
	 *            分割数
	 * @param length
	 *            長さ
	 * @return 分割された各要素の長さを含む配列
	 */
	public int[] uniformDivide(int size, int length) {
		int[] u = new int[size];
		int[] p = new int[size + 1];
		int min = 0;
		int max = length - size;
		p[0] = min;
		for (int j = 1; j < size; j++) {
			p[j] = min + fRandom.nextInt(max - min + 1);
		}
		p[size] = max;
		Arrays.sort(p);
		for (int i = 0; i < size; i++) {
			u[i] = p[i + 1] - p[i] + 1;
		}
		return u;
	}

	/**
	 * ランダムな整数の配列
	 */
	private int[] uniform(int size, int min, int max) {
		int[] u = new int[size];
		for (int i = 0; i < u.length; i++) {
			u[i] = min + fRandom.nextInt(max - min + 1);
		}
		return u;
	}

	/**
	 * ランダムな値を選ぶ
	 */
	private int uniformDistributed(int min, int max) {
		return min + fRandom.nextInt(max - min + 1);
	}

	/**
	 * ランダムな値を選ぶ
	 */
	private double uniformDistributed(double min, double max) {
		return min + fRandom.nextDouble() * (max - min);
	}

	/**
	 * 指数分布に従って値を選ぶ
	 * 
	 * @param lambda
	 * @param max
	 * @return 0～max の整数。max を超える値は max に丸められる。
	 */
	private int exponentialDistributed(double lambda, int max) {
		double v = -Math.log(fRandom.nextDouble()) / lambda;
		if (v > max) {
			v = max;
		}
		return (int) Math.floor(v);
	}

	/**
	 * 指数分布で、選ばれる値が x 以上になる確率が p となるような係数 λ を求める
	 * 
	 * @param x
	 * @param p
	 * @return
	 */
	private double lambdaForProbabilityAt(double x, double p) {
		return -(Math.log(p) / x);
	}

	/**
	 * Decay 分布に従って値を選ぶ
	 * 
	 * @param alpha
	 *            係数
	 * @param max
	 *            最大値
	 * @return
	 */
	private int decayDistributed(double alpha, int max) {
		for (int k = 1; k < max; k++) {
			if (fRandom.nextDouble() > alpha) {
				return k;
			}
		}
		return max;
	}

	/**
	 * Poisson 分布に従って値を選ぶ http://en.wikipedia.org/wiki/Poisson_distribution
	 * 
	 * @param lambda
	 *            期待値
	 * @param max
	 *            最大値 (期待値の2倍以上を指定するべき。ただし、あまり大きいとオーバーフローする)
	 * @return
	 */
	private int poissonDistributed(double lambda, int max) {
		double r = fRandom.nextDouble();
		double p = 0.0;
		double prevp = 0.0;
		for (int k = 0; k < max; k++) {
			p += Math.pow(lambda, k) * Math.exp(-lambda) / factorial(k);
			// max が大きすぎるとここで p が減る可能性がある。パラメータを変えたらチェックするべき
			if (p < prevp) {
				throw new Error(String.format("poissonDistributed(%f, %d): max is too large",
					lambda, max));
			}
			if (p > r) {
				return k;
			}
			prevp = p;
		}
		return max;
	}

	/**
	 * 階乗
	 */
	private long factorial(long n) {
		assert (n >= 0);
		long r = 1;
		while (n > 1) {
			r *= n--;
		}
		return r;
	}

	/**
	 * 配列の要素からランダムに n 個を選ぶ
	 * 
	 * @param n
	 *            選ぶ個数
	 * @param given
	 *            配列
	 * @return
	 */
	private String[] choose(int n, String[] given) {
		assert (0 < n && n <= given.length);
		ArrayList<String> a = new ArrayList<String>(Arrays.asList(given));
		ArrayList<String> b = new ArrayList<String>();
		for (int i = 0; i < n; i++) {
			b.add(a.remove(fRandom.nextInt(a.size())));
		}
		return b.toArray(new String[n]);
	}

	/**
	 * 2つの配列の内積 (対応する要素の積の和)
	 */
	private int innerProduct(int[] a, int[] b) {
		assert (a.length == b.length);
		int sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i] * b[i];
		}
		return sum;
	}

	/**
	 * 配列要素の最大値
	 */
	private int max(int[] a) {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
			}
		}
		return max;
	}

}
