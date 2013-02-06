#!/usr/bin/ruby

# リザルトアナライザ for 先物市場

vngoods = [1,10]		# 商品数
vnslots = [1,24,120,240,480,720]		# スロット数
vnagents = [[10,100],[10,400],[10,700],[10,1000]]		# [売り手数,買い手数]

# Rubyの配列(Arrayクラス)を拡張して和，分散，標準偏差，相関係数を計算する
# http://d.hatena.ne.jp/sesejun/20070502/p1
class Array
  def sum_with_number
    s = 0.0
    n = 0
    self.each do |v|
      next if v.nil?
      s += v.to_f
      n += 1
    end
    [s, n]
  end
  
  def sum
    s, n = self.sum_with_number
    s
  end
  
  def avg
    s, n = self.sum_with_number
    s / n
  end
  alias mean avg
  
  def var
    c = 0
    while self[c].nil?
      c += 1
    end
    mean = self[c].to_f
    sum = 0.0
    n = 1
    (c+1).upto(self.size-1) do |i|
      next if self[i].nil?
      sweep = n.to_f / (n + 1.0)
      delta = self[i].to_f - mean
      sum += delta * delta * sweep
      mean += delta / (n + 1.0)
      n += 1
    end
    sum / n.to_f
  end
  
  def stddev
    Math.sqrt(self.var)
  end
  
  def corrcoef(y)
    raise "Invalid Argument Array Size" unless self.size == y.size
    sum_sq_x = 0.0
    sum_sq_y = 0.0
    sum_coproduct = 0.0
    c = 0
    while self[c].nil? || y[c].nil?
      c += 1
    end
    mean_x = self[c].to_f
    mean_y = y[c].to_f
    n = 1
    (c+1).upto(self.size-1) do |i|
      next if self[i].nil? || y[i].nil?
      sweep = n.to_f / (n + 1.0)
      delta_x = self[i].to_f - mean_x
      delta_y = y[i].to_f - mean_y
      sum_sq_x += delta_x * delta_x * sweep
      sum_sq_y += delta_y * delta_y * sweep
      sum_coproduct += delta_x * delta_y * sweep
      mean_x += delta_x / (n + 1.0)
      mean_y += delta_y / (n + 1.0)
      n += 1
    end
    pop_sd_x = Math.sqrt(sum_sq_x / n.to_f)
    pop_sd_y = Math.sqrt(sum_sq_y / n.to_f)
    cov_x_y = sum_coproduct / n.to_f
    cov_x_y / (pop_sd_x * pop_sd_y)
  end
end

def analyze(m, n, g, t)
	runtimes = Array.new		# ソルバー実行時間
	drvtimes = Array.new		# 総実行時間
	buyrates = Array.new		# 買い約定率
	selrates = Array.new		# 売り約定率
	
	dirname = File.dirname($0) + File::SEPARATOR + format("m%dn%dg%dt%03d", m, n, g, t)
	next if not File.exist?(dirname)
	STDERR.puts dirname
	100.times do |i|
		filename = dirname + File::SEPARATOR + format("result%02d.txt", i)
		next if not File.exist?(filename)
		STDERR.puts filename
		IO.foreach(filename) do |line|
			line.chomp!
			name, sellbuy, bid, price, rate = line.split(/,/)
			if sellbuy == "sell"
				selrates.push(rate)
			elsif sellbuy == "buy"
				buyrates.push(rate)
			elsif name =~ /runtime=([\d.]+)/
				runtimes.push($1)
			elsif name =~ /drivetime=([\d.]+)/
				drvtimes.push($1)
			end
		end
	end
	[runtimes, drvtimes, selrates, buyrates]
end

def tablize(name, avgdata, stddata, vnagents, vnslots)
	print "[#{name}]\n"
	print "average\n"
	vnagents.each do |m, n|
		print "\t#{n}"
	end
	print "\n"
	vnslots.reverse.each do |t|
		print "#{t}"
		vnagents.each do |m, n|
			print "\t#{avgdata[10][n][10][t]}"
		end
		print "\n"
	end
	print "stddev\n"
	vnagents.each do |m, n|
		print "\t#{n}"
	end
	print "\n"
	vnslots.reverse.each do |t|
		print "#{t}"
		vnagents.each do |m, n|
			print "\t#{stddata[10][n][10][t]}"
		end
		print "\n"
	end
	print "\n"
end

rt = Hash.new		# ソルバー実行時間
dt = Hash.new		# 総実行時間
sr = Hash.new		# 売り約定率
br = Hash.new		# 買い約定率
avg_rt = Hash.new		# 平均ソルバー実行時間
avg_dt = Hash.new		# 平均総実行時間
avg_sr = Hash.new		# 平均売り約定率
avg_br = Hash.new		# 平均買い約定率
std_rt = Hash.new		# 標準偏差ソルバー実行時間
std_dt = Hash.new		# 標準偏差総実行時間
std_sr = Hash.new		# 標準偏差売り約定率
std_br = Hash.new		# 標準偏差買い約定率
vnagents.each do |m, n|
	rt[m] = Hash.new if not rt[m]
	dt[m] = Hash.new if not dt[m]
	sr[m] = Hash.new if not sr[m]
	br[m] = Hash.new if not br[m]
	avg_rt[m] = Hash.new if not avg_rt[m]
	avg_dt[m] = Hash.new if not avg_dt[m]
	avg_sr[m] = Hash.new if not avg_sr[m]
	avg_br[m] = Hash.new if not avg_br[m]
	std_rt[m] = Hash.new if not std_rt[m]
	std_dt[m] = Hash.new if not std_dt[m]
	std_sr[m] = Hash.new if not std_sr[m]
	std_br[m] = Hash.new if not std_br[m]
	rt[m][n] = Hash.new if not rt[m][n]
	dt[m][n] = Hash.new if not dt[m][n]
	sr[m][n] = Hash.new if not sr[m][n]
	br[m][n] = Hash.new if not br[m][n]
	avg_rt[m][n] = Hash.new if not avg_rt[m][n]
	avg_dt[m][n] = Hash.new if not avg_dt[m][n]
	avg_sr[m][n] = Hash.new if not avg_sr[m][n]
	avg_br[m][n] = Hash.new if not avg_br[m][n]
	std_rt[m][n] = Hash.new if not std_rt[m][n]
	std_dt[m][n] = Hash.new if not std_dt[m][n]
	std_sr[m][n] = Hash.new if not std_sr[m][n]
	std_br[m][n] = Hash.new if not std_br[m][n]
	vngoods.each do |g|
		rt[m][n][g] = Hash.new if not rt[m][n][g]
		dt[m][n][g] = Hash.new if not dt[m][n][g]
		sr[m][n][g] = Hash.new if not sr[m][n][g]
		br[m][n][g] = Hash.new if not br[m][n][g]
		avg_rt[m][n][g] = Hash.new if not avg_rt[m][n][g]
		avg_dt[m][n][g] = Hash.new if not avg_dt[m][n][g]
		avg_sr[m][n][g] = Hash.new if not avg_sr[m][n][g]
		avg_br[m][n][g] = Hash.new if not avg_br[m][n][g]
		std_rt[m][n][g] = Hash.new if not std_rt[m][n][g]
		std_dt[m][n][g] = Hash.new if not std_dt[m][n][g]
		std_sr[m][n][g] = Hash.new if not std_sr[m][n][g]
		std_br[m][n][g] = Hash.new if not std_br[m][n][g]
		vnslots.each do |t|
			rt[m][n][g][t], dt[m][n][g][t], sr[m][n][g][t], br[m][n][g][t] = analyze(m, n, g, t)
			avg_rt[m][n][g][t] = rt[m][n][g][t].avg
			avg_dt[m][n][g][t] = dt[m][n][g][t].avg
			avg_sr[m][n][g][t] = sr[m][n][g][t].avg
			avg_br[m][n][g][t] = br[m][n][g][t].avg
			std_rt[m][n][g][t] = rt[m][n][g][t].stddev
			std_dt[m][n][g][t] = dt[m][n][g][t].stddev
			std_sr[m][n][g][t] = sr[m][n][g][t].stddev
			std_br[m][n][g][t] = br[m][n][g][t].stddev
		end
	end
end
tablize("runtime", avg_rt, std_rt, vnagents, vnslots)
tablize("drivetime", avg_dt, std_dt, vnagents, vnslots)
tablize("sellrate", avg_sr, std_sr, vnagents, vnslots)
tablize("buyrate", avg_br, std_br, vnagents, vnslots)
