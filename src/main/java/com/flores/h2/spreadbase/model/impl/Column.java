package com.flores.h2.spreadbase.model.impl;

import com.flores.h2.spreadbase.model.IColumn;

/**
 * One of the main class of this project. Column metadata.  The {@code typeMap}
 * allows me to store and scale all potential data types and to choose the
 * highest in the hierarchy as the final data type.  The {@typeMap} is returnable
 * for the calling code to implement its own hierarchy rules.
 * 
 * @author Jason Flores
 */
public class Column implements IColumn {

	private String name;
	private String description;

	private DataType dataType;

	public Column(IColumn c) {
		this(c.getName());
	}

	public Column(String name) {
		this.name = name;
	}

	public DataType getDataType() {
		return dataType;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	
	@Override
	public void setDescription(String desc) {
		this.description = desc;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * TODO: add JAXB annotations
	 */
	public String toString() {
		return String.format("[name: %s desc: %s%n%s]%n"
				, name, description, dataType);
	}
}