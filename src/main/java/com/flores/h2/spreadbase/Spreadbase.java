package com.flores.h2.spreadbase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.flores.h2.spreadbase.util.SpreadbaseUtil.*;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.flores.h2.spreadbase.io.TableDefinitionWriter;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.ITable;
import com.flores.h2.spreadbase.model.impl.Column;
import com.flores.h2.spreadbase.model.impl.DataType;
import com.flores.h2.spreadbase.model.impl.Table;
import com.flores.h2.spreadbase.model.impl.h2.DataDefinitionBuilder;

import com.flores.h2.spreadbase.util.TypeHierarchy;

/**
 * Worksheet conversion project.  Allow for the analysis
 * of sheets in a workbook and produce H2 compatible Data Definition Language (DDL)
 * or output sheets as comma-separated files.
 * </ul>
 * @author Jason Flores
 */
public class Spreadbase {

	public static final int UNSET_INT = Integer.MIN_VALUE;
	
	//used as table description, but also comment in .sql file
	private static final String DLL_CREATED_FROM = "created from %s:%s";
	private static final String CONN_STR = "%s;MV_STORE=FALSE;FILE_LOCK=NO";
	public static final String EMPTY_CELL_DATA = "";

	private static final Logger logger = LoggerFactory.getLogger(Spreadbase.class);

	public static List<ITable> analyze(final File fin) throws Exception {
		return analyze(fin, null);
	}

	/**
	 * Inspect the current file and produce a table instance for each worksheet
	 * @param fin file in
	 * @param filter sheet names to ignore
	 * @return a non-null list of tables
	 * @throws Exception
	 * @see ITable
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
			for(int x = 1; x <= sheet.getLastRowNum(); x++) {
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
						logger.debug("error at cell {}:{}",
								columnNumberToExcelColumnName(x), y);

						//add empty cell data
						data.add(EMPTY_CELL_DATA);
					}
				}
			}

			tables.add(table);
		}
		
		return tables;
	}

	/**
	 * The focal method of this class orchestrating calls to analyze, write, and producing
	 * the resulting H2 database file.
	 * @param in XLS|XLSX workbook
	 * @throws Exception
	 * 
	 * @see TableDefinitionWriter
	 */
	public static void asDataSource(File in) throws Exception {
		File sqlFile;
		List<ITable> tables;
		File outDir = in.getParentFile();
		
		try {
			tables = Spreadbase.analyze(in);
			Spreadbase.write(in, outDir);
		} catch(Exception e) {
			logger.error("determining data definition: {}", e.getMessage());
			throw e;
		}

		//write the definitions from analysis
		try(TableDefinitionWriter w = new TableDefinitionWriter(
				sqlFile = fileAsSqlFile(in), new DataDefinitionBuilder())){
			w.write(tables);
		} catch(IOException ioe) {
			logger.error("writing table definition: {}", ioe.getMessage());
			throw ioe;
		}

		//load driver & open connection
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection(
				"jdbc:h2:" + String.format(CONN_STR, 
						fileAsH2Db(in)), "sa", "");

		try {
			//run the output script of the table definition process
			RunScript.execute(conn, new InputStreamReader(new FileInputStream(sqlFile)));
		} catch(SQLException sqle) {
			logger.debug("executing {} script: {}", sqlFile.getName(), sqle.getMessage());
			throw sqle;
		}
	}
	
	public static void write(File fin) throws Exception {
		write(fin, null);
	}

	public static void write(File fin, File outDir) throws Exception {
		write(fin, outDir, null);
	}

	/**
	 * Write the sql definition file (ddl).  Having separate {@code analyze}
	 * and {@code write} methods is a bit wasteful, but makes for a cleaner API.
	 * @param in file to handle
	 * @param filter list of sheets to ignore 
	 * @throws IOException
	 * @throws InvalidFormatException 
	 * @throws EncryptedDocumentException 
	 */
	public static void write(File in, File outDir, List<String>filter) throws Exception {
		Workbook workbook = WorkbookFactory.create(in);
		for(int i = 0; i < workbook.getNumberOfSheets(); i++) {
			Sheet sheet = workbook.getSheetAt(i);

			//make sure we skip the table of contents file
			if(isFiltered(sheet, filter))
				continue;

			//some spreadsheets might not have data
			if(containsRowData(sheet) == null)
				continue;

			File csvOut = new File(outDir == null 
					? in.getParent() : outDir.getPath()
							, sheet.getSheetName() + ".csv");
			
			//write the current sheet to a file as well
			try(CsvListWriter w = new CsvListWriter(new FileWriter(csvOut), CsvPreference.EXCEL_PREFERENCE)) {
				logger.debug("inspecting {}", sheet.getSheetName());
				for(int j = 0; j <= sheet.getLastRowNum(); j++) {
					Row row = sheet.getRow(j);
					List<Object>data = new LinkedList<>();
					
					int k = 0;
					Iterator<Cell>itr = row.iterator();
					while(itr.hasNext()) {
						
						try { data.add(getStringValue(itr.next())); }
						catch(Exception e) {
							//these errors are common enough to only debug log them
							logger.debug("error at cell {}:{}", 
									columnNumberToExcelColumnName(j), k);

							//add empty cell data
							data.add(EMPTY_CELL_DATA);
						}
					}

					w.write(data);
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
				, fin.getPath(), sheet.getSheetName());

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
		Map<String, IColumn>c = new LinkedHashMap<>();
		firstrow.forEach(r -> {
			String name = getStringValue(r);
			c.put(name, new Column(name));
		});
		return c;
	}
}

class DataTypeFactory {
	/**
	 * current type hierarchy impl.
	 */
	private static final TypeHierarchy hr = new TypeHierarchy();

	/**
	 * Make a DataType!  Infer type, scale, and precision based on
	 * the {@code dataValue} param.  Default to a string type
	 * @param dataValue to create
	 * @return a non-null DataType
	 */
	public static DataType makeDataType(String dataValue) {
		Class<?> type = null;
		int precision = UNSET_INT;
		int scale = UNSET_INT;

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
	 * @param _curr
	 * @param dataValue
	 * @return
	 */
	public static DataType mergeDataType(DataType _curr, String dataValue) {
		return mergeDataType(makeDataType(dataValue), _curr);
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