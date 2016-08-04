package com.flores.h2.spreadbase.io;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map.Entry;

import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.ITable;
import com.flores.h2.spreadbase.model.h2.DataTypeFactory;

public class TableDefinitionWriter implements Closeable {
	
	private Writer writer;
	
	//create table_name (
	private static final String TABLE_LINE = "create table %s (%n";

	//drop table if exists
	private static final String DROP_TABLE = "drop table if exists %s;%n";

	//tab, name, definition, newline
	private static final String COLUMN_LINE = "\t%s %s,%n";
	private static final String TABLE_END = ");%n%n";
	private static final String COMMENT = "-- %s%n";

	//terminate table and start select
	private static final String CSV_READ_LINE = ") as select * from csvread('%s');%n%n";

	public TableDefinitionWriter(File outputFile) throws IOException {
		writer = new BufferedWriter(new FileWriter(outputFile));
	}

	@SuppressWarnings("unchecked")
	public void write(Object o) throws IOException {
		if(o instanceof Collection)
			write((Collection<ITable>)o);
		else if(o instanceof ITable)
			write((ITable)o);
	}
	
	public void write(ITable table) throws IOException {
		writer.write(String.format(COMMENT, table.getDescription()));
		writer.write(String.format(DROP_TABLE, table.getName()));

		//writer the create line
		writer.write(String.format(TABLE_LINE, table.getName()));
		
		for(Entry<String, IColumn> e : table.entrySet()) {
			IColumn c = e.getValue();
			writer.write(String.format(COLUMN_LINE, c.getName()
				, DataTypeFactory.createDataDefinition(c).getDefinition()));
		}

		//terminate the table definition
		writer.write(String.format(CSV_READ_LINE, targetAsCsv(table.getFromFile())));
	}

	private File targetAsCsv(File fromFile) {
		String filename = fromFile.getName()
				.substring(0, fromFile.getName().indexOf(".")) + ".csv";
		return new File(fromFile.getParentFile(), filename);
	}

	/**
	 * Convenience method to handle multiple tables in a single call
	 * @param tables
	 * @throws IOException
	 */
	public void write(Collection<ITable> tables) throws IOException {
		for(ITable t : tables)
			write(t);
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}