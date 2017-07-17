package org.evosuite.coverage;

import java.util.List;

import org.evosuite.testcase.TestNoveltyFunction;
import org.evosuite.testsuite.TestSuiteChromosome;

public interface NoveltyMetricFactory<T extends TestNoveltyFunction> {

	public List<T> getCoverageGoals();
	
	public double getNovelty(TestSuiteChromosome suite);
	
}
