package org.zeith.solarflux.container;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.SlotItemHandler;

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
		IEnergyStorage e;
		return !stack.isEmpty()
			   && (e = stack.getCapability(Capabilities.EnergyStorage.ITEM)) != null
			   && e.canReceive() && e.getEnergyStored() < e.getMaxEnergyStored();
	}
}