# 
# 	WMart - single market experiment  (2011/04/19)
# 

# 買い手1人。

require "rubygems"
require "pp"
require "set"
require "pathname"
require "fileutils"
require "fastercsv"
require "mylib.rb"

$log_dir = Pathname.new(Time.now.strftime("%y%m%d-%H%M%S"))

# 複数回のシミュレーションを統計的に扱うクラス
class Experiment
	
	def initialize(seed_count = 100)
		return if seed_count < 1
		@term  = 30 # 運用日数
		@reach = 7  # 先物取引対象日数
		@seeds = 0..(seed_count - 1) # 乱数バリエーション
		@freqs = [1, 3, 5, 7, 10, 14, 20, 30, 40, 50] # 利用頻度
		@types = ["type1", "type2", "type3", "type4"] # メンバー
		@sims  = Hash.new
		@plots = Hash.new
		@report_file = $log_dir + "report.csv"
	end
	
	def run(mode)
		# mode = 0:チェック 1:実行のみ 2:集計のみ 3:実行&集計
		
		# チェック
		@seeds.each do |seed|
			@freqs.each do |freq|
				@types.each do |type|
					sim = Simulation.new(@term, @reach, freq, seed, type, @seeds.count)
					return unless sim.check(mode)
				end
			end
		end
		return if mode < 1
		
		time0 = Time.now
		sim_count = @seeds.count * @freqs.count * @types.count
		$log.print "  term  = %d\n" % @term
		$log.print "  reach = %d\n" % @reach
		$log.print "  seeds = %s\n" % @seeds.to_s
		$log.print "  freqs = %s\n" % @freqs.join(",")
		$log.print "  types = %s\n" % @types.join(",")
		$log.print "%s Start to run %d simulations\n" % [timestamp, sim_count]
		
		# 実行
		n = 0
		@seeds.each do |seed|
			@freqs.each do |freq|
				@types.each do |type|
					n += 1
					$log.print "%s (%3d/%3d) " % [timestamp, n, sim_count]
					sim = Simulation.new(@term, @reach, freq, seed, type, @seeds.count)
					sim.run(mode)
					@sims[name_of(freq, seed, type)] = sim
					$log.print "\n"
				end
			end
		end
		$log.print "%s Finished in total %s\n" % [timestamp, lapse(time0)]
		return if mode < 2
		
		# 集計
		@report_file.open("w") do |f|
			f.puts datetimestamp
			f.puts "x%03df%02d" % [@term, @reach]
			f.puts
		end
		report("global_utilization") do |sim| # 完遂量÷供給量
			sim.b_fv.sum / sim.s_ov.sum
		end
		report("market_price") do |sim| # 約定価格÷約定量
			sim.s_cp.sum / sim.s_cv.sum
		end
		report("completion_ratio") do |sim| # 完遂件数÷需要件数
			sim.b_fn.sum.to_f / sim.b_on.sum.to_f
		end
		report("cost_performance") do |sim| # 完遂注文価格÷全約定価格
			if sim.b_cp.sum > 0
				sim.b_fop.sum / sim.b_cp.sum
			else
				$log.print "    b_cp == 0 at %s %s" % [sim.scene, sim.type]
				0
			end
		end
	end
	
	def report(title)
		$log.print "%s reporting %s" % [timestamp, title]
		plot = ScatterPlot.new(title)
		@freqs.sort.each do |freq|
			@types.each do |type|
				xs = Array.new
				ys = Array.new
				@seeds.each do |seed|
					sim = @sims[name_of(freq, seed, type)]
					xs.push(sim.b_ov.sum / sim.s_ov.sum)
					ys.push(yield(sim))
				end
				x = xs.avg # 横軸 = 負荷の平均
				y = ys.avg # 縦軸 = ブロック評価値の平均
				plot.put(type, x, y)
				$log.print "\n  q%03d-%s\t%.2f\t%.2f" % [freq, type, x, y]
			end
		end
		# 散布図データを @report_file に追記
		@report_file.open("a") do |f|
			f.puts title
			f.puts plot.format
# 			f.puts plot.format2
			f.puts
		end
		$log.print "\n"
	end
	
	def name_of(freq, seed, type)
		"q%03dv%02d-%s" % [freq, seed, type]
	end
end


# 1回のシミュレーションを表すクラス
class Simulation
	
	def initialize(term, reach, freq, seed, type, seeds_count = 1)
		# パラメータ
		@term  = term  # 運用日数
		@reach = reach # 先物取引対象日数
		@freq  = freq  # 利用頻度
		@seed  = seed  # 乱数種
		@type  = type  # メンバー
		scenes = Array.new
		scenes[0] = "x%03df%02dq%03dv%02d" % [@term, @reach, @freq, @seed]
		scenes[1] = "x%03df%02dq%03dv%02d" % [@term, @reach, @freq, (@seed + 1) % seeds_count]
		@scene = scenes[0]
		@member_file = Pathname.new("member/#{@type}.csv")
		@demand_files = Array.new
		@demand_files[0] = Pathname.new("demand/#{scenes[0]}.csv")
		@demand_files[1] = Pathname.new("demand/#{scenes[1]}.csv")
		@result_dir = Pathname.new("result/#{@scene}-#{@type}")
		@result_dir.mkpath
		
		# 結果 (1エージェントにつき1要素の配列)
		@s_ov  = Array.new # 売り手の注文量
		@s_cv  = Array.new # 売り手の約定量
		@s_cp  = Array.new # 売り手の約定価格
		@s_pro = Array.new # 売り手の利益
		@s_wel = Array.new # 売り手の余剰
		@b_on  = Array.new # 買い手の注文件数
		@b_fn  = Array.new # 買い手の完遂件数
		@b_ov  = Array.new # 買い手の注文量
		@b_cv  = Array.new # 買い手の約定量
		@b_fv  = Array.new # 買い手の完遂量
		@b_op  = Array.new # 買い手の注文価格
		@b_cp  = Array.new # 買い手の約定価格
		@b_fop = Array.new # 買い手の完遂注文価格
		@b_fcp = Array.new # 買い手の完遂約定価格
		@b_pro = Array.new # 買い手の便益
		@b_wel = Array.new # 買い手の余剰
	end
	attr_reader :term, :reach, :freq, :seed, :type, :scene, :s_ov, :s_cv, :s_cp, :s_pro, :s_wel, :b_on, :b_fn, :b_ov, :b_cv, :b_fv, :b_op, :b_cp, :b_fop, :b_fcp, :b_pro, :b_wel
	
	def check(mode)
		# mode = 1:実行のみ 2:集計のみ 3:実行&集計
		result = true
		unless @member_file.exist?
			$log.print "No member_file %s exists\n" % [@member_file]
			result = false
		end
		unless @demand_files.all?{ |demand_file| demand_file.exist? }
			$log.print "No demand_file %s exists\n" % [@demand_files.join(";")]
			result = false
		end
		if mode == 2 && Pathname.glob(@result_dir + "trace_*.csv").empty?
			$log.print "No result exists in %s\n" % [@result_dir]
			result = false
		end
		result
	end
	
	def run(mode)
		# mode = 1:実行のみ 2:集計のみ 3:実行&集計
		time0 = Time.now
		$log.print "%s %s %6s ..." % [(mode == 2 ? "reading" : "running"), @scene, @type]
		
		# 実行
		if Pathname.glob(@result_dir + "trace_*.csv").empty?
			# まだ結果がない
			return if mode == 2
			jar_file  = "WMartSimulator.jar"
			lpslv_lib = "/usr/local/lib"
			cplex_lib = "/usr/ilog/cplex/bin/x86-64_debian4.0_4.1"
			cplex_jar = "/usr/ilog/cplex/lib/cplex.jar"
			mem_min   = "1G"
			mem_max   = "30G"
			demands   = @demand_files.join("\\;")
			parameter = "demandfile=#{demands}:logdir=#{@result_dir}"
			cmdline   = "java -d64 -Xms#{mem_min} -Xmx#{mem_max} -Djava.library.path=#{cplex_lib}:#{lpslv_lib} -jar #{jar_file} #{@seed} #{@term} #{@reach} #{@member_file} #{parameter} > stdout.txt 2> stderr.txt"
# 			puts cmdline
			system(cmdline)
			FileUtils.move(["stdout.txt", "stderr.txt", "wmart.log"], @result_dir)
			$log.print " done in %s [  OK  ]" % lapse(time0)
		else
			# すでに結果がある
			$log.print "                   [PASSED]"
		end
		return if mode < 2
		
		# 集計
		vd1 = (@reach + 2) # 集計開始日
		vd2 = (@term - 1)  # 集計終了日
		# 売り手
		valid_slots = ((vd1 - 1) * 24 + 1)..(vd2 * 24) # 集計対象とするスロット番号
		Pathname.glob(@result_dir + "trace_seller_*.csv") do |path|
			data = FastestCSV.read(path).select{ |row| valid_slots.include?(row[0].to_i) }
			@s_ov.push(data.collect{ |row| row[1].to_f }.sum) # 注文量
			@s_cv.push(data.collect{ |row| row[2].to_f }.sum) # 約定量
			@s_cp.push(data.collect{ |row| row[5].to_f }.sum) # 約定価格
			@s_pro.push(data.collect{ |row| row[6].to_f }.sum) # 利益
			@s_wel.push(data.collect{ |row| row[7].to_f }.sum) # 余剰
		end
		# 買い手
		Pathname.glob(@result_dir + "trace_buyer_*.csv") do |path|
			name = path.basename.to_s[/trace_buyer_(.*)\.csv/, 1]
			sysparam = FastestCSV.read(@member_file).find{ |row| row[0] == name }[6].to_s
			j = extract_sysparam(sysparam, "demandindex").to_i
			valid_lines = Set.new # 集計対象とする需要行番号
			FastestCSV.read(@demand_files[j]).each_with_index do |row, i|
				lineno = i + 1 # 行番号
				date = row[1].split("/")[0].to_i # 利用開始日
				valid_lines.add(lineno) if vd1 <= date && date <= vd2
			end
			data = FastestCSV.read(path).select{ |row| valid_lines.include?(row[0].to_i) }
			@b_on.push(data.length) # 注文件数
			@b_fn.push(data.count{ |row| row[3].to_i == 1 }) # 完遂件数
			@b_ov.push(data.collect{ |row| row[1].to_f }.sum) # 注文量
			@b_cv.push(data.collect{ |row| row[2].to_f }.sum) # 約定量
			@b_fv.push(data.collect{ |row| row[2].to_f * row[3].to_i }.sum) # 完遂量
			@b_op.push(data.collect{ |row| row[4].to_f }.sum) # 全注文価格
			@b_cp.push(data.collect{ |row| row[5].to_f }.sum) # 全約定価格
			@b_fop.push(data.collect{ |row| row[4].to_f * row[3].to_i }.sum) # 完遂注文価格
			@b_fcp.push(data.collect{ |row| row[5].to_f * row[3].to_i }.sum) # 完遂約定価格
			@b_pro.push(data.collect{ |row| row[6].to_f }.sum) # 便益
			@b_wel.push(data.collect{ |row| row[7].to_f }.sum) # 余剰
		end
	end
	
	# システムパラメータを読む
	#   sysparam_str = "キー1=値1:キー2=値2:..."
	def extract_sysparam(sysparam_str, key)
		sysparam_str.split(":").each do |e|
			k, v = e.split("=")
			return v if k == key
		end
		""
	end
end

if ARGV.count < 2
	STDERR.puts "usage: #{$0} level seed_count"
	STDERR.puts "  level = 0:check 1:run 2:report 3:run&report"
	exit 1
end
$log_dir.mkpath
$log = MyLogger.new($log_dir + "stdout.txt")
puts "Logging in #{$log_dir}"
exp = Experiment.new(ARGV[1].to_i)
exp.run(ARGV[0].to_i)
