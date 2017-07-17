package org.evosuite.testsuite;

import org.evosuite.Properties;
import org.evosuite.coverage.TestNoveltyFactory;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestNoveltyFunction;
import org.evosuite.testcase.execution.ExecutionTracer;

public abstract class AbstractNoveltyFactory <T extends TestNoveltyFunction> implements
TestNoveltyFactory<T> {
	
	public static long goalComputationTime = 0l;
	
	protected boolean isCUT(String className) {
		if (!Properties.TARGET_CLASS.equals("")
				&& !(className.equals(Properties.TARGET_CLASS) || className
						.startsWith(Properties.TARGET_CLASS + "$"))) {
			return false;
		}
		return true;
	}
	
	@Override
	public double getNovelty(TestSuiteChromosome suite) {

		ExecutionTracer.enableTraceCalls();

		int coveredGoals = 0;
		for (T goal : getCoverageGoals()) {
			for (TestChromosome test : suite.getTestChromosomes()) {
				if (goal.isCovered(test)) {
					coveredGoals++;
					break;
				}
			}
		}

		ExecutionTracer.disableTraceCalls();

		return getCoverageGoals().size() - coveredGoals;
	}

}
