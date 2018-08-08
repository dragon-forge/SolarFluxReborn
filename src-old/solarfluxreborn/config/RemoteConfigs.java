package com.zeitheron.solarfluxreborn.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.zeitheron.solarfluxreborn.blocks.BlockCable320;
import com.zeitheron.solarfluxreborn.blocks.BlockCable3200;
import com.zeitheron.solarfluxreborn.blocks.BlockCable320000;
import com.zeitheron.solarfluxreborn.utility.SFRLog;

public class RemoteConfigs
{
	public static double Cable1, Cable2, Cable3;
	public static boolean inherited = false;
	private static final List<TierConfiguration> mTierConfigurations = new ArrayList<>();
	private static float solarHeight;
	
	public static void reset()
	{
		SFRLog.info("Restoring client configs...");
		mTierConfigurations.clear();
		BlockCable320.TRANSFER_RATE = Cable1;
		BlockCable3200.TRANSFER_RATE = Cable2;
		BlockCable320000.TRANSFER_RATE = Cable3;
		solarHeight = ModConfiguration.getSolarThickness();
		inherited = false;
		SFRLog.info("...Fine!");
	}
	
	public static byte[] pack()
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream o = new DataOutputStream(baos);
			
			o.writeInt(ModConfiguration.getTierConfigurations().size());
			for(TierConfiguration c : ModConfiguration.getTierConfigurations())
			{
				o.writeInt(c.getCapacity());
				o.writeInt(c.getMaximumEnergyGeneration());
				o.writeInt(c.getMaximumEnergyTransfer());
			}
			
			o.writeDouble(Cable1);
			o.writeDouble(Cable2);
			o.writeDouble(Cable3);
			o.writeFloat(ModConfiguration.getSolarThickness());
			
			return baos.toByteArray();
		} catch(Throwable err)
		{
		}
		return new byte[0];
	}
	
	public static void unpack(InputStream input)
	{
		try
		{
			SFRLog.info("Accepting client configs...");
			DataInputStream ois = new DataInputStream(input);
			int size = ois.readInt();
			mTierConfigurations.clear();
			for(int i = 0; i < size; ++i)
			{
				int cap = ois.readInt();
				int maxGen = ois.readInt();
				int maxTransfer = ois.readInt();
				mTierConfigurations.add(new TierConfiguration(maxGen, maxTransfer, cap));
			}
			
			BlockCable320.TRANSFER_RATE = ois.readDouble();
			BlockCable3200.TRANSFER_RATE = ois.readDouble();
			BlockCable320000.TRANSFER_RATE = ois.readDouble();
			solarHeight = ois.readFloat();
			inherited = true;
			
			SFRLog.info("...Fine!");
		} catch(Throwable err)
		{
		}
	}
	
	public static TierConfiguration getTierConfiguration(int pTierIndex)
	{
		return mTierConfigurations == null || mTierConfigurations.size() <= pTierIndex ? ModConfiguration.getTierConfiguration(pTierIndex) : mTierConfigurations.get(pTierIndex);
	}
	
	public static float getSolarHeight()
	{
		if(solarHeight < 1 / 16F || solarHeight > 1F)
			solarHeight = 1 / 16F;
		return solarHeight;
	}
}