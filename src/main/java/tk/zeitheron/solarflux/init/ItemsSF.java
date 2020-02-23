package tk.zeitheron.solarflux.init;

import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.api.SolarFluxAPI;
import tk.zeitheron.solarflux.items.*;
import net.minecraft.item.Item;

import java.util.Arrays;

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

	public static final Item EFFICIENCY_UPGRADE = new ItemEfficiencyUpgrade();
	public static final Item TRANSFER_RATE_UPGRADE = new ItemTransferRateUpgrade();
	public static final Item CAPACITY_UPGRADE = new ItemCapacityUpgrade();
	public static final Item TRAVERSAL_UPGRADE = new ItemTraversalUpgrade();
	public static final Item DISPERSIVE_UPGRADE = new ItemDispersiveUpgrade();
	public static final Item BLOCK_CHARGING_UPGRADE = new ItemBlockChargingUpgrade();
	public static final Item FURNACE_UPGRADE = new ItemFurnaceUpgrade();

	public static void preInit()
	{
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