package com.flores.h2.spreadbase.model.h2;

import com.flores.h2.spreadbase.model.AbstractDataDefinition;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.DataType;

/**
 * 
 * @author Jason
 */
public class TinyInt extends AbstractDataDefinition {

	public static final int MIN_VALUE = -128;
	public static final int MAX_VALUE = 127;

	public TinyInt(IColumn column, DataType priority) {
		super(column, priority);
	}

	@Override
	public String getDefinition() {
		return "tinyint";
	}
	
	public static boolean inRange(int value) {
		return (value >= MIN_VALUE && value <= MAX_VALUE);
	}
}