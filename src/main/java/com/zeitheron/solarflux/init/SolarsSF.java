package com.zeitheron.solarflux.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.zeitheron.solarflux.InfoSF;
import com.zeitheron.solarflux.api.SolarFluxAPI;
import com.zeitheron.solarflux.api.SolarInfo;
import com.zeitheron.solarflux.block.BlockBaseSolar;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class SolarsSF
{
	public static final SolarInfo SOLAR_1 = new SolarInfo(1, 8, 25_000).setRegistryName(InfoSF.MOD_ID, "1");
	public static final SolarInfo SOLAR_2 = new SolarInfo(8, 64, 125_000).setRegistryName(InfoSF.MOD_ID, "2");
	public static final SolarInfo SOLAR_3 = new SolarInfo(32, 256, 425_000).setRegistryName(InfoSF.MOD_ID, "3");
	public static final SolarInfo SOLAR_4 = new SolarInfo(128, 1_024, 2_000_000).setRegistryName(InfoSF.MOD_ID, "4");
	public static final SolarInfo SOLAR_5 = new SolarInfo(512, 4_096, 8_000_000).setRegistryName(InfoSF.MOD_ID, "5");
	public static final SolarInfo SOLAR_6 = new SolarInfo(2_048, 16_384, 32_000_000).setRegistryName(InfoSF.MOD_ID, "6");
	public static final SolarInfo SOLAR_7 = new SolarInfo(8_192, 64_000, 64_000_000).setRegistryName(InfoSF.MOD_ID, "7");
	public static final SolarInfo SOLAR_8 = new SolarInfo(32_768, 256_000, 128_000_000).setRegistryName(InfoSF.MOD_ID, "8");
	
	private static File cfgDir;
	
	public static File getCfgDir()
	{
		return cfgDir;
	}
	
	public static File getCustomCfgDir()
	{
		File ccfg = new File(getCfgDir(), "_custom");
		if(!ccfg.isDirectory())
			ccfg.mkdirs();
		File rdm = new File(ccfg, "README.txt");
		
		String internalMarker = "~ README v1.1 ~";
		
		boolean write = !rdm.isFile();
		if(!write)
			try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(rdm))))
			{
				if(!br.readLine().equals(internalMarker))
					write = true;
			} catch(Throwable err)
			{
			}
		
		try(FileOutputStream fos = new FileOutputStream(rdm))
		{
			fos.write((internalMarker + System.lineSeparator()).getBytes());
			fos.write("This directory enables pack developers to add custom solar panels, with custom textures. Read this guide to understand, how to do so...\n\n\nThe first this you want to do is create a folder with internal solar panel name (registry ID). In-game, you would be able to give it to yourself using /give @p solarflux:custom_solar_panel_{NAME}\nAfter you've created the folder, make a new file called \"panel.json\"\nThere, fill out the following template:\n\n{\n\t\"capacity\": 0,\n\t\"generation\": 0,\n\t\"transfer\": 0,\n\t\"thickness\": 6,\n\t\"connected_textures\": true,\n\t\"localizations\": {\n\t\t\"en_us\": \"NAME Solar Panel\"\n\t}\n}\n\nWhen you're done, save it to \"panel.json\"\nOh also, please fill out the numerical fields to be greater than zero, or you'll run into troubles.\nSmall tips for the JSON:\n- You can remove \"thickness\" if you want to use standard 6-pixel thickness.\n- You can remove \"connected_textures\" if you want the panels to connect anyway.\n- Any language is supported, but the fallback is always \"en_us\", so keep that in place!\n\n\nNext up: textures!\nIn your panel folder, you're going to need 3 texture files: \"top.png\", \"top_full.png\" and \"base.png\".\nLet's have a quick look through each file...\n- base.png - The base texture. It's applied to sides and bottom of the solar panel.\n- top.png - this is what you would expect, the top face of the solar panel. HOWEVER! This texture MUST have borders of the \"base.png\", because this texture is rendered in the inventory.\n- top_full.png - this is the same as \"top.png\", but without any borders.\nThese textures can be animated, if provided with the NAME.mcmeta files, and fill them as in default minecraft resource packs.\n\n\nIf you're lazy enough to read all of this, I made a tutorial video, explaining all of this in details:\nhttps://youtu.be/AhEaUzP4ozk\n\n\nSincerely, Zeitheron.\nhttps://www.curseforge.com/projects/246974".getBytes());
		} catch(IOException ioe)
		{
		}
		
		return ccfg;
	}
	
	public static Ingredient getGeneratingSolars(long gen)
	{
		return Ingredient.fromStacks(SolarFluxAPI.SOLAR_PANELS.getValuesCollection().stream().filter(s -> s.maxGeneration == gen).map(SolarInfo::getBlock).map(ItemStack::new).collect(Collectors.toList()).toArray(new ItemStack[0]));
	}
	
	public static void preInit(File file)
	{
		String apath = file.getAbsolutePath();
		cfgDir = new File(apath.substring(0, apath.lastIndexOf('.')));
		if(!cfgDir.isDirectory())
			cfgDir.mkdirs();
		
		IForgeRegistry<Block> blocks = ForgeRegistries.BLOCKS;
		IForgeRegistry<Item> items = ForgeRegistries.ITEMS;
		IForgeRegistry<SolarInfo> solars = SolarFluxAPI.SOLAR_PANELS;
		
		Arrays.stream(SolarsSF.class.getDeclaredFields()).filter(f -> SolarInfo.class.isAssignableFrom(f.getType())).forEach(f ->
		{
			try
			{
				SolarInfo si = (SolarInfo) f.get(null);
				solars.register(si);
				BlockBaseSolar block = si.getBlock();
				blocks.register(block);
				Item model = new ItemBlock(block);
				model.setRegistryName(block.getRegistryName());
				items.register(model);
				SolarFluxAPI.renderRenderer.accept(model);
			} catch(Throwable err)
			{
				err.printStackTrace();
			}
		});
		
		File[] customPanels = getCustomCfgDir().listFiles(f -> f.isDirectory());
		
		for(File cpdir : customPanels)
		{
			File inf = new File(cpdir, "panel.json");
			
			if(inf.isFile())
			{
				SolarInfo si = new SolarInfo(0, 0, 0);
				si.setRegistryName(new ResourceLocation("solarflux", cpdir.getName()));
				si.isCustom = true;
				
				try(FileReader reader = new FileReader(inf))
				{
					JsonStreamParser j = new JsonStreamParser(reader);
					JsonObject cfg = j.next().getAsJsonObject();
					
					String err;
					
					JsonElement je = cfg.get("capacity");
					if(je == null || je.getAsLong() <= 0L)
						throw new ReportedException(new CrashReport(err = ((je == null ? "\"capacity\" FIELD IS NOT FOUND" : "\"capacity\" FIELD IS LESS THAN OR EQUAL TO ZERO") + " IN CUSTOM SOLAR PANEL: " + cpdir.getName()), new RuntimeException(err)));
					si.maxCapacity = je.getAsLong();
					
					je = cfg.get("generation");
					if(je == null || je.getAsLong() <= 0L)
						throw new ReportedException(new CrashReport(err = ((je == null ? "\"generation\" FIELD IS NOT FOUND" : "\"generation\" FIELD IS LESS THAN OR EQUAL TO ZERO") + " IN CUSTOM SOLAR PANEL: " + cpdir.getName()), new RuntimeException(err)));
					si.maxGeneration = je.getAsLong();
					
					je = cfg.get("transfer");
					if(je == null || je.getAsLong() <= 0L)
						throw new ReportedException(new CrashReport(err = ((je == null ? "\"transfer\" FIELD IS NOT FOUND" : "\"transfer\" FIELD IS LESS THAN OR EQUAL TO ZERO") + " IN CUSTOM SOLAR PANEL: " + cpdir.getName()), new RuntimeException(err)));
					si.maxTransfer = je.getAsInt();
					
					je = cfg.get("thickness");
					si.thiccness = je == null ? 6 : je.getAsInt();
					
					JsonElement connected_textures = cfg.get("connected_textures");
					si.connectTextures = connected_textures == null || connected_textures.getAsBoolean();
					
					try
					{
						solars.register(si);
						BlockBaseSolar block = si.getBlock();
						blocks.register(block);
						Item model = new ItemBlock(block);
						model.setRegistryName(block.getRegistryName());
						items.register(model);
						SolarFluxAPI.renderRenderer.accept(model);
					} catch(Throwable thr)
					{
						thr.printStackTrace();
					}
				} catch(IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
		}
	}
	
	public static void reloadConfigs()
	{
		IForgeRegistry<SolarInfo> infos = SolarFluxAPI.SOLAR_PANELS;
		
		for(SolarInfo si : infos.getValuesCollection())
		{
			si.getBlock().setCreativeTab(SolarFluxAPI.tab);
			ResourceLocation rn = si.getRegistryName();
			
			File f;
			
			if(si.isCustom)
			{
				f = new File(getCustomCfgDir(), si.getRegistryName().getPath() + File.separator + "panel.json");
			} else
			{
				f = new File(cfgDir, si.getCompatMod() != null ? si.getCompatMod() : rn.getNamespace());
				if(!f.isDirectory())
					f.mkdirs();
				f = new File(f, rn.getPath().replaceAll("/", "_") + ".json");
			}
			
			if(!f.isFile())
				try(FileWriter j = new FileWriter(f))
				{
					j.append('{');
					
					String nln = System.lineSeparator() + "\t";
					char s = '\"';
					
					j.append(nln + s + "capacity" + s + ": " + si.maxCapacity + ",");
					j.append(nln + s + "generation" + s + ": " + si.maxGeneration + ",");
					j.append(nln + s + "transfer" + s + ": " + si.maxTransfer + ",");
					j.append(nln + s + "thickness" + s + ": " + si.thiccness + ",");
					j.append(nln + s + "connected_textures" + s + ": " + si.connectTextures + System.lineSeparator());
					
					j.append('}');
					j.flush();
				} catch(IOException e)
				{
					e.printStackTrace();
				}
			
			try(InputStreamReader reader = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))
			{
				JsonStreamParser j = new JsonStreamParser(reader);
				JsonObject cfg = j.next().getAsJsonObject();
				
				si.maxCapacity = cfg.get("capacity").getAsLong();
				si.maxGeneration = cfg.get("generation").getAsLong();
				si.maxTransfer = cfg.get("transfer").getAsInt();
				
				JsonElement thickness = cfg.get("thickness");
				si.thiccness = thickness == null ? 6 : thickness.getAsInt();
				
				JsonElement connected_textures = cfg.get("connected_textures");
				si.connectTextures = connected_textures == null || connected_textures.getAsBoolean();
				
				if(si.isCustom)
				{
					JsonElement localsElem = cfg.get("localizations");
					if(localsElem != null)
					{
						JsonObject locals = localsElem.getAsJsonObject();
						Map<String, String> lmap = new HashMap<>();
						for(Map.Entry<String, JsonElement> langs : locals.entrySet())
							lmap.put(langs.getKey().toLowerCase(), langs.getValue().getAsString());
						si.localizations = Collections.unmodifiableMap(lmap);
					}
				}
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}