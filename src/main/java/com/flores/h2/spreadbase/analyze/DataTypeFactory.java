package com.flores.h2.spreadbase.analyze;

import com.flores.h2.spreadbase.model.impl.DataType;
import com.flores.h2.spreadbase.util.BuilderUtil;
import com.flores.h2.spreadbase.util.TypeHierarchy;

/**
 * Responsible for handling DataType operations
 * @author Jason
 */
public class DataTypeFactory {
	/**
	 * current type hierarchy impl.
	 */
	private static final TypeHierarchy hr = new TypeHierarchy();

	/**
	 * Make a DataType!  Infer type, scale, and precision based on
	 * the {@code dataValue} param.  Default to a string type
	 * @param dataValue to create
	 * @return a non-null DataType
	 */
	public static DataType makeDataType(String dataValue) {
		Class<?> type = null;
		int precision = BuilderUtil.UNSET_INT;
		int scale = BuilderUtil.UNSET_INT;

		try {	//determine type
			Double d = Double.parseDouble(dataValue);

				//parse for precision and scale
			String temp[] = d.toString().split("\\.");
			boolean hasScale = d != Math.floor(d);
			if(hasScale) {
				precision = Integer.parseInt(temp[0]);
				scale = Integer.parseInt(temp[1]);
			} else { //use the actual data value
				precision = Integer.parseInt(dataValue);
			}

			type = hasScale ? Double.class : Integer.class;
		}
		catch(NumberFormatException nfe) {
			//default to string
			type = String.class;
			precision = dataValue.length();
		}
		catch(NullPointerException npe) {
			//if dataValue is null, default string
			type = String.class;
			//leave precision and scale unset
		}

		return new DataType(type, precision, scale);
	}

	/**
	 * Merge directly from an incoming value
	 * @param _curr
	 * @param dataValue
	 * @return
	 */
	public static DataType mergeDataType(DataType _curr, String dataValue) {
		return mergeDataType(makeDataType(dataValue), _curr);
	}

	/**
	 * These values should never be null by design of {@link #makeDataType(String)}
	 * Type isn't tested because of the string default strategy in 
	 * {@code #makeDataType(String)}. 
	 * 
	 * @param _new the most recently created from a data cell.
	 * @param _curr the currently existing, if the type has been encountered
	 * already, otherwise pass in null
	 * @return a non-null datatype
	 */
	public static DataType mergeDataType(DataType _new, DataType _curr) {
		if(_curr == null)
			return _new;

		int precision = _new.getPrecision() >= _curr.getPrecision()
				? _new.getPrecision() : _curr.getPrecision();
		
		int scale = _new.getScale() >= _curr.getScale()
				? _new.getScale() : _curr.getScale();

		//rank indices
		int idx = hr.get(_new.getType()) <= hr.get(_curr.getType()) 
				? hr.get(_new.getType()) 
				: hr.get(_curr.getType());

		return new DataType(hr.classByIndex(idx), precision, scale);
	}
}
