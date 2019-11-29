package tk.zeitheron.solarflux.items;

import java.util.Arrays;

import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;
import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.SolarFlux;

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
	
	public static final Item EFFICIENCY_UPGRADE = new ItemEfficiencyUpgrade();
	public static final Item TRANSFER_RATE_UPGRADE = new ItemTransferRateUpgrade();
	public static final Item CAPACITY_UPGRADE = new ItemCapacityUpgrade();
	public static final Item TRAVERSAL_UPGRADE = new ItemTraversalUpgrade();
	public static final Item DISPERSIVE_UPGRADE = new ItemDispersiveUpgrade();
	public static final Item BLOCK_CHARGING_UPGRADE = new ItemBlockChargingUpgrade();
	public static final Item FURNACE_UPGRADE = new ItemFurnaceUpgrade();
	
	private static Item newItem()
	{
		return new Item(new Item.Properties().group(SolarFlux.ITEM_GROUP));
	}
	
	public static void register(IForgeRegistry<Item> items)
	{
		Arrays.stream(ItemsSF.class.getDeclaredFields()).filter(f -> Item.class.isAssignableFrom(f.getType())).forEach(f ->
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