package tk.zeitheron.solarflux.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

public class SlotChargable extends SlotItemHandler
{
	public SlotChargable(IItemHandlerModifiable inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn, index, xPosition, yPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return !stack.isEmpty() && stack.getCapability(CapabilityEnergy.ENERGY, null).map(e -> e.canReceive() && e.getEnergyStored() < e.getMaxEnergyStored()).orElse(Boolean.FALSE);
	}
}