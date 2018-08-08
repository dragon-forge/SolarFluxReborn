package com.zeitheron.solarflux;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zeitheron.holestorage.InfoBHS;
import com.zeitheron.solarflux.api.SolarFluxAPI;
import com.zeitheron.solarflux.api.SolarInfo;
import com.zeitheron.solarflux.block.BlockBaseSolar;
import com.zeitheron.solarflux.block.tile.TileBaseSolar;
import com.zeitheron.solarflux.gui.GuiHandlerSF;
import com.zeitheron.solarflux.init.ItemsSF;
import com.zeitheron.solarflux.init.RecipesSF;
import com.zeitheron.solarflux.init.SolarsSF;
import com.zeitheron.solarflux.proxy.ISFProxy;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(modid = InfoSF.MOD_ID, name = "Solar Flux Reborn", version = InfoSF.VERSION, certificateFingerprint = "4d7b29cd19124e986da685107d16ce4b49bc0a97")
public class SolarFlux
{
	public static final Logger LOG = LogManager.getLogger(InfoSF.MOD_ID);
	
	@SidedProxy(clientSide = InfoSF.PROXY_CLIENT, serverSide = InfoSF.PROXY_SERVER)
	public static ISFProxy proxy;
	
	@Instance
	public static SolarFlux instance;
	
	@EventHandler
	public void construct(FMLConstructionEvent evt)
	{
		SolarFluxAPI.SOLAR_PANELS = new RegistryBuilder<SolarInfo>().setName(new ResourceLocation(InfoSF.MOD_ID, "panels")).setType(SolarInfo.class).create();
		SolarFluxAPI.renderRenderer = proxy::render;
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		MinecraftForge.EVENT_BUS.register(proxy);
		MinecraftForge.EVENT_BUS.register(this);
		
		SolarFluxAPI.tab = new CreativeTabs(InfoSF.MOD_ID)
		{
			@Override
			public ItemStack createIcon()
			{
				return new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_2);
			}
			
			@Override
			public void displayAllRelevantItems(NonNullList<ItemStack> items)
			{
				NonNullList<ItemStack> sub = NonNullList.create();
				super.displayAllRelevantItems(sub);
				sub.sort((a, b) ->
				{
					if(a.getItem() instanceof ItemBlock && b.getItem() instanceof ItemBlock)
					{
						ItemBlock aib = (ItemBlock) a.getItem();
						ItemBlock bib = (ItemBlock) b.getItem();
						if(aib.getBlock() instanceof BlockBaseSolar && bib.getBlock() instanceof BlockBaseSolar)
						{
							BlockBaseSolar abs = (BlockBaseSolar) aib.getBlock();
							BlockBaseSolar bbs = (BlockBaseSolar) bib.getBlock();
							return abs.solarInfo.maxGeneration - bbs.solarInfo.maxGeneration;
						}
					}
					
					return a.getItem().getRegistryName().toString().compareTo(b.getItem().getRegistryName().toString());
				});
				items.addAll(sub);
			}
		};
		
		SolarsSF.preInit(e.getSuggestedConfigurationFile());
		ItemsSF.preInit();
		TileEntity.register(InfoSF.MOD_ID + ":base_solar", TileBaseSolar.class);
	}
	
	@SubscribeEvent
	public void registerRecipesEvent(RegistryEvent.Register<IRecipe> event)
	{
		RecipesSF.register(event.getRegistry());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e)
	{
		SolarsSF.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandlerSF());
		proxy.init();
	}
	
	@EventHandler
	public void certificateViolation(FMLFingerprintViolationEvent e)
	{
		LOG.warn("*****************************");
		LOG.warn("WARNING: Somebody has been tampering with SolarFluxReborn jar!");
		LOG.warn("It is highly recommended that you redownload mod from https://minecraft.curseforge.com/projects/246974 !");
		LOG.warn("*****************************");
		
		try
		{
			Class HammerCore = Class.forName("com.zeitheron.hammercore.HammerCore");
			Map<String, String> invalidCertificates = (Map<String, String>) HammerCore.getDeclaredField("invalidCertificates").get(null);
			invalidCertificates.put(InfoBHS.MOD_ID, "https://minecraft.curseforge.com/projects/246974");
		} catch(Throwable err)
		{
			if(err instanceof ClassNotFoundException)
				return;
			if(err instanceof NoClassDefFoundError)
				return;
			err.printStackTrace();
		}
	}
}