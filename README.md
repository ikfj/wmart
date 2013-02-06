# W-Mart #

An artificial marketplace simulator that supports double-sided combinatorial auctions.

## Author ##

Ikki Fujiwara, Ph.D. (National Institute of Informatics, Japan)

## Reference ##

- Ikki Fujiwara, Kento Aida, Isao Ono, "Applying Double-Sided Combinational Auctions to Resource Allocation in Cloud Computing", 10th IEEE/IPSJ International Symposium on Applications and the Internet (SAINT) 2010, pp.7-14, July 2010. http://dx.doi.org/10.1109/SAINT.2010.93
- Ikki Fujiwara, "Study on Combinatorial Action Mechanism for Resource Allocation in Cloud Computing Environment", Ph.D. Thesis, Department of Informatics, School of Multidisciplinary Sciences, The Graduate University for Advanced Studies (SOKENDAI), March 2012. http://www.nii.ac.jp/graduate/thesis/index_e.html#201203

## Source ##

+ Newest: https://github.com/ikfj/wmart/
+ Former: http://code.google.com/p/wmart/

## Requirements ##

+ Java 1.5 or later
+ Solver library (CPLEX or lp_solve)

+ CPLEX 10.0 (Linux) http://www-01.ibm.com/software/integration/optimization/cplex-optimizer/
+ lp_solve 5.5 (Linux) / lp_solve 5.1 (Windows) http://tech.groups.yahoo.com/group/lp_solve/

## Compile ##

Make an executable JAR with Eclipse.
The entry point 'public static void main()' is in 'core/WMartSimulator.java'.
Put it as 'work/WMartSimulator.jar'.

## Configure ##

Modify the following values in 'work/market_central.properties'.

	# 0:default, 1:LPSolve 5.1 (Windows), 2:LPSolve 5.5 (Linux x64), 4:CPLEX
	resource.allocator.solver = 4

	resource.allocator.timelimit = 300

Note: all other values may have no effect.

## Run ##

	$ cd wmart/work/
	$ java WMartSimulator <seed> <maxDays> <daysForward> <membersFile> [<parameters>]

Java may require sufficient options depending on your environment.
For example:

	$ java -d64 -Xms1G -Xmx30G -Djava.library.path=/usr/ilog/cplex/bin/x86-64_debian4.0_4.1:/usr/local/lib -jar WMartSimulator.jar 0 30 7 member/type1.csv demandfile=demand/trial.csv\;demand/trial.csv:logdir=result/trial-type1

## Further Info ##

See 'work/experiment*.rb' for what I did in my thesis.
See source for further details. Apologies for comments written in Japanese. :-)

## Original ##

W-Mart is based on the following works. Thanks a lot!

+ CATNETS http://www.catnets.org/
+ U-Mart http://www.u-mart.org/

## History ##

+ 2013-02-06 Moved to GitHub.
+ 2011-04-21 Final version. (in Google Code)
+ 2009-04-12 Initial version.
