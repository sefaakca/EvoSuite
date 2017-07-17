package org.evosuite.coverage.methodpair;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.setup.TestUsageChecker;
import org.evosuite.testsuite.AbstractFitnessFactory;

import ch.qos.logback.core.subst.Token.Type;

public class MethodPairFactory extends AbstractFitnessFactory<MethodPairTestFitness>{

	@Override
	public List<MethodPairTestFitness> getCoverageGoals() {
		
		List<MethodPairTestFitness> goals = new ArrayList<>();
		
		String className = Properties.TARGET_CLASS;
		Class<?> clazz = Properties.getTargetClassAndDontInitialise();
		java.util.Set<String> constructors = getUsableConstructor(clazz);
		Set<String> methods =getUsableMethods(clazz);
		
		for(String constructor: constructors)
			for(String method : methods)
				goals.add(new MethodPairTestFitness(className, constructor, method));
		for(String method1 :methods)
			for(String method2 : methods)
				goals.add(new MethodPairTestFitness(className, method1, method2));
		
		return goals;
	}
	
	protected Set<String> getUsableConstructor(Class<?> clazz){
		
		Set<String> constructors = new LinkedHashSet<>();
		Constructor<?>[] allConstructors = clazz.getDeclaredConstructors();
		
		for(Constructor<?> c : allConstructors)
		{
			if(TestUsageChecker.canUse(c))
			{
				String methodName = "<init>" + org.objectweb.asm.Type.getConstructorDescriptor(c);
				constructors.add(methodName);
			}
		}
		return constructors;
	}
	
	protected Set<String> getUsableMethods(Class<?> clazz)
	{
		Set<String> methods = new LinkedHashSet<>();
		Method[] allMethods = clazz.getDeclaredMethods();
		
		for(Method m : allMethods){
			if(TestUsageChecker.canUse(m)){
				String methodName = m.getName() + org.objectweb.asm.Type.getMethodDescriptor(m);
			}
		}
		return methods;
	}
	
	
	

}
