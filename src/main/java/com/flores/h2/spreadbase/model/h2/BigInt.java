package com.flores.h2.spreadbase.model.h2;

import com.flores.h2.spreadbase.model.DataDefinition;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.DataType;

/**
 * TODO: stop replicating {@link #inRange(int)}
 * @author Jason Flores
 */
public class BigInt extends DataDefinition {

	private static final int MIN_VALUE = -32768;
	private static final int MAX_VALUE = 32767;
	
	public BigInt(IColumn column, DataType priority) {
		super(column, priority);
	}

	@Override
	public String getDefinition() {
		return "bigint";
	}
	
	public static boolean inRange(int value) {
		return (value >= MIN_VALUE && value <= MAX_VALUE);
	}
}
