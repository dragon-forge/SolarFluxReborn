package com.zeitheron.solarfluxreborn.gui;

import com.zeitheron.hammercore.utils.InterItemStack;
import com.zeitheron.solarfluxreborn.te.SolarPanelTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSolarPanel extends Container
{
	protected static final int MOUSE_LEFT_CLICK = 0;
	protected static final int MOUSE_RIGHT_CLICK = 1;
	protected static final int FAKE_SLOT_ID = -999;
	protected static final int CLICK_MODE_NORMAL = 0;
	protected static final int CLICK_MODE_SHIFT = 1;
	protected static final int CLICK_MODE_KEY = 2;
	protected static final int CLICK_MODE_PICK_ITEM = 3;
	protected static final int CLICK_MODE_OUTSIDE = 4;
	protected static final int CLICK_DRAG_RELEASE = 5;
	protected static final int CLICK_MODE_DOUBLE_CLICK = 6;
	protected static final int CLICK_DRAG_MODE_PRE = 0;
	protected static final int CLICK_DRAG_MODE_SLOT = 1;
	protected static final int CLICK_DRAG_MODE_POST = 2;
	protected static final int PLAYER_INVENTORY_ROWS = 3;
	protected static final int PLAYER_INVENTORY_COLUMNS = 9;
	
	private final SolarPanelTileEntity solar;
	
	public ContainerSolarPanel(InventoryPlayer pInventoryPlayer, SolarPanelTileEntity pSolarPanelTileEntity)
	{
		solar = pSolarPanelTileEntity;
		// for(int i = 0; i < SolarPanelTileEntity.INVENTORY_SIZE; ++i)
		// addSlotToContainer(new SlotUpgrade(solar, i, 17 + i * 18, 59));
		addPlayerInventorySlotsToContainer(pInventoryPlayer, 8, 98);
		addPlayerActionSlotsToContainer(pInventoryPlayer, 8, 156);
	}
	
	@Override
	public void updateProgressBar(int pIndex, int pValue)
	{
		super.updateProgressBar(pIndex, pValue);
		if(pIndex == 0)
		{
			solar.setEnergyStored((solar.getEnergyStored() & 0xFFFF0000) | pValue);
		}
		if(pIndex == 1)
		{
			solar.setEnergyStored(solar.getEnergyStored() & 0xFFFF | (pValue << 16));
		}
		if(pIndex == 2)
		{
			solar.setCurrentEnergyGeneration((solar.getCurrentEnergyGeneration() & 0xFFFF0000) | pValue);
		}
		if(pIndex == 3)
		{
			solar.setCurrentEnergyGeneration(solar.getCurrentEnergyGeneration() & 0xFFFF | (pValue << 16));
		}
		if(pIndex == 4)
		{
			solar.setSunIntensity(pValue / 100.0f);
		}
	}
	
	protected void addPlayerActionSlotsToContainer(InventoryPlayer player, int x, int y)
	{
		for(int i = 0; i < 9; ++i)
			addSlotToContainer(new Slot(player, i, x + i * 18, y));
	}
	
	@Override
	public boolean canDragIntoSlot(Slot pSlot)
	{
		return super.canDragIntoSlot(pSlot);
	}
	
	protected void addPlayerInventorySlotsToContainer(InventoryPlayer pInventoryPlayer, int pLeft, int pTop)
	{
		for(int inventoryRowIndex = 0; inventoryRowIndex < PLAYER_INVENTORY_ROWS; ++inventoryRowIndex)
			for(int inventoryColumnIndex = 0; inventoryColumnIndex < PLAYER_INVENTORY_COLUMNS; ++inventoryColumnIndex)
				addSlotToContainer(new Slot(pInventoryPlayer, inventoryColumnIndex + inventoryRowIndex * 9 + 9, pLeft + inventoryColumnIndex * 18, pTop + inventoryRowIndex * 18));
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer pPlayer, int pSlotIndex)
	{
		return InterItemStack.NULL_STACK;
	}
	
	@Override
	public ItemStack slotClick(int slot, int btn, ClickType clickMode, EntityPlayer player)
	{
		return super.slotClick(slot, btn, clickMode, player);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer arg0)
	{
		return solar.getInventory().isUsableByPlayer(arg0, solar.getPos());
	}
}