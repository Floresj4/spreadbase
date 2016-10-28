package com.flores.h2.spreadbase;

import static com.flores.h2.spreadbase.DataTypeFactory.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.flores.h2.spreadbase.model.impl.DataType;
import com.flores.h2.spreadbase.util.SpreadbaseUtil;

/**
 * 
 * @author Jason
 */
public class TestDataTypeRules {

	/**
	 * Simple, up-front, creation testing...
	 */
	@Test
	public void testDataTypeCreation() {
		//create String
		DataType testDt = makeDataType("xxxxx");
		assertNotNull(testDt);
		assertTrue(testDt.getTypeName().equals(String.class.getTypeName()));
		assertEquals(5, testDt.getPrecision());
		assertEquals(SpreadbaseUtil.UNSET_INT, testDt.getScale());
		
		//create Double
		testDt = makeDataType("1000.23");
		assertNotNull(testDt);
		assertTrue(testDt.getTypeName().equals(Double.class.getTypeName()));
		assertEquals(1000, testDt.getPrecision());
		assertEquals(23, testDt.getScale());
		
		//create Integer
		testDt = makeDataType("1000");
		assertNotNull(testDt);
		assertTrue(testDt.getTypeName().equals(Integer.class.getTypeName()));
		assertEquals(1000, testDt.getPrecision());
		assertEquals(SpreadbaseUtil.UNSET_INT, testDt.getScale());
	}

	@Test
	public void testResizeString() {
		//precision should be adjusted
		DataType testDt = makeDataType("xxxxx");
		assertEquals(10, (testDt = mergeDataType(testDt, "yyyyyyyyyy"))
				.getPrecision());

		//precision should not be adjusted
		assertEquals(10, mergeDataType(testDt, makeDataType("y"))
				.getPrecision());
	}
	
	@Test
	public void testNumberResize() {
		//the precision should be adjusted
		DataType testDt = makeDataType("10");
		DataType adjcol = null;

		assertEquals(10, testDt.getPrecision());
		assertEquals(100, mergeDataType(testDt, makeDataType("100"))
				.getPrecision());

		//precision should not be adjusted
		testDt = makeDataType("125");
		assertEquals(125, mergeDataType(testDt, "120")
				.getPrecision());

		//precision should be adjusted
		testDt = makeDataType("58.27");
		assertEquals(58, testDt.getPrecision());
		assertEquals(27, testDt.getScale());

		adjcol = mergeDataType(testDt, "262.333");
		assertEquals(262, adjcol.getPrecision());
		assertEquals(333, adjcol.getScale());

		//scale or precision should not change
		adjcol = mergeDataType(adjcol, "1.1");
		assertEquals(262, adjcol.getPrecision());
		assertEquals(333, adjcol.getScale());

		//only one attribute changes
		adjcol = mergeDataType(adjcol, "224565.22");
		assertEquals(224565, adjcol.getPrecision());
		assertEquals(333, adjcol.getScale());
	}

	/**
	 * Integer -> Double
	 */
	@Test
	public void typeChangeIntegerDouble() {
		DataType testDt, adjcol;

		testDt = makeDataType("1");
		assertTrue(testDt.getTypeName().equals(Integer.class.getTypeName()));
		assertEquals(1, testDt.getPrecision());
		assertEquals(SpreadbaseUtil.UNSET_INT, testDt.getScale());
		
		//type should change along with precision and scale
		adjcol = mergeDataType(testDt, "22.2");
		assertTrue(adjcol.getTypeName().equals(Double.class.getTypeName()));
		assertEquals(22, adjcol.getPrecision());
		assertEquals(2, adjcol.getScale());
	}

	/**
	 * Integer -> String 
	 */
	@Test
	public void typeChangeIntegerString() {
		DataType testDt;
		
		testDt = makeDataType("24");
		assertTrue(testDt.getTypeName().equals(Integer.class.getTypeName()));
		assertTrue(mergeDataType(testDt, "Xxx-x:Y").getTypeName().equals(
				String.class.getTypeName()));
	}

	//TODO write tests for changes in scale or precision
	
	/**
	 * Double -> Integer
	 * This should not be possible, to narrow the scale
	 * of a DataType
	 */
	@Test
	public void typeChangeDoubleInteger() {
		DataType testDt = null;

		assertTrue((testDt = makeDataType("461.89")).getTypeName()
				.equals(Double.class.getTypeName()));

		assertTrue(mergeDataType(testDt, "6").getTypeName()
				.equals(Double.class.getTypeName()));
	}
}