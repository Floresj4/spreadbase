package com.flores.h2.spreadbase.model.h2;

import com.flores.h2.spreadbase.model.DataDefinition;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.DataType;

public class NVarchar extends DataDefinition {

	private static final String DEFINITION = "nvarchar(%d)";
	
	public NVarchar(IColumn column, DataType priority) {
		super(column, priority);
	}

	@Override
	public String getDefinition() {
		String largestValue = String.valueOf(priority.getPrecision());
		return String.format(DEFINITION, largestValue.length());
	}
}
