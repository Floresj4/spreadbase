package com.flores.h2.spreadbase.analyze;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

	private static final TypeHierarchy hr = new TypeHierarchy();

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
			String currentFilename = BuilderUtil.sheetNameToFilename(sheet);
			logger.debug("Extracting {}", sheet.getSheetName());

			//write the current sheet to a file as well
			try(CsvListWriter w = getListWriter(fin, currentFilename)) {
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
							DataType _new = makeDataType(value);
							DataType _curr = column.getTypeMap().get(_new.getClass());
							DataType finaltype = mergeDataType(_new, _curr);
							column.put(finaltype);

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

					//only write if data exists
					if(data.size() != 0)
						w.write(data);
				}

				tables.add(table);
			}
		}
		
		return tables;
	}
	/**
	 * @param sheet
	 * @return the first row if true
	 */
	private static Row containsRowData(Sheet sheet) {
		Row firstRow = sheet.getRow(0);
		if(firstRow == null)
			return firstRow;
		return null;
	}

	private static CsvListWriter getListWriter(File fin, String currentFilename) throws IOException {
		return new CsvListWriter(
				new FileWriter(new File(fin.getParent(), currentFilename.toLowerCase()))
					, CsvPreference.EXCEL_PREFERENCE);
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
		String description = String.format(CREATED_FROM, fin.getAbsolutePath(), sheet.getSheetName());
		ITable t = new Table(sheet.getSheetName(), fin);
		t.setDescription(description);
		return t;
	}

	private static boolean isFiltered(Sheet sheet, List<String> filter) {
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

	/**
	 * Make a DataType!  Infer type, scale, and precision based on
	 * the {@code dataValue} param.  Default to a string type
	 * @param dataValue to create
	 * @return a non-null DataType
	 */
	public static DataType makeDataType(String dataValue) {
		Class<?> type = null;
		int precision = BuilderUtil.UNSET_INT;
		int scale = BuilderUtil.UNSET_INT;

		try {	//determine type
			Double d = Double.parseDouble(dataValue);

				//parse for precision and scale
			String temp[] = d.toString().split("\\.");
			boolean hasScale = d != Math.floor(d);
			if(hasScale) {
				precision = Integer.parseInt(temp[0]);
				scale = Integer.parseInt(temp[1]);
			} else { //use the actual data value
				precision = Integer.parseInt(dataValue);
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

		return new DataType(type, precision, scale);
	}

	/**
	 * Merge directly from an incoming value
	 * @param _new
	 * @param dataValue
	 * @return
	 */
	public static DataType mergeDataType(DataType _new, String dataValue) {
		return mergeDataType(_new, makeDataType(dataValue));
	}

	/**
	 * These values should never be null by design of {@link #makeDataType(String)}
	 * Type isn't tested because of the string default strategy in 
	 * {@code #makeDataType(String)}. 
	 * 
	 * @param _new the most recently created from a data cell.
	 * @param _curr the currently existing, if the type has been encountered
	 * already, otherwise pass in null
	 * @return a non-null datatype
	 */
	public static DataType mergeDataType(DataType _new, DataType _curr) {
		if(_curr == null)
			return _new;

		int precision = _new.getPrecision() >= _curr.getPrecision()
				? _new.getPrecision() : _curr.getPrecision();
		
		int scale = _new.getScale() >= _curr.getScale()
				? _new.getScale() : _curr.getScale();

		//rank indices
		int idx = hr.get(_new.getType()) <= hr.get(_curr.getType()) 
				? hr.get(_new.getType()) 
				: hr.get(_curr.getType());

		return new DataType(hr.classByIndex(idx), precision, scale);
	}
}