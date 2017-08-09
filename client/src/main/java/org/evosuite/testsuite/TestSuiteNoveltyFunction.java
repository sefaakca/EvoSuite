package org.evosuite.testsuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.coverage.FitnessFunctions;
import org.evosuite.coverage.NoveltyFunctions;
import org.evosuite.coverage.TestFitnessFactory;
import org.evosuite.coverage.TestNoveltyFactory;
import org.evosuite.ga.NoveltyFunction;
import org.evosuite.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestNoveltyFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.ExecutionTracer;
import org.evosuite.testcase.execution.TestCaseExecutor;
import org.evosuite.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TestSuiteNoveltyFunction extends 
	NoveltyFunction<AbstractTestSuiteChromosome<? extends ExecutableChromosome>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 517901114059924117L;

	protected static final Logger logger = LoggerFactory.getLogger(TestSuiteNoveltyFunction.class);
	
	List<Map<Integer, Integer>> predicateCountList = new ArrayList<Map<Integer, Integer>>();

	@Override
	public boolean isMaximizationFunctionNovelty() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Deprecated
	public ExecutionResult runTest(TestCase test) {
		ExecutionResult result = new ExecutionResult(test, null);

		try {
			result = TestCaseExecutor.getInstance().execute(test);
			MaxStatementsStoppingCondition.statementsExecuted(result.getExecutedStatements());
		} catch (Exception e) {
			logger.warn("TG: Exception caught: " + e.getMessage(), e);
			try {
				Thread.sleep(1000);
				result.setTrace(ExecutionTracer.getExecutionTracer().getTrace());
			} catch (Exception e1) {
				throw new Error(e1);
			}

		}

		// System.out.println("TG: Killed "+result.getNumKilled()+" out of "+mutants.size());
		return result;
	}
	
	protected List<ExecutionResult> runTestSuite(
	        AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite) {
		List<ExecutionResult> results = new ArrayList<ExecutionResult>();

		for (ExecutableChromosome chromosome : suite.getTestChromosomes()) {
			// Only execute test if it hasn't been changed
			if (chromosome.isChanged() || chromosome.getLastExecutionResult() == null) {
				ExecutionResult result = chromosome.executeForNoveltyFunction(this);

				if (result != null) {
					results.add(result);

					chromosome.setLastExecutionResult(result); // .clone();
					chromosome.setChanged(false);
				}
			} else {
				results.add(chromosome.getLastExecutionResult());
			}
		}
		suite.setChanged(false);
		
		return results;
	}


	//@SuppressWarnings("finally")
	public double getNovelty(TestSuiteChromosome individual, List<TestChromosome> population,
			List<TestChromosome> archive) {
		// TODO Auto-generated method stub	
			
			double noveltymetric = 0;
			int cov=0;
			List<ExecutionResult> result=runTestSuite(individual);
			Iterator<ExecutionResult>itexec= result.iterator();
			while(itexec.hasNext())
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
		
				Collection<Integer> curvalues = currentpredicateCount.values();
				
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
				
				cov+=currentpredicateCount.size();

			}
			TestNoveltyFactory factory = NoveltyFunctions.getNoveltyFactory(Properties.Criterion.BRANCH);
			individual.setCoverageNovelty(this, (double)cov/(double)factory.getCoverageGoals().size());
			updateIndividual(this, individual, noveltymetric);
			return noveltymetric;
				
		//}
		

	}

		
	
}
