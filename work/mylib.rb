
# タイムスタンパー
def timestamp
	Time.now.strftime("%H:%M:%S")
end
def datetimestamp
	Time.now.strftime("%Y/%m/%d %H:%M:%S")
end
def lapse(start_time)
	Time.at(Time.now - start_time).utc.strftime("%Hh%Mm%Ss")
end
def avg_lapse(start_time, count)
	Time.at((Time.now - start_time) / count).utc.strftime("%Hh%Mm%Ss")
end

# FastestCSV
# http://raa.ruby-lang.org/project/csvscan/
require "csvscan"
class FastestCSV
	def self.read(filename)
		r = Array.new
		File.open(filename, "r") do |f|
			CSVScan.scan(f) do |row|
				r.push(row)
			end
		end
		r
	end
end

# ロガー。ファイルと標準出力に同時出力する
class MyLogger
	def initialize(filename)
		@fn = filename
	end
	def print(message)
		STDOUT.print message
		File.open(@fn, "a") do |f|
			f.print message
		end
	end
end

# 複数系列を持つ散布図を表すクラス
# {X座標 => {系列 => Y座標}}
class ScatterPlot
	def initialize(title)
		@title = title
		@data = Hash.new
		@series = Array.new
	end
	attr_accessor :title, :data, :series
	
	# 点を追加
	def put(s, x, y)
		s = s.to_s
		x = x.to_f
		y = y.to_f
		@data[x] ||= Hash.new
		@data[x][s] = y
		@series.push(s) unless @series.include?(s)
		self
	end
	
	# 点を取得
	def get(s, x)
		@data[x.to_f][s.to_s]
	end
	
	# 整形する
	def format(colsep = ",", rowsep = "\n")
		r = ""
		r << colsep
		@series.each do |s|
			r << s << colsep
		end
		r << rowsep
		@data.keys.sort.each do |x|
			r << x.to_s << colsep
			@series.each do |s|
				r << @data[x][s].to_s << colsep
			end
			r << rowsep
		end
		r
	end
	
	# 整形する (系列ごとに横軸も分ける)
	def format2(colsep = ",", rowsep = "\n")
		r = ""
		r << colsep
		@series.each do |s|
			r << s << colsep
		end
		r << rowsep
		@series.each do |s|
			@data.keys.sort.each do |x|
				next unless @data[x][s]
				r << x.to_s << colsep
				@series.each do |ss|
					r << @data[x][s].to_s if s == ss
					r << colsep
				end
				r << rowsep
			end
		end
		r
	end
end

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
