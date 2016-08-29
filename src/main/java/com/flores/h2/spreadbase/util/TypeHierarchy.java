package com.flores.h2.spreadbase.util;

import java.util.HashMap;

/**
 * A convenient way of storing priority during 
 * adjustment
 * @author Jason
 */
public final class TypeHierarchy extends HashMap<Class<?>, Integer> {
	private static final long serialVersionUID = 6130019034991343452L; {
		super.put(String.class, 1);
		super.put(Double.class, 2);
		super.put(Integer.class, 3);
	}

	public Class<?> classByIndex(int index) {
		Class<?>c = null;
		for(Entry<Class<?>, Integer> e : entrySet())
			if(e.getValue() == index) c = e.getKey();
		return c;
	}
	
	public Integer put(Class<?>c, Integer i) {
		throw new RuntimeException("Put is not support on the hierarchy");
	}
}