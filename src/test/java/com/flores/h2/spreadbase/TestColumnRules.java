package com.flores.h2.spreadbase;

import static org.junit.Assert.*;

import org.junit.Test;

import com.flores.h2.spreadbase.analyze.WorkbookAnalyzer;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.Column;
import com.flores.h2.spreadbase.util.BuilderUtil;

/**
 * 
 * @author Jason
 */
public class TestColumnRules {

	@Test
	public void testColumnCreation() {
		//create String
		IColumn column = WorkbookAnalyzer.makeColumn("xxxxx");
		assertNotNull(column);
		assertTrue(column.getType().getTypeName().equals(String.class.getTypeName()));
		assertEquals(5, column.getPrecision());
		assertEquals(BuilderUtil.UNSET_INT, column.getScale());
		
		//create Double
		column = WorkbookAnalyzer.makeColumn("1000.23");
		assertNotNull(column);
		assertTrue(column.getType().getTypeName().equals(Double.class.getTypeName()));
		assertEquals(4, column.getPrecision());
		assertEquals(2, column.getScale());
		
		//create Integer
		column = WorkbookAnalyzer.makeColumn("1000");
		assertNotNull(column);
		assertTrue(column.getType().getTypeName().equals(Integer.class.getTypeName()));
		assertEquals(1000, column.getPrecision());
		assertEquals(BuilderUtil.UNSET_INT, column.getScale());
	}

	@Test
	public void testResizeString() {
		String testval = "xxxxx";
		IColumn col = new Column(String.class, 4);
		assertEquals(testval.length(), WorkbookAnalyzer.adjustColumn(col, testval)
				.getPrecision());
	}
}