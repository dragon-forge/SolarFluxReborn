package com.zeitheron.solarflux.api.compat;

import java.util.List;

import com.zeitheron.solarflux.api.SolarInfo;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public interface ISolarFluxCompat
{
	void registerSolarInfos(List<SolarInfo> panels);
	
	default void preInit()
	{
	}
	
	default void init()
	{
	}
	
	default void postInit()
	{
	}
	
	default void registerRecipes(IForgeRegistry<IRecipe> ifr)
	{
	}
}