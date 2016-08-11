package com.flores.h2.spreadbase.model;

import com.flores.h2.spreadbase.model.impl.DataType;

/**
 * 
 * @author Jason
 */
public abstract class DataDefinition {
	
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

	public DataDefinition(IColumn column, DataType priority) {
		this.column = column;
	}
	
	public abstract String getDefinition();
}