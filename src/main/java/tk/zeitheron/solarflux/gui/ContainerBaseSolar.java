package tk.zeitheron.solarflux.gui;

import java.util.Arrays;

import tk.zeitheron.solarflux.block.tile.TileBaseSolar;
import tk.zeitheron.solarflux.net.ExpandedContainerListener;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBaseSolar extends Container
{
	public final TileBaseSolar tile;
	public final long[] prev;
	
	public ExpandedContainerListener networking;
	
	public ContainerBaseSolar(TileBaseSolar tile, InventoryPlayer playerInv)
	{
		this.tile = tile;
		this.prev = new long[tile.getVarCount()];
		addPlayerInventorySlotsToContainer(playerInv, 8, 98);
		addPlayerActionSlotsToContainer(playerInv, 8, 156);
		
		for(int i = 0; i < tile.upgradeInventory.getSizeInventory(); ++i)
			addSlotToContainer(new SlotUpgrade(tile, i, i * 18 + 9, 61));
		
		addSlotToContainer(new SlotChargable(tile.chargeInventory, 0, 151, 9));
		
		if(playerInv.player instanceof EntityPlayerMP)
			networking = new ExpandedContainerListener((EntityPlayerMP) playerInv.player);
		
		Arrays.fill(prev, -1L);
	}
	
	boolean fsync;
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		
		if(!fsync)
		{
			fsync = true;
			if(networking != null)
				for(int j = 0; j < tile.getVarCount(); ++j)
				{
					networking.sendWindowProperty2(this, j, tile.getVar(j));
					prev[j] = tile.getVar(j);
				}
		}
		
		if(networking != null)
			for(int j = 0; j < tile.getVarCount(); ++j)
				if(prev[j] != tile.getVar(j))
				{
					networking.sendWindowProperty2(this, j, tile.getVar(j));
					prev[j] = tile.getVar(j);
				}
	}
	
	@Override
	public void updateProgressBar(int id, int data)
	{
		tile.setVar(id, data);
	}
	
	public void updateProgressBar2(int id, long data)
	{
		prev[id] = data;
		tile.setVar(id, data);
	}
	
	protected void addPlayerInventorySlotsToContainer(InventoryPlayer pInventoryPlayer, int pLeft, int pTop)
	{
		for(int inventoryRowIndex = 0; inventoryRowIndex < 3; ++inventoryRowIndex)
			for(int inventoryColumnIndex = 0; inventoryColumnIndex < 9; ++inventoryColumnIndex)
				addSlotToContainer(new Slot(pInventoryPlayer, inventoryColumnIndex + inventoryRowIndex * 9 + 9, pLeft + inventoryColumnIndex * 18, pTop + inventoryRowIndex * 18));
	}
	
	protected void addPlayerActionSlotsToContainer(InventoryPlayer player, int x, int y)
	{
		for(int i = 0; i < 9; ++i)
			addSlotToContainer(new Slot(player, i, x + i * 18, y));
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
	{
		return tile.upgradeInventory.isUsableByPlayer(playerIn, tile.getPos());
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		return ItemStack.EMPTY;
	}
}