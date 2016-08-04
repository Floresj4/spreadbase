package com.flores.h2.spreadbase.model.h2;

import com.flores.h2.spreadbase.model.DataDefinition;
import com.flores.h2.spreadbase.model.IColumn;

/**
 * 
 * @author Jason Flores
 */
public class BigInt extends DataDefinition {

	public BigInt(IColumn column) {
		super(column);
	}

	@Override
	public String getDefinition() {
		return "bigint";
	}
}
