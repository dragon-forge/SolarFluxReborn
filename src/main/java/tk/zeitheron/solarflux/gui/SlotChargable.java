package tk.zeitheron.solarflux.gui;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class SlotChargable extends Slot
{
	public SlotChargable(IInventory inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		IEnergyStorage e;
		return !stack.isEmpty() && stack.hasCapability(CapabilityEnergy.ENERGY, null) && (e = stack.getCapability(CapabilityEnergy.ENERGY, null)).canReceive() && e.getEnergyStored() < e.getMaxEnergyStored();
	}
}