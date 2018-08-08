package com.zeitheron.solarflux.utils;

public interface IFieldHandler
{
	int getField(int id);
	
	void setField(int id, int value);
	
	int getFieldCount();
}