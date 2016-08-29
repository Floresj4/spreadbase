package com.flores;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;

/**
 * Simple logging init
 * @author Jason
 */
public class LoggedTest {
	@BeforeClass
	public static void init() {
		PropertyConfigurator.configure("./deliverables/log4j.properties");
	}
}
