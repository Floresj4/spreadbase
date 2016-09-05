package com.flores.h2.spreadbase.io;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flores.h2.spreadbase.exception.UnsupportedTypeException;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.IDataDefinition;
import com.flores.h2.spreadbase.model.IDefinitionBuilder;
import com.flores.h2.spreadbase.model.ITable;
import com.flores.h2.spreadbase.model.impl.ErrorDefinition;

public class TableDefinitionWriter implements Closeable {

	//create table_name (
	private static final String TABLE_LINE = "create table %s (%n";

	//drop table if exists
	private static final String DROP_TABLE = "drop table if exists %s;%n";

	//tab, name, definition, newline
	private static final String COLUMN_LINE = "\t%s %s,%n";
	
	//TODO make this configurable
	@SuppressWarnings("unused")
	private static final String TABLE_END = ");%n%n";
	private static final String COMMENT = "-- %s%n";

	//terminate table and start select
	private static final String CSV_READ_LINE = ") as select * from csvread('%s');%n%n";

	private Writer writer;
	private IDefinitionBuilder definitionBuilder;
	
	private static final Logger logger = LoggerFactory.getLogger(TableDefinitionWriter.class);

	public TableDefinitionWriter(File outputFile, IDefinitionBuilder defBuilder) throws IOException {
		writer = new BufferedWriter(new FileWriter(outputFile));
		this.definitionBuilder = defBuilder;
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
			
			IDataDefinition definition;
			try { definition = definitionBuilder.createDataDefinition(c); }
			catch(UnsupportedTypeException u) {
				logger.error(u.getMessage());
				definition = new ErrorDefinition(c);
			}
			
			writer.write(String.format(COLUMN_LINE, 
					c.getName().replaceAll(" ", "_").toLowerCase()
				, definition.getDefinition()));
		}

		//terminate the table definition
		writer.write(String.format(CSV_READ_LINE, targetAsCsv(table.getFromFile())));
	}

	private File targetAsCsv(File fromFile) {
		String filename = fromFile.getName();
		String csvname = filename.substring(0, filename.indexOf(".")) + ".csv";

		String directory = fromFile.getParentFile().getAbsolutePath();
		directory = directory.replace("\\.\\", "\\");

		return new File(directory, csvname);
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