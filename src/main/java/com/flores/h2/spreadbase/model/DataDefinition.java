package com.flores.h2.spreadbase.model;

public abstract class DataDefinition {
	protected IColumn column;
	
	public DataDefinition(IColumn column) {
		this.column = column;
	}
	
	public abstract String getDefinition();
}