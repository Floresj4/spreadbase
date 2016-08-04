package com.flores.h2.spreadbase.model.h2;

import com.flores.h2.spreadbase.model.DataDefinition;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.Accuracy;

/**
 * 
 * @author Jason Flores
 */
public class Decimal extends DataDefinition {

	private static final String DEFINITION = "decimal(%d,%d)";

	public Decimal(IColumn column) {
		super(column);
	}

	@Override
	public String getDefinition() {
		Accuracy acc = column.getAccuracy();
		return String.format(DEFINITION, acc.precision, acc.scale);
	}
}
