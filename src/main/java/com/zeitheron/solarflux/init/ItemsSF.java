package com.zeitheron.solarflux.init;

import java.util.Arrays;

import com.zeitheron.solarflux.InfoSF;
import com.zeitheron.solarflux.api.SolarFluxAPI;
import com.zeitheron.solarflux.api.SolarInfo;
import com.zeitheron.solarflux.block.BlockBaseSolar;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemsSF
{
	public static final Item MIRROR = new Item().setRegistryName(InfoSF.MOD_ID, "mirror");
	public static final Item PHOTOVOLTAIC_CELL_1 = new Item().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_1");
	public static final Item PHOTOVOLTAIC_CELL_2 = new Item().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_2");
	public static final Item PHOTOVOLTAIC_CELL_3 = new Item().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_3");
	public static final Item PHOTOVOLTAIC_CELL_4 = new Item().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_4");
	public static final Item BLANK_UPGRADE = new Item().setRegistryName(InfoSF.MOD_ID, "blank_upgrade");
	
	public static void preInit()
	{
		IForgeRegistry<Item> items = ForgeRegistries.ITEMS;
		
		Arrays.stream(ItemsSF.class.getDeclaredFields()).filter(f -> Item.class.isAssignableFrom(f.getType())).forEach(f ->
		{
			try
			{
				Item it = (Item) f.get(null);
				it.setTranslationKey(it.getRegistryName().toString());
				items.register(it);
				SolarFluxAPI.renderRenderer.accept(it);
				it.setCreativeTab(SolarFluxAPI.tab);
			} catch(Throwable err)
			{
				err.printStackTrace();
			}
		});
	}
}