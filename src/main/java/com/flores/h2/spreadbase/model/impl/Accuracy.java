package com.flores.h2.spreadbase.model.impl;

/**
 * 
 * @author Jason Flores
 */
public class Accuracy {

	/**
	 * Precision of the current data type.  For data types
	 * not requiring both precision and scale, this field is
	 * synonymous with size. 
	 */
	public int precision;
	
	/**
	 * For numeric data types with a decimal value
	 */
	public int scale;
	
	public Accuracy(int value) {
		this(value, -1);
	}

	public Accuracy(int precision, int scale) {
		this.precision = precision;
		this.scale = scale;
	}
	
	public String toString() {
		return scale == -1
			? String.format("Accuracy: {%d}", precision)
				: String.format("Accuracy: {%d %d}", precision, scale);
	}
}