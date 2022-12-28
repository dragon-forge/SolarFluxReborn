package org.zeith.solarflux.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.api.ISolarPanelTile;

public abstract class UpgradeItem
		extends Item
{
	public UpgradeItem(int stackSize)
	{
		super(new Item.Properties().stacksTo(stackSize).tab(SolarFlux.ITEM_GROUP));
	}
	
	public UpgradeItem(Item.Properties props)
	{
		super(props.tab(SolarFlux.ITEM_GROUP));
	}
	
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
	}
	
	public void onInstalled(ISolarPanelTile tile, int prevCount, int newCount)
	{
	}
	
	public void onRemoved(ISolarPanelTile tile, int prevCount, int newCount)
	{
	}
	
	public boolean canStayInPanel(ISolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return true;
	}
	
	public boolean canInstall(ISolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return true;
	}
	
	public int getMaxUpgradesInstalled(ISolarPanelTile tile)
	{
		return getMaxStackSize(getDefaultInstance());
	}
}