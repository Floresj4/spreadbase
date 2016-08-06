package com.flores.h2.spreadbase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.flores.h2.spreadbase.analyze.WorkbookAnalyzer;
import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.impl.Column;


/**
 * Unit test for simple App.
 */
public class TestColumnRules {

	@Test
	public void testResizeString() {
		IColumn sColumn = new Column(String.class, 4);
		
		IColumn nColumn = WorkbookAnalyzer.adjustColumn(sColumn, "xxxxx");
		assertEquals(5, sColumn.getPrecision());
	}
}