package com.zeitheron.solarflux.api;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class SolarFluxAPI
{
	public static final CreativeTabs tab = null;
	public static final IForgeRegistry<SolarInfo> SOLAR_PANELS = null;
	public static final Consumer<Item> registerItem = null;
	public static final Consumer<Item> renderRenderer = null;
	
	public static final Set<String> resourceDomains = new HashSet<>();
	static
	{
		resourceDomains.add("solarflux");
	}
}