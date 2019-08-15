package com.zeitheron.solarflux.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zeitheron.solarflux.api.SolarFluxAPI;
import com.zeitheron.solarflux.api.SolarInfo;
import com.zeitheron.solarflux.block.BlockBaseSolar;
import com.zeitheron.solarflux.init.SolarsSF;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.Locale;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.fml.common.Loader;

public class SolarFluxResourcePack implements IResourcePack, IResourceManagerReloadListener
{
	public final Map<ResourceLocation, IResourceStreamSupplier> resourceMap = new HashMap<>();
	public final Map<String, List<String>> langs = new HashMap<>();
	public final List<SolarInfo> infos = new ArrayList<>();
	public final Set<String> domains = new HashSet<>();
	
	private static IResourceStreamSupplier ofText(String text)
	{
		return new IResourceStreamSupplier()
		{
			@Override
			public boolean exists()
			{
				return true;
			}
			
			@Override
			public InputStream create() throws IOException
			{
				return new ByteArrayInputStream(text.getBytes());
			}
		};
	}
	
	private static IResourceStreamSupplier ofFile(File file)
	{
		return new IResourceStreamSupplier()
		{
			@Override
			public boolean exists()
			{
				return file.isFile();
			}
			
			@Override
			public InputStream create() throws IOException
			{
				return new FileInputStream(file);
			}
		};
	}
	
	public void rebake()
	{
		resourceMap.clear();
		langs.clear();
		infos.clear();
		domains.clear();
		
		domains.addAll(Loader.instance().getIndexedModList().keySet());
		domains.addAll(SolarFluxAPI.resourceDomains);
		
		if(SolarFluxAPI.SOLAR_PANELS != null)
			infos.addAll(SolarFluxAPI.SOLAR_PANELS.getValuesCollection());
		
		for(SolarInfo si : infos)
		{
			if(si.localizations != null)
			{
				for(String lang : si.localizations.keySet())
				{
					List<String> ls = langs.get(lang + ".lang");
					if(ls == null)
						langs.put(lang + ".lang", ls = new ArrayList<>());
					String v;
					if(!ls.contains(v = si.localizations.get(lang)))
						ls.add(si.getBlock().getTranslationKey() + ".name=" + v);
				}
			}
			
			BlockBaseSolar blk = si.getBlock();
			ResourceLocation reg = blk.getRegistryName();
			ResourceLocation reg2 = si.getRegistryName();
			
			domains.add(reg.getNamespace());
			
			ResourceLocation blockstate = new ResourceLocation(reg.getNamespace(), "blockstates/" + reg.getPath() + ".json");
			ResourceLocation models_block = new ResourceLocation(reg.getNamespace(), "models/block/" + reg.getPath() + ".json");
			ResourceLocation models_item = new ResourceLocation(reg.getNamespace(), "models/item/" + reg.getPath() + ".json");
			
			int thicc = si.thiccness;
			int reverseThicc = 16 - thicc;
			
			resourceMap.put(blockstate, ofText("{\"variants\":{\"normal\":{\"model\":\"" + reg.toString() + "\"}}}"));
			resourceMap.put(models_item, ofText("{\"parent\":\"" + reg.getNamespace() + ":block/" + reg.getPath() + "\"}"));
			
			// Block Model
			resourceMap.put(models_block, ofText("{\"parent\":\"block/block\",\"textures\":{\"side\":\"" + reg.getNamespace() + ":blocks/solar_base_" + reg2.getPath() + "\",\"particle\":\"" + reg.getNamespace() + ":blocks/solar_top_" + reg2.getPath() + "\"},\"elements\":[{\"from\":[0, 0, 0],\"to\":[16, " + thicc + ", 16],\"faces\":{\"north\":{\"uv\":[0, " + reverseThicc + ", 16, 16],\"texture\":\"#side\"},\"east\":{\"uv\":[0, " + reverseThicc + ", 16, 16],\"texture\":\"#side\"},\"south\":{\"uv\":[0, " + reverseThicc + ", 16, 16],\"texture\":\"#side\"},\"west\":{\"uv\":[0, " + reverseThicc + ", 16, 16],\"texture\":\"#side\"},\"up\":{\"uv\":[0, 0, 16, 16],\"texture\":\"#particle\"},\"down\":{\"uv\":[0, 0, 16, 16],\"texture\":\"#side\"}}}]}"));
			
			if(si.isCustom)
			{
				File customDir = new File(SolarsSF.getCfgDir(), "_custom");
				
				ResourceLocation textures_blocks_base = new ResourceLocation(reg.getNamespace(), "textures/blocks/solar_base_" + reg2.getPath() + ".png");
				ResourceLocation textures_blocks_topf = new ResourceLocation(reg.getNamespace(), "textures/blocks/solar_topf_" + reg2.getPath() + ".png");
				ResourceLocation textures_blocks_top = new ResourceLocation(reg.getNamespace(), "textures/blocks/solar_top_" + reg2.getPath() + ".png");
				ResourceLocation textures_blocks_base_mcmeta = new ResourceLocation(reg.getNamespace(), "textures/blocks/solar_base_" + reg2.getPath() + ".png.mcmeta");
				ResourceLocation textures_blocks_topf_mcmeta = new ResourceLocation(reg.getNamespace(), "textures/blocks/solar_topf_" + reg2.getPath() + ".png.mcmeta");
				ResourceLocation textures_blocks_top_mcmeta = new ResourceLocation(reg.getNamespace(), "textures/blocks/solar_top_" + reg2.getPath() + ".png.mcmeta");
				
				File theDir = new File(customDir, reg2.getPath());
				
				if(theDir.isDirectory())
				{
					resourceMap.put(textures_blocks_base, ofFile(new File(theDir, "base.png")));
					resourceMap.put(textures_blocks_base_mcmeta, ofFile(new File(theDir, "base.mcmeta")));
					
					resourceMap.put(textures_blocks_top, ofFile(new File(theDir, "top.png")));
					resourceMap.put(textures_blocks_top_mcmeta, ofFile(new File(theDir, "top.mcmeta")));
					
					resourceMap.put(textures_blocks_topf, ofFile(new File(theDir, "top_full.png")));
					resourceMap.put(textures_blocks_topf_mcmeta, ofFile(new File(theDir, "top_full.mcmeta")));
				}
			}
		}
		
		injectSolarPanelLanguages();
	}
	
	boolean calling = false;
	
	@Override
	public InputStream getInputStream(ResourceLocation location) throws IOException
	{
		if(calling)
			return null;
		
		calling = true;
		try
		{
			InputStream in = resourceMap.get(location).create();
			calling = false;
			return in;
		} catch(RuntimeException e)
		{
			calling = false;
			if(e.getCause() instanceof IOException)
				throw (IOException) e.getCause();
			throw e;
		}
	}
	
	@Override
	public boolean resourceExists(ResourceLocation location)
	{
		IResourceStreamSupplier s;
		return (s = resourceMap.get(location)) != null && s.exists();
	}
	
	@Override
	public Set<String> getResourceDomains()
	{
		return domains;
	}
	
	@Override
	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException
	{
		return null;
	}
	
	@Override
	public BufferedImage getPackImage() throws IOException
	{
		return null;
	}
	
	@Override
	public String getPackName()
	{
		return "SolarFluxReborn Builtin";
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		rebake();
		injectSolarPanelLanguages();
	}
	
	public static void injectSolarPanelLanguages()
	{
		if(SolarFluxAPI.SOLAR_PANELS == null)
			return;
		Field i18nLocalef = I18n.class.getDeclaredFields()[0];
		i18nLocalef.setAccessible(true);
		try
		{
			Locale locale = Locale.class.cast(i18nLocalef.get(null));
			Field propertiesf = Locale.class.getDeclaredFields()[2];
			propertiesf.setAccessible(true);
			Map<String, String> properties = Map.class.cast(propertiesf.get(locale));
			String code = Minecraft.getMinecraft().gameSettings.language;
			Map<String, List<String>> langs = new HashMap<>();
			for(SolarInfo si : SolarFluxAPI.SOLAR_PANELS.getValuesCollection())
				if(si.localizations != null)
				{
					for(String lang : si.localizations.keySet())
					{
						List<String> ls = langs.get(lang);
						if(ls == null)
							langs.put(lang, ls = new ArrayList<>());
						String v;
						if(!ls.contains(v = si.localizations.get(lang)))
							ls.add(si.getBlock().getTranslationKey() + ".name=" + v);
					}
					
					if(si.localizations.containsKey("en_us"))
						properties.put(si.getBlock().getTranslationKey() + ".name", si.localizations.get("en_us"));
					
					if(si.localizations.containsKey(code))
						properties.put(si.getBlock().getTranslationKey() + ".name", si.localizations.get(code));
				}
			if(langs.containsKey(code))
			{
				StringBuilder sb = new StringBuilder("# Builtin & Generated by Solar Flux Reborn lang file.\n");
				langs.get(code).stream().map(ln -> "\n" + ln).forEach(sb::append);
				LanguageMap.inject(new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));
			}
		} catch(ReflectiveOperationException err)
		{
			err.printStackTrace();
		}
	}
	
	public static interface IResourceStreamSupplier
	{
		boolean exists();
		
		InputStream create() throws IOException;
	}
}