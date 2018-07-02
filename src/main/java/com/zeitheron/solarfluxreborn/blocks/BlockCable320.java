package com.zeitheron.solarfluxreborn.blocks;

import com.zeitheron.solarfluxreborn.reference.InfoSFR;

import net.minecraft.util.ResourceLocation;

public class BlockCable320 extends BlockAbstractCable
{
	public static double TRANSFER_RATE = 320D;
	
	public BlockCable320()
	{
		setUnlocalizedName(InfoSFR.MOD_ID + ":wire_1");
	}
	
	@Override
	public double getTransferRate()
	{
		return TRANSFER_RATE;
	}
}