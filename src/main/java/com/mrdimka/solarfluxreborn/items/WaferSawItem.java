package com.mrdimka.solarfluxreborn.items;

import net.minecraft.item.ItemStack;

// TODO the class is not used yet. This item should be used to split SiliconBalls into Silicon Wafers
public class WaferSawItem extends SFItem {
	public WaferSawItem() {
		super("waferSaw");
		setMaxDamage(5);
		setMaxStackSize(1);
	}

	@Override
	public boolean isRepairable() {
		return false;
	}

	@Override
	public boolean hasContainerItem(ItemStack pStack) {
		return true;
	}

	@Override
	public ItemStack getContainerItem(ItemStack pItemStack) {
		pItemStack.setItemDamage(pItemStack.getItemDamage() + 1);
		return pItemStack;
	}
}
