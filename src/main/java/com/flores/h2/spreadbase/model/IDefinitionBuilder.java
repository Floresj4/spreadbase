package com.flores.h2.spreadbase.model;

import com.flores.h2.spreadbase.exception.UnsupportedTypeException;
import com.flores.h2.spreadbase.model.impl.AbstractDataDefinition;

/**
 * Base interface for expansion
 * @author Jason
 */
public interface IDefinitionBuilder {
	public AbstractDataDefinition createDataDefinition(IColumn column) throws UnsupportedTypeException;
}