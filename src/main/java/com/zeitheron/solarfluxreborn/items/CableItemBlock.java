package com.zeitheron.solarfluxreborn.items;

import java.util.List;

import com.zeitheron.hammercore.command.CommandTimeToTicks;
import com.zeitheron.solarfluxreborn.blocks.BlockCable320;
import com.zeitheron.solarfluxreborn.blocks.BlockCable3200;
import com.zeitheron.solarfluxreborn.blocks.BlockCable320000;
import com.zeitheron.solarfluxreborn.init.BlocksSFR;
import com.zeitheron.solarfluxreborn.utility.Lang;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CableItemBlock extends ItemBlock
{
	public CableItemBlock(Block src)
	{
		super(src);
	}
	
	@Override
	public void addInformation(ItemStack i, World worldIn, List<String> t, ITooltipFlag flagIn)
	{
		if(i.getItem() == Item.getItemFromBlock(BlocksSFR.cable1))
			t.add(Lang.localise("energy.transfer") + ": " + CommandTimeToTicks.fancyFormat((long) BlockCable320.TRANSFER_RATE) + " " + Lang.localise("rfPerTick"));
		if(i.getItem() == Item.getItemFromBlock(BlocksSFR.cable2))
			t.add(Lang.localise("energy.transfer") + ": " + CommandTimeToTicks.fancyFormat((long) BlockCable3200.TRANSFER_RATE) + " " + Lang.localise("rfPerTick"));
		if(i.getItem() == Item.getItemFromBlock(BlocksSFR.cable3))
			t.add(Lang.localise("energy.transfer") + ": " + CommandTimeToTicks.fancyFormat((long) BlockCable320000.TRANSFER_RATE) + " " + Lang.localise("rfPerTick"));
	}
}