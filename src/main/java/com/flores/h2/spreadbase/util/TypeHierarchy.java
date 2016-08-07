package com.flores.h2.spreadbase.util;

import java.util.HashMap;

import com.flores.h2.spreadbase.model.IColumn;

/**
 * A convenient way of storing priority during 
 * adjustment
 * @author Jason
 */
public class TypeHierarchy extends HashMap<Class<?>, Integer> {
	private static final long serialVersionUID = 6130019034991343452L; {
		put(String.class, 1);
		put(Double.class, 2);
		put(Integer.class, 3);
	}

	/**
	 * Compare to columns to determine the preferred type
	 * @param col1 instance
	 * @param col2 instance
	 * @return a class based on the hierarchy 
	 */
	public Class<?> compare(IColumn col1, IColumn col2) {
		return get(col1.getType()) < get(col2.getType()) ? col1.getType()
				: col2.getType();
	}
	
	public boolean createsChange(IColumn col1, IColumn col2) {
		return get(col1.getType()) < get(col2.getType()) ? true
				: false;
	}
}
