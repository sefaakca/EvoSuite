package org.evosuite.testsuite;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.ga.NoveltyFunction;
import org.evosuite.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.ExecutionTracer;
import org.evosuite.testcase.execution.TestCaseExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TestSuiteNoveltyFunction extends 
	NoveltyFunction<AbstractTestSuiteChromosome<? extends ExecutableChromosome>> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 517901114059924117L;

	protected static final Logger logger = LoggerFactory.getLogger(TestSuiteNoveltyFunction.class);
	

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

}
