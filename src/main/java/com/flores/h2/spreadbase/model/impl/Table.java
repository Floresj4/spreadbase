package com.flores.h2.spreadbase.model.impl;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.ITable;
import com.flores.h2.spreadbase.util.BuilderUtil;

/**
 * A map of columns based on a LinkedHashMap
 * 
 * @author Jason Flores
 * @see LinkedHashMap
 */
public class Table implements ITable {

	private String name;
	private String description;

	private File fromFile;
	
	protected Map<String, IColumn> columns;

	public Table(String name) {
		this(name, null);
	}

	public Table(String name, String description) {
		this(name, description, new String[0]);
	}

	public Table(String name, String description, String columns[]) {
		this(name, description, columns, null);
	}
	
	public Table(String name, String description, String columns[], File fromFile) {
		this.name = name;
		this.description = description;
		this.columns = new LinkedHashMap<>();
		for(String c : columns)	this.columns.put(c, new Column(c));
		this.fromFile = fromFile;
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
	public int size() {
		return columns.size();
	}

	@Override
	public boolean isEmpty() {
		return columns.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return columns.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return columns.containsValue(value);
	}

	@Override
	public IColumn get(Object key) {
		return columns.get(key);
	}

	@Override
	public IColumn put(String key, IColumn value) {
		return columns.put(key, value);
	}

	@Override
	public IColumn remove(Object key) {
		return columns.remove(key);
	}

	/**
	 * iterate the entrySet and return the appropriate column
	 * based on index
	 * @param index of column to find
	 * @return a column or null if not found
	 */
	public IColumn get(int index) { 
		if(index > this.size())
			throw new IndexOutOfBoundsException(
					String.format("The index %d is larger than the number of columns %d"
							, index, this.size()));
		int i = 0;
		for(Entry<String, IColumn> c : columns.entrySet()) {
			if(i == index)
				return c.getValue();
			i++;
		}
		
		return null;
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends IColumn> m) {
		columns.putAll(m);
	}

	@Override
	public void clear() {
		columns.clear();
	}

	@Override
	public Set<String> keySet() {
		return columns.keySet();
	}

	@Override
	public Collection<IColumn> values() {
		return columns.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, IColumn>> entrySet() {
		return columns.entrySet();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder(getName())
			.append(" (").append(BuilderUtil.NEW_LINE);
		for(Entry<String, IColumn> e : entrySet())
			builder.append(String.format("\t%s",e.getValue().toString()))
				.append(BuilderUtil.NEW_LINE);

		return builder.append(")").append(BuilderUtil.NEW_LINE).toString();
	}

	@Override
	public void setFromFile(File f) {
		this.fromFile = f;
	}

	@Override
	public File getFromFile() {
		return fromFile;
	}
}