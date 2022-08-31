package org.zeith.solarflux.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.items.*;
import org.zeith.solarflux.panels.JSHelper;

import java.util.List;
import java.util.function.BiConsumer;

@SimplyRegister
public class ItemsSF
{
	public static final List<JSItem> JS_MATERIALS = JSHelper.JS_MATERIALS;
	@RegistryName("mirror")
	public static final Item MIRROR = newItem();
	@RegistryName("photovoltaic_cell_1")
	public static final Item PHOTOVOLTAIC_CELL_1 = newItem();
	@RegistryName("photovoltaic_cell_2")
	public static final Item PHOTOVOLTAIC_CELL_2 = newItem();
	@RegistryName("photovoltaic_cell_3")
	public static final Item PHOTOVOLTAIC_CELL_3 = newItem();
	@RegistryName("photovoltaic_cell_4")
	public static final Item PHOTOVOLTAIC_CELL_4 = newItem();
	@RegistryName("photovoltaic_cell_5")
	public static final Item PHOTOVOLTAIC_CELL_5 = newItem();
	@RegistryName("photovoltaic_cell_6")
	public static final Item PHOTOVOLTAIC_CELL_6 = newItem();
	@RegistryName("blank_upgrade")
	public static final Item BLANK_UPGRADE = newItem();
	@RegistryName("blazing_coating")
	public static final Item BLAZING_COATING = newItem();
	@RegistryName("emerald_glass")
	public static final Item EMERALD_GLASS = newItem();
	@RegistryName("ender_glass")
	public static final Item ENDER_GLASS = newItem();
	@RegistryName("efficiency_upgrade")
	public static final Item EFFICIENCY_UPGRADE = new ItemEfficiencyUpgrade();
	@RegistryName("transfer_rate_upgrade")
	public static final Item TRANSFER_RATE_UPGRADE = new ItemTransferRateUpgrade();
	@RegistryName("capacity_upgrade")
	public static final Item CAPACITY_UPGRADE = new ItemCapacityUpgrade();
	@RegistryName("traversal_upgrade")
	public static final Item TRAVERSAL_UPGRADE = new ItemTraversalUpgrade();
	@RegistryName("dispersive_upgrade")
	public static final Item DISPERSIVE_UPGRADE = new ItemDispersiveUpgrade();
	@RegistryName("block_charging_upgrade")
	public static final Item BLOCK_CHARGING_UPGRADE = new ItemBlockChargingUpgrade();
	@RegistryName("furnace_upgrade")
	public static final Item FURNACE_UPGRADE = new ItemFurnaceUpgrade();
	
	private static Item newItem()
	{
		return new Item(new Item.Properties().tab(SolarFlux.ITEM_GROUP));
	}
	
	@SimplyRegister
	public static void registerItems(BiConsumer<ResourceLocation, Item> handler)
	{
		JSHelper.generateItems(handler);
	}
}