package com.flores.h2.spreadbase.model.h2;

import com.flores.h2.spreadbase.model.DataDefinition;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.DataType;

/**
 * 
 * @author Jason Flores
 */
public class Decimal extends DataDefinition {

	private static final String DEFINITION = "decimal(%d,%d)";

	public Decimal(IColumn column, DataType priority) {
		super(column, priority);
	}

	@Override
	public String getDefinition() {
		return String.format(DEFINITION
				, priority.getPrecision(), priority.getScale());
	}
}
