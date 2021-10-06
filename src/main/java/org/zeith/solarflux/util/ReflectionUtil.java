package org.zeith.solarflux.util;

import java.lang.reflect.Field;

public class ReflectionUtil
{
	public static Field lookupField(Class<?> clazz, Class<?> type)
	{
		Field ret = null;
		for(Field field : clazz.getDeclaredFields())
		{
			if(type.isAssignableFrom(field.getType()))
			{
				if(ret != null)
					return null;
				field.setAccessible(true);
				ret = field;
			}
		}
		return ret;
	}

	public static Class<?> getCaller()
	{
		try
		{
			return Class.forName(Thread.currentThread().getStackTrace()[1].getClassName());
		} catch(ClassNotFoundException e)
		{
			return null;
		}
	}
}