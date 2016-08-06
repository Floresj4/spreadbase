package com.flores.h2.spreadbase.model.h2;

import com.flores.h2.spreadbase.model.DataDefinition;
import com.flores.h2.spreadbase.model.IColumn;

public class NVarchar extends DataDefinition {

	private static final String DEFINITION = "nvarchar(%d)";
	
	public NVarchar(IColumn column) {
		super(column);
	}

	@Override
	public String getDefinition() {
		String largestValue = String.valueOf(column.getPrecision());
		return String.format(DEFINITION, largestValue.length());
	}
}
