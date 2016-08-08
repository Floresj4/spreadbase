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
		assertEquals(58, testcol.getPrecision());
		assertEquals(27, testcol.getScale());
		
		IColumn outcol = null;	//scale or precision should change
		outcol = adjustColumn(testcol, "262.333");
		assertEquals(262, outcol.getPrecision());
		assertEquals(333, outcol.getScale());
		
		//scale or precision should not change
		outcol = adjustColumn(outcol, "1.1");
		assertEquals(262, outcol.getPrecision());
		assertEquals(333, outcol.getScale());

		//only one attribute changes
		outcol = adjustColumn(outcol, "224565.22");
		assertEquals(224565, outcol.getPrecision());
		assertEquals(333, outcol.getScale());
	}
	
	@Test
	public void testTypeChange() {
		String testval;
		IColumn testcol, outcol;

		{
			/**
			 * Integer -> Double
			 */
			testval = "1";
			testcol = makeColumn(testval);
			assertTrue(testcol.getType().getTypeName().equals(Integer.class.getTypeName()));
			assertEquals(1, testcol.getPrecision());
			assertEquals(BuilderUtil.UNSET_INT, testcol.getScale());
			
			//type should change along with precision and scale
			outcol = adjustColumn(testcol, "22.2");
			assertTrue(outcol.getType().getTypeName().equals(Double.class.getTypeName()));
			assertEquals(22, outcol.getPrecision());
			assertEquals(2, outcol.getScale());
		}
		
		{
			/**
			 * Double -> Integer
			 * This should not be possible, to narrow the scale
			 * of a column
			 */
			testval = "461.89";
			testcol = makeColumn(testval);
			assertTrue(testcol.getType().getTypeName().equals(Double.class.getTypeName()));
			assertEquals(461, testcol.getPrecision());
			assertEquals(89, testcol.getScale());
	
			outcol = adjustColumn(testcol, "6");
			assertTrue(outcol.getType().getTypeName().equals(Double.class.getTypeName()));
			assertEquals(461, outcol.getPrecision());
			assertEquals(89, outcol.getScale());
		}
	}
}