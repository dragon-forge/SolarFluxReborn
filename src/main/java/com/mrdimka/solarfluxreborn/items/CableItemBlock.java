package com.mrdimka.solarfluxreborn.items;

import java.util.List;

import com.mrdimka.solarfluxreborn.blocks.BlockCable320;
import com.mrdimka.solarfluxreborn.blocks.BlockCable3200;
import com.mrdimka.solarfluxreborn.blocks.BlockCable320000;
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
		if(i.getItem() == Item.getItemFromBlock(ModBlocks.cable1)) t.add(Lang.localise("energy.transfer") + ": " + BlockCable320.TRANSFER_RATE +  " " + Lang.localise("rfPerTick"));
		if(i.getItem() == Item.getItemFromBlock(ModBlocks.cable2)) t.add(Lang.localise("energy.transfer") + ": " + BlockCable3200.TRANSFER_RATE +  " " + Lang.localise("rfPerTick"));
		if(i.getItem() == Item.getItemFromBlock(ModBlocks.cable3)) t.add(Lang.localise("energy.transfer") + ": " + BlockCable320000.TRANSFER_RATE +  " " + Lang.localise("rfPerTick"));
	}
}