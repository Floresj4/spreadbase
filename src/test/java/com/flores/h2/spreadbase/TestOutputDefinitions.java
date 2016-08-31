package com.flores.h2.spreadbase;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import com.flores.h2.spreadbase.analyze.WorkbookAnalyzer;
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
		in = new File(TEST_FILE);
		out = new File(OUTPUT_DIR, BuilderUtil.fileAsSqlFile(in)
				.getName());

		new File(OUTPUT_DIR).mkdir();
	}

	@Test
	public void testH2DataDefintion() throws Exception {
		WorkbookAnalyzer.write(in, out);
	}
}
