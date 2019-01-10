package com.zeitheron.solarflux.api.compat;

import java.util.List;

import com.zeitheron.solarflux.api.SolarInfo;
import com.zeitheron.solarflux.utils.charging.modules.IChargeModule;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public interface ISolarFluxCompat extends IChargeModule
{
	default void registerSolarInfos(List<SolarInfo> panels)
	{
	}
	
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