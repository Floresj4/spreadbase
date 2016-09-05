package com.flores.h2.spreadbase.model.impl;

/**
 * 
 * @author Jason
 */
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
	
	public String getTypeName() { return type.getName(); }
	
	public String getSimpleTypeName() { return type.getSimpleName(); }

	public String toString() {
		return String.format("\t\ttype: %s%n\t\tprecision: %d%n\t\tscale: %s"
				, type, precision, scale);
	}
}
