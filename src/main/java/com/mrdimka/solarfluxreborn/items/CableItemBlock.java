package com.mrdimka.solarfluxreborn.items;

import java.util.List;

import com.mrdimka.solarfluxreborn.init.ModBlocks;
import com.mrdimka.solarfluxreborn.utility.Lang;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class CableItemBlock extends ItemBlock
{
	public CableItemBlock(Block src)
	{
		super(src);
	}
	
	@Override
	public void addInformation(ItemStack i, EntityPlayer p_addInformation_2_, List<String> t, boolean p_addInformation_4_)
	{
		if(i.getItem() == Item.getItemFromBlock(ModBlocks.cable1)) t.add(Lang.localise("energy.transfer") + ": 80 " + Lang.localise("rfPerTick"));
		if(i.getItem() == Item.getItemFromBlock(ModBlocks.cable2)) t.add(Lang.localise("energy.transfer") + ": 320 " + Lang.localise("rfPerTick"));
		if(i.getItem() == Item.getItemFromBlock(ModBlocks.instaCable)) t.add(Lang.localise("energy.transfer") + ": Infinite " + Lang.localise("rfPerTick"));
	}
}