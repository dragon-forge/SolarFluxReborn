package tk.zeitheron.solarflux.utils;

public interface IVariableHandler
{
	long getVar(int id);
	
	void setVar(int id, long value);
	
	int getVarCount();
}