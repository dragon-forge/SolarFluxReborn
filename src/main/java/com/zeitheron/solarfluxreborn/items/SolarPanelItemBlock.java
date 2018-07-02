package com.zeitheron.solarfluxreborn.items;

import java.util.List;

import com.zeitheron.solarfluxreborn.blocks.AbstractSolarPanelBlock;
import com.zeitheron.solarfluxreborn.blocks.SolarPanelBlock;
import com.zeitheron.solarfluxreborn.config.RemoteConfigs;
import com.zeitheron.solarfluxreborn.reference.NBTConstants;
import com.zeitheron.solarfluxreborn.utility.Lang;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

public class SolarPanelItemBlock extends ItemBlock
{
	public SolarPanelItemBlock(Block pBlock)
	{
		super(pBlock);
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		
		addChargeTooltip(tooltip, stack);
		addUpgradeCount(tooltip, stack);
		
		if(getBlock() instanceof AbstractSolarPanelBlock)
		{
			AbstractSolarPanelBlock solar = (AbstractSolarPanelBlock) getBlock();
			tooltip.add(String.format("%s%s:%s %,d", TextFormatting.AQUA, Lang.localise("energy.capacity"), TextFormatting.GRAY, solar.cap));
			tooltip.add(String.format("%s%s:%s %,d", TextFormatting.AQUA, Lang.localise("energy.generation"), TextFormatting.GRAY, solar.maxGen));
			tooltip.add(String.format("%s%s:%s %,d", TextFormatting.AQUA, Lang.localise("energy.transfer"), TextFormatting.GRAY, solar.transfer));
			
			if(stack.hasTagCompound() && stack.getTagCompound().hasKey("MaxGen", NBT.TAG_INT) && stack.getTagCompound().getInteger("MaxGen") != solar.maxGen)
				tooltip.add(TextFormatting.AQUA + "MaxGen: " + TextFormatting.GRAY + stack.getTagCompound().getInteger("MaxGen"));
		} else if(getBlock() instanceof SolarPanelBlock)
		{
			SolarPanelBlock solar = (SolarPanelBlock) getBlock();
			addCapacityTooltip(tooltip, stack, solar);
			addGenerationTooltip(tooltip, stack, solar);
			addTransferTooltip(tooltip, stack, solar);
		}
	}
	
	private void addChargeTooltip(List tooltip, ItemStack stack)
	{
		if(hasNbtTag(stack, NBTConstants.ENERGY))
		{
			tooltip.add(String.format("%s%s:%s %,d", TextFormatting.GREEN, Lang.localise("energy.stored"), TextFormatting.GRAY, stack.getTagCompound().getInteger(NBTConstants.ENERGY)));
		}
	}
	
	private void addUpgradeCount(List tooltip, ItemStack stack)
	{
		if(hasNbtTag(stack, NBTConstants.TOOLTIP_UPGRADE_COUNT))
		{
			tooltip.add(String.format("%s%s:%s %,d", TextFormatting.GREEN, Lang.localise("upgrades.installed"), TextFormatting.GRAY, stack.getTagCompound().getInteger(NBTConstants.TOOLTIP_UPGRADE_COUNT)));
		}
	}
	
	private void addCapacityTooltip(List tooltip, ItemStack stack, SolarPanelBlock pSolar)
	{
		int value = RemoteConfigs.getTierConfiguration(pSolar.getTierIndex()).getCapacity();
		if(hasNbtTag(stack, NBTConstants.TOOLTIP_CAPACITY))
		{
			int itemValue = stack.getTagCompound().getInteger(NBTConstants.TOOLTIP_CAPACITY);
			if(itemValue != value)
			{
				value = itemValue;
			}
		}
		tooltip.add(String.format("%s%s:%s %,d", TextFormatting.AQUA, Lang.localise("energy.capacity"), TextFormatting.GRAY, value));
	}
	
	private void addGenerationTooltip(List tooltip, ItemStack stack, SolarPanelBlock pSolar)
	{
		final int value = RemoteConfigs.getTierConfiguration(pSolar.getTierIndex()).getMaximumEnergyGeneration();
		tooltip.add(String.format("%s%s:%s %,d", TextFormatting.AQUA, Lang.localise("energy.generation"), TextFormatting.GRAY, value));
	}
	
	private void addTransferTooltip(List tooltip, ItemStack stack, SolarPanelBlock pSolar)
	{
		int value = RemoteConfigs.getTierConfiguration(pSolar.getTierIndex()).getMaximumEnergyTransfer();
		if(hasNbtTag(stack, NBTConstants.TOOLTIP_TRANSFER_RATE))
		{
			int itemValue = stack.getTagCompound().getInteger(NBTConstants.TOOLTIP_TRANSFER_RATE);
			if(itemValue != value)
				value = itemValue;
		}
		tooltip.add(String.format("%s%s:%s %,d", TextFormatting.AQUA, Lang.localise("energy.transfer"), TextFormatting.GRAY, value));
	}
	
	private boolean hasNbtTag(ItemStack stack, String pNbtTag)
	{
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(pNbtTag);
	}
}