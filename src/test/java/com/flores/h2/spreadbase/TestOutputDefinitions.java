package com.flores.h2.spreadbase;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import com.flores.h2.spreadbase.analyze.WorkbookAnalyzer;

/**
 * @author Jason
 */
public class TestOutputDefinitions {

	private static final String TEST_FILE = "./deliverables/test.xlsx";

	@BeforeClass
	public static void directOutput() {
		
	}

	@Test
	public void testH2DataDefintion() throws Exception {
		WorkbookAnalyzer.write(new File(TEST_FILE));
	}
}
