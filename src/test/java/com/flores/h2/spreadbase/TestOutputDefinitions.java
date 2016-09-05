package com.flores.h2.spreadbase;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

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
	private static final String OUTPUT_DIR =  "./target/test-output";
	
	private static File in;
	private static File out;

	@BeforeClass
	public static void init() {
		LoggedTest.init();
		
		in = new File(TEST_FILE);
		out = new File(OUTPUT_DIR, BuilderUtil.fileAsSqlFile(in)
				.getName());

		new File(OUTPUT_DIR).mkdir();
	}

	@Test
	public void testH2DataDefintion() throws Exception {
		List<ITable> tables = WorkbookAnalyzer.analyze(in);
		TableDefinitionWriter w = new TableDefinitionWriter(out, new DataDefinitionBuilder());

		for(ITable t : tables)
			w.write(t);
		
		w.close();
	}
}
