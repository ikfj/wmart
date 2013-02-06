#!/usr/bin/ruby

# �I�[�_�[�W�F�l���[�^ for �敨�s��

vngoods = [10]		# ���i��
vnslots = [720]		# �X���b�g��
vnagents = [[10,10],[10,100],[10,400],[10,700],[10,1000],[10,2000],[10,4000]]		# [����萔,�����萔]

def generate(nsellers, nbuyers, ngoods, nslots)
	dirname = File.dirname($0) + File::SEPARATOR + format("m%dn%dg%dt%03d", nsellers, nbuyers, ngoods, nslots)
	puts dirname
	Dir.mkdir(dirname) if not File.exist?(dirname)
	srand(ARGV[0].to_i) if ARGV[0]
	vaskl = (nslots..nslots)		# ���蒷���͈̔�
	vbidl = (nslots < 12) ? (1..1) : (nslots < 24) ? (1..12) : (1..24)  		# ���������͈̔�
	vaskp = (1..1)		# ����P���͈̔�
	vbidp = (3..3)		# �����P���͈̔�
	vaskq = (100..100)		# ���萔�ʂ͈̔�
	vbidq = (1..100)		# �������ʂ͈̔�
	3.times do |i|
		filename = dirname + File::SEPARATOR + format("orders%02d.txt", i)
		next if File.exist?(filename)
		puts filename
		open(filename, "w") do |f|
			o = 0
			# ���蒍��
			nsellers.times do |m|
				l = rand_within(vaskl)
				a = rand_within(0..(nslots - vaskl.last))
				d = a + l - 1
				q = rand_within(vaskq)
				p = rand_within(vaskp) * q
# 				g = rand(ngoods)
				g = m
				spec = format("agent%03d,sell,%d,service%d,%d,%d,%d,%d", o, p, g, q, a, d, l)
				f.puts(spec)
				o += 1
			end
			# ��������
			nbuyers.times do |n|
				l = rand_within(vbidl)
				a = rand_within(0..(nslots - vbidl.last))
				d = a + l - 1
				p = 0
				spec = ""
# 				g = rand(ngoods)
				allgoods = Array.new(ngoods) {|g| g }
				pick_outof(allgoods, 1, 5).each do |g|
					q = rand_within(vbidq)
					p += rand_within(vbidp) * q * l
					spec += format(",service%d,%d,%d,%d,%d", g, q, a, d, l)
				end
				spec = format("agent%03d,buy,%d", o, p) + spec
				f.puts(spec)
				o += 1
			end
		end
	end
end

# range �͈͓̔��̐���������Ԃ��B
def rand_within(range)
	raise("out of range (#{range.first}..#{range.last})") if range.first > range.last or range.first < 0 or range.last < 0
	if range.first == range.last
		range.first
	else
		range.first + rand(range.last - range.first + (range.exclude_end? ? 0 : 1))
	end
end

# �z�� array ���� min �ȏ� max �ȉ��̗v�f�������_���ɑI�񂾔z���Ԃ��B
# max == array.length * n �̂Ƃ��A�e�v�f�����傤�� n �񂸂����B
def pick_outof(array, min, max)
	a = array.dup
	x = Array.new
	rand_within(min..max).times do |i|
		k = rand_within(0...a.size)
		x.push(a[k])
		a.delete_at(k)
		a = array.dup if a.empty?
	end
	x
end

vngoods.each do |ngoods|
	vnslots.each do |nslots|
		vnagents.each do |nsellers, nbuyers|
			generate(nsellers, nbuyers, ngoods, nslots)
		end
	end
end

