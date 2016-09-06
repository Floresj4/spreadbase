package com.flores.h2.spreadbase;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import org.h2.tools.RunScript;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flores.LoggedTest;
import com.flores.h2.spreadbase.analyze.WorkbookAnalyzer;
import com.flores.h2.spreadbase.io.TableDefinitionWriter;
import com.flores.h2.spreadbase.model.ITable;
import com.flores.h2.spreadbase.model.impl.h2.DataDefinitionBuilder;
import com.flores.h2.spreadbase.util.BuilderUtil;

/**
 * @author Jason
 */
public class TestOutputDefinitions {

	private static final String TEST_FILE = "./deliverables/test/test.xlsx";
	private static final String TEST_CONN_STR_DB = "./target/test-db/test;MV_STORE=FALSE;FILE_LOCK=NO";
	private static final String OUTPUT_DIR =  "./target/test-output";
	
	private static File in;
	private static File sqlOut;

	private static final Logger logger = LoggerFactory.getLogger(TestOutputDefinitions.class);
	
	@BeforeClass
	public static void init() {
		LoggedTest.init();
		
		in = new File(TEST_FILE);
		sqlOut = new File(OUTPUT_DIR, BuilderUtil.fileAsSqlFile(in)
				.getName());

		new File(OUTPUT_DIR).mkdir();
	}

	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testH2DataDefintion() throws Exception {
		List<ITable> tables = WorkbookAnalyzer.analyze(in);
		TableDefinitionWriter w = new TableDefinitionWriter(sqlOut, new DataDefinitionBuilder());

		for(ITable t : tables)
			w.write(t);
		
		w.close();
	}
	
	@Test
	public void testLoad() throws Exception {
		logger.debug("determining data definitions...");

		File outDir = new File(OUTPUT_DIR);
		List<ITable> tables = WorkbookAnalyzer.analyze(in);
		WorkbookAnalyzer.write(in, outDir);
		
		TableDefinitionWriter w = new TableDefinitionWriter(sqlOut, new DataDefinitionBuilder());
		w.write(tables);
		w.close();

		//load driver
		Class.forName("org.h2.Driver");
		Connection conn = DriverManager.getConnection("jdbc:h2:" + TEST_CONN_STR_DB, "sa", "");
		
		//run the output script of the table definition process
		RunScript.execute(conn, new InputStreamReader(new FileInputStream(sqlOut)));
		
		conn.close();
	}
}
