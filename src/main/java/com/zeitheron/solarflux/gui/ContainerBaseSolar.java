package com.zeitheron.solarflux.gui;

import java.util.Arrays;

import com.zeitheron.solarflux.block.tile.TileBaseSolar;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBaseSolar extends Container
{
	public final TileBaseSolar tile;
	public final int[] prev;
	
	public ContainerBaseSolar(TileBaseSolar tile, InventoryPlayer playerInv)
	{
		this.tile = tile;
		this.prev = new int[tile.getFieldCount()];
		addPlayerInventorySlotsToContainer(playerInv, 8, 98);
		addPlayerActionSlotsToContainer(playerInv, 8, 156);
		
		Arrays.fill(prev, -1);
	}
	
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		
		for(int j = 0; j < tile.getFieldCount(); ++j)
			if(prev[j] != tile.getField(j))
			{
				for(int i = 0; i < this.listeners.size(); ++i)
					this.listeners.get(i).sendWindowProperty(this, j, tile.getField(j));
				prev[j] = tile.getField(j);
			}
	}
	
	@Override
	public void updateProgressBar(int id, int data)
	{
		tile.setField(id, data);
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
		return tile.items.isUsableByPlayer(playerIn, tile.getPos());
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
	{
		return ItemStack.EMPTY;
	}
}