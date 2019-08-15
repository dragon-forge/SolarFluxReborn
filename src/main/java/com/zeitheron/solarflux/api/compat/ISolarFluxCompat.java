package com.zeitheron.solarflux.api.compat;

import java.util.List;

import com.zeitheron.solarflux.api.SolarInfo;
import com.zeitheron.solarflux.utils.charging.IPlayerInventoryLister;
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
	
	default ISolarFluxCompat merge(final ISolarFluxCompat other)
	{
		final ISolarFluxCompat dis = this;
		
		return new ISolarFluxCompat()
		{
			@Override
			public void registerSolarInfos(List<SolarInfo> panels)
			{
				dis.registerSolarInfos(panels);
				other.registerSolarInfos(panels);
			}
			
			@Override
			public void preInit()
			{
				dis.preInit();
				other.preInit();
			}
			
			@Override
			public void init()
			{
				dis.init();
				other.init();
			}
			
			@Override
			public void postInit()
			{
				dis.postInit();
				other.postInit();
			}
			
			@Override
			public void registerRecipes(IForgeRegistry<IRecipe> ifr)
			{
				dis.registerRecipes(ifr);
				other.registerRecipes(ifr);
			}
			
			@Override
			public void registerInvListers(List<IPlayerInventoryLister> listers)
			{
				dis.registerInvListers(listers);
				other.registerInvListers(listers);
			}
		};
	}
}