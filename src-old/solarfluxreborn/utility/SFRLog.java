package com.zeitheron.solarfluxreborn.utility;

import net.minecraftforge.fml.common.FMLLog;

import org.apache.logging.log4j.Level;

import com.zeitheron.solarfluxreborn.reference.InfoSFR;

public class SFRLog
{
	public static void log(Level level, String format, Object... data)
	{
		FMLLog.log(InfoSFR.MOD_ID, level, format, data);
	}
	
	public static void severe(String format, Object... data)
	{
		log(Level.ERROR, format, data);
	}
	
	public static void bigWarning(String format, Object... data)
	{
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		log(Level.WARN, "****************************************");
		log(Level.WARN, "* " + format, data);
		for(int i = 2; i < 8 && i < trace.length; ++i)
			log(Level.WARN, "*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
		log(Level.WARN, "****************************************");
	}
	
	public static void warning(String format, Object... data)
	{
		log(Level.WARN, format, data);
	}
	
	public static void info(String format, Object... data)
	{
		log(Level.INFO, format, data);
	}
	
	public static void fine(String format, Object... data)
	{
		log(Level.DEBUG, format, data);
	}
	
	public static void finer(String format, Object... data)
	{
		log(Level.TRACE, format, data);
	}
}