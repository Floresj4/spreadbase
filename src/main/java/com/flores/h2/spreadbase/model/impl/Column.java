package com.flores.h2.spreadbase.model.impl;

import java.util.HashMap;
import java.util.Map;

import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.util.BuilderUtil;

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
	
	/**
	 * Store all the encountered datatypes
	 */
	protected Map<Class<?>, DataType> typeMap;

	public Column(DataType type) {
		this(null, type);
	}

	public Column(IColumn c) {
		this(c.getName());

		this.typeMap = c.getTypeMap();
	}

	public Column(String name) {
		this(name, new DataType(String.class, 0, BuilderUtil.UNSET_INT));
	}

	public Column(String name, DataType dt) {
		this.name = name;
		typeMap = new HashMap<>();
		typeMap.put(dt.type, dt);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Map<Class<?>, DataType> getTypeMap() {
		return typeMap;
	}

	public void put(DataType t) {
		typeMap.put(t.getClass(), t);
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
		StringBuilder builder = new StringBuilder();
		builder.append("Column { name: ").append(name).append(BuilderUtil.NEW_LINE);

		//show all types encountered
		typeMap.forEach((c, d) -> {
			builder.append(d);
		});

		return builder.append("}").toString();
	}
}