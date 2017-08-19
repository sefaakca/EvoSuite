package org.evosuite.ga;

import java.io.Serializable;
import java.util.List;

import org.evosuite.testcase.TestChromosome;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author sefa
 *
 * @param <T>
 */
public abstract class NoveltyFunction<T extends Chromosome> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -211404680068258869L;
	/**
	 * 
	 */

	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(NoveltyFunction.class);

	protected void updateIndividual(NoveltyFunction<?> nf, T individual, double novel) {
		individual.setNovelty(nf, novel);
	}
	
	protected void updateIndividual(NoveltyFunction<?> nf, T individual, double novel, int toCoverTargets) {
		individual.setNovelty(nf, novel);
	}
	
	public T getBestStoredIndividual(){
		return null;
	}
	
	public abstract double getNovelty(T individual);
	
	public abstract double getNovelty(T individual, List<T> population , List<T> archive);
	
	
	
	public static double normalize(double value) throws IllegalArgumentException {
		if (value < 0d) {
			throw new IllegalArgumentException("Values to normalize cannot be negative");
		}
		if (Double.isInfinite(value)) {
			return 1.0;
		}
		return value / (1.0 + value);
		
	}

	public boolean updateCoveredGoals(){
		return false;
	}


	
}
