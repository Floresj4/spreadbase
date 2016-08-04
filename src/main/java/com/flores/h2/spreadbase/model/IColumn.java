package com.flores.h2.spreadbase.model;

import com.flores.h2.spreadbase.model.impl.Accuracy;

/**
 * 
 * @author Jason Flores
 */
public interface IColumn extends IDescribable {
	public Class<?> getType();
	public Accuracy getAccuracy();
	
	public void setAccuracy(Accuracy accuracy);	
	public void setType(Class<?> clazz);
}
