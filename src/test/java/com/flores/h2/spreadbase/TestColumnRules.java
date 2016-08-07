package com.flores.h2.spreadbase;

import static org.junit.Assert.*;
import static com.flores.h2.spreadbase.analyze.WorkbookAnalyzer.*;

import org.junit.Test;

import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.util.BuilderUtil;

/**
 * 
 * @author Jason
 */
public class TestColumnRules {

	@Test
	public void testColumnCreation() {
		//create String
		IColumn testcol = makeColumn("xxxxx");
		assertNotNull(testcol);
		assertTrue(testcol.getType().getTypeName().equals(String.class.getTypeName()));
		assertEquals(5, testcol.getPrecision());
		assertEquals(BuilderUtil.UNSET_INT, testcol.getScale());
		
		//create Double
		testcol = makeColumn("1000.23");
		assertNotNull(testcol);
		assertTrue(testcol.getType().getTypeName().equals(Double.class.getTypeName()));
		assertEquals(4, testcol.getPrecision());
		assertEquals(2, testcol.getScale());
		
		//create Integer
		testcol = makeColumn("1000");
		assertNotNull(testcol);
		assertTrue(testcol.getType().getTypeName().equals(Integer.class.getTypeName()));
		assertEquals(1000, testcol.getPrecision());
		assertEquals(BuilderUtil.UNSET_INT, testcol.getScale());
	}

	@Test
	public void testResizeString() {
		String testval = "xxxxx";	//precision should be adjusted
		IColumn testcol = makeColumn(testval);
		assertEquals(5, testcol.getPrecision());
		assertEquals(10, adjustColumn(
				testcol, "yyyyyyyyyy").getPrecision());
		
		testval = "xxx";	//precision should not be adjusted
		testcol = makeColumn(testval);
		assertEquals(3, testcol.getPrecision());
		assertEquals(3, adjustColumn(
				testcol, "y").getPrecision());
	}
	
	@Test
	public void testNumberResize() {
		String testval = "10";	//precision should be adjusted
		IColumn testcol = makeColumn(testval);
		assertEquals(10, testcol.getPrecision());
		assertEquals(100, adjustColumn(
				testcol, "100").getPrecision());
		
		testval = "125";	//precision should not be adjusted
		testcol = makeColumn(testval);
		assertEquals(125, testcol.getPrecision());
		assertEquals(125, adjustColumn(
				testcol, "120").getPrecision());

		testval = "58.27";	//precision should be adjusted
		testcol = makeColumn(testval);
		assertEquals(2, testcol.getPrecision());
		assertEquals(2, testcol.getScale());
		
		IColumn outCol = null;	//scale or precision should change
		outCol = adjustColumn(testcol, "262.333");
		assertEquals(3, outCol.getPrecision());
		assertEquals(3, outCol.getScale());
		
		//scale or precision should not change
		outCol = adjustColumn(testcol, "1.1");
		assertEquals(2, outCol.getPrecision());
		assertEquals(2, outCol.getScale());

		//only one attribute changes
		outCol = adjustColumn(testcol, "224565.22");
		assertEquals(6, outCol.getPrecision());
		assertEquals(2, outCol.getScale());
	}
	
	@Test
	public void testTypeChange() {
		String testval = "1";	//precision should be adjusted
		IColumn testcol = makeColumn(testval);
		assertTrue(testcol.getType().getTypeName().equals(Integer.class.getTypeName()));
		assertEquals(1, testcol.getPrecision());
		assertEquals(BuilderUtil.UNSET_INT, testcol.getScale());
		
		testcol = makeColumn("22.2");
		
	}
}