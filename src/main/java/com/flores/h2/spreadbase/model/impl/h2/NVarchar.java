package com.flores.h2.spreadbase.model.impl.h2;

import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.AbstractDataDefinition;
import com.flores.h2.spreadbase.model.impl.DataType;

public class NVarchar extends AbstractDataDefinition {

	private static final String DEFINITION = "nvarchar(%d)";
	
	public NVarchar(IColumn column, DataType priority) {
		super(column, priority);
	}

	@Override
	public String getDefinition() {
		String largestValue = String.valueOf(dt.getPrecision());
		return String.format(DEFINITION, largestValue.length());
	}
}
