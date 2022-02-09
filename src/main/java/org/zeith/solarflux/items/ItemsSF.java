package org.zeith.solarflux.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import org.zeith.solarflux.InfoSF;
import org.zeith.solarflux.SolarFlux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemsSF
{
	public static final Item MIRROR = newItem().setRegistryName(InfoSF.MOD_ID, "mirror");
	public static final Item PHOTOVOLTAIC_CELL_1 = newItem().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_1");
	public static final Item PHOTOVOLTAIC_CELL_2 = newItem().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_2");
	public static final Item PHOTOVOLTAIC_CELL_3 = newItem().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_3");
	public static final Item PHOTOVOLTAIC_CELL_4 = newItem().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_4");
	public static final Item PHOTOVOLTAIC_CELL_5 = newItem().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_5");
	public static final Item PHOTOVOLTAIC_CELL_6 = newItem().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_6");
	public static final Item BLANK_UPGRADE = newItem().setRegistryName(InfoSF.MOD_ID, "blank_upgrade");
	public static final Item BLAZING_COATING = newItem().setRegistryName(InfoSF.MOD_ID, "blazing_coating");
	public static final Item EMERALD_GLASS = newItem().setRegistryName(InfoSF.MOD_ID, "emerald_glass");
	public static final Item ENDER_GLASS = newItem().setRegistryName(InfoSF.MOD_ID, "ender_glass");

	public static final Item EFFICIENCY_UPGRADE = new ItemEfficiencyUpgrade();
	public static final Item TRANSFER_RATE_UPGRADE = new ItemTransferRateUpgrade();
	public static final Item CAPACITY_UPGRADE = new ItemCapacityUpgrade();
	public static final Item TRAVERSAL_UPGRADE = new ItemTraversalUpgrade();
	public static final Item DISPERSIVE_UPGRADE = new ItemDispersiveUpgrade();
	public static final Item BLOCK_CHARGING_UPGRADE = new ItemBlockChargingUpgrade();
	public static final Item FURNACE_UPGRADE = new ItemFurnaceUpgrade();

	private static Item newItem()
	{
		return new Item(new Item.Properties().tab(SolarFlux.ITEM_GROUP));
	}

	private static final List<JSItem> ITEMS2REG = new ArrayList<>();
	public static final List<JSItem> JS_MATERIALS = Collections.unmodifiableList(ITEMS2REG);

	public static Item newJSItem(String name)
	{
		JSItem i = new JSItem(new Item.Properties().tab(SolarFlux.ITEM_GROUP));
		i.setRegistryName(name);
		ITEMS2REG.add(i);
		return i;
	}

	public static void register(IForgeRegistry<Item> items)
	{
		ITEMS2REG.forEach(items::register);
		Arrays.stream(ItemsSF.class.getDeclaredFields())
				.filter(f -> Item.class.isAssignableFrom(f.getType()))
				.forEach(f ->
				{
					try
					{
						items.register(Item.class.cast(f.get(null)));
					} catch(Throwable err)
					{
						err.printStackTrace();
					}
				});
	}
}