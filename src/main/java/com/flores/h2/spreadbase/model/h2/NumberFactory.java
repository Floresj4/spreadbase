package com.flores.h2.spreadbase.model.h2;

import com.flores.h2.spreadbase.exception.UnsupportedTypeException;
import com.flores.h2.spreadbase.model.DataDefinition;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.Accuracy;

/**
 * @author Jason Flores
 */
public class NumberFactory {

	/**
	 * Get the appropriate data defintion for a numeric value
	 * @param column to translate
	 * @return a numeric data definition
	 * @throws UnsupportedTypeException on types outside of Integer and Double, currently
	 */
	public static DataDefinition getDataDefinition(IColumn column) throws UnsupportedTypeException {
		//only handling integers and doubles at the moment
		if(!column.getType().equals(Integer.class) && !column.getType().equals(Double.class))
			throw new UnsupportedTypeException("The type " + 
					column.getType().getSimpleName() + " is not a supported number class");
		
		//get the current columns accuracy
		Accuracy acc = column.getAccuracy();
		return (acc.scale == -1)
			? asNaturalNumber(column, acc.precision)
					: asRationalNumber(column, acc.precision, acc.scale);
	}

	/**
	 * Create a data definition based on the current column
	 * @param column to produce a data definition from
	 * @param maxValue determine while analyzing the input file
	 * @return a valid numeric data type
	 */
	private static DataDefinition asNaturalNumber(IColumn column, int maxValue) {
		if (maxValue >= -128 && maxValue <= 127) { 
			return new TinyInt(column); 
		} else if (maxValue >= -32768 && maxValue <= 32767) {
			return new SmallInt(column);
		} else {
			return new Int(column);
		}
	}
	
	/**
	 * Create a data definition based on the current column
	 * @param column to produce a data definition from
	 * @param precision determine while analyzing the input file
	 * @param scale determine while analyzing the input file
	 * @return a valid numeric data type
	 */
	private static DataDefinition asRationalNumber(IColumn column, int precision, int scale) {
		return new Double(column);
	}
}
