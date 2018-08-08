package com.zeitheron.solarfluxreborn.utility;

import com.zeitheron.solarfluxreborn.reference.InfoSFR;

import net.minecraft.client.resources.I18n;

public final class Lang
{
	public static final String MOD_PREFIX = "info." + InfoSFR.MOD_ID.toLowerCase() + ".";
	
	private Lang()
	{
	}
	
	public static String localise(String text)
	{
		return localise(text, true);
	}
	
	public static String localise(String text, boolean appendModPrefix)
	{
		if(appendModPrefix)
			text = MOD_PREFIX + text;
		return I18n.format(text);
	}
}
