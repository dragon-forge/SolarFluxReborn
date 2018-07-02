package com.zeitheron.solarfluxreborn.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.zeitheron.solarfluxreborn.SolarFluxReborn;
import com.zeitheron.solarfluxreborn.blocks.BlockCable320;
import com.zeitheron.solarfluxreborn.blocks.BlockCable3200;
import com.zeitheron.solarfluxreborn.blocks.BlockCable320000;
import com.zeitheron.solarfluxreborn.reference.InfoSFR;
import com.zeitheron.solarfluxreborn.utility.MetricUnits;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModConfiguration
{
	public static final List<TierConfiguration> DEFAULT_TIER_CONFIGURATIONS = ImmutableList.of(new TierConfiguration(1, 25 * MetricUnits.KILO), new TierConfiguration(1 * 8, 125 * MetricUnits.KILO), new TierConfiguration(1 * 8 * 4, 425 * MetricUnits.KILO), new TierConfiguration(1 * 8 * 4 * 4, 2 * MetricUnits.MEGA), new TierConfiguration(1 * 8 * 4 * 4 * 4, 8 * MetricUnits.MEGA), new TierConfiguration(1 * 8 * 4 * 4 * 4 * 4, 32 * MetricUnits.MEGA), new TierConfiguration(8_192, 64 * MetricUnits.KILO, 64 * MetricUnits.MEGA), new TierConfiguration(32_768, 256 * MetricUnits.KILO, 128 * MetricUnits.MEGA));
	private static final String UPGRADE_CATEGORY = "upgrades";
	private static Configuration cfg;
	private static boolean autoBalanceEnergy;
	private static boolean keepEnergyWhenDismantled;
	private static boolean keepInventoryWhenDismantled;
	private static float rainGenerationFactor;
	private static float solarThickness;
	private static float thunderGenerationFactor;
	private static List<TierConfiguration> mTierConfigurations;
	private static boolean mEfficiencyUpgradeActive;
	private static float mEfficiencyUpgradeIncrease;
	private static float mEfficiencyUpgradeReturnsToScale;
	private static int mEfficiencyUpgradeMax;
	private static boolean mLowLightUpgradeActive;
	private static int mLowLightUpgradeMax;
	private static boolean mTraversalUpgradeActive;
	private static int mTraversalUpgradeIncrease;
	private static int mTraversalUpgradeUpdateRate;
	private static int mTraversalUpgradeMax;
	private static boolean mTransferRateUpgradeActive;
	private static float mTransferRateUpgradeIncrease;
	private static float mTransferRateUpgradeReturnsToScale;
	private static int mTransferRateUpgradeMax;
	private static boolean mCapacityUpgradeActive;
	private static float mCapacityUpgradeIncrease;
	private static float mCapacityUpgradeReturnsToScale;
	private static int mCapacityUpgradeMax;
	private static boolean mFurnaceUpgradeActive;
	private static int mFurnaceUpgradeHeatingConsumption;
	private static boolean mConnectedTextures;
	
	public static boolean wasConfigReplaced = false;
	public static boolean willNotify = false;
	
	public static boolean addUnprepared = true;
	
	public static void initialize(File pConfigFile, File versionFile)
	{
		String version = "";
		try
		{
			InputStream i = new FileInputStream(versionFile);
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(i);
			version = nbt.getString("version");
			i.close();
		} catch(Throwable err)
		{
			saveUpdateVersionDat(versionFile);
		}
		
		if(!InfoSFR.VERSION.equals(version))
		{
			pConfigFile.renameTo(new File(SolarFluxReborn.cfgFolder, "main.cfg.old"));
			wasConfigReplaced = true;
			willNotify = true;
			saveUpdateVersionDat(versionFile);
		}
		
		if(cfg == null)
			cfg = new Configuration(pConfigFile);
		MinecraftForge.EVENT_BUS.register(new ModConfiguration());
		MinecraftForge.EVENT_BUS.register(new DraconicEvolutionConfigs());
		loadConfiguration();
	}
	
	private static void saveUpdateVersionDat(File version)
	{
		try
		{
			if(!version.isFile())
				version.createNewFile();
			FileOutputStream o = new FileOutputStream(version);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("version", InfoSFR.VERSION);
			nbt.setBoolean("willNotify", willNotify);
			CompressedStreamTools.writeCompressed(nbt, o);
			o.close();
		} catch(Throwable err2)
		{
		}
	}
	
	public static void updateNotification(boolean willNotify)
	{
		ModConfiguration.willNotify = willNotify;
		saveUpdateVersionDat(new File(SolarFluxReborn.cfgFolder, "version.dat"));
	}
	
	private static void loadConfiguration()
	{
		if(cfg == null)
			initialize(new File(SolarFluxReborn.cfgFolder, "main.cfg"), new File(SolarFluxReborn.cfgFolder, "version.dat"));
		autoBalanceEnergy = cfg.getBoolean("BalanceEnergy", Configuration.CATEGORY_GENERAL, true, "Neighbor solar panels share their energy if set to true.");
		keepEnergyWhenDismantled = cfg.getBoolean("KeepEnergyWhenDismantled", Configuration.CATEGORY_GENERAL, true, "Whether or not the solar panels keep their internal energy when dismantled with a wrench.");
		keepInventoryWhenDismantled = cfg.getBoolean("KeepInventoryWhenDismantled", Configuration.CATEGORY_GENERAL, true, "Whether or not the solar panels keep their internal inventory when dismantled with a wrench.");
		rainGenerationFactor = cfg.getFloat("RainProductionFactor", Configuration.CATEGORY_GENERAL, 0.4f, 0, 1, "Factor used to reduce the energy generation during rainy weather.");
		thunderGenerationFactor = cfg.getFloat("ThunderProductionFactor", Configuration.CATEGORY_GENERAL, 0.4f, 0, 1, "Factor used to reduce the energy generation during stormy weather.");
		mConnectedTextures = cfg.getBoolean("ConnectedTextures", Configuration.CATEGORY_GENERAL, true, "Use connected textures for the solar panels.");
		addUnprepared = cfg.getBoolean("UnpreparedSolars", "crafting", true, "Whether or not unprepared solar panels will be added to the game.");
		solarThickness = cfg.getFloat("Panel Thickness", "general", 6 / 16F, 1 / 16F, 1F, "What thickness should panels be?");
		
		loadTierConfigurations();
		loadUpgradesConfiguration();
		
		RemoteConfigs.Cable1 = BlockCable320.TRANSFER_RATE = cfg.getFloat("cable t1", "cables", 320F, 1F, 1000000F, "How much RF can first cable transfer.");
		RemoteConfigs.Cable2 = BlockCable3200.TRANSFER_RATE = cfg.getFloat("cable t2", "cables", 3200F, 1F, 1000000F, "How much RF can second cable transfer.");
		RemoteConfigs.Cable3 = BlockCable320000.TRANSFER_RATE = cfg.getFloat("cable t3", "cables", 320000F, 1F, 1000000F, "How much RF can last cable transfer.");
		
		if(cfg.hasChanged())
			cfg.save();
	}
	
	private static void loadUpgradesConfiguration()
	{
		// Efficiency upgrade
		mEfficiencyUpgradeActive = cfg.getBoolean("EfficiencyUpgradeActive", UPGRADE_CATEGORY, true, "Whether or not efficiency upgrades should be added to the game.");
		mEfficiencyUpgradeIncrease = cfg.getFloat("EfficiencyUpgradeIncrease", UPGRADE_CATEGORY, 0.05F, 0.01F, 10, "Factor by which the energy production is increased per upgrade.");
		mEfficiencyUpgradeReturnsToScale = cfg.getFloat("EfficiencyUpgradeReturnsToScale", UPGRADE_CATEGORY, 0.9F, 0.1F, 2, "Returns to scale. How does the efficiency scales as you add more upgrades. 1 is linear. Below 1 reduces the efficiency as you add upgrades, Above 1 does the opposite.");
		mEfficiencyUpgradeMax = cfg.getInt("EfficiencyUpgradeMax", UPGRADE_CATEGORY, 8, 1, 256, "Maximum number of efficiency upgrade that can be added to a single solar panel.");
		
		// Low light upgrade
		mLowLightUpgradeActive = cfg.getBoolean("LowLightUpgradeActive", UPGRADE_CATEGORY, true, "Whether or not low light upgrades should be added to the game.");
		mLowLightUpgradeMax = cfg.getInt("LowLightUpgradeMax", UPGRADE_CATEGORY, 8, 1, 256, "Maximum number of low light upgrade that can be added to a single solar panel.");
		
		// Traversal upgrade
		mTraversalUpgradeActive = cfg.getBoolean("TraversalUpgradeActive", UPGRADE_CATEGORY, true, "Whether or not traversal upgrades should be added to the game.");
		mTraversalUpgradeIncrease = cfg.getInt("TraversalUpgradeIncrease", UPGRADE_CATEGORY, 1, 1, 10, "How many extra machines can be traversed per extra upgrade.");
		mTraversalUpgradeUpdateRate = cfg.getInt("TraversalUpgradeUpdateRate", UPGRADE_CATEGORY, 25, 1, 20 * 60, "Update rate of traversal. Increase to reduce lag. But machines will be discovered slower.");
		mTraversalUpgradeMax = cfg.getInt("TraversalUpgradeMax", UPGRADE_CATEGORY, 64, 1, 256, "Maximum number of traversal upgrade that can be added to a single solar panel.");
		
		// Transfer upgrade
		mTransferRateUpgradeActive = cfg.getBoolean("TransferRateUpgradeActive", UPGRADE_CATEGORY, true, "Whether or not transfer rate upgrades should be added to the game.");
		mTransferRateUpgradeIncrease = cfg.getFloat("TransferRateUpgradeIncrease", UPGRADE_CATEGORY, 0.1F, 0.01F, 10, "Factor by which the transfer rate is increased per upgrade.");
		mTransferRateUpgradeReturnsToScale = cfg.getFloat("TransferRateUpgradeReturnsToScale", UPGRADE_CATEGORY, 0.9F, 0.1F, 2, "Returns to scale. How does the transfer rate scales as you add more upgrades. 1 is linear. Below 1 reduces the efficiency as you add upgrades, Above 1 does the opposite.");
		mTransferRateUpgradeMax = cfg.getInt("TransferRateUpgradeMax", UPGRADE_CATEGORY, 8, 1, 256, "Maximum number of transfer rate upgrade that can be added to a single solar panel.");
		
		// Capacity upgrade
		mCapacityUpgradeActive = cfg.getBoolean("CapacityUpgradeActive", UPGRADE_CATEGORY, true, "Whether or not capacity upgrades should be added to the game.");
		mCapacityUpgradeIncrease = cfg.getFloat("CapacityUpgradeIncrease", UPGRADE_CATEGORY, 0.1F, 0.01F, 10, "Factor by which the capacity is increased per upgrade.");
		mCapacityUpgradeReturnsToScale = cfg.getFloat("CapacityUpgradeReturnsToScale", UPGRADE_CATEGORY, 1, 0.1F, 2, "Returns to scale. How does the transfer rate scales as you add more upgrades. 1 is linear. Below 1 reduces the efficiency as you add upgrades, Above 1 does the opposite.");
		mCapacityUpgradeMax = cfg.getInt("CapacityUpgradeMax", UPGRADE_CATEGORY, 16, 1, 256, "Maximum number of capacity upgrade that can be added to a single solar panel.");
		
		// Furnace upgrade
		mFurnaceUpgradeActive = cfg.getBoolean("FurnaceUpgradeActive", UPGRADE_CATEGORY, true, "Whether or not furnace upgrades should be added to the game.");
		mFurnaceUpgradeHeatingConsumption = cfg.getInt("FurnaceUpgradeHeatingConsumption", UPGRADE_CATEGORY, 8, 1, 64000, "Amount of RF per tick used to heat up a furnace.");
	}
	
	private static void loadTierConfigurations()
	{
		mTierConfigurations = Lists.newArrayList();
		for(int tier = 0;; ++tier)
		{
			TierConfiguration config = loadTierConfiguration(tier);
			if(config == null)
				break;
			else
				mTierConfigurations.add(config);
		}
	}
	
	private static TierConfiguration loadTierConfiguration(int pTierNumber)
	{
		String category = "solar_panel_tier" + pTierNumber;
		boolean active = cfg.getBoolean("Active", category, pTierNumber < DEFAULT_TIER_CONFIGURATIONS.size(), "Whether or not this tier of Solar Panel should be added to the game.");
		if(active)
		{
			// Find a default config for the default values
			TierConfiguration defaultConfig;
			if(pTierNumber < DEFAULT_TIER_CONFIGURATIONS.size())
				defaultConfig = DEFAULT_TIER_CONFIGURATIONS.get(pTierNumber);
			else
			{
				int deltaTier = pTierNumber - DEFAULT_TIER_CONFIGURATIONS.size() + 1;
				TierConfiguration lastConfig = DEFAULT_TIER_CONFIGURATIONS.get(DEFAULT_TIER_CONFIGURATIONS.size() - 1);
				defaultConfig = new TierConfiguration((int) (lastConfig.getMaximumEnergyGeneration() * Math.pow(2, deltaTier)), (int) (lastConfig.getMaximumEnergyTransfer() * Math.pow(2, deltaTier)), (int) (lastConfig.getCapacity() * Math.pow(1.2, deltaTier)));
			}
			
			return new TierConfiguration(cfg.getInt("MaximumEnergyGeneration", category, defaultConfig.getMaximumEnergyGeneration(), 1, 2 * MetricUnits.GIGA, "Maximum amount of RF generated per tick."), cfg.getInt("MaximumEnergyTransfer", category, defaultConfig.getMaximumEnergyTransfer(), 1, 2 * MetricUnits.GIGA, "Maximum amount of RF transferred per tick."), cfg.getInt("Capacity", category, defaultConfig.getCapacity(), 1, 2 * MetricUnits.GIGA, "Amount of RF that can be stored."));
		}
		return null;
	}
	
	public static Configuration getConfiguration()
	{
		return cfg;
	}
	
	public static boolean doesAutoBalanceEnergy()
	{
		return autoBalanceEnergy;
	}
	
	public static boolean doesKeepEnergyWhenDismantled()
	{
		return keepEnergyWhenDismantled;
	}
	
	public static boolean doesKeepInventoryWhenDismantled()
	{
		return keepInventoryWhenDismantled;
	}
	
	public static float getRainGenerationFactor()
	{
		return rainGenerationFactor;
	}
	
	public static float getThunderGenerationFactor()
	{
		return thunderGenerationFactor;
	}
	
	public static List<TierConfiguration> getTierConfigurations()
	{
		return mTierConfigurations;
	}
	
	public static TierConfiguration getTierConfiguration(int pTierIndex)
	{
		return mTierConfigurations.get(pTierIndex);
	}
	
	public static boolean isEfficiencyUpgradeActive()
	{
		return mEfficiencyUpgradeActive;
	}
	
	public static float getEfficiencyUpgradeIncrease()
	{
		return mEfficiencyUpgradeIncrease;
	}
	
	public static int getEfficiencyUpgradeMax()
	{
		return mEfficiencyUpgradeMax;
	}
	
	public static float getEfficiencyUpgradeReturnsToScale()
	{
		return mEfficiencyUpgradeReturnsToScale;
	}
	
	public static boolean isLowLightUpgradeActive()
	{
		return mLowLightUpgradeActive;
	}
	
	public static int getLowLightUpgradeMax()
	{
		return mLowLightUpgradeMax;
	}
	
	public static boolean isTraversalUpgradeActive()
	{
		return mTraversalUpgradeActive;
	}
	
	public static int getTraversalUpgradeIncrease()
	{
		return mTraversalUpgradeIncrease;
	}
	
	public static int getTraversalUpgradeMax()
	{
		return mTraversalUpgradeMax;
	}
	
	public static int getTraversalUpgradeUpdateRate()
	{
		return mTraversalUpgradeUpdateRate;
	}
	
	public static boolean isTransferRateUpgradeActive()
	{
		return mTransferRateUpgradeActive;
	}
	
	public static float getTransferRateUpgradeIncrease()
	{
		return mTransferRateUpgradeIncrease;
	}
	
	public static int getTransferRateUpgradeMax()
	{
		return mTransferRateUpgradeMax;
	}
	
	public static float getTransferRateUpgradeReturnsToScale()
	{
		return mTransferRateUpgradeReturnsToScale;
	}
	
	public static boolean isCapacityUpgradeActive()
	{
		return mCapacityUpgradeActive;
	}
	
	public static float getCapacityUpgradeIncrease()
	{
		return mCapacityUpgradeIncrease;
	}
	
	public static int getCapacityUpgradeMax()
	{
		return mCapacityUpgradeMax;
	}
	
	public static float getCapacityUpgradeReturnsToScale()
	{
		return mCapacityUpgradeReturnsToScale;
	}
	
	public static boolean isFurnaceUpgradeActive()
	{
		return mFurnaceUpgradeActive;
	}
	
	public static int getFurnaceUpgradeHeatingConsumption()
	{
		return mFurnaceUpgradeHeatingConsumption;
	}
	
	public static boolean useConnectedTextures()
	{
		return mConnectedTextures;
	}
	
	protected static float getSolarThickness()
	{
		return RemoteConfigs.inherited ? RemoteConfigs.getSolarHeight() : solarThickness;
	}
	
	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent pEvent)
	{
		if(pEvent.getModID().equalsIgnoreCase(InfoSFR.MOD_ID))
			loadConfiguration();
	}
}