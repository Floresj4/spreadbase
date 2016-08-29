package com.flores.h2.spreadbase.model;

import com.flores.h2.spreadbase.model.impl.DataType;


/**
 * @author Jason Flores
 */
public interface IColumn extends IDescribable {
	public DataType getDataType();
	public void setDataType(DataType type);
}