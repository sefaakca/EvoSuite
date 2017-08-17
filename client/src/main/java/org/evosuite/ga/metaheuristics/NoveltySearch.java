package org.evosuite.ga.metaheuristics;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.TimeController;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.ga.NoveltyFunction;
import org.evosuite.ga.NoveltyReplacementFunction;
import org.evosuite.ga.ReplacementFunction;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoveltySearch<T extends Chromosome> extends GeneticAlgorithm<T>{

	private static final long serialVersionUID = -662894538523753916L;

	private final Logger logger = LoggerFactory.getLogger(NoveltySearch.class);


	protected ReplacementFunction replacementFunction;
	
	List<T> archive = new ArrayList<T>();
	public NoveltySearch(ChromosomeFactory<T> factory) {
		super(factory);
		// TODO Auto-generated constructor stub
		
		setReplacementFunction(new NoveltyReplacementFunction());
		
	}
	
	protected boolean keepOffspring(Chromosome parent1, Chromosome parent2, Chromosome offspring1,
			Chromosome offspring2) {
		return replacementFunction.keepOffspring(parent1, parent2, offspring1, offspring2);
	}
	
	private T newRandomIndividual() {
		T randomChromosome = chromosomeFactory.getChromosome();
		for (NoveltyFunction<?> noveltyFunction : this.noveltyFunctions) {
			randomChromosome.addNovelty(noveltyFunction);
		}
		return randomChromosome;
	}
	
	private double getBestNovelty() {
		T bestIndividual = getBestIndividualNovelty();
		for (NoveltyFunction<T> nf : noveltyFunctions) {
			nf.getNovelty(bestIndividual,population,archive);
		}
		return bestIndividual.getNovelty();
		
	}

	@Override
	protected void evolve() {
		// TODO Auto-generated method stub
		List<T> newGeneration = new ArrayList<T>();
		// Elitism
		logger.debug("Elitism");
		newGeneration.addAll(elitisimForNovelty());
		archive.addAll(elitisimForNovelty());
		setArchive(archive);
		while (!isNextPopulationFull(newGeneration)) // 
		{
			logger.debug("Generating offspring");
			
			T parent1 = selectionFunction.select(population);
			T parent2 = selectionFunction.select(population);
			
			T offspring1 = (T) parent1.clone();
			T offspring2 = (T) parent2.clone();

			try {
				// Crossover
				if (Randomness.nextDouble() <= Properties.CROSSOVER_RATE) {
					crossoverFunction.crossOver(offspring1, offspring2);
				}
				
				// Mutation
				notifyMutation(offspring1);
				offspring1.mutate();
				notifyMutation(offspring2);
				offspring2.mutate();
				
				if(offspring1.isChanged()) {
					offspring1.updateAge(currentIteration);
				}
				if(offspring2.isChanged()) {
					offspring2.updateAge(currentIteration);
				}
				

			} catch (ConstructionFailedException e) {
				logger.info("CrossOver failed");
				continue;
			}
			
			if (!isTooLong(offspring1))
				newGeneration.add(offspring1);
			else
				newGeneration.add(parent1);

			if (!isTooLong(offspring2))
				newGeneration.add(offspring2);
			else
				newGeneration.add(parent2);

			
			}

			population = newGeneration;
			// archive
			updateNoveltyFunctionsAndValues(archive);
			currentIteration++;

	}

	
	/* (non-Javadoc)
	 * @see org.evosuite.ga.metaheuristics.GeneticAlgorithm#initializePopulation()
	 * 
	 * Create the first random population and initialize the them.
	 */
	@Override
	public void initializePopulation() {
		// TODO Auto-generated method stub
		notifySearchStarted();
		currentIteration = 0;
		
		generateInitialPopulationForNoveltySearch(Properties.POPULATION);
		logger.debug("Calculating novelty of initial population");
		
		calculateNoveltyAndSortPopulation(archive);
		
		this.notifyIteration();
		
		
	}
	
	private static final double DELTA = 0.000000001;

	@Override
	public void generateSolution() {
		// TODO Auto-generated method stub
		if (Properties.ENABLE_SECONDARY_OBJECTIVE_AFTER > 0 || Properties.ENABLE_SECONDARY_OBJECTIVE_STARVATION) {
			disableFirstSecondaryCriterion();
		}
		
		if (population.isEmpty()) {
			initializePopulation();
			assert!population.isEmpty() : "Could not create any test";
		}
		logger.debug("Starting evolution");
		setArchive(archive);
		int starvationCounter = 0;
		double bestNoveltyMetric = 0;//Double.MAX_VALUE;
		double lastbestNoveltyMetric =0;// Double.MAX_VALUE;
		if (getNoveltyFunction().isMaximizationFunctionNovelty()) {
			bestNoveltyMetric = Double.MAX_VALUE;
			lastbestNoveltyMetric = Double.MAX_VALUE;
		}

		while (!isFinished()) {//!isFinished()
			
			logger.info("Population size before: " + population.size());
			// related to Properties.ENABLE_SECONDARY_OBJECTIVE_AFTER;
			// check the budget progress and activate a secondary criterion
			// according to the property value.

				evolve();
				calculateNoveltyAndSortPopulation(archive);
				applyLocalSearchNovelty();
				
				
			
			double newNoveltyMetric = getBestIndividualNovelty().getNovelty();

			if (getNoveltyFunction().isMaximizationFunctionNovelty())
				assert(newNoveltyMetric >= (bestNoveltyMetric - DELTA)) : "best novelty metric was: " + bestNoveltyMetric
						+ ", now best novelty metric is " + newNoveltyMetric;
			else
				assert(newNoveltyMetric <= (bestNoveltyMetric + DELTA)) : "best novelty metric was: " + bestNoveltyMetric
						+ ", now best novelty metric is " + newNoveltyMetric;
				bestNoveltyMetric = newNoveltyMetric;

			if (Double.compare(bestNoveltyMetric, lastbestNoveltyMetric) == 0) {
				starvationCounter++;
			} else {
				logger.info("reset starvationCounter after " + starvationCounter + " iterations");
				starvationCounter = 0;
				lastbestNoveltyMetric = bestNoveltyMetric;

			}

			updateSecondaryCriterion(starvationCounter);
			
			this.notifyIteration();
			

		}
		// archive
		
		updateBestIndividualFromArchiveNovelty();
		//updateBestIndividualFromArchive();
		notifySearchFinished();
		
	}
	
	
	public void setReplacementFunction(ReplacementFunction replacement_function) {
		this.replacementFunction = replacement_function;
	}
	public ReplacementFunction getReplacementFunction() {
		return replacementFunction;
	}
	

}
