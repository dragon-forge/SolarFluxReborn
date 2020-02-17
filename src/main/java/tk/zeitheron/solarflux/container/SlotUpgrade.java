package tk.zeitheron.solarflux.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import tk.zeitheron.solarflux.block.SolarPanelTile;

public class SlotUpgrade extends SlotItemHandler
{
	SolarPanelTile tile;
	
	public SlotUpgrade(SolarPanelTile inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn.upgradeInventory, index, xPosition, yPosition);
		this.tile = inventoryIn;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return tile.upgradeInventory.isItemValid(getSlotIndex(), stack);
	}
}