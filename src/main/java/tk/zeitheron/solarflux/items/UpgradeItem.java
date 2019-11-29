package tk.zeitheron.solarflux.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.block.SolarPanelTile;
import tk.zeitheron.solarflux.util.SimpleInventory;

public abstract class UpgradeItem extends Item
{
	public UpgradeItem(int stackSize)
	{
		super(new Item.Properties().maxStackSize(stackSize).group(SolarFlux.ITEM_GROUP));
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