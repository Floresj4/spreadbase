package com.flores.h2.spreadbase.model.h2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flores.h2.spreadbase.exception.UnsupportedTypeException;
import com.flores.h2.spreadbase.model.AbstractRankingFactory;
import com.flores.h2.spreadbase.model.DataDefinition;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.DataType;
import com.flores.h2.spreadbase.util.BuilderUtil;
import com.flores.h2.spreadbase.util.TypeHierarchy;

/**
 * 
 * @author Jason Flores
 */
public class DataTypeFactory extends AbstractRankingFactory {
	
	public DataTypeFactory(TypeHierarchy hierarchy) {
		super(hierarchy);
	}

	private static final Logger logger = LoggerFactory.getLogger(DataTypeFactory.class);
	
	public DataDefinition createDataDefinition(IColumn column) throws UnsupportedTypeException {
		logger.debug("creating definition for {}, column");
		DataType priority = hierarchy.getPriorityDataType(column.getTypeMap());
		
		DataDefinition definition = null;
		switch(priority.getType().getSimpleName()) {
			case "String":
				definition = new NVarchar(column, priority);
				break;

			default:

				//only handling integers and doubles at the moment
				if(!priority.getType().equals(Integer.class) && !column.getTypeMap().equals(Double.class))
					throw new UnsupportedTypeException("The type " + 
							priority.getType().getSimpleName() + " is not a supported number class");

				return (priority.getScale() == BuilderUtil.UNSET_INT)
					? asNaturalNumber(column, priority)
							: asRationalNumber(column, priority);
				
		}

		return definition;
	}
	
	/**
	 * Create a data definition based on the current column
	 * @param column to produce a data definition from
	 * @param maxValue determine while analyzing the input file
	 * @return a valid numeric data type
	 */
	private static DataDefinition asNaturalNumber(IColumn column, DataType priority) {

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
	private static DataDefinition asRationalNumber(IColumn column, DataType priority) {
		return new Double(column, priority);
	}
}