package com.zeitheron.solarfluxreborn.core;

import com.zeitheron.solarfluxreborn.reference.InfoSFR;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.ModMetadata;

public class SFRModC extends DummyModContainer
{
	public SFRModC()
	{
		super(new ModMetadata());
		getMetadata().modId = getModId();
		getMetadata().name = getName();
		getMetadata().version = getVersion();
	}
	
	@Override
	public String getModId()
	{
		return InfoSFR.MOD_ID + "core";
	}
	
	@Override
	public String getName()
	{
		return InfoSFR.MOD_NAME + ": Core";
	}
	
	@Override
	public String getVersion()
	{
		return InfoSFR.VERSION;
	}
}