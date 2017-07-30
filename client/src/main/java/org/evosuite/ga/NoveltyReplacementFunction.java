package org.evosuite.ga;

public class NoveltyReplacementFunction extends ReplacementFunction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3325510415203217872L;

	/**
	 * <p>Constructor for FitnessReplacementFunction.</p>
	 *
	 * @param maximize a boolean.
	 */
	public NoveltyReplacementFunction(boolean maximize) {
		super(maximize);
	}
	
	/**
	 * <p>Constructor for FitnessReplacementFunction.</p>
	 */
	public NoveltyReplacementFunction(){
		this(false);
	}
	
	/**
	 * <p>
	 * isBetter
	 * </p>
	 * 
	 * @param chromosome1
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @param chromosome2
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @return a boolean.
	 */
	protected boolean isBetter(Chromosome chromosome1, Chromosome chromosome2) {
		if (maximize) {
			return chromosome1.compareToNovelty(chromosome2) > 0;
		} else {
			return chromosome1.compareToNovelty(chromosome2) > 0;
		}
	}

	/**
	 * <p>
	 * isBetterOrEqual
	 * </p>
	 * 
	 * @param chromosome1
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @param chromosome2
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @return a boolean.
	 */
	protected boolean isBetterOrEqual(Chromosome chromosome1, Chromosome chromosome2) {
		if (maximize) {
			return chromosome1.compareToNovelty(chromosome2) >= 0;
		} else {
			return chromosome1.compareToNovelty(chromosome2) <= 0;
		}
	}

	/**
	 * <p>
	 * getBest
	 * </p>
	 * 
	 * @param chromosome1
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @param chromosome2
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @return a {@link org.evosuite.ga.Chromosome} object.
	 */
	protected Chromosome getBest(Chromosome chromosome1, Chromosome chromosome2) {
		if (isBetter(chromosome1, chromosome2))
			return chromosome1;
		else
			return chromosome2;
	}

	/**
	 * Decide whether to keep the offspring or the parents
	 * 
	 * @param parent1
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @param parent2
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @param offspring1
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @param offspring2
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @return a boolean.
	 */
	public boolean keepOffspring(Chromosome parent1, Chromosome parent2,
	        Chromosome offspring1, Chromosome offspring2) {
		if (maximize) {
			return compareBestOffspringToBestParent(parent1, parent2, offspring1,
			                                        offspring2) >= 0;
		} else {
			return compareBestOffspringToBestParent(parent1, parent2, offspring1,
			                                        offspring2) >= 0;
		}
	}

	/**
	 * Check how the best offspring compares with best parent
	 * 
	 * @param parent1
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @param parent2
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @param offspring1
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @param offspring2
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @return a int.
	 */
	protected int compareBestOffspringToBestParent(Chromosome parent1,
	        Chromosome parent2, Chromosome offspring1, Chromosome offspring2) {

		Chromosome bestOffspring = getBest(offspring1, offspring2);
		Chromosome bestParent = getBest(parent1, parent2);

		return bestOffspring.compareToNovelty(bestParent);
	}

	/**
	 * Decide which of two offspring to keep
	 * 
	 * @param parent
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @param offspring
	 *            a {@link org.evosuite.ga.Chromosome} object.
	 * @deprecated should not be used, as it does not handle
	 *             Properties.CHECK_PARENTS_LENGTH
	 * @return a boolean.
	 */
	@Deprecated
	public boolean keepOffspring(Chromosome parent, Chromosome offspring) {
		return isBetterOrEqual(offspring, parent);
	}

}
