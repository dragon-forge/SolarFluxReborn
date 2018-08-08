package com.zeitheron.solarfluxreborn.items;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import com.google.common.collect.Lists;
import com.zeitheron.solarfluxreborn.utility.Lang;
import com.zeitheron.solarfluxreborn.utility.Utils;

public class UpgradeItem extends SFItem
{
	private final int mMaximumPerSolarPanel;
	private final List<String> mUpgradeInfos = Lists.newArrayList();
	
	public UpgradeItem(String pName, int pMaximumPerSolarPanel, List<String> pUpgradeInfos)
	{
		super(pName);
		mMaximumPerSolarPanel = pMaximumPerSolarPanel;
		mUpgradeInfos.addAll(pUpgradeInfos);
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		super.addInformation(stack, worldIn, tooltip, flagIn);
		if(Utils.isShiftKeyDown())
		{
			tooltip.add(TextFormatting.AQUA + Lang.localise("solar.panel.upgrade") + TextFormatting.GRAY);
			tooltip.addAll(mUpgradeInfos);
			tooltip.add(Lang.localise("maximum") + " " + getMaximumPerSolarPanel());
		} else
		{
			tooltip.add(String.format(Lang.localise("hold.for.info"), TextFormatting.YELLOW + Lang.localise("shift") + TextFormatting.GRAY));
		}
	}
	
	/**
	 * The maximum number of this upgrade that stacked in one solar panel.
	 */
	public int getMaximumPerSolarPanel()
	{
		return mMaximumPerSolarPanel;
	}
}
