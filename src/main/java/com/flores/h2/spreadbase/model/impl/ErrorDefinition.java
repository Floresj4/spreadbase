package com.flores.h2.spreadbase.model.impl;

import com.flores.h2.spreadbase.model.IColumn;

/**
 * 
 * @author Jason
 */
public class ErrorDefinition extends AbstractDataDefinition {

	public ErrorDefinition(IColumn column) {
		super(column, null);
	}

	@Override
	public String getDefinition() {
		return String.format("//error creating {}", column.getName());
	}
	
}
