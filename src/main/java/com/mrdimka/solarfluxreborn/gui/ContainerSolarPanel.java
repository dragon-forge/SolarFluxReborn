package com.mrdimka.solarfluxreborn.gui;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.google.common.collect.Maps;
import com.mrdimka.solarfluxreborn.items.UpgradeItem;
import com.mrdimka.solarfluxreborn.te.SolarPanelTileEntity;
import com.mrdimka.solarfluxreborn.utility.Utils;

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
    private final Map<Integer, Integer> mProgressBarValues = Maps.newHashMap();
    
    private final SolarPanelTileEntity mSolarPanelTileEntity;

    public ContainerSolarPanel(InventoryPlayer pInventoryPlayer, SolarPanelTileEntity pSolarPanelTileEntity) {
        mSolarPanelTileEntity = pSolarPanelTileEntity;

        for (int i = 0; i < SolarPanelTileEntity.INVENTORY_SIZE; ++i)
            addSlotToContainer(new SlotUpgrade(mSolarPanelTileEntity, i, 17 + i * 18, 59));
        addPlayerInventorySlotsToContainer(pInventoryPlayer, 8, 98);
        addPlayerActionSlotsToContainer(pInventoryPlayer, 8, 156);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int pIndex, int pValue) {
        super.updateProgressBar(pIndex, pValue);
        if (pIndex == 0) {
            mSolarPanelTileEntity.setEnergyStored((mSolarPanelTileEntity.getEnergyStored() & 0xFFFF0000) | pValue);
        }
        if (pIndex == 1) {
            mSolarPanelTileEntity.setEnergyStored(mSolarPanelTileEntity.getEnergyStored() & 0xFFFF | (pValue << 16));
        }
        if (pIndex == 2) {
            mSolarPanelTileEntity.setCurrentEnergyGeneration((mSolarPanelTileEntity.getCurrentEnergyGeneration() & 0xFFFF0000) | pValue);
        }
        if (pIndex == 3) {
            mSolarPanelTileEntity.setCurrentEnergyGeneration(mSolarPanelTileEntity.getCurrentEnergyGeneration() & 0xFFFF | (pValue << 16));
        }
        if (pIndex == 4) {
            mSolarPanelTileEntity.setSunIntensity(pValue / 100.0f);
        }
    }

    protected void addPlayerActionSlotsToContainer(InventoryPlayer pInventoryPlayer, int pLeft, int pTop) {
        for (int actionBarSlotIndex = 0; actionBarSlotIndex < 9; ++actionBarSlotIndex) {
            this.addSlotToContainer(new Slot(pInventoryPlayer, actionBarSlotIndex, pLeft + actionBarSlotIndex * 18, pTop));
        }
    }
    
    @Override
    public boolean canDragIntoSlot(Slot pSlot) {
        return pSlot.getSlotIndex() >= SolarPanelTileEntity.INVENTORY_SIZE;
    }

    protected void addPlayerInventorySlotsToContainer(InventoryPlayer pInventoryPlayer, int pLeft, int pTop) {
        for (int inventoryRowIndex = 0; inventoryRowIndex < PLAYER_INVENTORY_ROWS; ++inventoryRowIndex) {
            for (int inventoryColumnIndex = 0; inventoryColumnIndex < PLAYER_INVENTORY_COLUMNS; ++inventoryColumnIndex) {
                addSlotToContainer(
                        new Slot(
                                pInventoryPlayer,
                                inventoryColumnIndex + inventoryRowIndex * 9 + 9,
                                pLeft + inventoryColumnIndex * 18,
                                pTop + inventoryRowIndex * 18));
            }
        }
    }
    
    @SuppressWarnings("unused")
	@Override
    public ItemStack transferStackInSlot(EntityPlayer pPlayer, int pSlotIndex) {
    	if(true) return null;
        ItemStack itemStack = null;
        Slot slot = getSlot(pSlotIndex);

        if (slot != null && slot.getHasStack()) {
            ItemStack slotItemStack = slot.getStack();
            itemStack = slotItemStack.copy();

            if (pSlotIndex < SolarPanelTileEntity.INVENTORY_SIZE) {
                // From container to player's inventory.
                if (!mergeItemStack(slotItemStack, SolarPanelTileEntity.INVENTORY_SIZE, inventorySlots.size(), false)) {
                    return null;
                }
            } else {
                // From player's inventory to container.

                // Special treatment for upgrades
                if (slotItemStack.getItem() instanceof UpgradeItem) {
                    int canAdd = Math.min(mSolarPanelTileEntity.additionalUpgradeAllowed(slotItemStack), slotItemStack.stackSize);
                    if (canAdd > 0) {
                        ItemStack merging = slotItemStack.splitStack(canAdd);
                        if (mergeItemStack(merging, 0, SolarPanelTileEntity.INVENTORY_SIZE, false)) {
                            slotItemStack.stackSize += merging.stackSize;
                        }
                        if (slotItemStack.stackSize > 0) {
                            slot.putStack(slotItemStack);
                        } else {
                            slot.putStack(null);
                        }
                    }
                }

                // Normal behaviour.
                if (!mergeItemStack(slotItemStack, 0, SolarPanelTileEntity.INVENTORY_SIZE, false)) {
                    return null;
                }
            }

            if (slotItemStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemStack;
    }
    
    @Override
    public ItemStack slotClick(int slot, int btn, ClickType clickMode, EntityPlayer player)
    {
    	ItemStack oldItemStack = player.inventory.getItemStack();
        if (SolarPanelTileEntity.UPGRADE_SLOTS.contains(slot) && oldItemStack != null && oldItemStack.getItem() instanceof UpgradeItem) {
            if (btn == MOUSE_LEFT_CLICK || btn == MOUSE_RIGHT_CLICK) {
                // TODO add a check for slot -999 (had a crash before line just below)
                ItemStack currentItemInSlot = mSolarPanelTileEntity.getStackInSlot(slot);
                if (currentItemInSlot == null || Utils.itemStacksEqualIgnoreStackSize(oldItemStack, currentItemInSlot)) {
                    int canAdd = mSolarPanelTileEntity.additionalUpgradeAllowed(oldItemStack);
                    if (canAdd > 0) {
                        if (btn == MOUSE_RIGHT_CLICK) {
                            canAdd = 1;
                        }
                        ItemStack newStack;
                        if (canAdd >= oldItemStack.stackSize) {
                            newStack = oldItemStack;
                            oldItemStack = null;
                        } else {
                            newStack = oldItemStack.splitStack(canAdd);
                        }
                        player.inventory.setItemStack(newStack);
                        ItemStack result = super.slotClick(slot, btn, clickMode, player);
                        player.inventory.setItemStack(oldItemStack);
                        return result;
                    }
                }
            }
        }
    	return super.slotClick(slot, btn, clickMode, player);
    }

	@Override
	public boolean canInteractWith(EntityPlayer arg0)
	{
		return true;
	}
}