#!/bin/bash

LPSLVLIB="/usr/local/lib"
CPLEXDIR="/usr/ilog/cplex"
CPLEXLIB="$CPLEXDIR/bin/x86-64_debian4.0_4.1"
CPLEXJAR="$CPLEXDIR/lib/cplex.jar"
JARFILE="WMartStandAloneCUI.jar"
LOGFILE="work/`date +%y%m%d-%H%M%S`.log"
COMMAND="java -d64 -Xms1G -Xmx24G -Djava.library.path=$CPLEXLIB:$LPSLVLIB -jar $JARFILE"
# COMMAND="echo"

vnbuyers="1 2 5 10 20 50 100"

generate_members() {
	mkdir -p ${dir}/members
	memf="${dir}/members/MembersSA.csv"
	echo "LoginName,Password,Attribute,Connection,Access,RealName,SystemParameters,Seed,InitialCash,TradingUnit,FeePerUnit,MarginRate,MaxLoan,Interest" > $memf
	i=1
	for g in "A" "B" "C" "D" "E"; do
		echo "user${i},passwd${i},Machine,Local,,org.wmart.agent.WSimpleSellerAgent,minprice=1:maxprice=1:volume=100:good=${g},${i},1000000000,1000,0,300000,30000000,0.1" >> $memf
		i=`expr ${i} + 1`
	done
	for ((nb = 0; nb < $1; nb++)); do
		echo "user${i},passwd${i},Machine,Local,,org.wmart.agent.WSimpleBuyerAgent,minprice=2:maxprice=10:maxvolume=20:goods=A;B;C;D;E:maxwidth=3,${i},1000000000,1000,0,300000,30000000,0.1" >> $memf
		i=`expr ${i} + 1`
	done
}

if [ "$1" != "" ]; then
	$COMMAND $* > stdout.txt 2> stderr.txt
else
	echo "$0: start!"
	for ((ii = 0; ii < 1; ii++)); do
		i=`printf %02d $ii`
		echo "$0: iter=$ii log=$LOGFILE"
		for n in $vnbuyers; do
			dir="work/b${n}"
			stdoutf="${dir}/stdout${i}.txt"
			stderrf="${dir}/stderr${i}.txt"
			generate_members $n
			#if [ ! -f $stdoutf ]; then
				cmd="$COMMAND detail $dir 0 40 7"
				echo $cmd
				$cmd > $stdoutf 2> $stderrf
				rtncode="$?"
				echo "finish=`date +%y%m%d-%H%M%S` return=$rtncode" >> $LOGFILE
			#else
				#echo "exists" >> $LOGFILE
			#fi
		done
	done
	echo "$0: finish!"
fi

