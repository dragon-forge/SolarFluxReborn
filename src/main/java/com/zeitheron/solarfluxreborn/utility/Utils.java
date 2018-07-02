package com.zeitheron.solarfluxreborn.utility;

import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;

public final class Utils
{
	public static final Random RANDOM = new Random();
	
	/**
	 * Returns true if the two ItemStacks contains exactly the same item without
	 * taking stack size into account. Credit to Pahimar.
	 */
	public static boolean itemStacksEqualIgnoreStackSize(ItemStack pItemStack1, ItemStack pItemStack2)
	{
		if(pItemStack1 != null && pItemStack2 != null)
		{
			// Compare itemID
			if(Item.getIdFromItem(pItemStack1.getItem()) - Item.getIdFromItem(pItemStack2.getItem()) == 0)
			{
				// Compare item
				if(pItemStack1.getItem() == pItemStack2.getItem())
				{
					// Compare meta
					if(pItemStack1.getItemDamage() == pItemStack2.getItemDamage())
					{
						// Compare NBT presence
						if(pItemStack1.hasTagCompound() && pItemStack2.hasTagCompound())
						{
							// Compare NBT
							if(ItemStack.areItemStackTagsEqual(pItemStack1, pItemStack2))
							{
								return true;
							}
						} else if(!pItemStack1.hasTagCompound() && !pItemStack2.hasTagCompound())
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public static boolean isShiftKeyDown()
	{
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
}
