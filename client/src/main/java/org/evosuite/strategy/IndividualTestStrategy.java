/**
 * Copyright (C) 2010-2017 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.strategy;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.ShutdownTestWriter;
import org.evosuite.Properties.Criterion;
import org.evosuite.coverage.NoveltyFunctions;
import org.evosuite.coverage.TestFitnessFactory;
import org.evosuite.coverage.TestNoveltyFactory;
import org.evosuite.rmi.ClientServices;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.NoveltyFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.evosuite.ga.stoppingconditions.StoppingCondition;
import org.evosuite.statistics.RuntimeVariable;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.ExecutionTracer;
import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.TestNoveltyFunction;
import org.evosuite.testsuite.AbstractTestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteFitnessFunction;
import org.evosuite.testsuite.TestSuiteMinimizer;
import org.evosuite.testsuite.TestSuiteNoveltyFunction;
import org.evosuite.testsuite.factories.FixedSizeTestSuiteChromosomeFactory;
import org.evosuite.utils.ArrayUtil;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This strategy selects one coverage goal at a time
 * and generates a test that satisfies it.
 * 
 * The order of goals is randomized. Coincidental coverage is 
 * checked.
 * 
 * @author gordon
 *
 */
public class IndividualTestStrategy extends TestGenerationStrategy {

	private static final Logger logger = LoggerFactory.getLogger(IndividualTestStrategy.class);
	
	@Override
	public TestSuiteChromosome generateTests() {
		// Set up search algorithm
		LoggingUtils.getEvoLogger().info("* Setting up search algorithm for individual test generation");
		ExecutionTracer.enableTraceCalls();

		PropertiesTestGAFactory factory = new PropertiesTestGAFactory();
		
		List<TestSuiteFitnessFunction> fitnessFunctions = getFitnessFunctions();
		
		List<TestSuiteNoveltyFunction> noveltyFunctions = getNoveltyFunctions();

		long start_time = System.currentTimeMillis() / 1000;
		boolean flag =false;
		// Get list of goals
        List<TestFitnessFactory<? extends TestFitnessFunction>> goalFactories = getFitnessFactories();
        
        List<TestNoveltyFactory<? extends TestNoveltyFunction>> goalFactoriesNovelty = getNoveltyFactories();
		// long goalComputationStart = System.currentTimeMillis();
		List<TestFitnessFunction> goals = new ArrayList<TestFitnessFunction>();
		
		List<TestNoveltyFunction> goalsNovelty = new ArrayList<TestNoveltyFunction>();
		
		LoggingUtils.getEvoLogger().info("* Total number of test goals: ");
        for (TestFitnessFactory<? extends TestFitnessFunction> goalFactory : goalFactories) {
            goals.addAll(goalFactory.getCoverageGoals());
            LoggingUtils.getEvoLogger().info("  - " + goalFactory.getClass().getSimpleName().replace("CoverageFactory", "")
                    + " " + goalFactory.getCoverageGoals().size());
        }
        
        for (TestNoveltyFactory<? extends TestNoveltyFunction> goalFactoryNovelty : goalFactoriesNovelty) {
            goalsNovelty.addAll(goalFactoryNovelty.getCoverageGoals());
           
        }

		if(!canGenerateTestsForSUT()) {
			LoggingUtils.getEvoLogger().info("* Found no testable methods in the target class "
					+ Properties.TARGET_CLASS);
			ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals, goals.size());

			return new TestSuiteChromosome();
		}

		// Need to shuffle goals because the order may make a difference
		if (Properties.SHUFFLE_GOALS) {
			// LoggingUtils.getEvoLogger().info("* Shuffling goals");
			Randomness.shuffle(goals);
			Randomness.shuffle(goalsNovelty);
		}
		ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals,
		                                                                 goals.size());

		LoggingUtils.getEvoLogger().info("* Total number of test goals: " + goals.size());

		// Bootstrap with random testing to cover easy goals
		//statistics.searchStarted(suiteGA);

		StoppingCondition stoppingCondition = getStoppingCondition();
		TestSuiteChromosome suite = (TestSuiteChromosome) bootstrapRandomSuite(fitnessFunctions.get(0), goalFactories.get(0)); // FIXME: just one fitness and one factory?!
		TestSuiteChromosome suiteNOvelty = (TestSuiteChromosome) bootstrapRandomSuite(noveltyFunctions.get(0), goalFactories.get(0));
	
		Set<Integer> covered = new HashSet<Integer>();
		int covered_goals = 0;
		int num = 0;

		for (TestFitnessFunction fitness_function : goals) {
			if (fitness_function.isCoveredBy(suite)) {
				covered.add(num);
				covered_goals++;
			}
			num++;
		}
		num=0;
		for (TestNoveltyFunction novelty_function : goalsNovelty) {
			if (novelty_function.isCoveredBy(suiteNOvelty)) {
				covered.add(num);
				covered_goals++;
			}
			num++;
		}
		
		if (covered_goals > 0)
			LoggingUtils.getEvoLogger().info("* Random bootstrapping covered "
			                                         + covered_goals + " test goals");

		int total_goals = goals.size();
		if (covered_goals == total_goals)
			zeroFitness.setFinished();

		int current_budget = 0;

		long total_budget = Properties.SEARCH_BUDGET;
		LoggingUtils.getEvoLogger().info("* Budget: "
		                                         + NumberFormat.getIntegerInstance().format(total_budget));

		while (current_budget < total_budget && covered_goals < total_goals
		        && !globalTime.isFinished() && !ShutdownTestWriter.isInterrupted()) {
			long budget = (total_budget - current_budget) / (total_goals - covered_goals);
			logger.info("Budget: " + budget + "/" + (total_budget - current_budget));
			logger.info("Statements: " + current_budget + "/" + total_budget);
			logger.info("Goals covered: " + covered_goals + "/" + total_goals);
			stoppingCondition.setLimit(budget);

			num = 0;
			// int num_statements = 0;
			// //MaxStatementsStoppingCondition.getNumExecutedStatements();
			for (TestFitnessFunction fitnessFunction : goals) {

				if (covered.contains(num)) {
					num++;
					continue;
				}
				
				GeneticAlgorithm<TestChromosome> ga = factory.getSearchAlgorithm();

				// ga.resetStoppingConditions();
				// ga.clearPopulation();
				//ga.setChromosomeFactory(getChromosomeFactory(fitnessFunction));

				if (Properties.PRINT_CURRENT_GOALS)
					LoggingUtils.getEvoLogger().info("* Searching for goal " + num + ": "
					                                         + fitnessFunction.toString());
				logger.info("Goal " + num + "/" + (total_goals - covered_goals) + ": "
				        + fitnessFunction);

				if (ShutdownTestWriter.isInterrupted()) {
					num++;
					continue;
				}
				if (globalTime.isFinished()) {
					LoggingUtils.getEvoLogger().info("Skipping goal because time is up");
					num++;
					continue;
				}

				// FitnessFunction fitness_function = new
				ga.addFitnessFunction(fitnessFunction);
				if(ga.getClass().getName().contains("Novelty"))
				{
					flag=true;
					for (TestNoveltyFunction nf : goalsNovelty) {
						if(!ga.getNoveltyFunctions().contains(nf))
						{
							ga.addNoveltyFunction(nf);
							
						ga.generateSolution();
						if (ga.getBestIndividualNovelty().getNovelty()>0.0 ){//> ) {
							if (Properties.PRINT_COVERED_GOALS)
								LoggingUtils.getEvoLogger().info("* Covered!"); // : " +
							// fitness_function.toString());
							logger.info("Found solution, adding to test suite at "
						        + MaxStatementsStoppingCondition.getNumExecutedStatements());
							TestChromosome best = (TestChromosome) ga.getBestIndividualNovelty();
							//LoggingUtils.getEvoLogger().info("* distance : "+best.getTestCase().getCoveredGoals().size());
							
							if(!suiteNOvelty.getTestChromosomes().contains(best))
							{
								best.getTestCase().addCoveredGoal(nf);
								suiteNOvelty.addTest(best);
							// Calculate and keep track of overall fitness
							for (TestSuiteNoveltyFunction novelty_function : noveltyFunctions){
								novelty_function.getNovelty(suiteNOvelty, ga.getPopulation(), ga.getArchiveNovelty()); //getFitness(suite);
							}
							}
							covered_goals++;
							covered.add(num);

							// experiment:
							if (Properties.SKIP_COVERED) {
								Set<Integer> additional_covered_nums = getAdditionallyCoveredGoals(goals,
							                                                                   covered,
							                                                                   best);
								// LoggingUtils.getEvoLogger().info("Additionally covered: "+additional_covered_nums.size());
								for (Integer covered_num : additional_covered_nums) {
									covered_goals++;
									covered.add(covered_num);
							}//end of for
						}//end of if SKIP_COVERED

					}//end of if ga.getBestIndividual().getFitness() == 0.0
						
							else {
								logger.info("Found no solution for " + fitnessFunction + " at "
								        + MaxStatementsStoppingCondition.getNumExecutedStatements());
							}//end of else
						}//if ga not contains
					}//end of for
					
				}//end of if Novelty
				else
				{
					flag=false;
					// Perform search
					logger.info("Starting evolution for goal " + fitnessFunction);
					ga.generateSolution();
				
					if (ga.getBestIndividual().getFitness() == 0.0) {
						if (Properties.PRINT_COVERED_GOALS)
							LoggingUtils.getEvoLogger().info("* Covered!"); // : " +
						// fitness_function.toString());
						logger.info("Found solution, adding to test suite at "
					        + MaxStatementsStoppingCondition.getNumExecutedStatements());
						TestChromosome best = (TestChromosome) ga.getBestIndividual();
						best.getTestCase().addCoveredGoal(fitnessFunction);
						suite.addTest(best);
						// Calculate and keep track of overall fitness
						for (TestSuiteFitnessFunction fitness_function : fitnessFunctions)
							fitness_function.getFitness(suite);

						covered_goals++;
						covered.add(num);

						// experiment:
						if (Properties.SKIP_COVERED) {
							Set<Integer> additional_covered_nums = getAdditionallyCoveredGoals(goals,
						                                                                   covered,
						                                                                   best);
							// LoggingUtils.getEvoLogger().info("Additionally covered: "+additional_covered_nums.size());
							for (Integer covered_num : additional_covered_nums) {
								covered_goals++;
								covered.add(covered_num);
						}//end of for
					}//end of if SKIP_COVERED

				}//end of if ga.getBestIndividual().getFitness() == 0.0
					else {
						logger.info("Found no solution for " + fitnessFunction + " at "
					        + MaxStatementsStoppingCondition.getNumExecutedStatements());
					}//end of else
				}//end of else
			}//end of for
			//statistics.iteration(suiteGA);
			if (Properties.REUSE_BUDGET)
				current_budget += stoppingCondition.getCurrentValue();
			else
				current_budget += budget + 1;

			// print console progress bar
			if (Properties.SHOW_PROGRESS
					&& !(Properties.PRINT_COVERED_GOALS || Properties.PRINT_CURRENT_GOALS)) {
				double percent = current_budget;
				percent = percent / total_budget * 100;

				double coverage = covered_goals;
				coverage = coverage / total_goals * 100;

				// ConsoleProgressBar.printProgressBar((int) percent, (int)
				// coverage);
				}

			if (current_budget > total_budget)
				break;
			num++;

			// break;
			}//end of while
		
		if (Properties.SHOW_PROGRESS)
			LoggingUtils.getEvoLogger().info("");

		// for testing purposes
		if (globalTime.isFinished())
			LoggingUtils.getEvoLogger().info("! Timeout reached");
		if (current_budget >= total_budget)
			LoggingUtils.getEvoLogger().info("! Budget exceeded");
		else
			LoggingUtils.getEvoLogger().info("* Remaining budget: "
			                                         + (total_budget - current_budget));

//		stoppingCondition.setLimit(Properties.SEARCH_BUDGET);
//		stoppingCondition.forceCurrentValue(current_budget);
//		suiteGA.setStoppingCondition(stopping_condition);
//		suiteGA.addStoppingCondition(global_time);
		// printBudget(suiteGA);

		int c = 0;
		int uncovered_goals = total_goals - covered_goals;
		if (uncovered_goals < 10)
			for (TestFitnessFunction goal : goals) {
				if (!covered.contains(c)) {
					LoggingUtils.getEvoLogger().info("! Unable to cover goal " + c + " "
					                                         + goal.toString());
				}
				c++;
			}
		else
			LoggingUtils.getEvoLogger().info("! #Goals that were not covered: "
			                                         + uncovered_goals);

		//statistics.searchFinished(suiteGA);
		long end_time = System.currentTimeMillis() / 1000;
		
		if(flag)
		{
			//suite=suiteNOvelty;
			LoggingUtils.getEvoLogger().info("* Search finished after "
                    + (end_time - start_time)
                    + "s, "
                    + current_budget
                    + " statements, best individual has novelty metric "
                    + suiteNOvelty.getNovelty());
		}
		else
		{
			LoggingUtils.getEvoLogger().info("* Search finished after "
                    + (end_time - start_time)
                    + "s, "
                    + current_budget
                    + " statements, best individual has fitness "
                    + suite.getFitness());
		}
			
		
		
        // Search is finished, send statistics
        sendExecutionStatistics();

		//LoggingUtils.getEvoLogger().info("* Covered " + covered_goals + "/"
		  //                                       + goals.size() + " goals");
		logger.info("Resulting test suite: " + suite.size() + " tests, length "
		        + suite.totalLengthOfTestCases());

		if(flag)
			return suiteNOvelty;
		else
			return suite;
	}

	private Set<Integer> getAdditionallyCoveredGoals(
	        List<? extends TestFitnessFunction> goals, Set<Integer> covered,
	        TestChromosome best) {

		Set<Integer> r = new HashSet<Integer>();
		ExecutionResult result = best.getLastExecutionResult();
		assert (result != null);
		// if (result == null) {
		// result = TestCaseExecutor.getInstance().execute(best.test);
		// }
		int num = -1;
		for (TestFitnessFunction goal : goals) {
			num++;
			if (covered.contains(num))
				continue;
			if (goal.isCovered(best, result)) {
				r.add(num);
				if (Properties.PRINT_COVERED_GOALS)
					LoggingUtils.getEvoLogger().info("* Additionally covered: "
					                                         + goal.toString());
			}
		}
		return r;
	}
	
	private TestSuiteChromosome bootstrapRandomSuite(FitnessFunction<?> fitness,
	        TestFitnessFactory<?> goals) {

		if (ArrayUtil.contains(Properties.CRITERION, Criterion.DEFUSE)
	            || ArrayUtil.contains(Properties.CRITERION, Criterion.ALLDEFS)) {
			LoggingUtils.getEvoLogger().info("* Disabled random bootstraping for dataflow criterion");
			Properties.RANDOM_TESTS = 0;
		}

		if (Properties.RANDOM_TESTS > 0) {
			LoggingUtils.getEvoLogger().info("* Bootstrapping initial random test suite");
		} // else
		  // LoggingUtils.getEvoLogger().info("* Bootstrapping initial random test suite disabled!");

		FixedSizeTestSuiteChromosomeFactory factory = new FixedSizeTestSuiteChromosomeFactory(Properties.RANDOM_TESTS);

		TestSuiteChromosome suite = factory.getChromosome();
		if (Properties.RANDOM_TESTS > 0) {
			TestSuiteMinimizer minimizer = new TestSuiteMinimizer(goals);
			minimizer.minimize(suite, true);
			LoggingUtils.getEvoLogger().info("* Initial test suite contains "
			                                         + suite.size() + " tests");
		}

		return suite;
	}
	
	private TestSuiteChromosome bootstrapRandomSuite(NoveltyFunction<?> novelty,
			TestFitnessFactory<?> goals) {

		if (ArrayUtil.contains(Properties.CRITERION, Criterion.DEFUSE)
	            || ArrayUtil.contains(Properties.CRITERION, Criterion.ALLDEFS)) {
			LoggingUtils.getEvoLogger().info("* Disabled random bootstraping for dataflow criterion");
			Properties.RANDOM_TESTS = 0;
		}

		if (Properties.RANDOM_TESTS > 0) {
			LoggingUtils.getEvoLogger().info("* Bootstrapping initial random test suite");
		} // else
		  // LoggingUtils.getEvoLogger().info("* Bootstrapping initial random test suite disabled!");

		FixedSizeTestSuiteChromosomeFactory factory = new FixedSizeTestSuiteChromosomeFactory(Properties.RANDOM_TESTS);

		TestSuiteChromosome suite = factory.getChromosome();
		if (Properties.RANDOM_TESTS > 0) {
			TestSuiteMinimizer minimizer = new TestSuiteMinimizer(goals);
			minimizer.minimize(suite, true);
			LoggingUtils.getEvoLogger().info("* Initial test suite contains "
			                                         + suite.size() + " tests");
		}

		return suite;
	}

}