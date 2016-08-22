package com.flores.h2.spreadbase.util;

import java.util.HashMap;
import java.util.Map;

import com.flores.h2.spreadbase.exception.UnsupportedTypeException;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.DataType;

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
	
	public DataType getPriorityDataType(IColumn column) throws UnsupportedTypeException {
		return getPriorityDataType(column.getTypeMap());
	}

	public DataType getPriorityDataType(final Map<Class<?>, DataType> encounteredTypes) throws UnsupportedTypeException {
		if(encounteredTypes == null)
			throw new NullPointerException("map<class,datatype> argument");
		
		DataType priority = null;
		for(DataType d : encounteredTypes.values()) {
			if(priority == null)
				priority = d;
			else {
				priority = get(d.getClass()) < get(priority.getClass())
					? d : priority;
			}
		}
		
		if(priority == null)
			throw new UnsupportedTypeException(
					"A type has been definied that does not exist in the current hierarchy");

		return priority;
	}
}
