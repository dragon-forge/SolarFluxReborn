package com.zeitheron.solarfluxreborn.config;

import java.io.File;

import com.zeitheron.solarfluxreborn.SolarFluxReborn;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

public class DraconicEvolutionConfigs
{
	private static Configuration cfg;
	
	public static boolean draconicSolar, chaoticSolar;
	public static boolean canIntegrate = false;
	
	public static void initialize(File cfgFile)
	{
		canIntegrate = Loader.isModLoaded("draconicevolution");
		if(cfg == null)
			cfg = new Configuration(cfgFile);
		loadConfigs();
	}
	
	public static void loadConfigs()
	{
		if(!Loader.isModLoaded("draconicevolution"))
			return;
		File cfgf = new File(SolarFluxReborn.cfgFolder, "DraconicEvolution.cfg");
		
		if(cfg == null)
			initialize(cfgf);
		
		draconicSolar = cfg.getBoolean("Draconic Solar", "solars", true, "Whether or not this Solar Panel should be added to the game.");
		chaoticSolar = cfg.getBoolean("Chaotic Solar", "solars", true, "Whether or not this Solar Panel should be added to the game.");
		
		if(cfg.hasChanged())
			cfg.save();
	}
}