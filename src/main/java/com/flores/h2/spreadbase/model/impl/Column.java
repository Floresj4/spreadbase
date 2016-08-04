package com.flores.h2.spreadbase.model.impl;

import com.flores.h2.spreadbase.model.IColumn;

/**
 * 
 * @author Jason Flores
 */
public class Column implements IColumn {

	private String name;
	private String description;
	
	private Accuracy accuracy;

	private Class<?> type;
	
	public Column(String name) {
		this(name, null);
	}
	
	public Column(String name, String description) {
		this(name, description, null, null);
	}

	public Column(String name, String description, Class<?> type, Accuracy acc) {
		this.name = name;
		this.description = description;
		this.type = type;
		this.accuracy = acc;
	}
	
	@Override
	public Accuracy getAccuracy() {
		return accuracy;
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
	public Class<?> getType() {
		return type;
	}

	@Override
	public void setAccuracy(Accuracy accuracy) {
		this.accuracy = accuracy;
	}

	@Override
	public void setType(Class<?> type) {
		this.type = type;
	}
	
	public String toString() {
		String acc = accuracy == null ? "unset" : accuracy.toString();
		String classType = type == null ?  "unset" : type.getSimpleName();
		return String.format("Column:{%s, %s, %s}", name, classType, acc);
	}
}