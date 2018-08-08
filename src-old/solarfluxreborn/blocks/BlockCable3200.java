package com.zeitheron.solarfluxreborn.blocks;

import com.zeitheron.solarfluxreborn.reference.InfoSFR;

public class BlockCable3200 extends BlockAbstractCable
{
	public static double TRANSFER_RATE = 3200D;
	
	public BlockCable3200()
	{
		setUnlocalizedName(InfoSFR.MOD_ID + ":wire_2");
	}
	
	@Override
	public double getTransferRate()
	{
		return TRANSFER_RATE;
	}
}