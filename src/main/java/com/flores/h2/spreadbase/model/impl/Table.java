package com.flores.h2.spreadbase.model.impl;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.ITable;
import com.flores.h2.spreadbase.util.SpreadbaseUtil;

/**
 * A map of columns backed by a LinkedHashMap.  If any constructor 
 * calls are made where the {@code String columns[]} parameter is 
 * set to null, the table will be created with an empty map of columns.
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

	public Table(String name, File fromFile) {
		this(name, fromFile, new LinkedHashMap<>());
	}

	public Table(String name, File fromFile, Map<String, IColumn>columns) {
		this.name = name;
		this.fromFile = fromFile;
		this.columns = columns;
	}
	
	@Override
	public void clear() {
		columns.clear();
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
	public Set<java.util.Map.Entry<String, IColumn>> entrySet() {
		return columns.entrySet();
	}

	@Override
	public IColumn get(Object key) {
		
		if(key instanceof Integer) {
			int k = (int)key;

			if(k > this.size())
				throw new IndexOutOfBoundsException(
						String.format("The index %d is larger than the number of columns %d"
								, k, this.size()));
			int i = 0;
			for(Entry<String, IColumn> c : columns.entrySet()) {
				if(i == k) return c.getValue();
				i++;
			}
		}
		
		
		return columns.get(key);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public File getFromFile() {
		return fromFile;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isEmpty() {
		return columns.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return columns.keySet();
	}

	public IColumn put(IColumn c) {
		return columns.put(c.getName(), c);
	}

	@Override
	public IColumn put(String key, IColumn value) {
		return columns.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends IColumn> m) {
		columns.putAll(m);
	}

	@Override
	public IColumn remove(Object key) {
		return columns.remove(key);
	}

	@Override
	public void setDescription(String desc) {
		this.description = desc;
	}
	
	@Override
	public void setFromFile(File f) {
		this.fromFile = f;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int size() {
		return columns.size();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder(getName())
			.append(" (").append(SpreadbaseUtil.NEW_LINE);
		
		this.forEach((k,v) -> {
			builder.append( String.format("\t%s", v.getName()))
				.append(SpreadbaseUtil.NEW_LINE);	
		});
		
		return builder.append(")").append(SpreadbaseUtil.NEW_LINE).toString();
	}

	@Override
	public Collection<IColumn> values() {
		return columns.values();
	}
}