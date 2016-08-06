package com.flores.h2.spreadbase.model.h2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flores.h2.spreadbase.exception.UnsupportedTypeException;
import com.flores.h2.spreadbase.model.DataDefinition;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.Column;

/**
 * 
 * @author Jason Flores
 */
public class DataTypeFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(DataTypeFactory.class);

	public static DataDefinition createDataDefinition(IColumn column) {
		try {
			String clazz = column.getType().getSimpleName();
			DataDefinition definition = null;

			switch(clazz) {
				case "String":
					definition = new NVarchar(column);
					break;

				default:
					//try to convert a suitable number type
					try { definition = NumberFactory.getDataDefinition(column); }
					catch (UnsupportedTypeException e) { logger.error(e.getMessage()); }
			}

			return definition;
		} catch(NullPointerException npe) {
			return createDataDefinition(new DefaultColumn(column.getName()));
		}
	}
}

/**
 * If a type was left undetermined ("unset"), because it was probably
 * an empty sheet or csv, use this class as a placeholder
 * @author Jason
 */
class DefaultColumn extends Column {

	public DefaultColumn(String name) {
		this(name, String.class, 5, -1);
	}

	public DefaultColumn(String name, Class<?>c, int precision, int scale) {
		super(name, null, c, precision, scale);
	}	
}