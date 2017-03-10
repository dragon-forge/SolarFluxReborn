package com.mrdimka.solarfluxreborn.config;

import static com.mrdimka.solarfluxreborn.config.ModConfiguration.DEFAULT_TIER_CONFIGURATIONS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.Configuration;

import com.google.common.collect.Lists;
import com.mrdimka.solarfluxreborn.utility.MetricUnits;
import com.mrdimka.solarfluxreborn.utility.SFRLog;

public class RemoteConfigs
{
	private static final List<TierConfiguration> mTierConfigurations = new ArrayList<>();
	
	public static void reset()
	{
		SFRLog.info("Restoring client configs...");
		mTierConfigurations.clear();
		SFRLog.info("...Fine!");
	}
	
	public static byte[] pack()
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(baos);
			
			o.writeInt(ModConfiguration.getTierConfigurations().size());
			for(TierConfiguration c : ModConfiguration.getTierConfigurations())
			{
				o.writeInt(c.getCapacity());
				o.writeInt(c.getMaximumEnergyGeneration());
				o.writeInt(c.getMaximumEnergyTransfer());
			}
			
			return baos.toByteArray();
		}
		catch(Throwable err) {}
		return new byte[0];
	}
	
	public static void unpack(byte[] data)
	{
		try
		{
			SFRLog.info("Accepting client configs...");
			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
			int size = ois.readInt();
			mTierConfigurations.clear();
			for(int i = 0; i < size; ++i)
			{
				int cap = ois.readInt();
				int maxGen = ois.readInt();
				int maxTransfer = ois.readInt();
				mTierConfigurations.add(new TierConfiguration(maxGen, maxTransfer, cap));
			}
			SFRLog.info("...Fine!");
		}
		catch(Throwable err) {}
	}
	
	public static TierConfiguration getTierConfiguration(int pTierIndex)
	{
		return mTierConfigurations == null || mTierConfigurations.size() <= pTierIndex ? ModConfiguration.getTierConfiguration(pTierIndex) : mTierConfigurations.get(pTierIndex);
	}
}