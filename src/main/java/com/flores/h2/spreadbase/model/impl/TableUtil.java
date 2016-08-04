package com.flores.h2.spreadbase.model.impl;

import java.io.File;

/**
 * 
 * @author Jason Flores
 */
public class TableUtil {
	
	/**
	 * @param file to retrieve name from
	 * @return the file name without an extension
	 */
	public static final String getTableName(File file) {
		String name = file.getName().replaceAll(" ", "_");
		return name.substring(0, name.indexOf("."));
	}
}
