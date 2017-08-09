package org.evosuite.coverage.branch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.evosuite.coverage.ControlFlowDistance;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.runtime.RuntimeSettings;
import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.TestNoveltyFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.ExecutionTrace;
import org.evosuite.testcase.execution.MethodCall;
import org.evosuite.testsuite.AbstractTestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteChromosome;

import junit.framework.TestCase;

public class BranchCoverageTestNovelty extends TestNoveltyFunction {
	
	List<Map<Integer, Integer>> predicateCountList = new ArrayList<Map<Integer, Integer>>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1429307853078217502L;
	/**
	 * 
	 */
	
	private final BranchCoverageGoal goal;
	
	public Branch getBranch() {
		return goal.getBranch();
	}

	public boolean getValue() {
		return goal.getValue();
	}
	
	public BranchCoverageGoal getBranchGoal() {
		return goal;
	}
	
	public boolean getBranchExpressionValue() {
		return goal.getValue();
	}

	public BranchCoverageTestNovelty(BranchCoverageGoal goal)  throws IllegalArgumentException {
		if(goal == null){
			throw new IllegalArgumentException("goal cannot be null");
		}
		this.goal = goal;
	}
	
	public double getUnNovel(ExecutableChromosome individual, ExecutionResult result) {

		double sum = 0.0;
		boolean methodExecuted = false;

		for (MethodCall call : result.getTrace().getMethodCalls()) {
			if (call.className.equals(goal.getClassName())
			        && call.methodName.equals(goal.getMethodName())) {
				methodExecuted = true;
				if (goal.getBranch() != null) {
					for (int i = 0; i < call.branchTrace.size(); i++) {
						if (call.branchTrace.get(i) == goal.getBranch().getInstruction().getInstructionId()) {
							if (goal.getValue())
								sum += call.falseDistanceTrace.get(i);
							else
								sum += call.trueDistanceTrace.get(i);
						}
					}
				}
			}
		}

		if (goal.getBranch() == null) {
			// logger.info("Branch is null? " + goal.branch);
			if (goal.getValue())
				sum = methodExecuted ? 1.0 : 0.0;
			else
				sum = methodExecuted ? 0.0 : 1.0;

		}

		return sum;
	}

	@Override
	public double getNovelty(TestChromosome individual, ExecutionResult result) throws ConcurrentModificationException {
		// TODO Auto-generated method stub
		
		ControlFlowDistance distance = goal.getDistance(result);
		
		double novelty = distance.getResultingBranchNovelty();
		
		
		if(logger.isDebugEnabled()) {
			logger.debug("Goal at line "+goal.getLineNumber()+": approach level = " + distance.getApproachLevel()
					+ " / branch distance = " + distance.getBranchDistance() + ", novelty metric = " + novelty);
		}
		updateIndividual(this, individual, novelty);
		
		return novelty;
		
	}
	
	@Override
	public String toString() {
		return goal.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.evosuite.testcase.TestFitnessFunction#getTargetClass()
	 */
	@Override
	public String getTargetClass() {
		return getClassName();
	}
	
	@Override
	public int compareTo(TestNoveltyFunction other) {
		if (other instanceof BranchCoverageTestNovelty) {
			BranchCoverageTestNovelty otherBranchFitness = (BranchCoverageTestNovelty) other;
			return goal.compareTo(otherBranchFitness.goal);
		}
		return compareClassName(other);
	}

	/* (non-Javadoc)
	 * @see org.evosuite.testcase.TestFitnessFunction#getTargetMethod()
	 */
	@Override
	public String getTargetMethod() {
		return getMethod();
	}
	
	public String getClassName() {
		return goal.getClassName();
	}
	
	public String getMethod() {
		return goal.getMethodName();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		final int prime = 31;
		int result = 1;
		result = prime * result + ((goal == null) ? 0 : goal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BranchCoverageTestNovelty other = (BranchCoverageTestNovelty) obj;
		if (goal == null) {
			if (other.goal != null)
				return false;
		} else if (!goal.equals(other.goal))
			return false;
		return true;
	}

	

	@SuppressWarnings("finally")
	@Override
	public double getNovelty(TestChromosome individual, List<TestChromosome> population , List<TestChromosome> archive) {
		// TODO Auto-generated method stub
		try
		{
			Thread.sleep(50);
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			
		org.evosuite.testcase.TestCase indtestcase=individual.getTestCase();
		ExecutionResult result = runTest(indtestcase);

		Map<Integer, Integer> currentpredicateCount = new HashMap<Integer, Integer>();
		Map<Integer, Integer> restofthepopCount = new HashMap<Integer, Integer>();			
		currentpredicateCount.putAll(result.getTrace().getPredicateExecutionCount());	
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
		updateIndividual(this, individual, distance);
		return distance;
		}
	}

	
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
			
		org.evosuite.testcase.TestCase indtestcase=individual.getTests().get(0);//.getTestCase();
		ExecutionResult result = runTest(indtestcase);

		Map<Integer, Integer> currentpredicateCount = new HashMap<Integer, Integer>();
		Map<Integer, Integer> restofthepopCount = new HashMap<Integer, Integer>();			
		currentpredicateCount.putAll(result.getTrace().getPredicateExecutionCount());	
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
		//updateIndividual(this, individual, distance);
		return distance;
		}
	}

}
