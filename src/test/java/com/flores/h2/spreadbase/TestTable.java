package com.flores.h2.spreadbase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.flores.h2.spreadbase.model.IColumn;
import com.flores.h2.spreadbase.model.ITable;
import com.flores.h2.spreadbase.model.impl.Column;
import com.flores.h2.spreadbase.model.impl.Table;

public class TestTable {
	
	@Test
	public void testCreate() {
		ITable t = new Table("table1", "testing table...", new String[]{"A", "B"});
		assertNotNull(t.get(0));
		
		//test the get by index functionality
		IColumn c1 = t.get(0);
		assertEquals(Column.class, c1.getClass());
		assertEquals("A", c1.getName());
		
		IColumn c2 = t.get("B");
		assertEquals("B", c2.getName());
	}
}
