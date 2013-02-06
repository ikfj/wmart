#!/usr/bin/ruby -Ku

require "csv"

# 複数回のシミュレーションを統計的に扱うクラス
class Experiment
	def initialize(dir)
		# dir = ワーキングディレクトリ名 ("work" など)
		@mydir = dir
	end
	def analyze()
		dirname = @mydir # 仮
# 		Dir.glob("#{@mydir}/*/").each do |dirname|
			s = Scenario.new(dirname)
			s.prepare()
			s.analyze()
			s.report()
# 		end
		
		
	end
end

# 1回のシミュレーションを扱うクラス
class Scenario
	def initialize(dir)
		# dirname = ログディレクトリ名 ("100125-201530" など)
		@mydir = dir
		@executionsdirname = "#{@mydir}/execution"
		@orders = Hash.new
		@buy_orders = Hash.new
		puts "Scenario #{dir} initialized."
	end
	attr_accessor :orders, :buy_orders
	
	def prepare()
		# 注文一覧を読み込む
		puts "Loading orders..."
		Dir.glob("#{@mydir}/order/*.csv").each do |filename|
			data = CSV.readlines(filename)
			data.shift # 先頭行を捨てる
			data.each do |row|
				id = row[5].to_i
				raise "multiple orders with same id #{id}!" if @orders.include?(id)
				o = Order.new(id)
				o.sell_buy = row[8].to_i
				o.order_price = o.order_price_of(row[10], row[11])
				o.order_volume = o.order_area_of(row[11])
				@orders[o.id] = o
				@buy_orders[o.id] = o if o.sell_buy == 2
			end
		end
		# 約定一覧を読み込む
		puts "Loading executions..."
		Dir.glob("#{@mydir}/execution/*.csv").each do |filename|
			data = CSV.readlines(filename)
			data.shift # 先頭行を捨てる
			data.each do |row|
				id = row[6].to_i
				raise "order id #{id} is executed but not ordered!" if not @orders.include?(id)
				o = @orders[id]
				o.contract_price = row[4]
				o.contract_volume = o.contract_area_of(row[5])
			end
		end
	end
	def analyze()
		@orders.keys.sort.each do |key|
			o = @orders[key]
			puts "#{o.id}: order #{o.order_volume} $#{o.order_price}    \t==> #{o.contract_volume} $#{o.contract_price}\t"
		end
	end
	def report()
		# 成功率
		buy_order_0 = self.buy_orders
		buy_order_1 = buy_order_0.select { |id, o| o.contract_volume }
		success_ratio = buy_order_1.size.to_f / buy_order_0.size.to_f
		puts "success_ratio = #{success_ratio}"
		
		# 負荷率
		sell_area = {"A"=>1600, "B"=>1600, "C"=>1600, "D"=>1600, "E"=>1600} # 仮。どうやって引き継ごう?
# 		buy_area = Hash.new(0)
# 		load_ratio = Hash.new
# 		sell_area.each do |good, area|
# 			buy_area[good] += 
# 		end
		
		
		
		puts "OK"
	end
end

# 1個の注文を表すクラス
class Order
	def initialize(id)
		@id = id
	end
	attr_accessor :id, :sell_buy, :good, :order_price, :order_volume, :contract_price, :contract_volume
	
	# 注文価格を求める (単価ではなく)
	def order_price_of(unitprice, orderspec)
		if (self.sell_buy == 2) # 買い注文
			unitprice.to_i
		else
			a = orderspec.split(/;/)
			raise "selling order #{id} has multiple goods!" if a.size > 1
			unitprice.to_i * a[0].split(/:/)[4].to_i
		end
	end
	# 注文「面積」＝数量×時間を求める
	def order_area_of(orderspec)
		area = Hash.new
		orderspec.split(/;/).each do |cell|
			good, volume, earliest, latest, total = cell.split(/:/)
			area[good] = volume.to_i * total.to_i
		end
		area
	end
	# 約定「面積」を求める
	def contract_area_of(volume)
		if (self.sell_buy == 2) # 買い注文
			@order_volume.clone
		else
			area = Hash.new
			area[@order_volume.keys[0]] = volume
			area
		end
	end
	# 余剰を求める
	def welfare()
		if @order_price && @contract_price
			(@contract_price - @order_price).abs
		else
			0
		end
	end
end

ARGV.each do |arg|
	e = Experiment.new(arg)
	e.analyze()
end
