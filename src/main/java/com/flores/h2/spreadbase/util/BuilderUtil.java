package com.flores.h2.spreadbase.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * 
 * @author Jason Flores
 */
public class BuilderUtil {

	public static final int UNSET_INT = Integer.MIN_VALUE;
	public static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * @param file to retrieve an extension from
	 * @return the extension of a given file's name
	 */
	public static final String getFileExtension(File file) {
		String name = file.getName();
		return name.substring(name.lastIndexOf(".") + 1, name.length());
	}
	
	/**
	 * 
	 * @param f
	 * @return
	 */
	public static File fileAsSqlFile(File f) {
		String path = f.isDirectory() ? f.getPath() : f.getParentFile().getPath();
		String name = f.getName();
		return new File(path, name + ".sql");
	}
	
	/**
	 * Ensure that the directory actually exists and is in-fact a 
	 * directory
	 * @param directoryPath to evaluate
	 * @return file instance of the directory
	 * @throws IOException
	 */
	public static File validateDirectoryPath(String directoryPath) throws IOException {
		if(directoryPath == null)
			throw new IOException("'directoryPath' cannont be null.");

		File dir = new File(directoryPath);
		if(!dir.exists())
			throw new FileNotFoundException(String.format(
					"%s does not exist", directoryPath));
		else if(!dir.isDirectory())
			throw new IOException(String.format(
					"%s is not a valid directory", directoryPath));
		
		return dir;
	}
	
	public static File validateFilePath(String filepath) throws IOException {
		if(filepath == null)
			throw new IOException("'filepath' cannot be null.");
		
		File file = new File(filepath);
		if(!file.exists())
			throw new FileNotFoundException(String.format(
					"%s does not exist", filepath));
		return file;
	}

	public static boolean isCsvFile(File path) {
		return BuilderUtil.getFileExtension(path).equals("csv");
	}
	
	public static String sheetNameToFilename(Sheet sheet) {
		return stringNameToFilename(sheet.getSheetName());
	}
	
	public static String stringNameToFilename(String name) {
		int i = name.length();
		for(; i > 0 && name.charAt(i - 1) == ' '; i--){ }
		return String.format("%s.csv", name.substring(0, i).toLowerCase().replaceAll(" ","_"));
	}

	/**
	 * TODO fix this...
	 * Translate the current column number to the excel character convention
	 * @param columnNumber numeric value
	 * @return a string representing the cell in Excel conventions
	 */
	public static String columnNumberToExcelColumnName(int columnNumber) {
		int alpha = (int) Math.floor(columnNumber / 27);
		int remainder = columnNumber - (alpha * 26);
		
		char[] columnName = new char[2];
		columnName[0] = alpha > 0 ? (char)(alpha + 64) : ' ';
		columnName[1] = (remainder > 0) ? (char)(remainder + 64) : ' ';
		return new String(columnName);
			
	}
}