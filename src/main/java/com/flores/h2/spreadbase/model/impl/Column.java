package com.flores.h2.spreadbase.model.impl;

import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.IMeasurable;
import com.flores.h2.spreadbase.util.BuilderUtil;

/**
 * One of the main class of this project. Column metadata.
 * 
 * @author Jason Flores
 */
public class Column implements IColumn, IMeasurable {

	private String name;
	private String description;

	/**
	 * Precision of the current data type. For data types not requiring both
	 * precision and scale, this field is synonymous with size.
	 */
	protected int precision;

	/**
	 * For numeric data types with a decimal value
	 */
	protected int scale;

	private Class<?> type;

	public Column(Class<?> c, int precision) {
		this(c, precision, BuilderUtil.UNSET_INT);
	}

	public Column(Class<?> c, int precision, int scale) {
		this(null, null, c, precision, scale);
	}

	public Column(IColumn c) {
		this(c.getName()
			, c.getDescription()
			, c.getType()
			, c.getPrecision()
			, c.getScale());
	}

	public Column(String name) {
		this(name, null, null, 0, BuilderUtil.UNSET_INT);
	}

	public Column(String name, String description) {
		this(name, description, null, 0, BuilderUtil.UNSET_INT);
	}
	
	public Column(String name, String description, Class<?> c, int precision) {
		this(name, description, c, precision, BuilderUtil.UNSET_INT);
	}

	public Column(String name, String description, Class<?> c, int precision, int scale) {
		this.name = name;
		this.description = description;
		this.type = c;
		this.precision = precision;
		this.scale = scale;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getPrecision() {
		return precision;
	}

	public int getScale() {
		return scale;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public void setDescription(String desc) {
		this.description = desc;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	@Override
	public void setType(Class<?> type) {
		this.type = type;
	}

	/**
	 * TODO: add JAXB annotations
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Column{ name: ").append(name).append(BuilderUtil.NEW_LINE)
			.append("\ttype: ").append(type).append(BuilderUtil.NEW_LINE)
			.append("\tprecision: ").append(precision).append(BuilderUtil.NEW_LINE)
			.append("\tscale: ").append(scale).append(BuilderUtil.NEW_LINE)
			.append("}");
		
		return builder.toString();
	}
}