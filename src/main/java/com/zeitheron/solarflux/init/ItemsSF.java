package com.zeitheron.solarflux.init;

import java.util.Arrays;

import com.zeitheron.solarflux.InfoSF;
import com.zeitheron.solarflux.api.SolarFluxAPI;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemsSF
{
	public static final Item MIRROR = new Item().setRegistryName(InfoSF.MOD_ID, "mirror");
	public static final Item PHOTOVOLTAIC_CELL_1 = new Item().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_1");
	public static final Item PHOTOVOLTAIC_CELL_2 = new Item().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_2");
	public static final Item PHOTOVOLTAIC_CELL_3 = new Item().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_3");
	public static final Item PHOTOVOLTAIC_CELL_4 = new Item().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_4");
	public static final Item PHOTOVOLTAIC_CELL_5 = new Item().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_5");
	public static final Item PHOTOVOLTAIC_CELL_6 = new Item().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_6");
	public static final Item BLANK_UPGRADE = new Item().setRegistryName(InfoSF.MOD_ID, "blank_upgrade");
	
	public static void preInit()
	{
		IForgeRegistry<Item> items = ForgeRegistries.ITEMS;
		
		Arrays.stream(ItemsSF.class.getDeclaredFields()).filter(f -> Item.class.isAssignableFrom(f.getType())).forEach(f ->
		{
			try
			{
				SolarFluxAPI.registerItem.accept((Item) f.get(null));
			} catch(Throwable err)
			{
				err.printStackTrace();
			}
		});
	}
}