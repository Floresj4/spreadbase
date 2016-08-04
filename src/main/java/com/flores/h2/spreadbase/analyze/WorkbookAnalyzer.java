package com.flores.h2.spreadbase.analyze;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.ITable;
import com.flores.h2.spreadbase.model.impl.Accuracy;
import com.flores.h2.spreadbase.model.impl.Table;
import com.flores.h2.spreadbase.util.BuilderUtil;

/**
 * 
 * @author Jason Flores
 */
public class WorkbookAnalyzer {

	public static final int UNSET_INT = Integer.MIN_VALUE;
	private static final String CREATED_FROM = "created from %s:%s";
	private static final Logger logger = LoggerFactory.getLogger(WorkbookAnalyzer.class);

	private static String currentFilename;

	/**
	 * 
	 * @param fin
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public static List<ITable> analyzeAndWrite(final File fin, List<String>filter) throws Exception {
		List<ITable> tables = new LinkedList<>();

		Workbook workbook = WorkbookFactory.create(fin);
		for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
			Sheet sheet = workbook.getSheetAt(i);

			//make sure we skip the table of contents file
			if(isFiltered(sheet, filter))
				continue;

			//some spreadsheets might not have data
			Row firstRow = null;
			if((firstRow = containsRowData(sheet)) != null)
				continue;

			//create the output filename
			setCurrentFilename(sheet);

			//write the current sheet to a file as well
			int totalColumns = UNSET_INT;
			try(CsvListWriter w = getListWriter(fin, currentFilename)) {
				//initialize the table
				ITable table = initializeTable(sheet, fin, firstRow);

				List<Object>data = null;
				for(int x = 1; x < sheet.getLastRowNum(); x++) {
					Row row = sheet.getRow(x);

					//reset to build the tab delimited data
					data = new LinkedList<>();
					for(int y = 0; y < totalColumns; y++) {
						try {
							String columnName = table.get(y);
							String value = getStringValue(row.getCell(y));

							adjustColumn(table.get(columnName), value);

							data.add(value);
						} catch(Exception e) {
							//these errors are common enough to only debug log them
							logger.debug("obtaining the {} value of cell {}:{}", currentFilename
									, BuilderUtil.columnNumberToExcelColumnName(x), y);
							//add empty cell data
							data.add("");
						}
					}

					//only write if data exists
					if(data.size() != 0)
						w.write(data);
				}

				tables.add(table);
			}
		}
		
		return tables;
	}
	
	private static ITable initializeTable(Sheet sheet, File fin, Row firstRow) {
		List<String>columnNames = getColumnNames(firstRow);
		
		return new Table(sheet.getSheetName(), 
				String.format(CREATED_FROM, fin.getAbsolutePath(), sheet.getSheetName()),
					columnNames.toArray(new String[columnNames.size()])
					, fin);
	}

	/**
	 * Columns are considered valid if they are <em>not</em> empty
	 * or null.
	 * @param firstRow of the current sheet
	 * @return a non-null list of column names
	 */
	private static List<String> getColumnNames(Row firstRow) {
		List<String> columnNames = new LinkedList<>();
		int totalColumns = firstRow.getLastCellNum();
		for(int x = 0; x < totalColumns; x++) {
			if(firstRow.getCell(x) != null) {
				String name = getStringValue(firstRow.getCell(x));
				
				//if not empty
				if(name.trim().isEmpty())
					continue;
				
				//correct and add the name
				columnNames.add(name
						.replaceAll(" ", "_")	//blank spaces are invalid
						.replaceAll("-", "_"));	//dashes are also invalid
			}
		}

		return columnNames;
	}

	private static CsvListWriter getListWriter(File fin, String currentFilename) throws IOException {
		return new CsvListWriter(
				new FileWriter(new File(fin.getParent(), currentFilename.toLowerCase()))
					, CsvPreference.EXCEL_PREFERENCE);
	}

	private static Row containsRowData(Sheet sheet) {
		Row firstRow = sheet.getRow(0);
		if(firstRow == null)
			return firstRow;
		return null;
	}

	private static boolean isFiltered(Sheet sheet, List<String> filter) {
		if(!filter.contains(sheet.getSheetName())) {
			logger.debug("ignoring {}", sheet.getSheetName());
			return true;
		}
		return false;
	}

	private static void setCurrentFilename(Sheet sheet) {
		currentFilename = BuilderUtil.sheetNameToFilename(sheet);
		logger.debug("Extracting {} as {}", sheet.getSheetName(), currentFilename);
	}

	/**
	 * Get whatever value is in the current cell as a string for file output
	 * @param cell value at the current iteration
	 * @return a non-null string value for the current cell.  Rational numbers with 0
	 */
	private static String getStringValue(Cell cell) {
		boolean trimFieldValue = false;
		Object cellValue = null;
		switch(cell.getCellType()) {
			case Cell.CELL_TYPE_BLANK:		cellValue = ""; break;
			case Cell.CELL_TYPE_BOOLEAN:	cellValue = cell.getBooleanCellValue(); break;
			case Cell.CELL_TYPE_ERROR:		cellValue = "[ERROR_TYPE]"; break;
			case Cell.CELL_TYPE_FORMULA:	cellValue = cell.getCellFormula(); break;
			case Cell.CELL_TYPE_NUMERIC:
				/**
				 * determine whether to trim the trailing zero, the original
				 * files created using the jacob-bridge did not include it
				 */				
				trimFieldValue = cell.getNumericCellValue() % 1 == 0;
				cellValue = cell.getNumericCellValue();
				break;
			case Cell.CELL_TYPE_STRING:		cellValue = cell.getStringCellValue(); break;
		}

		//get the value and strip out newline
		String sValue = String.valueOf(cellValue).replaceAll("\r\n", "")
				.replaceAll("\n", "");
		return trimFieldValue 
			? sValue.substring(0, sValue.indexOf("."))
				: sValue;
	}

	private static void adjustColumn(IColumn column, String currentValue) {
		Accuracy acc = null;
		Class<?> c = null;

		try {
			if(currentValue == null || currentValue.trim().isEmpty())
				return;

			if(currentValue.startsWith(("0")))
				throw new NumberFormatException();

			//try to parse as a double first
			Double.parseDouble(currentValue);

			/**
			 * if the parse was successful get the precision
			 * if a decimal is present otherwise use as an
			 * integer
			 */
			int decimalIndex = currentValue.indexOf(".");
			if(decimalIndex != -1) {
				int precision = currentValue.substring(0, decimalIndex).length();
				int scale = currentValue.substring(decimalIndex).length();
				
				//create the accuracy and class type
				acc = new Accuracy(precision, scale);
				c = Double.class;
			} else {
				acc = new Accuracy(Integer.parseInt(currentValue));
				c = Integer.class;
			}			
		} catch(NumberFormatException e) {
			//the parse failed, use as a simple string
			acc = new Accuracy(currentValue.length());
			c = String.class;
		} catch(NullPointerException e) {
			//default to zero for now
			acc = new Accuracy(0);
			c = String.class;
		}
	
		//if unset, set
		if(column.getAccuracy() == null) {
			column.setAccuracy(acc);
			column.setType(c);
		} else {
			Accuracy currentAcc = column.getAccuracy();

			if(c == String.class)
				column.setType(c);

			//take the larger of the precision values
			if(acc.precision > currentAcc.precision)
				currentAcc.precision = acc.precision;

			//table the larger of the scale values
			if(acc.scale > currentAcc.scale)
				currentAcc.scale = acc.scale;
		}
	}
}