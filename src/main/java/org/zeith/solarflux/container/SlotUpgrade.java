package org.zeith.solarflux.container;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.zeith.solarflux.block.SolarPanelTile;
import org.zeith.solarflux.items.UpgradeItem;

public class SlotUpgrade
		extends SlotItemHandler
{
	SolarPanelTile tile;
	
	public SlotUpgrade(SolarPanelTile inventoryIn, int index, int xPosition, int yPosition)
	{
		super(inventoryIn.upgradeInventory, index, xPosition, yPosition);
		this.tile = inventoryIn;
	}
	
	@Override
	public @NotNull ItemStack remove(int amount)
	{
		var it = getItem();
		if(it.getItem() instanceof UpgradeItem up)
		{
			int prevCount = tile.getUpgrades(up);
			var it0 = super.remove(amount);
			int updCount = tile.getUpgrades(up);
			up.onRemoved(tile, prevCount, updCount);
			return it0;
		}
		
		return super.remove(amount);
	}
	
	@Override
	public ItemStack safeInsert(ItemStack item, int amount)
	{
		if(item.getItem() instanceof UpgradeItem up)
		{
			int prev = tile.getUpgrades(up);
			
			if(!item.isEmpty() && this.mayPlace(item))
			{
				ItemStack itemstack = this.getItem();
				
				int insert = Math.min(Math.min(amount, item.getCount()), this.getMaxStackSize(item) - itemstack.getCount());
				insert = Math.min(insert, up.getMaxUpgradesInstalled(tile) - prev);
				
				if(itemstack.isEmpty())
				{
					this.set(item.split(insert));
				} else if(ItemStack.isSameItemSameTags(itemstack, item))
				{
					item.shrink(insert);
					itemstack.grow(insert);
					this.set(itemstack);
				}
				
				if(insert > 0)
				{
					int cur = tile.getUpgrades(up);
					up.onInstalled(tile, prev, cur);
				}
				
				return item;
			} else
			{
				return item;
			}
		}
		
		return super.safeInsert(item, amount);
	}
	
	@Override
	public boolean mayPlace(ItemStack stack)
	{
		return tile.upgradeInventory.isItemValid(getSlotIndex(), stack);
	}
}