package org.zeith.solarflux.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.block.SolarPanelTile;

public abstract class UpgradeItem
		extends Item
{
	public UpgradeItem(int stackSize)
	{
		super(new Item.Properties().stacksTo(stackSize).tab(SolarFlux.ITEM_GROUP));
	}

	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
	}

	public boolean canStayInPanel(SolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return true;
	}

	public boolean canInstall(SolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return true;
	}
}