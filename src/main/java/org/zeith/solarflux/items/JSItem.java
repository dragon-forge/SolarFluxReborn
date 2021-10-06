package org.zeith.solarflux.items;

import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public class JSItem
		extends Item
{
	private LanguageData langs;

	public JSItem(Properties properties)
	{
		super(properties);
	}

	public LanguageData langBuilder()
	{
		return new LanguageData(this);
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

		final JSItem material;

		public LanguageData(JSItem material)
		{
			this.material = material;
		}

		public LanguageData put(String lang, String loc)
		{
			lang = lang.toLowerCase();
			if(lang.equals("en_us"))
				def = loc;
			langToName.put(lang, loc);
			return this;
		}

		public String getName(String lang)
		{
			return langToName.getOrDefault(lang, def);
		}

		public JSItem build()
		{
			if(def == null)
				throw new RuntimeException("Unable to apply languages: no 'en_us' value found!");
			material.langs = this;
			return material;
		}
	}
}