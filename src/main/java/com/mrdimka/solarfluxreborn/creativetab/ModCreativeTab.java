package com.mrdimka.solarfluxreborn.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.mrdimka.solarfluxreborn.init.ModItems;
import com.mrdimka.solarfluxreborn.reference.Reference;

public class ModCreativeTab {
	public static final CreativeTabs MOD_TAB = new CreativeTabs(Reference.MOD_ID.toLowerCase()) {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(ModItems.solarCell3);
		}
	};
}
