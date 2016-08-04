package com.flores.h2.spreadbase.io.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * 
 * @author Jason Flores
 */
public class XlsFilter implements FileFilter {
	@Override public boolean accept(File file) {
		return file.isDirectory() || 
			file.getName().endsWith(".xls") ||
			file.getName().endsWith(".xlsx");
	}
}