package com.flores.h2.spreadbase;

import static com.flores.h2.spreadbase.analyze.DataTypeFactory.makeDataType;
import static com.flores.h2.spreadbase.model.h2.DataDefinitionFactory.createDataDefinition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.flores.LoggedTest;
import com.flores.h2.spreadbase.exception.UnsupportedTypeException;
import com.flores.h2.spreadbase.model.h2.Int;
import com.flores.h2.spreadbase.model.h2.NVarchar;
import com.flores.h2.spreadbase.model.h2.SmallInt;
import com.flores.h2.spreadbase.model.h2.TinyInt;
import com.flores.h2.spreadbase.model.impl.Column;
import com.flores.h2.spreadbase.model.impl.DataType;

/**
 * Test the creation of DataType definitions
 * @author Jason
 */
public class TestDataDefinitions extends LoggedTest {
	/**
	 * Simply test non-null
	 * @throws UnsupportedTypeException 
	 */
	@Test
	public void testCreation() throws UnsupportedTypeException {
		assertNotNull(createDataDefinition(
				new MockColumn(makeDataType("test"))));
	}
	
	@Test
	public void testSupportedTypes() throws UnsupportedTypeException {
		Column c = new MockColumn(makeDataType("test"));
		
		//nvarchar
		assertEquals(NVarchar.class.getSimpleName(), 
				createDataDefinition(c).getClass().getSimpleName());
		
		//tinyint
		c = new MockColumn(makeDataType("6"));
		assertEquals(TinyInt.class.getSimpleName(),
				createDataDefinition(c).getClass().getSimpleName());
		
		//smallint
		c = new MockColumn(makeDataType("240"));
		assertEquals(SmallInt.class.getSimpleName(),
				createDataDefinition(c).getClass().getSimpleName());
		
		//int
		c = new MockColumn(makeDataType("33000"));
		assertEquals(Int.class.getSimpleName(),
				createDataDefinition(c).getClass().getSimpleName());
	}
	
	/**
	 * Use this class to avoid adding new constructors.  Allows
	 * the creation of a column directly from a DataType
	 * @author Jason
	 * @see DataType
	 */
	class MockColumn extends Column {
		public MockColumn(DataType t) { 
			super(""); 
			setDataType(t);
		}
	}
}
