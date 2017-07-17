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

}
