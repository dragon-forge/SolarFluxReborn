package com.zeitheron.solarflux.items;

import com.zeitheron.solarflux.block.tile.TileBaseSolar;
import com.zeitheron.solarflux.utils.InventoryDummy;

import net.minecraft.item.Item;

public abstract class ItemUpgrade extends Item
{
	public ItemUpgrade()
	{
		setMaxStackSize(Math.min(64, getMaxUpgrades()));
	}
	
	public abstract int getMaxUpgrades();
	
	public void update(TileBaseSolar tile, int amount)
	{
	}
	
	public boolean canInstall(TileBaseSolar tile, InventoryDummy upgradeInv)
	{
		return true;
	}
}