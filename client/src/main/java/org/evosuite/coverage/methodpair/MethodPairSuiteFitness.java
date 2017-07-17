package org.evosuite.coverage.methodpair;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testsuite.AbstractTestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteFitnessFunction;
import org.hsqldb.lib.HashSet;

public class MethodPairSuiteFitness extends TestSuiteFitnessFunction{
	
	private final Set<MethodPairTestFitness> allMethodPairs = new java.util.HashSet<>();
	@Override
	public double getFitness(AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite)
	{
		double fitness = 0.0;
		
		List<ExecutionResult> results = runTestSuite(suite);
		Set<MethodPairTestFitness> coveredMethodPairs = new java.util.HashSet<>();
		
		for(MethodPairTestFitness goal : allMethodPairs)
		{
			for(ExecutionResult result: results)
			{
				if(goal.isCovered(result)){
					coveredMethodPairs.add(goal);
					break;
				}
					
			}
		}
		
		fitness =allMethodPairs.size() - coveredMethodPairs.size();
		
		updateIndividual(this, suite, fitness);
		
		suite.setNumOfCoveredGoals(this, coveredMethodPairs.size());
		
		if(!allMethodPairs.isEmpty())
			suite.setCoverage(this, (double)coveredMethodPairs.size() / (double) allMethodPairs.size());
		else
			suite.setCoverage(this, 1.0);
		
		return fitness;
	}
	public MethodPairSuiteFitness(){
		allMethodPairs.addAll(new MethodPairFactory().getCoverageGoals());
	}

}
