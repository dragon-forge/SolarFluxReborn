package com.mrdimka.solarfluxreborn.items;

import java.util.List;

import com.mrdimka.solarfluxreborn.utility.Lang;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class CraftingItem extends SFItem
{
    public CraftingItem(String name)
    {
    	super(name);
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tip, boolean p_addInformation_4_)
    {
    	tip.add(Lang.localise("craftitem", true));
    }
}