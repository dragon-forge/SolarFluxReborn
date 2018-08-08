package com.zeitheron.solarflux.api;

import java.util.function.Consumer;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class SolarFluxAPI
{
	public static CreativeTabs tab;
	public static IForgeRegistry<SolarInfo> SOLAR_PANELS;
	public static Consumer<Item> renderRenderer;
}