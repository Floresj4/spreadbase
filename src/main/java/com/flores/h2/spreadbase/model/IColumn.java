package com.flores.h2.spreadbase.model;

import java.util.Map;

import com.flores.h2.spreadbase.model.impl.DataType;

/**
 * @author Jason Flores
 */
public interface IColumn extends IDescribable {
	public Map<Class<?>, DataType> getTypeMap();
	public void put(DataType t);
}