package com.flores.h2.spreadbase;

/**
 * Test the conversion between datatypes.  Strings
 * will always have priority in order to successfully
 * create a working DDL.  If data comes in as {@code '8' -> tinyint}
 * and then {@code 'eight' -> nvarchar(5)} should be the new type with
 * adjusted precision and scale.
 * 
 * @author Jason
 *
 */
public class TestConversions {

}
