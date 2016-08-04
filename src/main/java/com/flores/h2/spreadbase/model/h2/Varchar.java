package com.flores.h2.spreadbase.model.h2;

import com.flores.h2.spreadbase.model.DataDefinition;
import com.flores.h2.spreadbase.model.IColumn;

/**
 * 
 * @author Jason Flores
 */
public class Varchar extends DataDefinition {

	private static final String DEFINITION = "varchar(%d)";
	
	public Varchar(IColumn column) {
		super(column);
	}

	@Override
	public String getDefinition() {
		return String.format(DEFINITION, column.getAccuracy().precision);
	}
}