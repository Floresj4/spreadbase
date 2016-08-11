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
		this(null, null, type);
	}

	public Column(IColumn c) {
		this(c.getName()
			, c.getDescription());

		this.typeMap = c.getTypeMap();
	}

	public Column(String name) {
		this(name, null, new DataType(String.class, 0, BuilderUtil.UNSET_INT));
	}

	public Column(String name, String description) {
		this(name, description, new DataType(String.class, 0, BuilderUtil.UNSET_INT));
	}

	public Column(String name, String description, DataType dt) {
		this.name = name;
		this.description = description;

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

		typeMap.forEach((c, d) -> {
			builder.append("\t type: ").append(d.type).append(BuilderUtil.NEW_LINE)
			.append("\tprecision: ").append(d.precision).append(BuilderUtil.NEW_LINE)
			.append("\tscale: ").append(d.scale).append(BuilderUtil.NEW_LINE);
		});

		return builder.append("}").toString();
	}

	@Override
	public Map<Class<?>, DataType> getTypeMap() {
		return typeMap;
	}
}