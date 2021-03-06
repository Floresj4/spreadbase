package com.flores.h2.spreadbase;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.flores.LoggedTest;
import com.flores.h2.spreadbase.io.TableDefinitionWriter;
import com.flores.h2.spreadbase.model.ITable;
import com.flores.h2.spreadbase.model.impl.h2.DataDefinitionBuilder;
import com.flores.h2.spreadbase.util.SpreadbaseUtil;

/**
 * @author Jason
 */
public class TestOutputDefinitions {

	private static final String TEST_FILE = "./src/test/resources/test.xlsx";
	private static final String TEST_CONN_STR_DB = "%s;MV_STORE=FALSE;FILE_LOCK=NO";
	private static final String OUTPUT_DIR =  "./target/test-output";
	
	private static File in;
	private static File sqlOut;

	@BeforeClass
	public static void init() {
		LoggedTest.init();
		
		in = new File(TEST_FILE);
		sqlOut = new File(OUTPUT_DIR, 
			SpreadbaseUtil.fileAsSqlFile(in).getName());

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
		
		Spreadbase.asDataSource(in);
			
		//load driver & open connection
		Class.forName("org.h2.Driver");
		try(Connection conn = DriverManager.getConnection(
				"jdbc:h2:" + String.format(TEST_CONN_STR_DB, SpreadbaseUtil.fileAsH2Db(in))
				, "sa", "")) {

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
