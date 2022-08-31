package org.zeith.solarflux.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.zeith.solarflux.InfoSF;
import org.zeith.solarflux.SolarFlux;

import java.util.HashMap;
import java.util.Map;

public class JSItem
		extends Item
{
	final ResourceLocation registryName;
	LanguageData langs;
	
	public JSItem(ResourceLocation registryName, Properties properties, LanguageData langs)
	{
		super(properties);
		this.registryName = registryName;
		this.langs = langs;
	}
	
	public ResourceLocation getRegistryName()
	{
		return registryName;
	}
	
	public boolean hasLang()
	{
		return langs != null;
	}
	
	public LanguageData getLang()
	{
		return langs;
	}
	
	public static class LanguageData
	{
		public final Map<String, String> langToName = new HashMap<>();
		public String def;
		final FutureJSGenerator material;
		
		public LanguageData(FutureJSGenerator material)
		{
			this.material = material;
		}
		
		public LanguageData put(String lang, String loc)
		{
			lang = lang.toLowerCase();
			if(lang.equalsIgnoreCase("en_us"))
				def = loc;
			langToName.put(lang, loc);
			return this;
		}
		
		public String getName(String lang)
		{
			return langToName.getOrDefault(lang, def);
		}
		
		public ItemLike build()
		{
			if(def == null)
				throw new RuntimeException("Unable to apply languages: no 'en_us' value found!");
			material.langs = this;
			return material;
		}
	}
	
	public static class FutureJSGenerator
			implements ItemLike
	{
		public final String name;
		private JSItem item;
		private LanguageData langs;
		
		public FutureJSGenerator(String name)
		{
			this.name = name;
		}
		
		public LanguageData langBuilder()
		{
			return new LanguageData(this);
		}
		
		public JSItem create()
		{
			if(item != null) return item;
			var i = new JSItem(new ResourceLocation(InfoSF.MOD_ID, name), new Item.Properties().tab(SolarFlux.ITEM_GROUP), langs);
			item = i;
			return i;
		}
		
		@Override
		public Item asItem()
		{
			return item;
		}
	}
}