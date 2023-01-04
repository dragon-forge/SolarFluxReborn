package org.zeith.solarflux.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.zeith.solarflux.block.SolarPanelTile;
import org.zeith.solarflux.items._base.UpgradeItem;

import javax.annotation.Nonnull;

public class SlotUpgrade
		extends SlotItemHandler
{
	SolarPanelTile tile;
	
	public SlotUpgrade(SolarPanelTile inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn.upgradeInventory, index, xPosition, yPosition);
		this.tile = inventoryIn;
	}
	
	@Nonnull
	@Override
	public ItemStack remove(int amount)
	{
		ItemStack it = getItem();
		if(it.getItem() instanceof UpgradeItem && tile.isOnServer())
		{
			UpgradeItem up = (UpgradeItem) it.getItem();
			int prevCount = tile.getUpgrades(up);
			ItemStack it0 = super.remove(amount);
			int updCount = tile.getUpgrades(up);
			up.onRemoved(tile, prevCount, updCount);
			return it0;
		}
		
		return super.remove(amount);
	}
	
	@Override
	public void set(@Nonnull ItemStack stack)
	{
		if(stack.getItem() instanceof UpgradeItem && tile.isOnServer())
		{
			UpgradeItem up = (UpgradeItem) stack.getItem();
			int prevCount = tile.getUpgrades(up);
			
			super.set(stack);
			
			int updCount = tile.getUpgrades(up);
			up.onInstalled(tile, prevCount, updCount);
			
			return;
		}
		
		super.set(stack);
	}
	
	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return tile.upgradeInventory.isItemValid(getSlotIndex(), stack)
				&& stack.getItem() instanceof UpgradeItem
				&& tile.getUpgrades(stack.getItem()) < ((UpgradeItem) stack.getItem()).getMaxUpgradesInstalled(tile);
	}
}