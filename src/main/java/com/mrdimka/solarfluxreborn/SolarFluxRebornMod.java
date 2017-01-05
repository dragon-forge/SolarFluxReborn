package com.mrdimka.solarfluxreborn;

import java.io.File;

import com.mrdimka.solarfluxreborn.config.DraconicEvolutionConfigs;
import com.mrdimka.solarfluxreborn.config.ModConfiguration;
import com.mrdimka.solarfluxreborn.err.NoSolarsRegisteredException;
import com.mrdimka.solarfluxreborn.gui.GuiHandler;
import com.mrdimka.solarfluxreborn.init.ModBlocks;
import com.mrdimka.solarfluxreborn.init.ModItems;
import com.mrdimka.solarfluxreborn.init.RecipeIO;
import com.mrdimka.solarfluxreborn.intr.tesla.TeslaAPI;
import com.mrdimka.solarfluxreborn.proxy.CommonProxy;
import com.mrdimka.solarfluxreborn.reference.Reference;
import com.mrdimka.solarfluxreborn.te.DraconicSolarPanelTileEntity;
import com.mrdimka.solarfluxreborn.te.SolarPanelTileEntity;
import com.mrdimka.solarfluxreborn.te.cable.TileCustomCable;
import com.mrdimka.solarfluxreborn.utility.SFRLog;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY, dependencies = "required-after:hammercore")
public class SolarFluxRebornMod
{
	@Mod.Instance(Reference.MOD_ID)
	public static SolarFluxRebornMod instance;
	
	@SidedProxy(clientSide = "com.mrdimka.solarfluxreborn.proxy.ClientProxy", serverSide = "com.mrdimka.solarfluxreborn.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static File cfgFolder;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent pEvent)
	{
		String cfg = pEvent.getSuggestedConfigurationFile().getAbsolutePath();
		cfg = cfg.substring(0, cfg.length() - 3);
		cfgFolder = new File(cfg);
		cfgFolder.mkdirs();
		File main_cfg = new File(cfgFolder, "main.cfg");
		File draconicevolution = new File(cfgFolder, "DraconicEvolution.cfg");
		File version_file = new File(cfgFolder, "version.dat");
		ModConfiguration.initialize(main_cfg, version_file);
		DraconicEvolutionConfigs.initialize(draconicevolution);
		
		GameRegistry.registerTileEntity(SolarPanelTileEntity.class, Reference.MOD_ID + ":solar");
		GameRegistry.registerTileEntity(DraconicSolarPanelTileEntity.class, Reference.MOD_ID + ":draconicsolar");
		GameRegistry.registerTileEntity(TileCustomCable.class, Reference.MOD_ID + ":cable_custom");
		ModBlocks.initialize();
		
		if(ModBlocks.getSolarPanels().isEmpty())
		{
			try { main_cfg.delete(); } catch(Throwable err) {}
			throw new NoSolarsRegisteredException("No solar panels was registered in config file.\nSolarFluxReborn configs were removed. If not, please remove file \"" + main_cfg.getAbsolutePath() + "\" manually.\nTry restarting game.", false);
		}
		
		ModItems.initialize();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		RecipeIO.reload();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent pEvent)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		FMLInterModComms.sendMessage("Waila", "register", "com.mrdimka.solarfluxreborn.intr.waila.WailaIntegrar.registerWAIA");
		proxy.init();
	}
	
	@EventHandler
	public void loadWorld(FMLServerStartingEvent e)
	{
		SFRLog.info("Loading TeslaAPI...");
		int classesLoaded = TeslaAPI.refreshTeslaClassData();
		SFRLog.info("TeslaAPI loaded " + classesLoaded + "/" + TeslaAPI.allClasses.size() + " required classes.");
	}
	
	@EventHandler
	public void printMessage(FMLServerStartedEvent e)
	{
		if(ModConfiguration.willNotify)
		{
			SFRLog.bigWarning(TextFormatting.RED + "WARNING: Your configs have been replaced.");
			ModConfiguration.updateNotification(false);
		}
	}
}