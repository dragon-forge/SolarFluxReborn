package tk.zeitheron.solarflux.gui;

import tk.zeitheron.solarflux.block.tile.TileBaseSolar;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotUpgrade extends Slot
{
	TileBaseSolar tile;
	
	public SlotUpgrade(TileBaseSolar inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn.upgradeInventory, index, xPosition, yPosition);
		this.tile = inventoryIn;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return tile.upgradeInventory.isItemValidForSlot(getSlotIndex(), stack);
	}
}