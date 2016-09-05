package com.flores.h2.spreadbase.model.impl.h2;

import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.AbstractDataDefinition;
import com.flores.h2.spreadbase.model.impl.DataType;

public class Int extends AbstractDataDefinition {

	public Int(IColumn column, DataType priority) {
		super(column, priority);
	}

	@Override
	public String getDefinition() {
		return "int";
	}	
}
