package com.flores.h2.spreadbase.io;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import com.flores.h2.spreadbase.model.ITable;

public class InsertStatementWriter implements Closeable {

	private BufferedWriter writer;
	private StatementType type;
	
	private static final String INSERT_SELECT_AS = "insert into %s select '%s', %s from csvread('%s');%n";

	public InsertStatementWriter(File out) throws IOException {
		this(out, StatementType.ASTERISK);
	}

	public InsertStatementWriter(File out, StatementType type) throws IOException {
		this.writer = new BufferedWriter(new FileWriter(out));
		this.type = type;
	}
	
	public void write(Collection<ITable> tables) throws IOException {
		for(ITable t : tables)
			write(t);
	}

	public void write(ITable table) throws IOException {
		//use the directory as the key
		String fromDirectory = table.getFromFile().getParentFile().getName();
		
		StringBuilder columns = new StringBuilder();
		switch(type) {
			case ASTERISK: columns.append("*"); break;
			case COLUMNS: 
				//build the column list ignoring the version column
				table.entrySet().stream()
					.filter(e -> !e.getValue().getName().equals("version"))
					.forEach(e -> columns.append(e.getKey()).append(", "));
				
				//remove the trailing ', '
				columns.delete(columns.length() - 2, columns.length());
				break;
		}

		writer.write(String.format(INSERT_SELECT_AS, 
				table.getName(), fromDirectory, 
				columns.toString(), targetAsCsv(table.getFromFile())));
	}

	private File targetAsCsv(File fromFile) {
		String filename = fromFile.getName()
				.substring(0, fromFile.getName().indexOf(".")) + ".csv";
		return new File(fromFile.getParentFile(), filename);
	}
	
	@Override
	public void close() throws IOException {
		writer.close();
	}
	
	public enum StatementType {
		/**
		 * Use an asterisk to imply all columns
		 */
		ASTERISK,
		/**
		 * Explicitly define all columns as provided
		 * by the table instance
		 */
		COLUMNS;
	}
}
