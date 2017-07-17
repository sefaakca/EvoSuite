package org.evosuite.coverage.branch;

import java.util.Arrays;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.SystemTestBase;
import org.evosuite.Properties.Criterion;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.strategy.TestGenerationStrategy;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.mutation.SimpleMutationExample2;

public class BranchSystemTestNovelty extends SystemTestBase{
	
	private Properties.Criterion[] oldCriteria = Arrays.copyOf(Properties.CRITERION, Properties.CRITERION.length); 
	private Properties.StoppingCondition oldStoppingCondition = Properties.STOPPING_CONDITION; 
	private double oldPrimitivePool = Properties.PRIMITIVE_POOL;
	
	@Before
	public void beforeTest() {
		oldCriteria = Arrays.copyOf(Properties.CRITERION, Properties.CRITERION.length);
		oldStoppingCondition = Properties.STOPPING_CONDITION;
		oldPrimitivePool = Properties.PRIMITIVE_POOL;
		//Properties.MINIMIZE = false;
	}
	
	@After
	public void restoreProperties() {
		Properties.CRITERION = oldCriteria;
		Properties.STOPPING_CONDITION = oldStoppingCondition;
		Properties.PRIMITIVE_POOL = oldPrimitivePool;
	}
	/*
	@Test
	public void testOnlyBranchWithArchive() {
		EvoSuite evosuite = new EvoSuite();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = true;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.ONLYBRANCH };

		String targetClass = SimpleMutationExample2.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = evosuite.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividualNovelty();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoalsNovelty());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getNoveltyFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(2, goals );
		//Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverageNovelty(), 0.001);
	}
*/
	@Test
	public void testOnlyBranchWithoutArchive() {
		EvoSuite evosuite = new EvoSuite();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = false;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.ONLYBRANCH };

		String targetClass = SimpleMutationExample2.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = evosuite.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividualNovelty();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoalsNovelty());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getNoveltyFactories().get(0).getCoverageGoals().size();

		
		Assert.assertEquals(3, goals );
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverageNovelty(), 0.001);
	}
	
	@Test
	public void testBranchWithArchive() {
		EvoSuite evosuite = new EvoSuite();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = true;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.BRANCH };

		String targetClass = SimpleMutationExample2.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = evosuite.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividualNovelty();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoalsNovelty());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getNoveltyFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(3, goals );
		//Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverageNovelty(), 0.001);
	}

	@Test
	public void testBranchWithoutArchive() {
		EvoSuite evosuite = new EvoSuite();
		boolean archive = Properties.TEST_ARCHIVE;
		Properties.TEST_ARCHIVE = false;
        Properties.CRITERION = new Properties.Criterion[] { Criterion.BRANCH };

		String targetClass = SimpleMutationExample2.class.getCanonicalName();
		
		String[] command = new String[] { "-generateSuite", "-class", targetClass };
		Object result = evosuite.parseCommandLine(command);
		GeneticAlgorithm<?> ga = getGAFromResult(result);
		TestSuiteChromosome best = (TestSuiteChromosome) ga.getBestIndividualNovelty();
		Properties.TEST_ARCHIVE = archive;
		
		System.out.println("CoveredGoals:\n" + best.getCoveredGoalsNovelty());
		System.out.println("EvolvedTestSuite:\n" + best);
		int goals = TestGenerationStrategy.getNoveltyFactories().get(0).getCoverageGoals().size(); // assuming single fitness function
		Assert.assertEquals(3, goals);
		Assert.assertEquals("Non-optimal coverage: ", 1d, best.getCoverageNovelty(), 0.001);
	}

}
