package com.zeitheron.solarfluxreborn.items;

import com.zeitheron.solarfluxreborn.creativetab.CreativeTabSFR;
import com.zeitheron.solarfluxreborn.reference.InfoSFR;

import net.minecraft.item.Item;

public class SFItem extends Item
{
	public SFItem(String name)
	{
		setUnlocalizedName(InfoSFR.MOD_ID + ":" + name);
		setCreativeTab(CreativeTabSFR.MOD_TAB);
	}
}