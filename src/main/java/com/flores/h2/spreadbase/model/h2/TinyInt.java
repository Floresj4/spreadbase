package com.flores.h2.spreadbase.model.h2;

import com.flores.h2.spreadbase.model.DataDefinition;
import com.flores.h2.spreadbase.model.IColumn;

/**
 * 
 * @author Jason
 */
public class TinyInt extends DataDefinition {

	public static final int MIN_VALUE = -128;
	public static final int MAX_VALUE = 127;

	public TinyInt(IColumn column) {
		super(column);
	}

	@Override
	public String getDefinition() {
		return "tinyint";
	}
	
	public static boolean inRange(int value) {
		return (value >= MIN_VALUE && value <= MAX_VALUE);
	}
}