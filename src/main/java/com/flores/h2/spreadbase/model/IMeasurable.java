package com.flores.h2.spreadbase.model;

/**
 * Capture datatype metadata for inspection and management 
 * @author Jason
 */
public interface IMeasurable {
	/**
	 * Precision of the current data type.  For data types
	 * not requiring both precision and scale, this field is
	 * synonymous with size. 
	 */
	public int getPrecision();
	
	/**
	 * For numeric data types with a decimal value
	 */
	public int getScale();
	

	/**
	 * Precision of the current data type.  For data types
	 * not requiring both precision and scale, this field is
	 * synonymous with size. 
	 */
	public void setPrecision(int precision);
	public void lastPrecision(int precision);
	
	/**
	 * For numeric data types with a decimal value
	 */
	public void setScale(int scale);
	public void lastScale(int scale);
}
