package com.zeitheron.solarfluxreborn;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TooltipFlagSFR implements ITooltipFlag
{
	public static final TooltipFlagSFR instance = new TooltipFlagSFR();
	
	@Override
	public boolean isAdvanced()
	{
		return Minecraft.getMinecraft().gameSettings.advancedItemTooltips;
	}
}