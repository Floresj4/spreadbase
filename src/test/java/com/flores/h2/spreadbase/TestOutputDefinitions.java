package com.flores.h2.spreadbase;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.h2.tools.RunScript;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flores.LoggedTest;
import com.flores.h2.spreadbase.analyze.Spreadbase;
import com.flores.h2.spreadbase.io.TableDefinitionWriter;
import com.flores.h2.spreadbase.model.ITable;
import com.flores.h2.spreadbase.model.impl.h2.DataDefinitionBuilder;
import com.flores.h2.spreadbase.util.BuilderUtil;

/**
 * @author Jason
 */
public class TestOutputDefinitions {

	private static final String TEST_FILE = "./src/test/resources/test.xlsx";
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
		List<ITable> tables = Spreadbase.analyze(in);
		TableDefinitionWriter w = new TableDefinitionWriter(sqlOut, new DataDefinitionBuilder());

		for(ITable t : tables)
			w.write(t);
		
		w.close();
	}
	
	/**
	 * Test the translation from xlsx to database
	 * @throws Exception
	 */
	@Test
	public void testLoad() throws Exception {
		logger.debug("determining data definitions...");

		Connection conn = null;
		
		try {
			File outDir = new File(OUTPUT_DIR);
			List<ITable> tables = Spreadbase.analyze(in);
			Spreadbase.write(in, outDir);
	
			//write the definitions from analysis
			TableDefinitionWriter w = new TableDefinitionWriter(sqlOut, new DataDefinitionBuilder());
			w.write(tables);
			w.close();
	
			//load driver & open connection
			Class.forName("org.h2.Driver");
			conn = DriverManager.getConnection("jdbc:h2:" + TEST_CONN_STR_DB, "sa", "");
			
			//run the output script of the table definition process
			RunScript.execute(conn, new InputStreamReader(new FileInputStream(sqlOut)));
			
			String TEST_QUERY = "select * from employees e "
					+ "left join address a on e.id = a.id	"
					+ "left join scores s on s.id = e.id";
			
			System.out.println("=======================================================");
			
			//test database contents
			Statement stmnt = conn.createStatement();
			if(stmnt.execute(TEST_QUERY)) {
				ResultSet rs = stmnt.getResultSet();
				while(rs.next()) {
	
					//get data
					Employee e = new Employee(
						rs.getInt(1), 
						rs.getString(2),
						rs.getString(3),
						rs.getDouble(11));
					
					//test content
					assertTrue(e.fname != null && e.fname.length() > 0);
					assertTrue(e.lname != null && e.lname.length() > 0);
					assertTrue(e.s1 > 0);
					
					System.out.println(e);
				}
			} else fail();
			
			System.out.println("=======================================================");
		} finally {
			if(conn != null) conn.close();
		}
	}
	
	/**
	 * Simple pojo
	 * @author Jason
	 */
	class Employee {
		int id;
		String fname;
		String lname;
		double s1;
		public Employee(int id, String fname, String lname, double s1) {
			this.id = id;
			this.fname = fname;
			this.lname = lname;
			this.s1 = s1;
		}
		
		public String toString() {
			return String.format("  id = %d, fname: %s lname: %s score1 %4f", id, fname, lname, s1);
		}
	}
}
