package com.flores.h2.spreadbase.model.impl.h2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flores.h2.spreadbase.exception.UnsupportedTypeException;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.IDefinitionBuilder;
import com.flores.h2.spreadbase.model.impl.AbstractDataDefinition;
import com.flores.h2.spreadbase.model.impl.DataType;
import com.flores.h2.spreadbase.util.BuilderUtil;

/**
 * Utility class for creating H2 definitions
 * @author Jason Flores
 */
public class DataDefinitionBuilder implements IDefinitionBuilder {
	private static final Logger logger = LoggerFactory.getLogger(DataDefinitionBuilder.class);
	
	@Override
	public AbstractDataDefinition createDataDefinition(IColumn column) throws UnsupportedTypeException {
		logger.debug("creating definition for {}, column");

		DataType dt =  column.getDataType();
		AbstractDataDefinition definition = null;

		switch(dt.getSimpleTypeName()) {
			case "String":
				definition = new NVarchar(column, dt);
				break;

			default:

				//only handling integers and doubles at the moment
				if(!dt.getType().equals(Integer.class) && !dt.getType().equals(Double.class))
					throw new UnsupportedTypeException("The type " + 
							dt.getType().getSimpleName() + " is not a supported number class");

				return (dt.getScale() == BuilderUtil.UNSET_INT)
					? asNaturalNumber(column, dt)
							: asRationalNumber(column, dt);
				
		}

		return definition;
	}
	
	/**
	 * Create a data definition based on the current column
	 * @param column to produce a data definition from
	 * @param maxValue determine while analyzing the input file
	 * @return a valid numeric data type
	 */
	private static AbstractDataDefinition asNaturalNumber(IColumn column, DataType priority) {
		if (TinyInt.inRange(priority.getPrecision())) 
			return new TinyInt(column, priority); 
		 
		else if (SmallInt.inRange(priority.getPrecision()))
			return new SmallInt(column, priority);
		
		else
			return new Int(column, priority);
	}
	
	/**
	 * Create a data definition based on the current column
	 * @param column to produce a data definition from
	 * @param precision determine while analyzing the input file
	 * @param scale determine while analyzing the input file
	 * @return a valid numeric data type
	 */
	private static AbstractDataDefinition asRationalNumber(IColumn column, DataType priority) {
		return new Double(column, priority);
	}
}