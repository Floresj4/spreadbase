package com.flores.h2.spreadbase.model;

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
	
	public DataDefinition(IColumn column) {
		this.column = column;
	}
	
	public abstract String getDefinition();
}