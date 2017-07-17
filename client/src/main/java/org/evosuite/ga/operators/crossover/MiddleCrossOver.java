package org.evosuite.ga.operators.crossover;

import org.evosuite.ga.Chromosome;

import org.evosuite.ga.ConstructionFailedException;

public class MiddleCrossOver extends CrossOverFunction{
	
	@Override
	public void crossOver(Chromosome parent1, Chromosome parent2) throws ConstructionFailedException
	{
		// TO DO
		if(parent1.size() < 2 || parent2.size() < 2)
			return;
		
		int middle1 = (int) Math.round(parent1.size() / 2);
		int middle2 = (int) Math.round(parent2.size() / 2);
		
		
		Chromosome t1 = parent1.clone();
		Chromosome t2 = parent2.clone();
		
		parent1.crossOver(t2, middle1, middle2);
		parent2.crossOver(t1, middle2, middle1);
	}

}
