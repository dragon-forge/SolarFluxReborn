package com.mrdimka.solarfluxreborn.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Constants.NBT;

import com.mrdimka.solarfluxreborn.blocks.AbstractSolarPanelBlock;
import com.mrdimka.solarfluxreborn.blocks.SolarPanelBlock;
import com.mrdimka.solarfluxreborn.config.RemoteConfigs;
import com.mrdimka.solarfluxreborn.reference.NBTConstants;
import com.mrdimka.solarfluxreborn.utility.Color;
import com.mrdimka.solarfluxreborn.utility.Lang;

public class SolarPanelItemBlock extends ItemBlock {
    public SolarPanelItemBlock(Block pBlock) {
        super(pBlock);
    }

    @Override
    public void addInformation(ItemStack pItemStack, EntityPlayer pPlayer, List pList, boolean pBoolean) {
        super.addInformation(pItemStack, pPlayer, pList, pBoolean);
        
        addChargeTooltip(pList, pItemStack);
        addUpgradeCount(pList, pItemStack);
        
        if(getBlock() instanceof SolarPanelBlock)
        {
        	SolarPanelBlock solar = (SolarPanelBlock) getBlock();
        	addCapacityTooltip(pList, pItemStack, solar);
            addGenerationTooltip(pList, pItemStack, solar);
            addTransferTooltip(pList, pItemStack, solar);
        }else
        
        if(getBlock() instanceof AbstractSolarPanelBlock)
        {
        	AbstractSolarPanelBlock solar = (AbstractSolarPanelBlock) getBlock();
        	pList.add(String.format("%s%s:%s %,d", Color.AQUA, Lang.localise("energy.capacity"), Color.GREY, solar.cap));
        	pList.add(String.format("%s%s:%s %,d", Color.AQUA, Lang.localise("energy.generation"), Color.GREY, solar.maxGen));
        	pList.add(String.format("%s%s:%s %,d", Color.AQUA, Lang.localise("energy.transfer"), Color.GREY, solar.transfer));
        	
        	if(pItemStack.hasTagCompound() && pItemStack.getTagCompound().hasKey("MaxGen", NBT.TAG_INT) && pItemStack.getTagCompound().getInteger("MaxGen") != solar.maxGen) pList.add(Color.AQUA + "MaxGen: " + Color.GREY + pItemStack.getTagCompound().getInteger("MaxGen"));
        }
    }

    private void addChargeTooltip(List pList, ItemStack pItemStack) {
        if (hasNbtTag(pItemStack, NBTConstants.ENERGY)) {
            pList.add(String.format("%s%s:%s %,d", Color.GREEN, Lang.localise("energy.stored"), Color.GREY, pItemStack.getTagCompound().getInteger(NBTConstants.ENERGY)));
        }
    }

    private void addUpgradeCount(List pList, ItemStack pItemStack) {
        if (hasNbtTag(pItemStack, NBTConstants.TOOLTIP_UPGRADE_COUNT)) {
            pList.add(
                    String.format(
                            "%s%s:%s %,d",
                            Color.GREEN,
                            Lang.localise("upgrades.installed"),
                            Color.GREY,
                            pItemStack.getTagCompound().getInteger(NBTConstants.TOOLTIP_UPGRADE_COUNT)));
        }
    }

    private void addCapacityTooltip(List pList, ItemStack pItemStack, SolarPanelBlock pSolar) {
        int value = RemoteConfigs.getTierConfiguration(pSolar.getTierIndex()).getCapacity();
        if (hasNbtTag(pItemStack, NBTConstants.TOOLTIP_CAPACITY)) {
            int itemValue = pItemStack.getTagCompound().getInteger(NBTConstants.TOOLTIP_CAPACITY);
            if (itemValue != value) {
                value = itemValue;
            }
        }
        pList.add(String.format("%s%s:%s %,d", Color.AQUA, Lang.localise("energy.capacity"), Color.GREY, value));
    }

    private void addGenerationTooltip(List pList, ItemStack pItemStack, SolarPanelBlock pSolar) {
        final int value = RemoteConfigs.getTierConfiguration(pSolar.getTierIndex()).getMaximumEnergyGeneration();
        pList.add(String.format("%s%s:%s %,d", Color.AQUA, Lang.localise("energy.generation"), Color.GREY, value));
    }

    private void addTransferTooltip(List pList, ItemStack pItemStack, SolarPanelBlock pSolar)
    {
        int value = RemoteConfigs.getTierConfiguration(pSolar.getTierIndex()).getMaximumEnergyTransfer();
        if(hasNbtTag(pItemStack, NBTConstants.TOOLTIP_TRANSFER_RATE))
        {
            int itemValue = pItemStack.getTagCompound().getInteger(NBTConstants.TOOLTIP_TRANSFER_RATE);
            if(itemValue != value) value = itemValue;
        }
        pList.add(String.format("%s%s:%s %,d", Color.AQUA, Lang.localise("energy.transfer"), Color.GREY, value));
    }
    
    private boolean hasNbtTag(ItemStack pItemStack, String pNbtTag)
    {
        return pItemStack.hasTagCompound() && pItemStack.getTagCompound().hasKey(pNbtTag);
    }
}