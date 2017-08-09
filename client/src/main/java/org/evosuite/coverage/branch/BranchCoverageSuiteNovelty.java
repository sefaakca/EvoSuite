package org.evosuite.coverage.branch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Random;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.archive.TestsArchiveNovelty;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.graphs.cfg.CFGMethodAdapter;
import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.TestNoveltyFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testsuite.AbstractTestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteNoveltyFunction;
import org.evosuite.utils.LoggingUtils;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BranchCoverageSuiteNovelty extends TestSuiteNoveltyFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2986859399730533523L;
	private final static Logger logger = LoggerFactory.getLogger(TestSuiteNoveltyFunction.class);
	List<Map<Integer, Integer>> predicateCountList = new ArrayList<Map<Integer, Integer>>();
	List<Map<Integer, Integer>> predicateCountListIndividual = new ArrayList<Map<Integer, Integer>>();
	
	// Coverage targets
		public int totalGoals;
		public int totalMethods;
		public int totalBranches;
		public final int numBranchlessMethods;
		private final Set<String> branchlessMethods;
		private final Set<String> methods;

		protected final Set<Integer> branchesId;
		
		// Some stuff for debug output
		public int maxCoveredBranches = 0;
		public int maxCoveredMethods = 0;
		public double bestNovelty = Double.MAX_VALUE;

		// Each test gets a set of distinct covered goals, these are mapped by branch id
		protected final Map<Integer, TestNoveltyFunction> branchCoverageTrueMap = new HashMap<Integer, TestNoveltyFunction>();
		protected final Map<Integer, TestNoveltyFunction> branchCoverageFalseMap = new HashMap<Integer, TestNoveltyFunction>();
		private final Map<String, TestNoveltyFunction> branchlessMethodCoverageMap = new HashMap<String, TestNoveltyFunction>();

		private final Set<Integer> toRemoveBranchesT = new HashSet<>();
		private final Set<Integer> toRemoveBranchesF = new HashSet<>();
		private final Set<String> toRemoveRootBranches = new HashSet<>();	
		
		private final Set<Integer> removedBranchesT = new HashSet<>();
		private final Set<Integer> removedBranchesF = new HashSet<>();
		private final Set<String> removedRootBranches = new HashSet<>();	
		
		// Total coverage value, used by Regression
		public double totalCovered = 0.0;	

		public BranchCoverageSuiteNovelty() {

			this(TestGenerationContext.getInstance().getClassLoaderForSUT());
		}
		
		public BranchCoverageSuiteNovelty(ClassLoader classLoader) {
			
			String prefix = Properties.TARGET_CLASS_PREFIX;

			if (prefix.isEmpty())
				prefix = Properties.TARGET_CLASS;

			totalMethods = CFGMethodAdapter.getNumMethodsPrefix(classLoader, prefix);
			totalBranches = BranchPool.getInstance(classLoader).getBranchCountForPrefix(prefix);
			numBranchlessMethods = BranchPool.getInstance(classLoader).getNumBranchlessMethodsPrefix(prefix);
			branchlessMethods = BranchPool.getInstance(classLoader).getBranchlessMethodsPrefix(prefix);
			methods = CFGMethodAdapter.getMethodsPrefix(classLoader, prefix);
			
			branchesId = new HashSet<>();

			totalGoals = 2 * totalBranches + numBranchlessMethods;

			logger.info("Total branch coverage goals: " + totalGoals);
			logger.info("Total branches: " + totalBranches);
			logger.info("Total branchless methods: " + numBranchlessMethods);
			logger.info("Total methods: " + totalMethods + ": " + methods);

			determineCoverageGoals();
		}
		
		protected void determineCoverageGoals() {
			List<BranchCoverageTestNovelty> goals = new BranchCoverageFactoryNovelty().getCoverageGoals();
			for (BranchCoverageTestNovelty goal : goals) {

				// Skip instrumented branches - we only want real branches
				if(goal.getBranch() != null) {
					if(goal.getBranch().isInstrumented()) {
						continue;
					}
				}
				if(Properties.TEST_ARCHIVE)
					TestsArchiveNovelty.instance.addGoalToCover(this, goal);
				if (goal.getBranch() == null) {
					branchlessMethodCoverageMap.put(goal.getClassName() + "."
					                                        + goal.getMethod(), goal);
				} else {
					branchesId.add(goal.getBranch().getActualBranchId());
					if (goal.getBranchExpressionValue())
						branchCoverageTrueMap.put(goal.getBranch().getActualBranchId(), goal);
					else
						branchCoverageFalseMap.put(goal.getBranch().getActualBranchId(), goal);
				}
			}
			totalGoals = goals.size();
		}
	
		
	@Override
	public double getNovelty(
			AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite) {
		
		logger.trace("Calculating branch novelty");
		double novelty = 0.0;
		
		List<ExecutionResult> results = runTestSuite(suite);
		
		Map<Integer, Double> trueDistance = new HashMap<Integer, Double>();
		Map<Integer, Double> falseDistance = new HashMap<Integer, Double>();
		Map<Integer, Integer> predicateCount = new HashMap<Integer, Integer>();
		Map<String, Integer> callCount = new HashMap<String, Integer>();

		// Collect stats in the traces 
		boolean hasTimeoutOrTestException = analyzeTraces(suite, results, predicateCount,
		                                                  callCount, trueDistance,
		                                                 falseDistance);
		
		
		// In case there were exceptions in a constructor
		handleConstructorExceptions(suite, results, callCount);

		// Collect branch distances of covered branches
		int numCoveredBranches = 0;

		
			
			for(Integer key : predicateCount.keySet())
			{
				
				double df = 0.0;
				double dt = 0.0;
				int numExecuted = predicateCount.get(key);
				
				if (trueDistance.containsKey(key)) {
					dt =  trueDistance.get(key);
				}
				if(falseDistance.containsKey(key)){
					df = falseDistance.get(key);
				}
				
				if(numExecuted==1)
					novelty +=1;
				else
					novelty+=normalize(df) + normalize(dt);
				
				if (falseDistance.containsKey(key)&&(Double.compare(dt, 0.0) == 0))
					numCoveredBranches++;

				if (trueDistance.containsKey(key)&&(Double.compare(dt, 0.0) == 0))
					numCoveredBranches++;
				
				
			}
			
		//}
		
		
		novelty += 2 * (totalBranches - predicateCount.size());
		// Ensure all methods are called
		int missingMethods = 0;
		for (String e : methods) {
			if (!callCount.containsKey(e)) {
				novelty += 1.0;
				missingMethods += 1;
			}
		}
		printStatusMessages(suite, numCoveredBranches, totalMethods - missingMethods,
				novelty);

		// Calculate coverage
		int coverage = numCoveredBranches;
		for (String e : branchlessMethodCoverageMap.keySet()) {
			if (callCount.keySet().contains(e)) {
				coverage++;
			}

		}

		coverage +=removedBranchesF.size();
		coverage +=removedBranchesT.size();
		coverage +=removedRootBranches.size();
	
 		
		if (totalGoals > 0)
			suite.setCoverageNovelty(this, (double) coverage / (double) totalGoals);
		else 
            suite.setCoverageNovelty(this, 1);
		
		totalCovered = suite.getCoverageNovelty(this);

		suite.setNumOfCoveredGoals(this, coverage);
		suite.setNumOfNotCoveredGoals(this, totalGoals-coverage);
		
		if (hasTimeoutOrTestException) {
			logger.info("Test suite has timed out, setting novelty to max value "
			        + (totalBranches * 2 + totalMethods));
			novelty = totalBranches * 2 + totalMethods;
			//suite.setCoverage(0.0);
		}

		updateIndividual(this, suite, novelty);

		assert (coverage <= totalGoals) : "Covered " + coverage + " vs total goals "
		        + totalGoals;
		assert (novelty >= 0.0);
		//assert (novelty != 0.0 || coverage == totalGoals) : "Novelty: " + novelty + ", "
		 //       + "coverage: " + coverage + "/" + totalGoals;
		assert (suite.getCoverageNovelty(this) <= 1.0) && (suite.getCoverageNovelty(this) >= 0.0) : "Wrong coverage value "
		        + suite.getCoverageNovelty(this); 
		return novelty;
	}
	private void handleConstructorExceptions(AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite, List<ExecutionResult> results,
	        Map<String, Integer> callCount) {

		for (ExecutionResult result : results) {
			if (result.hasTimeout() || result.hasTestException()
			        || result.noThrownExceptions())
				continue;

			Integer exceptionPosition = result.getFirstPositionOfThrownException();
			
			// TODO: Not sure why that can happen
			if(exceptionPosition >= result.test.size())
				continue;
			
			
			Statement statement = null;
			if(result.test.hasStatement(exceptionPosition))
				statement = result.test.getStatement(exceptionPosition);
			if (statement instanceof ConstructorStatement) {
				ConstructorStatement c = (ConstructorStatement) statement;
				String className = c.getConstructor().getName();
				String methodName = "<init>"
				        + Type.getConstructorDescriptor(c.getConstructor().getConstructor());
				String name = className + "." + methodName;
				if (!callCount.containsKey(name)) {
					callCount.put(name, 1);
					if (branchlessMethodCoverageMap.containsKey(name)) {
						result.test.addCoveredGoal(branchlessMethodCoverageMap.get(name));
						if(Properties.TEST_ARCHIVE) {
							TestsArchiveNovelty.instance.putTest(this, branchlessMethodCoverageMap.get(name), result);
							toRemoveRootBranches.add(name);
							suite.isToBeUpdated(true);
						}
					}

				}
			}

		}
	}


	protected void handleBranchlessMethods(AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite, ExecutionResult result, Map<String, Integer> callCount) {
		for (Entry<String, Integer> entry : result.getTrace().getMethodExecutionCount().entrySet()) {

			if (entry.getKey() == null || !methods.contains(entry.getKey()) || removedRootBranches.contains(entry.getKey()))
				continue;
			if (!callCount.containsKey(entry.getKey()))
				callCount.put(entry.getKey(), entry.getValue());
			else {
				callCount.put(entry.getKey(),
						callCount.get(entry.getKey()) + entry.getValue());
			}
			// If a specific target method is set we need to check
			// if this is a target branch or not
			if (branchlessMethodCoverageMap.containsKey(entry.getKey())) {
				result.test.addCoveredGoal(branchlessMethodCoverageMap.get(entry.getKey()));
				if (Properties.TEST_ARCHIVE) {
					TestsArchiveNovelty.instance.putTest(this, branchlessMethodCoverageMap.get(entry.getKey()), result);
					toRemoveRootBranches.add(entry.getKey());
					suite.isToBeUpdated(true);
				}
			}
		}
	}

	protected void handlePredicateCount(AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite, ExecutionResult result, Map<Integer, Integer> predicateCount) {
		for (Entry<Integer, Integer> entry : result.getTrace().getPredicateExecutionCount().entrySet()) {
			if (!branchesId.contains(entry.getKey())
					|| (removedBranchesT.contains(entry.getKey())
					&& removedBranchesF.contains(entry.getKey())))
				continue;
			if (!predicateCount.containsKey(entry.getKey()))
				predicateCount.put(entry.getKey(), entry.getValue());
			else {
				predicateCount.put(entry.getKey(),
						predicateCount.get(entry.getKey())
								+ entry.getValue());
			}
		}
	}

	protected void handleTrueDistances(AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite, ExecutionResult result, Map<Integer, Double> trueDistance) {
		for (Entry<Integer, Double> entry : result.getTrace().getTrueDistances().entrySet()) {
			if(!branchesId.contains(entry.getKey())||removedBranchesT.contains(entry.getKey())) continue;
			if (!trueDistance.containsKey(entry.getKey()))
				trueDistance.put(entry.getKey(), entry.getValue());
			else {
				trueDistance.put(entry.getKey(),
						Math.min(trueDistance.get(entry.getKey()),
								entry.getValue()));
			}
			if ((Double.compare(entry.getValue(), 0.0) == 0)) {
				result.test.addCoveredGoal(branchCoverageTrueMap.get(entry.getKey()));
				if(Properties.TEST_ARCHIVE) {
					TestsArchiveNovelty.instance.putTest(this, branchCoverageTrueMap.get(entry.getKey()), result);
					toRemoveBranchesT.add(entry.getKey());
					suite.isToBeUpdated(true);
				}
			}
		}

	}

	protected void handleFalseDistances(AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite, ExecutionResult result, Map<Integer, Double> falseDistance) {
		for (Entry<Integer, Double> entry : result.getTrace().getFalseDistances().entrySet()) {
			if(!branchesId.contains(entry.getKey())||removedBranchesF.contains(entry.getKey())) continue;
			if (!falseDistance.containsKey(entry.getKey()))
				falseDistance.put(entry.getKey(), entry.getValue());
			else {
				falseDistance.put(entry.getKey(),
						Math.min(falseDistance.get(entry.getKey()),
								entry.getValue()));
			}
			if ((Double.compare(entry.getValue(), 0.0) == 0)) {
				result.test.addCoveredGoal(branchCoverageFalseMap.get(entry.getKey()));
				if(Properties.TEST_ARCHIVE) {
					TestsArchiveNovelty.instance.putTest(this, branchCoverageFalseMap.get(entry.getKey()), result);
					toRemoveBranchesF.add(entry.getKey());
					suite.isToBeUpdated(true);
				}
			}
		}

	}

	
	private boolean analyzeTraces(AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite,
			List<ExecutionResult> results, Map<Integer, Integer> predicateCount, Map<String, Integer> callCount,
			Map<Integer, Double> trueDistance, Map<Integer, Double> falseDistance) {
		boolean hasTimeoutOrTestException = false;
		for (ExecutionResult result : results) {
			if (result.hasTimeout() || result.hasTestException()) {
				hasTimeoutOrTestException = true;
				continue;
			}

			handleBranchlessMethods(suite, result, callCount);
			handlePredicateCount(suite, result, predicateCount);
			handleTrueDistances(suite, result, trueDistance);
			handleFalseDistances(suite, result, falseDistance);
		}
		return hasTimeoutOrTestException;
	}
	
	private void printStatusMessages(
	        AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite,
	        int coveredBranches, int coveredMethods, double novelty) {
		if (coveredBranches > maxCoveredBranches) {
			maxCoveredBranches = coveredBranches;
			logger.info("(Branches) Best individual covers " + coveredBranches + "/"
			        + (totalBranches * 2) + " branches and " + coveredMethods + "/"
			        + totalMethods + " methods");
			logger.info("Novelty: " + novelty + ", size: " + suite.size() + ", length: "
			        + suite.totalLengthOfTestCases());
		}
		if (coveredMethods > maxCoveredMethods) {
			logger.info("(Methods) Best individual covers " + coveredBranches + "/"
			        + (totalBranches * 2) + " branches and " + coveredMethods + "/"
			        + totalMethods + " methods");
			maxCoveredMethods = coveredMethods;
			logger.info("Novelty: " + novelty + ", size: " + suite.size() + ", length: "
			        + suite.totalLengthOfTestCases());
		}
		if (novelty < bestNovelty) {
			logger.info("(Novelty) Best individual covers " + coveredBranches + "/"
			        + (totalBranches * 2) + " branches and " + coveredMethods + "/"
			        + totalMethods + " methods");
			bestNovelty = novelty;
			logger.info("Novelty: " + novelty + ", size: " + suite.size() + ", length: "
			        + suite.totalLengthOfTestCases());
		}
	}

	@SuppressWarnings("finally")
	@Override
	public double getNovelty(AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite,
				List<AbstractTestSuiteChromosome<? extends ExecutableChromosome>> population,
				List<AbstractTestSuiteChromosome<? extends ExecutableChromosome>> archive)
					throws ConcurrentModificationException {

		
		try
		{
			Thread.sleep(50);
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			Map<Integer, Integer> currentpredicateCount = new HashMap<Integer, Integer>();
			
			Iterator<AbstractTestSuiteChromosome<? extends ExecutableChromosome>> it = population.iterator();
		
			while (it.hasNext()) {
			
			AbstractTestSuiteChromosome<? extends ExecutableChromosome> others =it.next();

			List<ExecutionResult> results = runTestSuite(others);
			Map<Integer, Double> trueDistance = new HashMap<Integer, Double>();
			Map<Integer, Double> falseDistance = new HashMap<Integer, Double>();
			Map<Integer, Integer> predicateCount = new HashMap<Integer, Integer>();
			Map<String, Integer> callCount = new HashMap<String, Integer>();
			
			boolean hasTimeoutOrTestException = analyzeTraces(others, results, predicateCount, 
												callCount, trueDistance, falseDistance);
			handleConstructorExceptions(others, results, callCount);
			
			if(others.equals(suite))
			{
				currentpredicateCount= predicateCount;
				
				int numCoveredBranches = 0;
				
				for(Integer key : predicateCount.keySet())
				{
					double df = 0.0;
					double dt = 0.0;
					
					int numExecuted = predicateCount.get(key);
					
					if (trueDistance.containsKey(key)) {
						dt =  trueDistance.get(key);
					}
					if(falseDistance.containsKey(key)){
						df = falseDistance.get(key);
					}
					
					if (falseDistance.containsKey(key)&&(Double.compare(df, 0.0) == 0))
						numCoveredBranches++;

					if (trueDistance.containsKey(key)&&(Double.compare(dt, 0.0) == 0))
						numCoveredBranches++;
				}
				
				int missingMethods = 0;
				for (String e : methods) {
					if (!callCount.containsKey(e)) {
						
						missingMethods += 1;
					}
				}
				
				// Calculate coverage
				int coverage = numCoveredBranches;
				for (String e : branchlessMethodCoverageMap.keySet()) {
					if (callCount.keySet().contains(e)) {
						coverage++;
					}

				}

				coverage +=removedBranchesF.size();
				coverage +=removedBranchesT.size();
				coverage +=removedRootBranches.size();
				
				if (totalGoals > 0)
					suite.setCoverageNovelty(this, (double) coverage / (double) totalGoals);
				else 
		            suite.setCoverageNovelty(this, 1);
				
				totalCovered = suite.getCoverageNovelty(this);

				suite.setNumOfCoveredGoals(this, coverage);
				suite.setNumOfNotCoveredGoals(this, totalGoals-coverage);
			}
			

			predicateCountList.add(predicateCount);
			
		}
		
		noveltywithArchive(predicateCountList,archive);
		
		Collection<Integer> curvalues = currentpredicateCount.values();
		double distance =0;
		for(int i =0 ; i<predicateCountList.size();i++)
		{
			Collection<Integer> othervalues = predicateCountList.get(i).values();
			
			Iterator<Integer> currentIterator = curvalues.iterator();
			
			Iterator<Integer> otherIterator = othervalues.iterator();
			
			if(currentIterator.hasNext() && otherIterator.hasNext())
				distance +=Math.abs(currentIterator.next()-otherIterator.next());
			else if(otherIterator.hasNext())
				distance +=otherIterator.next();
				
		}//end of for loop
		
		
		distance = distance/population.size();
		distance = normalize(distance);
		
		return distance;
		}
	
	}

	private void noveltywithArchive(List<Map<Integer, Integer>> predicateCountList2,
			List<AbstractTestSuiteChromosome<? extends ExecutableChromosome>> archive)
	{
			Iterator<AbstractTestSuiteChromosome<? extends ExecutableChromosome>> it = archive.iterator();
		
			while (it.hasNext()) {
			
			AbstractTestSuiteChromosome<? extends ExecutableChromosome> others =it.next();

			List<ExecutionResult> results = runTestSuite(others);
			Map<Integer, Double> trueDistance = new HashMap<Integer, Double>();
			Map<Integer, Double> falseDistance = new HashMap<Integer, Double>();
			Map<Integer, Integer> predicateCount = new HashMap<Integer, Integer>();
			Map<String, Integer> callCount = new HashMap<String, Integer>();
			
			boolean hasTimeoutOrTestException = analyzeTraces(others, results, predicateCount, 
												callCount, trueDistance, falseDistance);
			handleConstructorExceptions(others, results, callCount);
			
			
			predicateCountList.add(predicateCount);
			
		}
		
	}
	
	/*
	@SuppressWarnings("finally")
	public double getNovelty(TestSuiteChromosome individual, List<TestChromosome> population,
			List<TestChromosome> archive) {
		// TODO Auto-generated method stub	
		try
		{
			Thread.sleep(50);
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			double noveltymetric = 0;
			int cov=0;
			//LoggingUtils.getEvoLogger().info("TEST SUITE NOVELTY FUNCTION");
			List<ExecutionResult> result=runTestSuite(individual);
			Iterator<ExecutionResult>itexec= result.iterator();
			Map<Integer, Integer> Counter = new HashMap<Integer, Integer>();
			while(itexec.hasNext())
			{
				Map<Integer, Integer> currentpredicateCount = new HashMap<Integer, Integer>();
				Map<Integer, Integer> restofthepopCount = new HashMap<Integer, Integer>();			
				if(currentpredicateCount.containsKey(itexec.next().getTrace().getPredicateExecutionCount()))
				currentpredicateCount.putAll(itexec.next().getTrace().getPredicateExecutionCount());	
				Iterator<TestChromosome> popit = population.iterator();
				Iterator<TestChromosome> archit = archive.iterator();
				
			
				
				//Predicate Counts for archive
				while(archit.hasNext()){
					org.evosuite.testcase.TestCase arc = archit.next().getTestCase();
					ExecutionResult otherresult = runTest(arc);
					restofthepopCount.putAll(otherresult.getTrace().getPredicateExecutionCount());
					if(!predicateCountList.contains(restofthepopCount))
						predicateCountList.add(restofthepopCount);
				}
			
				//Predicate Counts for rest of the pop
				while (popit.hasNext()) {
			
					org.evosuite.testcase.TestCase others = popit.next().getTestCase();

					ExecutionResult otherresult = runTest(others);
			
					restofthepopCount.putAll(otherresult.getTrace().getPredicateExecutionCount());
					if(!predicateCountList.contains(restofthepopCount))
						predicateCountList.add(restofthepopCount);
				}
			
				//predicateCountList.add(restofthepopCount);
		
				Collection<Integer> curvalues = currentpredicateCount.values();
				//double noveltymetric = 0;
				//Collection<Integer> restvalues = restofthepopCount.values();
				
				for(int i =0 ; i<predicateCountList.size();i++)
				{
					Collection<Integer> othervalues = predicateCountList.get(i).values();
					Iterator<Integer> currentIterator = curvalues.iterator();
					Iterator<Integer> otherIterator = othervalues.iterator();
					if(currentIterator.hasNext() && otherIterator.hasNext())
						noveltymetric +=Math.abs(currentIterator.next()-otherIterator.next());
					else if(otherIterator.hasNext())
						noveltymetric +=otherIterator.next();

				}

				noveltymetric = (noveltymetric/population.size());
				noveltymetric = normalize(noveltymetric);
				
			}
			individual.setCoverageNovelty(this, (double)cov/(double)totalGoals);
			updateIndividual(this, individual, noveltymetric);
			return noveltymetric;
			
			/*
			List<ExecutionResult> result=runTestSuite(individual);
			Iterator<ExecutionResult>itexec= result.iterator();
			
			if(itexec.hasNext())
			{
				Map<Integer, Integer> currentpredicateCount = new HashMap<Integer, Integer>();
				Map<Integer, Integer> restofthepopCount = new HashMap<Integer, Integer>();			
				currentpredicateCount.putAll(itexec.next().getTrace().getPredicateExecutionCount());	
				Iterator<TestChromosome> popit = population.iterator();
				Iterator<TestChromosome> archit = archive.iterator();
			
				//Predicate Counts for archive
				while(archit.hasNext()){
					org.evosuite.testcase.TestCase arc = archit.next().getTestCase();
					ExecutionResult otherresult = runTest(arc);
					restofthepopCount.putAll(otherresult.getTrace().getPredicateExecutionCount());
				
				}
			
				//Predicate Counts for rest of the pop
				while (popit.hasNext()) {
			
					org.evosuite.testcase.TestCase others = popit.next().getTestCase();

					ExecutionResult otherresult = runTest(others);
			
					restofthepopCount.putAll(otherresult.getTrace().getPredicateExecutionCount());
				}
			
				predicateCountList.add(restofthepopCount);
		
				Collection<Integer> curvalues = currentpredicateCount.values();
				double distance = 0;
				//Collection<Integer> restvalues = restofthepopCount.values();
		
				for(int i =0 ; i<predicateCountList.size();i++)
				{
					Collection<Integer> othervalues = predicateCountList.get(i).values();
					Iterator<Integer> currentIterator = curvalues.iterator();
					Iterator<Integer> otherIterator = othervalues.iterator();
					if(currentIterator.hasNext() && otherIterator.hasNext())
						distance +=Math.abs(currentIterator.next()-otherIterator.next());
					else if(otherIterator.hasNext())
						distance +=otherIterator.next();
				}

				distance = distance/population.size();
				distance = normalize(distance);
				updateIndividual(this, individual, distance);
				return distance;
			}
		else{
			updateIndividual(this, individual, 0);
			return 0;
		}
			
		}
		

	}
	*/	
}

