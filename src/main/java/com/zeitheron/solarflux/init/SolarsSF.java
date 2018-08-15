package com.zeitheron.solarflux.init;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.zeitheron.solarflux.InfoSF;
import com.zeitheron.solarflux.api.SolarFluxAPI;
import com.zeitheron.solarflux.api.SolarInfo;
import com.zeitheron.solarflux.block.BlockBaseSolar;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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
	public static final SolarInfo SOLAR_8 = new SolarInfo(32768, 256_000, 128_000_000).setRegistryName(InfoSF.MOD_ID, "8");
	
	private static File cfgDir;
	
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
	}
	
	public static void reloadConfigs()
	{
		IForgeRegistry<SolarInfo> infos = SolarFluxAPI.SOLAR_PANELS;
		
		for(SolarInfo si : infos.getValuesCollection())
		{
			si.getBlock().setCreativeTab(SolarFluxAPI.tab);
			
			ResourceLocation rn = si.getRegistryName();
			File f = new File(cfgDir, si.getCompatMod() != null ? si.getCompatMod() : rn.getNamespace());
			if(!f.isDirectory())
				f.mkdirs();
			f = new File(f, rn.getPath().replaceAll("/", "_") + ".json");
			
			if(!f.isFile())
				try(FileWriter j = new FileWriter(f))
				{
					j.append('{');
					
					String nln = System.lineSeparator() + "\t";
					char s = '\"';
					
					j.append(nln + s + "capacity" + s + ": " + si.maxCapacity + ",");
					j.append(nln + s + "generation" + s + ": " + si.maxGeneration + ",");
					j.append(nln + s + "transfer" + s + ": " + si.maxTransfer + ",");
					j.append(nln + s + "connected_textures" + s + ": " + si.connectTextures + System.lineSeparator());
					
					j.append('}');
					j.flush();
				} catch(IOException e)
				{
					e.printStackTrace();
				}
			
			try(FileReader reader = new FileReader(f))
			{
				JsonStreamParser j = new JsonStreamParser(reader);
				JsonObject cfg = (JsonObject) j.next();
				
				si.maxCapacity = cfg.get("capacity").getAsInt();
				si.maxGeneration = cfg.get("generation").getAsInt();
				si.maxTransfer = cfg.get("transfer").getAsInt();
				
				JsonElement connected_textures = cfg.get("connected_textures");
				si.connectTextures = connected_textures == null || connected_textures.getAsBoolean();
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}