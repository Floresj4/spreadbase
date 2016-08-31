package com.flores.h2.spreadbase.analyze;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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
import com.flores.h2.spreadbase.model.impl.Column;
import com.flores.h2.spreadbase.model.impl.DataType;
import com.flores.h2.spreadbase.model.impl.Table;
import com.flores.h2.spreadbase.util.BuilderUtil;

/**
 * 
 * @author Jason Flores
 */
public class WorkbookAnalyzer {

	public static final int UNSET_INT = Integer.MIN_VALUE;
	
	//used as table description, but also comment in .sql file
	private static final String DLL_CREATED_FROM = "created from %s:%s";
	
	public static final String EMPTY_CELL_DATA = "";

	private static final Logger logger = LoggerFactory.getLogger(WorkbookAnalyzer.class);

	/**
	 * 
	 * @param fin
	 * @param filter
	 * @return
	 * @throws Exception
	 */
	public static List<ITable> analyze(final File fin, List<String>filter) throws Exception {
		List<ITable> tables = new LinkedList<>();
		
		Workbook workbook = WorkbookFactory.create(fin);
		for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
			Sheet sheet = workbook.getSheetAt(i);

			//make sure we skip the table of contents file
			if(isFiltered(sheet, filter))
				continue;

			//some spreadsheets might not have data
			Row firstRow = null;
			if((firstRow = containsRowData(sheet)) == null)
				continue;

			logger.debug("inspecting {}", sheet.getSheetName());

			//initialize the table and add columns
			ITable table = initializeTable(sheet, fin);
			table.putAll(createColumns(firstRow));

			List<Object>data = null;
			for(int x = 1; x < sheet.getLastRowNum(); x++) {
				Row row = sheet.getRow(x);

				//reset to build the tab delimited data
				data = new LinkedList<>();
				for(int y = 0; y < table.size(); y++) {
					try {
						IColumn column = table.get(y);
						String value = getStringValue(row.getCell(y));

						//for DDL handling
						DataType finaltype = DataTypeFactory.mergeDataType(
								DataTypeFactory.makeDataType(value),
								column.getDataType());
						column.setDataType(finaltype);

						data.add(value);
					}
					catch(Exception e) {
						//these errors are common enough to only debug log them
						logger.debug("error at cell {}:{}", BuilderUtil
								.columnNumberToExcelColumnName(x), y);

						//add empty cell data
						data.add(EMPTY_CELL_DATA);
					}
				}
			}

			tables.add(table);
		}
		
		return tables;
	}
	
	public static void write(File fin) throws Exception {
		write(fin, BuilderUtil.fileAsSqlFile(fin));
	}

	public static void write(File fin, File out) throws Exception {
		write(fin, out, null);
	}

	/**
	 * Write the sql definition file (ddl).  Having separate {@code analyze}
	 * and {@code write} methods is a bit wasteful, but makes for a cleaner API.
	 * @param in file to handle
	 * @param out file to create
	 * @param filter list of sheets to ignore 
	 * @throws IOException
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 */
	public static void write(File in, File out, List<String>filter) throws Exception {
		//write the current sheet to a file as well
		try(CsvListWriter w = new CsvListWriter(new FileWriter(out), CsvPreference.EXCEL_PREFERENCE)) {
			
			Workbook workbook = WorkbookFactory.create(in);
			for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
				Sheet sheet = workbook.getSheetAt(i);

				//make sure we skip the table of contents file
				if(isFiltered(sheet, filter))
					continue;

				//some spreadsheets might not have data
				if(containsRowData(sheet) == null)
					continue;

				logger.debug("inspecting {}", sheet.getSheetName());
				for(int j = 0, cols = 0; j < sheet.getLastRowNum(); j++) {
					Row row = sheet.getRow(j);
					List<Object>data = new LinkedList<>();
					
					int k = 0;
					while(row.iterator().hasNext() && k < cols) {
						try {
							String value = getStringValue(row.getCell(k));
							data.add(value);
						}
						catch(Exception e) {
							//these errors are common enough to only debug log them
							logger.debug("error at cell {}:{}", BuilderUtil
									.columnNumberToExcelColumnName(j), k);

							//add empty cell data
							data.add(EMPTY_CELL_DATA);
						}
					}

					if(j == 0) //set the total based on the first row
						cols = data.size();
				}
			}
		}
	}

	/**
	 * @param sheet
	 * @return the first row if true
	 */
	private static Row containsRowData(Sheet sheet) {
		return (sheet.getRow(0) == null) ? null : sheet.getRow(0);
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

	private static ITable initializeTable(Sheet sheet, File fin) {
		//create a description
		String description = String.format(DLL_CREATED_FROM
				, fin.getAbsolutePath(), sheet.getSheetName());

		//produce a retunable table
		ITable t = new Table(sheet.getSheetName(), fin);
		t.setDescription(description);
		return t;
	}

	private static boolean isFiltered(Sheet sheet, List<String> filter) {
		if(filter == null)
			return false;

		if(!filter.contains(sheet.getSheetName())) {
			logger.debug("ignoring {}", sheet.getSheetName());
			return true;
		}
		return false;
	}

	private static Map<String, IColumn> createColumns(Row firstrow) {
		Map<String, IColumn>c = new HashMap<>();
		firstrow.forEach(r -> {
			String name = getStringValue(r);
			c.put(name, new Column(name));
		});
		return c;
	}
}