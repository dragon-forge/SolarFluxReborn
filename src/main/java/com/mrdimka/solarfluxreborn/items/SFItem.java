package com.mrdimka.solarfluxreborn.items;

import net.minecraft.item.Item;

import com.mrdimka.solarfluxreborn.creativetab.ModCreativeTab;
import com.mrdimka.solarfluxreborn.reference.Reference;

public class SFItem extends Item
{
	public SFItem(String name)
	{
		setUnlocalizedName(Reference.MOD_ID + ":" + name);
		setCreativeTab(ModCreativeTab.MOD_TAB);
	}
}