package com.flores.h2.spreadbase.model;


/**
 * TODO: move IMeasurable to a separate IColumn derivative
 * @author Jason Flores
 */
public interface IColumn extends IDescribable, IMeasurable {
	public Class<?> getType();
	public void setType(Class<?> clazz);
}
