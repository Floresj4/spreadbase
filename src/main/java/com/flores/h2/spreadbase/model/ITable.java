package com.flores.h2.spreadbase.model;

import java.io.File;
import java.util.Map;

/**
 * 
 * @author Jason Flores
 */
public interface ITable extends Map<String, IColumn>, IDescribable {
	public String getName();
	public String getDescription();
	public void setDescription(String description);
	public void setFromFile(File f);
	public File getFromFile();
}
