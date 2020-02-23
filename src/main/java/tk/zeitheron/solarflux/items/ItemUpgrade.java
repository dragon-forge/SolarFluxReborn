package tk.zeitheron.solarflux.items;

import tk.zeitheron.solarflux.block.tile.TileBaseSolar;
import tk.zeitheron.solarflux.utils.InventoryDummy;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ItemUpgrade extends Item
{
	public ItemUpgrade()
	{
		setMaxStackSize(Math.min(64, getMaxUpgrades()));
	}
	
	public abstract int getMaxUpgrades();
	
	public void update(TileBaseSolar tile, ItemStack stack, int amount)
	{
	}
	
	public boolean canStayInPanel(TileBaseSolar tile, ItemStack stack, InventoryDummy upgradeInv)
	{
		return true;
	}
	
	public boolean canInstall(TileBaseSolar tile, ItemStack stack, InventoryDummy upgradeInv)
	{
		return true;
	}
}