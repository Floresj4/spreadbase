package com.flores.h2.spreadbase.io.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * Filter to allow only CSV or Excel file extension
 * @author Jason Flores
 */
public class CsvXlsFilter implements FileFilter {
	@Override public boolean accept(File file) {
		String name = file.getName();
		return file.isDirectory() ||
				name.endsWith(".csv") || 
				name.endsWith(".xls") || 
				name.endsWith(".xlsx");
	}
}