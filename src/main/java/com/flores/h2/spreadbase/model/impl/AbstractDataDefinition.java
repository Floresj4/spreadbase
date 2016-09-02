package com.flores.h2.spreadbase.model.impl;

import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.IDataDefinition;

/**
 * 
 * @author Jason
 */
public abstract class AbstractDataDefinition implements IDataDefinition {
	
	/**
	 * The column for which the data definition
	 * will be created
	 */
	protected IColumn column;
	/**
	 * Although it resides in the the
	 * typemap within the column this
	 * is the one chosen externally by a hierarchy
	 */
	protected DataType priority;

	public AbstractDataDefinition(IColumn column, DataType priority) {
		this.column = column;
	}
}