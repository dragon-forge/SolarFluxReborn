package com.zeitheron.solarfluxreborn.config;

import java.io.File;

import com.zeitheron.solarfluxreborn.SolarFluxReborn;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

public class BlackHoleStorageConfigs
{
	private static Configuration cfg;
	
	public static boolean darkMatterSolar, solarcellDM, DMSolarRequiresTransformation, unpreparedSolarsNeedAT;
	public static boolean canIntegrate = false;
	
	public static void initialize(File cfgFile)
	{
		canIntegrate = Loader.isModLoaded("blackholestorage");
		if(cfg == null)
			cfg = new Configuration(cfgFile);
		loadConfigs();
	}
	
	public static void loadConfigs()
	{
		if(!Loader.isModLoaded("blackholestorage"))
			return;
		File cfgf = new File(SolarFluxReborn.cfgFolder, "BlackHoleStorage.cfg");
		
		if(cfg == null)
			initialize(cfgf);
		
		darkMatterSolar = cfg.getBoolean("Dark Matter Solar", "solars", true, "Whether or not this Solar Panel should be added to the game.");
		
		solarcellDM = cfg.getBoolean("Dark Matter Photovoltaic Cell", "items", true, "Whether or not this Photovoltaic Cell should be added to the game.");
		
		DMSolarRequiresTransformation = cfg.getBoolean("Dark Matter Solar Needs Transformation", "crafting", true, "Whether or not this Dark Matter Solar Panel should use atomic tranformation crafting system from Black Hole Storage.");
		
		unpreparedSolarsNeedAT = cfg.getBoolean("Unprepared Solars Need Transformation", "crafting", true, "Whether or not Unprepared Solar Panels should use atomic tranformation crafting system from Black Hole Storage.");
		
		if(cfg.hasChanged())
			cfg.save();
	}
}