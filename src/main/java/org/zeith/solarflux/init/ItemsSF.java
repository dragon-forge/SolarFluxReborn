package org.zeith.solarflux.init;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.items.JSItem;
import org.zeith.solarflux.items.upgrades.*;
import org.zeith.solarflux.panels.JSHelper;

import java.util.List;
import java.util.function.BiConsumer;

@SimplyRegister
public interface ItemsSF
{
	List<JSItem> JS_MATERIALS = JSHelper.JS_MATERIALS;
	
	@RegistryName("mirror")
	Item MIRROR = newItem();
	
	@RegistryName("photovoltaic_cell_1")
	Item PHOTOVOLTAIC_CELL_1 = newItem();
	
	@RegistryName("photovoltaic_cell_2")
	Item PHOTOVOLTAIC_CELL_2 = newItem();
	
	@RegistryName("photovoltaic_cell_3")
	Item PHOTOVOLTAIC_CELL_3 = newItem();
	
	@RegistryName("photovoltaic_cell_4")
	Item PHOTOVOLTAIC_CELL_4 = newItem();
	
	@RegistryName("photovoltaic_cell_5")
	Item PHOTOVOLTAIC_CELL_5 = newItem();
	
	@RegistryName("photovoltaic_cell_6")
	Item PHOTOVOLTAIC_CELL_6 = newItem();
	
	@RegistryName("blazing_coating")
	Item BLAZING_COATING = newItem();
	@RegistryName("emerald_glass")
	Item EMERALD_GLASS = newItem();
	@RegistryName("ender_glass")
	Item ENDER_GLASS = newItem();
	
	@RegistryName("blank_upgrade")
	Item BLANK_UPGRADE = newItem();
	
	@RegistryName("efficiency_upgrade")
	Item EFFICIENCY_UPGRADE = new ItemEfficiencyUpgrade();
	
	@RegistryName("transfer_rate_upgrade")
	Item TRANSFER_RATE_UPGRADE = new ItemTransferRateUpgrade();
	
	@RegistryName("capacity_upgrade")
	Item CAPACITY_UPGRADE = new ItemCapacityUpgrade();
	
	@RegistryName("traversal_upgrade")
	Item TRAVERSAL_UPGRADE = new ItemTraversalUpgrade();
	
	@RegistryName("dispersive_upgrade")
	Item DISPERSIVE_UPGRADE = new ItemDispersiveUpgrade();
	
	@RegistryName("block_charging_upgrade")
	Item BLOCK_CHARGING_UPGRADE = new ItemBlockChargingUpgrade();
	
	@RegistryName("furnace_upgrade")
	Item FURNACE_UPGRADE = new ItemFurnaceUpgrade();
	
	static Item newItem()
	{
		return new Item(new Item.Properties().tab(SolarFlux.ITEM_GROUP))
		{
			@Override
			public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_)
			{
				p_41423_.add(Component.translatable("info." + SolarFlux.MOD_ID + ".craftitem").setStyle(Style.EMPTY.withColor(0x666666)));
			}
		};
	}
	
	@SimplyRegister
	static void registerItems(BiConsumer<ResourceLocation, Item> handler)
	{
		JSHelper.generateItems(handler);
	}
}