package org.evosuite.coverage.archive;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.evosuite.Properties;
import org.evosuite.ga.Archive;
import org.evosuite.ga.NoveltyFunction;
import org.evosuite.setup.TestCluster;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestNoveltyFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.statements.FunctionalMockStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.statements.reflection.PrivateFieldStatement;
import org.evosuite.testcase.statements.reflection.PrivateMethodStatement;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.Randomness;
import org.evosuite.utils.generic.GenericAccessibleObject;
import org.evosuite.utils.generic.GenericClass;
import org.evosuite.utils.generic.GenericConstructor;
import org.evosuite.utils.generic.GenericMethod;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum TestsArchiveNovelty implements Archive<TestSuiteChromosome>, Serializable {
	instance;

	private static final Logger logger = LoggerFactory.getLogger(TestsArchiveNovelty.class);
	
	 private final Map<NoveltyFunction<?>, Set<TestNoveltyFunction>> coveredGoals;

	  private final Map<NoveltyFunction<?>, Integer> goalsCountMap;

	  // This can probably be optimised, but to remove the testsuitechromosome
	  // I'm just replicating the maps we used in here
	  private final Map<NoveltyFunction<?>, Integer> coveredGoalsCountMap;
	  private final Map<NoveltyFunction<?>, Double> coverageMap;

	  private final Map<NoveltyFunction<?>, Set<TestNoveltyFunction>> goalMap;
	  private final Map<String, Set<TestNoveltyFunction>> methodMap;

	  private final Map<TestNoveltyFunction, ExecutionResult> testMap;
	
	  private TestsArchiveNovelty() {
		// TODO Auto-generated constructor stub
		  coveredGoals = new LinkedHashMap<>();
		  goalsCountMap = new LinkedHashMap<>();
		  coveredGoalsCountMap = new LinkedHashMap<>();
		  coverageMap = new LinkedHashMap<>();
		  goalMap = new LinkedHashMap<>();
		  methodMap = new LinkedHashMap<>();
		  testMap = new LinkedHashMap<>();
	}
	  
	  public void reset() {
		    coveredGoals.clear();
		    goalMap.clear();
		    goalsCountMap.clear();
		    methodMap.clear();
		    testMap.clear();
		    coveredGoalsCountMap.clear();
		    coverageMap.clear();
		  }
	  
	  public void addGoalToCover(NoveltyFunction<?> nf, TestNoveltyFunction goal) {
		    String key = getGoalKey(goal);

		    if (!methodMap.containsKey(key)) {
		      methodMap.put(key, new LinkedHashSet<>());
		    }

		    if (!goalMap.containsKey(nf)) {
		      goalMap.put(nf, new LinkedHashSet<>());
		      goalsCountMap.put(nf, 0);
		    }

		    goalMap.get(nf).add(goal);
		    methodMap.get(key).add(goal);
		    goalsCountMap.put(nf, goalsCountMap.get(nf) + 1);

		    logger.debug("Registering new goal: " + goal);
		  }
	  public void putTest(NoveltyFunction<?> nf, TestNoveltyFunction goal, ExecutionResult result) {

		    if (!goalMap.containsKey(nf)) {
		      return;
		    }

		    if (!coveredGoals.containsKey(nf)) {
		      coveredGoals.put(nf, new LinkedHashSet<>());
		    }

		    boolean isNewCoveredGoal = !coveredGoals.get(nf).contains(goal);

		    if (isNewCoveredGoal) {
		      coveredNewGoal(nf, goal);
		    }

		    boolean better = isBetterThanCurrent(goal, result);

		    if (isNewCoveredGoal || better) {
		      ExecutionResult copy = result.clone();
		      TestCase copyTest = copy.test.clone(); //result.clone() does not clone the test
		      copy.setTest(copyTest);

		      // Remove all statements after an exception
		      if (!copy.noThrownExceptions()) {
		        copy.test.chop(copy.getFirstPositionOfThrownException() + 1);
		      }

		      testMap.put(goal, copy);
		      handleCollateralCoverage(
		          copy); //check for collateral only when there is improvement over current goal
		    }
		  }
	@SuppressWarnings({"rawtypes", "unchecked"})  
	@Override
	public TestSuiteChromosome createMergedSolution(TestSuiteChromosome suite) {
		// TODO Auto-generated method stub

	    // Deactivate in case a test is executed and would access the archive
	    // as this might cause a concurrent access
		Properties.TEST_ARCHIVE = false;
	    TestSuiteChromosome best = null;
	    try {
	    	//LoggingUtils.getEvoLogger().info("TEST ARCHIVE NOVELTY");
	      best = suite.clone();
	      for (Entry<TestNoveltyFunction, ExecutionResult> entry : testMap.entrySet()) {
	    	  if (!entry.getKey().isCoveredBy(best)) {
	          TestChromosome chromosome = new TestChromosome();
	          ExecutionResult copy = entry.getValue().clone();
	          TestCase copyTest = copy.test.clone();
	          copy.setTest(copyTest);
	          chromosome.setTestCase(copy.test);
	          chromosome.setLastExecutionResult(copy);
	          best.addTest(chromosome); //should avoid re-execute the tests
	        }
	      }
	      for (NoveltyFunction nf : coveredGoals.keySet()) {
	    	  nf.getNovelty(best);
	    	 
	      }
	    } finally {
	      Properties.TEST_ARCHIVE = true;
	    }

	    logger.info("Final test suite size from archive: " + best.size());

	    return best;
	    
	}
	
	 public boolean isArchiveEmpty() {
		    return testMap.isEmpty();
		  }
	 public int getTotalNumberOfGoals() {
		    int total = 0;
		    for (Integer numGoals : goalsCountMap.values()) {
		      total += numGoals;
		    }
		    return total;
		  }
	 public int getNumberOfCoveredGoals() {
		    int covered = 0;
		    for (Integer numGoals : coveredGoalsCountMap.values()) {
		      covered += numGoals;
		    }
		    return covered;
		  }
	 public TestCase getCloneAtRandom() {
		    /*
		            Note: this gives higher probability to tests that cover more targets.
		            Maybe it is not the best way, but likely the quickest to compute
		     */
		    ExecutionResult res = Randomness.choice(testMap.values());
		    if (!res.noThrownExceptions()) {
		      // If the test ends with an exception, remove the statement
		      // that throws the exception
		      TestCase copy = res.test.clone();
		      copy.chop(res.getFirstPositionOfThrownException());
		      return copy;
		    }
		    return res.test.clone();
		  }
	 @Override
	  public String toString() {
	    int sum = 0;
	    for (NoveltyFunction<?> nf : coveredGoals.keySet()) {
	      sum += coveredGoals.get(nf).size();
	    }
	    return "Goals covered: " + sum;
	  }
	 private void coveredNewGoal(NoveltyFunction<?> nf, TestNoveltyFunction goal) {
		    if (!coveredGoals.containsKey(nf)) {
		      coveredGoals.put(nf, new LinkedHashSet<>());
		    }
		    logger.debug("Adding covered goal to archive: " + goal);
		    coveredGoals.get(nf).add(goal);
		    updateMaps(nf, goal);
		    setCoverage(nf, goal);
		    if (isMethodFullyCovered(getGoalKey(goal))) {
		      removeTestCall(goal.getTargetClass(), goal.getTargetMethod());
		    }
	 }
	 
	 private void handleCollateralCoverage(ExecutionResult copy) {

		    //check if this improves upon already covered targets
		    for (Entry<NoveltyFunction<?>, Set<TestNoveltyFunction>> entry : coveredGoals.entrySet()) {
		      for (TestNoveltyFunction goal : entry.getValue()) {
		        if (isBetterThanCurrent(goal, copy)) {
		          testMap.put(goal, copy);
		        }
		      }
		    }

		    Map<NoveltyFunction<?>, Set<TestNoveltyFunction>> toUpdate = new LinkedHashMap<>();

		    //does it cover new targets?
		    for (Entry<NoveltyFunction<?>, Set<TestNoveltyFunction>> entry : goalMap.entrySet()) {
		      Set<TestNoveltyFunction> set = new LinkedHashSet<>();
		      toUpdate.put(entry.getKey(), set);

		      for (TestNoveltyFunction goal : entry.getValue()) {
		        if (goal.isCovered(copy)) {
		          set.add(goal); //keep track, as cannot modify goalMap while looping over it
		          testMap.put(goal, copy);
		        }
		      }
		    }

		    for (Entry<NoveltyFunction<?>, Set<TestNoveltyFunction>> entry : toUpdate.entrySet()) {
		      for (TestNoveltyFunction goal : entry.getValue()) {
		        coveredNewGoal(entry.getKey(), goal);
		      }
		    }

		  }
	 
	 private boolean isBetterThanCurrent(TestNoveltyFunction goal, ExecutionResult result) {

		    if (!goal.isCovered(result)) {
		      return false;
		    }

		    if (testMap.get(goal) == null) {
		      return true;
		    }

		    TestCase current = testMap.get(goal).test;
		    TestCase candidate = result.test;

		    int penaltyCurrent = calculatePenalty(current);
		    int penaltyCandidate = calculatePenalty(candidate);

				/*
					Check if tests are using any functional mock or private access.
					Those will be worse than a test that do not use them
				 */
		    if (penaltyCandidate < penaltyCurrent) {
		      return true;
		    } else if (penaltyCandidate > penaltyCurrent) {
		      return false;
		    }

		    // only look at length if penalty scores are the same
		    assert penaltyCandidate == penaltyCurrent;

		    // If we try to add a test for a goal we've already covered
		    // and the new test is shorter, keep the shorter one
		    if (candidate.size() < current.size()) {
		      return true;
		    }

		    return false;
		  }
	 private int calculatePenalty(TestCase tc) {
		    int penalty = 0;

		    if (hasFunctionalMocks(tc)) {
		      penalty++;
		    }
		    if (hasFunctionalMocksForGenerableTypes(tc)) {
		      penalty++;
		    }
		    if (hasPrivateAccess(tc)) {
		      penalty++;
		    }
		    return penalty;
		  }
	 
	  private boolean hasFunctionalMocks(TestCase tc) {
		    for (Statement st : tc) {
		      if (st instanceof FunctionalMockStatement) {
		        return true;
		      }
		    }
		    return false;
		  }
	  private boolean hasFunctionalMocksForGenerableTypes(TestCase tc) {
		    for (Statement st : tc) {
		      if (st instanceof FunctionalMockStatement) {
		        FunctionalMockStatement fm = (FunctionalMockStatement) st;
		        Class<?> target = fm.getTargetClass();
		        GenericClass gc = new GenericClass(target);
		        if (TestCluster.getInstance().hasGenerator(gc)) {
		          return true;
		        }
		      }
		    }
		    return false;
		  }
	  private boolean hasPrivateAccess(TestCase tc) {
		    for (Statement st : tc) {
		      if (st instanceof PrivateFieldStatement || st instanceof PrivateMethodStatement) {
		        return true;
		      }
		    }
		    return false;
		  }
	  private void setCoverage(NoveltyFunction<?> nf, TestNoveltyFunction goal) {
		    assert (goalsCountMap != null);
		    int covered = coveredGoals.get(nf).size();
		    int total = goalsCountMap.containsKey(nf) ? goalsCountMap.get(nf) : 0;
		    double coverage = total == 0 ? 1.0 : (double) covered / (double) total;
		    coveredGoalsCountMap.put(nf, covered);
		    coverageMap.put(nf, coverage);
		  }
	  private void writeObject(ObjectOutputStream oos) throws IOException {
		    throw new RuntimeException("AAARGH"); //FIXME what the heck is this???
		  }
	  protected boolean isMethodFullyCovered(String methodKey) {
		    if (!methodMap.containsKey(methodKey)) {
		      return true;
		    }
		    return methodMap.get(methodKey).isEmpty();
		  }

		  public int getNumRemainingGoals(String methodKey) {
		    if (!methodMap.containsKey(methodKey)) {
		      return 0;
		    }
		    return methodMap.get(methodKey).size();
		  }
		  
		  protected void removeTestCall(String className, String methodName) {
			    TestCluster cluster = TestCluster.getInstance();
			    List<GenericAccessibleObject<?>> calls = cluster.getTestCalls();
			    for (GenericAccessibleObject<?> call : calls) {
			      if (!call.getDeclaringClass().getName().equals(className)) {
			        continue;
			      }
			      if (call instanceof GenericMethod) {
			        GenericMethod genericMethod = (GenericMethod) call;
			        if (!methodName.startsWith(genericMethod.getName())) {
			          continue;
			        }
			        String desc = Type.getMethodDescriptor(genericMethod.getMethod());
			        if ((genericMethod.getName() + desc).equals(methodName)) {
			          logger.info("Removing method " + methodName + " from cluster");
			          cluster.removeTestCall(call);
			          logger.info("Testcalls left: " + cluster.getNumTestCalls());
			        }
			      } else if (call instanceof GenericConstructor) {
			        GenericConstructor genericConstructor = (GenericConstructor) call;
			        if (!methodName.startsWith("<init>")) {
			          continue;
			        }
			        String desc = Type.getConstructorDescriptor(genericConstructor.getConstructor());
			        if (("<init>" + desc).equals(methodName)) {
			          logger.info("Removing constructor " + methodName + " from cluster");
			          cluster.removeTestCall(call);
			          logger.info("Testcalls left: " + cluster.getNumTestCalls());
			        }
			      }
			    }
			  }
		  private void updateMaps(NoveltyFunction<?> nf, TestNoveltyFunction goal) {
			    String key = getGoalKey(goal);
			    if (!goalMap.containsKey(nf)) {
			      return;
			    }
			    goalMap.get(nf).remove(goal);
			    methodMap.get(key).remove(goal);
			  }
		  private String getGoalKey(TestNoveltyFunction goal) {
			    return goal.getTargetClass() + goal.getTargetMethod();
			  }

		  @SuppressWarnings({"rawtypes", "unchecked"})
		@Override
		public TestSuiteChromosome createMergedSolution(TestSuiteChromosome suite,
				List<TestSuiteChromosome> population, List<TestSuiteChromosome> archive) {
			// TODO Auto-generated method stub
			 Properties.TEST_ARCHIVE = false;
			    TestSuiteChromosome best = null;
			    try {
			      best = suite.clone();

			      for (Entry<TestNoveltyFunction, ExecutionResult> entry : testMap.entrySet()) {
			        if (!entry.getKey().isCoveredBy(best)) {
			          TestChromosome chromosome = new TestChromosome();
			          ExecutionResult copy = entry.getValue().clone();
			          TestCase copyTest = copy.test.clone();
			          copy.setTest(copyTest);
			          chromosome.setTestCase(copy.test);
			          chromosome.setLastExecutionResult(copy);
			          best.addTest(chromosome); //should avoid re-execute the tests
			        }
			      }
			      for (NoveltyFunction nf : coveredGoals.keySet()) {
			    	  nf.getNovelty(best,population,archive);
			      }
			    } finally {
			      Properties.TEST_ARCHIVE = true;
			    }

			    logger.info("Final test suite size from archive: " + best.size());
			    return best;
		}


}
