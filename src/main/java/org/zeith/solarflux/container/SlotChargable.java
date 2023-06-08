package org.zeith.solarflux.container;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class SlotChargable
		extends SlotItemHandler
{
	public SlotChargable(IItemHandlerModifiable inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return !stack.isEmpty() && stack.getCapability(ForgeCapabilities.ENERGY, null)
				.map(e -> e.canReceive() && e.getEnergyStored() < e.getMaxEnergyStored()).orElse(Boolean.FALSE);
	}
}