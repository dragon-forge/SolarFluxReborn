package com.zeitheron.solarfluxreborn.creativetab;

import com.zeitheron.solarfluxreborn.init.ItemsSFR;
import com.zeitheron.solarfluxreborn.reference.InfoSFR;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabSFR
{
	public static final CreativeTabs MOD_TAB = new CreativeTabs(InfoSFR.MOD_ID.toLowerCase())
	{
		@Override
		public ItemStack getTabIconItem()
		{
			return new ItemStack(ItemsSFR.solarCell3);
		}
	};
}
