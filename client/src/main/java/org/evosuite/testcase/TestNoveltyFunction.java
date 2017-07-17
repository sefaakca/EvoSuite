package org.evosuite.testcase;

import java.util.List;

import org.evosuite.ga.NoveltyFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.TestCaseExecutor;
import org.evosuite.testsuite.TestSuiteChromosome;

public abstract class TestNoveltyFunction extends NoveltyFunction<TestChromosome> 
								 implements Comparable<TestNoveltyFunction>{

	private static final long serialVersionUID = 2005610799823014777L;
	
	static boolean warnedAboutIsSimilarTo = false;
	
	public abstract double getNovelty(TestChromosome individual, ExecutionResult result);
	
	
	@Override
	public int compareTo(TestNoveltyFunction o) {
		// TODO Auto-generated method stub
		return 0;
	}

	protected final int compareClassName(TestNoveltyFunction other){
		return this.getClass().getName().compareTo(other.getClass().getName());
	}
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object other);
	
	public boolean isCovered(List<TestCase> tests) {
		for (TestCase test : tests) {
			if (isCovered(test))
				return true;
		}
		return false;
	}
	
	public boolean isCoveredByResults(List<ExecutionResult> tests) {
		for (ExecutionResult result : tests) {
			if (isCovered(result))
				return true;
		}
		return false;
	}
	public boolean isCoveredBy(TestSuiteChromosome testSuite) {
		int num = 1;
		for (TestChromosome test : testSuite.getTestChromosomes()) {
			logger.debug("Checking goal against test "+num+"/"+testSuite.size());
			num++;
			if (isCovered(test))
				return true;
		}
		return false;
	}
	
	public boolean isCovered(TestCase test) {
		TestChromosome c = new TestChromosome();
		c.test = test;
		return isCovered(c);
	}
	
	public boolean isCovered(TestChromosome tc) {
		if(tc.getTestCase().isGoalCovered(this)){
			return true;
		}

		ExecutionResult result = tc.getLastExecutionResult();
		if (result == null || tc.isChanged()) {
			result = runTest(tc.test);
			tc.setLastExecutionResult(result);
			tc.setChanged(false);
		}

		return isCovered(tc, result);
	}
	
	public boolean isCovered(TestChromosome individual, ExecutionResult result) {
		boolean covered = getNovelty(individual, result) == 0.0;
		if (covered) {
			individual.test.addCoveredGoal(this);
		}
		return covered;
	}
	
	public boolean isCovered(ExecutionResult result) {
		TestChromosome chromosome = new TestChromosome();
		chromosome.setTestCase(result.test);
		chromosome.setLastExecutionResult(result);
		chromosome.setChanged(false);
		return isCovered(chromosome, result);
	}
	
	public abstract String getTargetClass();

	public abstract String getTargetMethod();
	
	@Override
	public double getNovelty(TestChromosome individual) {
		// TODO Auto-generated method stub
		logger.trace("Executing test case on original");
		ExecutionResult origResult = individual.getLastExecutionResult();
		if (origResult == null || individual.isChanged()) {
			origResult = runTest(individual.test);
			individual.setLastExecutionResult(origResult);
			individual.setChanged(false);
		}
		

		double noveltyMetric = getNovelty(individual, origResult);
		updateIndividual(this, individual, noveltyMetric);

		return noveltyMetric;
	}
	
	

	private ExecutionResult runTest(TestCase test) {
		// TODO Auto-generated method stub
		return TestCaseExecutor.runTest(test);
	}
	@Override
	public boolean isMaximizationFunctionNovelty() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	@Override
	public double getNovelty(TestChromosome individual , List<TestChromosome> population)
	{
		logger.trace("Executing test case on original");
		ExecutionResult origResult = individual.getLastExecutionResult();
		if (origResult == null || individual.isChanged()) {
			origResult = runTest(individual.test);
			individual.setLastExecutionResult(origResult);
			individual.setChanged(false);
		}

		double noveltyMetric = getNovelty(individual, origResult);
		updateIndividual(this, individual, noveltyMetric);

		return noveltyMetric;
	}
*/
}
