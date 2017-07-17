package org.evosuite.coverage.branch;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.MethodNameMatcher;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.setup.DependencyAnalysis;
import org.evosuite.testsuite.AbstractNoveltyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BranchCoverageFactoryNovelty extends
AbstractNoveltyFactory<BranchCoverageTestNovelty>{

	private static final Logger logger = LoggerFactory.getLogger(BranchCoverageFactoryNovelty.class);
	@Override
	public List<BranchCoverageTestNovelty> getCoverageGoals() {
		// TODO Auto-generated method stub
		return computeCoverageGoals(true);
	}
	
	private List<BranchCoverageTestNovelty> computeCoverageGoals(boolean limitToCUT){
		long start = System.currentTimeMillis();
		List<BranchCoverageTestNovelty> goals = new ArrayList<BranchCoverageTestNovelty>();

		// logger.info("Getting branches");
		for (String className : BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).knownClasses()) {
			//when limitToCUT== true, if not the class under test of a inner/anonymous class, continue
			if(limitToCUT && !isCUT(className)) continue;
			//when limitToCUT==false, consider all classes, but excludes libraries ones according the INSTRUMENT_LIBRARIES property
			if(!limitToCUT && (!Properties.INSTRUMENT_LIBRARIES && !DependencyAnalysis.isTargetProject(className))) continue;
			final MethodNameMatcher matcher = new MethodNameMatcher();
			// Branchless methods
			for (String method : BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).getBranchlessMethods(className)) {
				if (matcher.fullyQualifiedMethodMatches(method)) {
					goals.add(createRootBranchTestNovelty(className, method));
				}
			}

			// Branches
			for (String methodName : BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).knownMethods(className)) {
				if (!matcher.methodMatches(methodName)) {
					logger.info("Method " + methodName + " does not match criteria. ");
					continue;
				}

				for (Branch b : BranchPool.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT()).retrieveBranchesInMethod(className,
						methodName)) {
                    if(!b.isInstrumented()) {
                        goals.add(createBranchCoverageTestNovelty(b, true));
                        goals.add(createBranchCoverageTestNovelty(b, false));
                    }
				}
			}
		}
		goalComputationTime = System.currentTimeMillis() - start;
		return goals;
	}
	
	public List<BranchCoverageTestNovelty> getCoverageGoalsForAllKnownClasses() {
		return computeCoverageGoals(false); 
	}
	
	public static BranchCoverageTestNovelty createBranchCoverageTestNovelty(
			ControlDependency cd) {
		return createBranchCoverageTestNovelty(cd.getBranch(),
				cd.getBranchExpressionValue());
	}
	public static BranchCoverageTestNovelty createBranchCoverageTestNovelty(
			Branch b, boolean branchExpressionValue) {

		return new BranchCoverageTestNovelty(new BranchCoverageGoal(b,
				branchExpressionValue, b.getClassName(), b.getMethodName()));
	}
	
	public static BranchCoverageTestNovelty createRootBranchTestNovelty(
			String className, String method) {

		return new BranchCoverageTestNovelty(new BranchCoverageGoal(className,
				method.substring(method.lastIndexOf(".") + 1)));
	}
	
	public static BranchCoverageTestNovelty createRootBranchTestNovelty(
			BytecodeInstruction instruction) {
		if (instruction == null)
			throw new IllegalArgumentException("null given");

		return createRootBranchTestNovelty(instruction.getClassName(),
				instruction.getMethodName());
	}

}
