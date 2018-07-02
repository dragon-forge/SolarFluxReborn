package com.zeitheron.solarfluxreborn;

import java.io.File;

import com.zeitheron.hammercore.client.witty.SplashModPool;
import com.zeitheron.solarfluxreborn.config.BlackHoleStorageConfigs;
import com.zeitheron.solarfluxreborn.config.DraconicEvolutionConfigs;
import com.zeitheron.solarfluxreborn.config.ModConfiguration;
import com.zeitheron.solarfluxreborn.err.NoSolarsRegisteredException;
import com.zeitheron.solarfluxreborn.gui.GuiHandler;
import com.zeitheron.solarfluxreborn.init.BlocksSFR;
import com.zeitheron.solarfluxreborn.init.ItemsSFR;
import com.zeitheron.solarfluxreborn.init.RecipeIO;
import com.zeitheron.solarfluxreborn.proxy.CommonProxy;
import com.zeitheron.solarfluxreborn.reference.InfoSFR;
import com.zeitheron.solarfluxreborn.te.AbstractSolarPanelTileEntity;
import com.zeitheron.solarfluxreborn.te.SolarPanelTileEntity;
import com.zeitheron.solarfluxreborn.te.cable.TileCustomCable;
import com.zeitheron.solarfluxreborn.utility.SFRLog;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(modid = InfoSFR.MOD_ID, name = InfoSFR.MOD_NAME, version = InfoSFR.VERSION, guiFactory = "com.zeitheronzeitheron.solarfluxreborn.config.ConfigurationGuiFactory", dependencies = "required-after:redstoneflux;required-after:hammercore;after:blackholestorage")
public class SolarFluxReborn
{
	@Mod.Instance(InfoSFR.MOD_ID)
	public static SolarFluxReborn instance;
	
	@SidedProxy(clientSide = "com.zeitheron.solarfluxreborn.proxy.ClientProxy", serverSide = "com.zeitheron.solarfluxreborn.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static File cfgFolder;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent pEvent)
	{
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(proxy);
		String cfg = pEvent.getSuggestedConfigurationFile().getAbsolutePath();
		cfg = cfg.substring(0, cfg.lastIndexOf("."));
		cfgFolder = new File(cfg);
		cfgFolder.mkdirs();
		File main_cfg = new File(cfgFolder, "main.cfg");
		File draconicevolution = new File(cfgFolder, "DraconicEvolution.cfg");
		File blackholestorage = new File(cfgFolder, "BlackHoleStorage.cfg");
		File version_file = new File(cfgFolder, "version.dat");
		ModConfiguration.initialize(main_cfg, version_file);
		DraconicEvolutionConfigs.initialize(draconicevolution);
		BlackHoleStorageConfigs.initialize(blackholestorage);
		
		SplashModPool.link("blackholestorage", "Black Hole Storage");
		SplashModPool.link("draconicevolution", "Draconic Evolution");
		SplashModPool.link("wearsfr", "Wearable Solars");
		
		GameRegistry.registerTileEntity(SolarPanelTileEntity.class, new ResourceLocation(InfoSFR.MOD_ID, "solar"));
		GameRegistry.registerTileEntity(AbstractSolarPanelTileEntity.class, new ResourceLocation(InfoSFR.MOD_ID, "abstractsolar"));
		GameRegistry.registerTileEntity(TileCustomCable.class, new ResourceLocation(InfoSFR.MOD_ID, "cable_custom"));
		BlocksSFR.initialize();
		
		if(BlocksSFR.getSolarPanels().isEmpty())
		{
			boolean deleted = main_cfg.delete();
			throw new NoSolarsRegisteredException("No solar panels was registered in config file." + (deleted ? "\nSolarFluxReborn configs were removed." : "Please remove file \"" + main_cfg.getAbsolutePath() + "\" manually.") + "\nTry restarting game.", false);
		}
		
		ItemsSFR.initialize();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent pEvent)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent evt)
	{
		proxy.postInit();
	}
	
	@EventHandler
	public void printMessage(FMLServerStartedEvent e)
	{
		if(ModConfiguration.willNotify && proxy.getClass() != CommonProxy.class)
		{
			SFRLog.bigWarning(TextFormatting.RED + "WARNING: Your configs have been replaced.");
			ModConfiguration.updateNotification(false);
		}
	}
	
	@SubscribeEvent
	public void addRecipes(RegistryEvent.Register<IRecipe> reg)
	{
		IForgeRegistry<IRecipe> fr = reg.getRegistry();
		RecipeIO.collect() //
		        .stream() //
		        .filter(r -> r != null) //
		        .forEach(fr::register);
	}
}