package com.zeitheron.solarflux;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zeitheron.holestorage.InfoBHS;
import com.zeitheron.solarflux.api.SolarFluxAPI;
import com.zeitheron.solarflux.api.SolarInfo;
import com.zeitheron.solarflux.api.compat.ISolarFluxCompat;
import com.zeitheron.solarflux.api.compat.SFCompat;
import com.zeitheron.solarflux.block.BlockBaseSolar;
import com.zeitheron.solarflux.block.tile.TileBaseSolar;
import com.zeitheron.solarflux.commands.CommandSolarFlux;
import com.zeitheron.solarflux.gui.GuiHandlerSF;
import com.zeitheron.solarflux.init.ItemsSF;
import com.zeitheron.solarflux.init.RecipesSF;
import com.zeitheron.solarflux.init.SolarsSF;
import com.zeitheron.solarflux.net.NetworkSF;
import com.zeitheron.solarflux.proxy.ISFProxy;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(modid = InfoSF.MOD_ID, name = "Solar Flux Reborn", version = InfoSF.VERSION, certificateFingerprint = "4d7b29cd19124e986da685107d16ce4b49bc0a97", updateJSON = "https://pastebin.com/raw/EJgJGHLv")
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
		MinecraftForge.EVENT_BUS.register(proxy);
		MinecraftForge.EVENT_BUS.register(this);
		
		if(FinalFieldHelper.setSFField(SolarFluxAPI.class, "renderRenderer", (Consumer<Item>) proxy::render))
			LOG.info("Applied render register to SolarFluxAPI.renderRenderer");
		else
			LOG.error("Failed to set SolarFluxAPI.renderRenderer to a valid consumer!");
		
		if(FinalFieldHelper.setSFField(SolarFluxAPI.class, "registerItem", (Consumer<Item>) item ->
		{
			item.setTranslationKey(item.getRegistryName().toString());
			ForgeRegistries.ITEMS.register(item);
			SolarFluxAPI.renderRenderer.accept(item);
			item.setCreativeTab(SolarFluxAPI.tab);
		}))
			LOG.info("Applied item register to SolarFluxAPI.registerItem");
		else
			LOG.error("Failed to set SolarFluxAPI.registerItem to a valid consumer!");
	}
	
	public static final Set<ISolarFluxCompat> compats = new HashSet<>();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		for(ASMData data : e.getAsmData().getAll(SFCompat.class.getCanonicalName()))
			try
			{
				Class c = Class.forName(data.getClassName());
				SFCompat compat = (SFCompat) c.getAnnotation(SFCompat.class);
				if(compat != null && !ISolarFluxCompat.class.isAssignableFrom(c))
				{
					LOG.error("Found class that expects a compat from SolarFlux, but it doesn't implement " + ISolarFluxCompat.class.getName() + "!");
					continue;
				}
				if(Loader.isModLoaded(compat.modid()))
				{
					compats.add(ISolarFluxCompat.class.cast(c.newInstance()));
					LOG.info("Added SolarFlux compat - " + c.getCanonicalName());
				} else
					LOG.debug("Skipped SolarFlux compat - " + c.getCanonicalName() + " @" + compat.modid() + " not found!");
			} catch(Throwable err)
			{
				err.printStackTrace();
			}
		
		if(FinalFieldHelper.setSFField(SolarFluxAPI.class, "tab", new CreativeTabs(InfoSF.MOD_ID)
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
		}))
			LOG.info("Applied new tab to SolarFluxAPI.tab!");
		else
			LOG.error("Failed to set SolarFluxAPI.tab to new creative tab!");
		
		SolarsSF.preInit(e.getSuggestedConfigurationFile());
		
		// Register plugin's panels
		List<SolarInfo> subs = new ArrayList<>();
		compats.forEach(i ->
		{
			List<SolarInfo> lo = new ArrayList<>();
			i.registerSolarInfos(lo);
			lo.forEach(si -> si.setCompatMod(i.getClass().getAnnotation(SFCompat.class).modid()));
			subs.addAll(lo);
		});
		subs.forEach(si ->
		{
			SolarFluxAPI.SOLAR_PANELS.register(si);
			BlockBaseSolar block = si.getBlock();
			ForgeRegistries.BLOCKS.register(block);
			Item model = new ItemBlock(block);
			model.setRegistryName(block.getRegistryName());
			ForgeRegistries.ITEMS.register(model);
			SolarFluxAPI.renderRenderer.accept(model);
		});
		subs.clear();
		
		ItemsSF.preInit();
		
		compats.forEach(ISolarFluxCompat::preInit);
		
		TileEntity.register(InfoSF.MOD_ID + ":base_solar", TileBaseSolar.class);
	}
	
	@SubscribeEvent
	public void createRegistries(RegistryEvent.NewRegistry e)
	{
		if(FinalFieldHelper.setSFField(SolarFluxAPI.class, "SOLAR_PANELS", new RegistryBuilder<SolarInfo>().setName(new ResourceLocation(InfoSF.MOD_ID, "panels")).setType(SolarInfo.class).create()))
			LOG.info("Applied new registry to SolarFluxAPI.SOLAR_PANELS!");
		else
			LOG.error("Failed to set SolarFluxAPI.SOLAR_PANELS to new registry!");
	}
	
	@SubscribeEvent
	public void registerRecipesEvent(RegistryEvent.Register<IRecipe> event)
	{
		RecipesSF.register(event.getRegistry());
		compats.forEach(s -> s.registerRecipes(event.getRegistry()));
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e)
	{
		FinalFieldHelper.setSFField(NetworkSF.class, "INSTANCE", new NetworkSF());
		
		SolarsSF.reloadConfigs();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandlerSF());
		proxy.init();
		
		compats.forEach(ISolarFluxCompat::init);
	}
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CommandSolarFlux());
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
	
	private static class FinalFieldHelper
	{
		private static Field modifiersField;
		private static Object reflectionFactory;
		private static Method newFieldAccessor;
		private static Method fieldAccessorSet;
		
		static boolean setSFField(Class<?> cls, String var, Object val)
		{
			try
			{
				Field f = cls.getDeclaredField(var);
				if(Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()))
				{
					makeWritable(f);
					setField(f, null, val);
					return true;
				}
				return false;
			} catch(Throwable err)
			{
				err.printStackTrace();
			}
			return false;
		}
		
		static Field makeWritable(Field f) throws ReflectiveOperationException
		{
			f.setAccessible(true);
			if(modifiersField == null)
			{
				Method getReflectionFactory = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("getReflectionFactory");
				reflectionFactory = getReflectionFactory.invoke(null);
				newFieldAccessor = Class.forName("sun.reflect.ReflectionFactory").getDeclaredMethod("newFieldAccessor", Field.class, boolean.class);
				fieldAccessorSet = Class.forName("sun.reflect.FieldAccessor").getDeclaredMethod("set", Object.class, Object.class);
				modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
			}
			modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
			return f;
		}
		
		static void setField(Field field, @Nullable Object instance, Object thing) throws ReflectiveOperationException
		{
			Object fieldAccessor = newFieldAccessor.invoke(reflectionFactory, field, false);
			fieldAccessorSet.invoke(fieldAccessor, instance, thing);
		}
	}
}