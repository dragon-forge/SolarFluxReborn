package com.zeitheron.solarflux.utils;

public interface IVariableHandler
{
	int getVar(int id);
	
	void setVar(int id, int value);
	
	int getVarCount();
}