package com.flores.h2.spreadbase.model.h2;

import com.flores.h2.spreadbase.model.DataDefinition;
import com.flores.h2.spreadbase.model.IColumn;

/**
 * 
 * @author Jason Flores
 */
public class Double extends DataDefinition {

	private static final String DEFINITION = "double(%d, %d)";
	
	public Double(IColumn column) {
		super(column);
	}

	@Override
	public String getDefinition() {
		return String.format(DEFINITION
				, column.getPrecision(), column.getScale());
	}
}
