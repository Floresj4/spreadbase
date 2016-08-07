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
import com.flores.h2.spreadbase.model.impl.Column;
import com.flores.h2.spreadbase.model.impl.Table;
import com.flores.h2.spreadbase.util.BuilderUtil;
import com.flores.h2.spreadbase.util.TypeHierarchy;

/**
 * 
 * @author Jason Flores
 */
public class WorkbookAnalyzer {

	public static final int UNSET_INT = Integer.MIN_VALUE;
	private static final String CREATED_FROM = "created from %s:%s";
	public static final String EMPTY_CELL_DATA = "";
	
	private static final Logger logger = LoggerFactory.getLogger(WorkbookAnalyzer.class);

	private static String currentFilename;

	protected static TypeHierarchy hierarchy = new TypeHierarchy();

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
							IColumn column = table.get(y);
							String columnName = column.getName();
							String value = getStringValue(row.getCell(y));

							adjustColumn(table.get(columnName), value);

							data.add(value);
						} catch(Exception e) {
							//these errors are common enough to only debug log them
							logger.debug("obtaining the {} value of cell {}:{}", currentFilename
									, BuilderUtil.columnNumberToExcelColumnName(x), y);
							//add empty cell data
							data.add(EMPTY_CELL_DATA);
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

	/**
	 * Adjust the column detail (type, precision, scale) of a column
	 * based on the value being passed in.
	 * @param column to adjust
	 * @param value to adjust based on
	 * @return a non-null IColumn implementation modified based on the
	 * current rules
	 */
	public static IColumn adjustColumn(IColumn column, String value) {
		IColumn newColumn = makeColumn(value);
		newColumn.setName(column.getName());
		newColumn.setDescription(column.getDescription());
		
		//adjust the attributes
		newColumn.setType(hierarchy.compare(column, newColumn));
		newColumn.setPrecision(column.getPrecision() > newColumn.getPrecision() 
				? column.getPrecision() : newColumn.getPrecision());
		newColumn.setScale(column.getScale() > newColumn.getScale()
				? column.getScale() : newColumn.getScale());

		return newColumn;
	}
	
	/**
	 * Make a column!  Infer type, scale, and precision based on
	 * the {@code dataValue} param
	 * @param dataValue to create
	 * @return
	 */
	public static IColumn makeColumn(String dataValue) {
		Class<?> type = null;
		int precision = BuilderUtil.UNSET_INT;
		int scale = BuilderUtil.UNSET_INT;
		
		try {	//determine type
			Double d = Double.parseDouble(dataValue);
			
				//parse for precision and scale
			String temp[] = d.toString().split("\\.");
			boolean hasScale = d != Math.floor(d);
			if(hasScale) {
				precision = temp[0].length();
				scale = temp[1].length();
			} else { //use the actual data value
				precision = Integer.valueOf(dataValue);
			}

			type = hasScale ? Double.class : Integer.class;
		}
		catch(NumberFormatException nfe) {
			//default to string
			type = String.class;
			precision = dataValue.length();
		}
		catch(NullPointerException npe) {
			//if dataValue is null, default string
			type = String.class;
			//leave precision and scale unset
		}
		
		return new Column(type, precision, scale);
	}
	
}