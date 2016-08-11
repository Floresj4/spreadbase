package com.flores.h2.spreadbase.model.impl;

public class DataType {
	
	Class<?>type;
	
	int precision, scale;

	public DataType(Class<?> type, int precision, int scale) {
		this.type = type;
		this.precision = precision;
		this.scale = scale;
	}
	
	public int getPrecision() {	return precision; }
	public int getScale() { return scale; }
	public Class<?> getType() { return type; }
	
	public String toString() {
		return String.format("%s %d %s", type, precision, scale);
	}
}