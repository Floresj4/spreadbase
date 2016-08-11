package com.flores.h2.spreadbase.model;

import com.flores.h2.spreadbase.util.TypeHierarchy;

/**
 * Couldn't think of a better name.  Anything implementing
 * a rank evaluator should provide access to the TypeHierarchy
 * or it's derivatives. <em>An additional abstraction may be required</em>.
 * 
 * @author Jason
 */
public interface IRankEvaluator {
	public TypeHierarchy getTypeHierarchy();
}
