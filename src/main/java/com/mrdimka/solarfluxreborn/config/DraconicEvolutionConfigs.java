package com.mrdimka.solarfluxreborn.config;

import java.io.File;

import com.mrdimka.solarfluxreborn.SolarFluxRebornMod;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

public class DraconicEvolutionConfigs
{
	private static Configuration cfg;
	
	public static boolean draconicSolar, chaoticSolar, useFusionForChaotic;
	public static boolean canIntegrate = false;
	
	public static void initialize(File cfgFile)
	{
		canIntegrate = Loader.isModLoaded("draconicevolution");
		if(cfg == null) cfg = new Configuration(cfgFile);
		loadConfigs();
	}
	
	public static void loadConfigs()
	{
		if(!Loader.isModLoaded("draconicevolution")) return;
		File cfgf = new File(SolarFluxRebornMod.cfgFolder, "DraconicEvolution.cfg");
		
		if(cfg == null) initialize(cfgf);
		
		draconicSolar = cfg.getBoolean("Draconic Solar", "solars", true, "Whether or not this Solar Panel should be added to the game.");
		chaoticSolar = cfg.getBoolean("Chaotic Solar", "solars", true, "Whether or not this Solar Panel should be added to the game.");
		useFusionForChaotic = cfg.getBoolean("Chaotic Solar Needs Fusion", "crafting", false, "Whether or not this Chaotic Solar Panel should use fusion crafting system from Draconic Evolution.");
		
		if(cfg.hasChanged()) cfg.save();
	}
}