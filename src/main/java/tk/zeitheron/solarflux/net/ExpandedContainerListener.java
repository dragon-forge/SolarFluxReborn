package tk.zeitheron.solarflux.net;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class ExpandedContainerListener implements IContainerListener
{
	public final EntityPlayerMP player;
	
	public ExpandedContainerListener(EntityPlayerMP player)
	{
		this.player = player;
	}
	
	@Override
	public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList)
	{
		player.sendAllContents(containerToSend, itemsList);
	}
	
	@Override
	public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack)
	{
		player.sendSlotContents(containerToSend, slotInd, stack);
	}
	
	@Override
	public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue)
	{
		sendWindowProperty2(containerIn, varToUpdate, newValue);
	}
	
	public void sendWindowProperty2(@Nonnull Container containerIn, int varToUpdate, long newValue)
	{
		NetworkSF.INSTANCE.sendWindowProperty(player, containerIn, varToUpdate, newValue);
	}
	
	@Override
	public void sendAllWindowProperties(Container containerIn, IInventory inventory)
	{
		player.sendAllWindowProperties(containerIn, inventory);
	}
}