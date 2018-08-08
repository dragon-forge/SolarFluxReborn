package com.zeitheron.solarfluxreborn.blocks;

import com.zeitheron.solarfluxreborn.reference.InfoSFR;

public class BlockCable320000 extends BlockAbstractCable
{
	public static double TRANSFER_RATE = 320000D;
	
	public BlockCable320000()
	{
		setUnlocalizedName(InfoSFR.MOD_ID + ":wire_3");
	}
	
	@Override
	public double getTransferRate()
	{
		return TRANSFER_RATE;
	}
}