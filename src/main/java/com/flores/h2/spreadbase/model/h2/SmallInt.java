package com.flores.h2.spreadbase.model.h2;

import com.flores.h2.spreadbase.model.DataDefinition;
import com.flores.h2.spreadbase.model.IColumn;

public class SmallInt extends DataDefinition {

	public SmallInt(IColumn column) {
		super(column);
	}

	@Override
	public String getDefinition() {
		return "smallint";
	}
}