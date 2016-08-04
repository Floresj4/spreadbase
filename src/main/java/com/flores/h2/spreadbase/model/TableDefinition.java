package com.flores.h2.spreadbase.model;

import java.util.Map.Entry;

import com.flores.h2.spreadbase.model.h2.DataTypeFactory;
import com.flores.h2.spreadbase.util.BuilderUtil;

/**
 * 
 * @author Jason Flores
 */
public class TableDefinition implements ITableDefinition {
	private static final String DEFINITION = "create table %s (%n%s%n);%n";
	
	private ITable table;
	public TableDefinition(ITable table) {
		this.table = table;
	}
	
	@Override
	public String getTableDefinition() {
		StringBuilder definitions = new StringBuilder();
		
		for(Entry<String, IColumn> e : table.entrySet()) {
			IColumn c = e.getValue();
			definitions.append("\t")
				.append(c.getName())
				.append(" ")
				.append(DataTypeFactory.createDataDefinition(c).getDefinition())
				.append(",")
				.append(BuilderUtil.NEW_LINE);
		}
		
		return String.format(DEFINITION, table.getName(), definitions.toString());
	}
}