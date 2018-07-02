package com.zeitheron.solarfluxreborn.core;

import java.util.Map;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion(MinecraftForge.MC_VERSION)
public class SFRCore implements IFMLLoadingPlugin
{
	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] { SFRSerializedTransformer.class.getName() };
	}
	
	@Override
	public String getAccessTransformerClass()
	{
		return SFRSerializedTransformer.class.getName();
	}
	
	@Override
	public String getModContainerClass()
	{
		return SFRModC.class.getName();
	}
	
	@Override
	public String getSetupClass()
	{
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> arg0)
	{
	}
}