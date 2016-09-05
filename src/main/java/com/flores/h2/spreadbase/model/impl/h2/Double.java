package com.flores.h2.spreadbase.model.impl.h2;

import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.AbstractDataDefinition;
import com.flores.h2.spreadbase.model.impl.DataType;

/**
 * 
 * @author Jason Flores
 */
public class Double extends AbstractDataDefinition {

	private static final String DEFINITION = "double(%d, %d)";
	
	public Double(IColumn column, DataType priority) {
		super(column, priority);
	}

	@Override
	public String getDefinition() {
		return String.format(DEFINITION
				, dt.getPrecision(), dt.getScale());
	}
}
