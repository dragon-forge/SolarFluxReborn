package org.zeith.solarflux.container;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.zeith.solarflux.block.SolarPanelTile;

public class SlotUpgrade
		extends SlotItemHandler
{
	SolarPanelTile tile;

	public SlotUpgrade(SolarPanelTile inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn.upgradeInventory, index, xPosition, yPosition);
		this.tile = inventoryIn;
	}

	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return tile.upgradeInventory.isItemValid(getSlotIndex(), stack);
	}
}