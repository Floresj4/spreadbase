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
		assertEquals(1000, testcol.getPrecision());
		assertEquals(23, testcol.getScale());
		
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
		//the precision should be adjusted
		String testval = "10";
		IColumn testcol = makeColumn(testval);
		IColumn adjcol = null;

		assertEquals(10, testcol.getPrecision());
		assertEquals(100, adjustColumn(
				testcol, "100").getPrecision());

		//precision should not be adjusted
		testval = "125";	
		testcol = makeColumn(testval);
		assertEquals(125, testcol.getPrecision());
		assertEquals(125, adjustColumn(testcol, "120")
				.getPrecision());

		//precision should be adjusted
		testval = "58.27";
		testcol = makeColumn(testval);
		assertEquals(58, testcol.getPrecision());
		assertEquals(27, testcol.getScale());

		adjcol = adjustColumn(testcol, "262.333");
		assertEquals(262, adjcol.getPrecision());
		assertEquals(333, adjcol.getScale());

		//scale or precision should not change
		adjcol = adjustColumn(adjcol, "1.1");
		assertEquals(262, adjcol.getPrecision());
		assertEquals(333, adjcol.getScale());

		//only one attribute changes
		adjcol = adjustColumn(adjcol, "224565.22");
		assertEquals(224565, adjcol.getPrecision());
		assertEquals(333, adjcol.getScale());
	}

	/**
	 * Integer -> Double
	 */
	@Test
	public void typeChangeIntegerDouble() {
		IColumn testcol, adjcol;
		String testval;

		testval = "1";
		testcol = makeColumn(testval);
		assertTrue(testcol.getType().getTypeName().equals(Integer.class.getTypeName()));
		assertEquals(1, testcol.getPrecision());
		assertEquals(BuilderUtil.UNSET_INT, testcol.getScale());
		
		//type should change along with precision and scale
		adjcol = adjustColumn(testcol, "22.2");
		assertTrue(adjcol.getType().getTypeName().equals(Double.class.getTypeName()));
		assertEquals(22, adjcol.getPrecision());
		assertEquals(2, adjcol.getScale());
	}

	/**
	 * Integer -> String 
	 */
	@Test
	public void typeChangeIntegerString() {
		IColumn testcol, adjcol;
		String testval = "24";
		
		testcol = makeColumn(testval);
		assertTrue(testcol.getType().getTypeName().equals(Integer.class.getTypeName()));
		assertEquals(24, testcol.getPrecision());
		
		adjcol = adjustColumn(testcol, "Xxx-x:Y");
		assertTrue(adjcol.getType().getTypeName().equals(String.class.getTypeName()));
		assertEquals(10, adjcol.getPrecision());
	}
	
	/**
	 * Double -> Integer
	 * This should not be possible, to narrow the scale
	 * of a column
	 */
	@Test
	public void typeChangeDoubleInteger() {
		IColumn testcol, adjcol;
		String testval;

		testval = "461.89";
		testcol = makeColumn(testval);
		assertTrue(testcol.getType().getTypeName().equals(Double.class.getTypeName()));
		assertEquals(461, testcol.getPrecision());
		assertEquals(89, testcol.getScale());

		adjcol = adjustColumn(testcol, "6");
		assertTrue(adjcol.getType().getTypeName().equals(Double.class.getTypeName()));
		assertEquals(461, adjcol.getPrecision());
		assertEquals(89, adjcol.getScale());
	}
}