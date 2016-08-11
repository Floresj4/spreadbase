package com.flores.h2.spreadbase.model.impl;

public class DataType {
	
	Class<?>type;
	
	int precision, scale;

	public DataType(Class<?> type, int precision, int scale) {
		this.type = type;
		this.precision = precision;
		this.scale = scale;
	}
}
