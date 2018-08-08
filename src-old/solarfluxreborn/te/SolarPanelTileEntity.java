package com.zeitheron.solarfluxreborn.te;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.zeitheron.hammercore.internal.capabilities.CapabilityEJ;
import com.zeitheron.hammercore.tile.ITileDroppable;
import com.zeitheron.hammercore.tile.TileSyncableTickable;
import com.zeitheron.hammercore.tile.tooltip.ITooltipTile;
import com.zeitheron.hammercore.tile.tooltip.ProgressBar;
import com.zeitheron.hammercore.tile.tooltip.eNumberFormat;
import com.zeitheron.hammercore.tile.tooltip.eTooltipEngine;
import com.zeitheron.hammercore.utils.InterItemStack;
import com.zeitheron.hammercore.utils.WorldLocation;
import com.zeitheron.hammercore.utils.energy.IPowerStorage;
import com.zeitheron.hammercore.utils.inventory.InventoryDummy;
import com.zeitheron.solarfluxreborn.bars.energy.NumShortening;
import com.zeitheron.solarfluxreborn.bars.energy.ProgressBarColors;
import com.zeitheron.solarfluxreborn.blocks.SolarPanelBlock;
import com.zeitheron.solarfluxreborn.blocks.StatefulEnergyStorage;
import com.zeitheron.solarfluxreborn.blocks.modules.EnergySharingModule;
import com.zeitheron.solarfluxreborn.blocks.modules.SimpleEnergyDispenserModule;
import com.zeitheron.solarfluxreborn.blocks.modules.TraversalEnergyDispenserModule;
import com.zeitheron.solarfluxreborn.blocks.modules.iTileEntityModule;
import com.zeitheron.solarfluxreborn.config.ModConfiguration;
import com.zeitheron.solarfluxreborn.config.RemoteConfigs;
import com.zeitheron.solarfluxreborn.config.TierConfiguration;
import com.zeitheron.solarfluxreborn.init.ItemsSFR;
import com.zeitheron.solarfluxreborn.items.UpgradeItem;
import com.zeitheron.solarfluxreborn.reference.InfoSFR;
import com.zeitheron.solarfluxreborn.reference.NBTConstants;
import com.zeitheron.solarfluxreborn.utility.Lang;

import cofh.redstoneflux.api.IEnergyProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class SolarPanelTileEntity extends TileSyncableTickable implements IInventory, IPowerStorage, IEnergyStorage, ITileDroppable, IEnergyProvider, ITooltipTile
{
	public static final int INVENTORY_SIZE = 5;
	public static final Range<Integer> UPGRADE_SLOTS = Range.closedOpen(0, INVENTORY_SIZE);
	protected StatefulEnergyStorage mEnergyStorage;
	/**
	 * Index of this tier of SolarPanel.
	 */
	private int mTierIndex;
	private iTileEntityModule mEnergySharingModule;
	private iTileEntityModule mEnergyDispenserModule = new SimpleEnergyDispenserModule(this);
	/**
	 * The amount of RF currently generated per tick.
	 */
	private int mCurrentEnergyGeneration;
	
	/**
	 * The amount of light that the solar panel can see. This is only updated on
	 * the server and is sent to the client.
	 */
	private float mSunIntensity;
	
	private InventoryDummy mInventory = new InventoryDummy(INVENTORY_SIZE);
	private Map<Item, Integer> mUpgradeCache = Maps.newHashMap();
	
	public InventoryDummy getInventory()
	{
		return mInventory;
	}
	
	public SolarPanelTileEntity()
	{
		this(0);
	}
	
	public int getTier()
	{
		return mTierIndex;
	}
	
	public SolarPanelTileEntity(int pTierIndex)
	{
		mTierIndex = pTierIndex;
		mEnergyStorage = new StatefulEnergyStorage(getCapacity(), getTransfer());
		if(ModConfiguration.doesAutoBalanceEnergy())
			mEnergySharingModule = new EnergySharingModule(this);
	}
	
	private TierConfiguration getTierConfiguration()
	{
		return ModConfiguration.getTierConfigurations().get(mTierIndex);
	}
	
	@Override
	public void tick()
	{
		updateCurrentEnergyGeneration(!world.isRemote ? pos.up() : pos);
		if(!world.isRemote)
		{
			if(atTickRate(20))
				refreshUpgradeCache();
			
			mEnergyDispenserModule.tick();
			generateEnergy();
			if(mEnergySharingModule != null)
				mEnergySharingModule.tick();
			if(atTickRate(20))
				sync();
		}
	}
	
	/**
	 * Returns the internal RF storage of the SolarPanel.
	 */
	public StatefulEnergyStorage getEnergyStorage()
	{
		return mEnergyStorage;
	}
	
	public int getCurrentEnergyGeneration()
	{
		return mCurrentEnergyGeneration;
	}
	
	/**
	 * Updates the amount of RF currently generated per tick. This must only be
	 * used on the client to set the value received by the server.
	 */
	public void setCurrentEnergyGeneration(int pCurrentEnergyGeneration)
	{
		mCurrentEnergyGeneration = pCurrentEnergyGeneration;
	}
	
	public int getMaximumEnergyGeneration()
	{
		return getMaxGen();
	}
	
	/**
	 * Updates the current amount of RF generated per tick.
	 */
	public void updateCurrentEnergyGeneration(BlockPos pos)
	{
		computeSunIntensity(pos);
		double energyGeneration = getMaxGen() * mSunIntensity;
		energyGeneration *= (1 + ModConfiguration.getEfficiencyUpgradeIncrease() * Math.pow(getUpgradeCount(ItemsSFR.mUpgradeEfficiency), ModConfiguration.getEfficiencyUpgradeReturnsToScale()));
		mCurrentEnergyGeneration = (int) Math.round(energyGeneration);
	}
	
	public float getSunIntensity()
	{
		return mSunIntensity;
	}
	
	/**
	 * This must only be used on the client to set the value received by the
	 * server.
	 */
	public void setSunIntensity(float pSunIntensity)
	{
		mSunIntensity = pSunIntensity;
	}
	
	/**
	 * Compute the intensity of the sun that can be used by the Solar Panel.
	 */
	public void computeSunIntensity(BlockPos at)
	{
		if(world.canBlockSeeSky(at))
		{
			// Intensity based on the position of the sun.
			float multiplicator = 1.5f - (getUpgradeCount(ItemsSFR.mUpgradeLowLight) * 0.122f);
			float displacement = 1.2f + (getUpgradeCount(ItemsSFR.mUpgradeLowLight) * 0.08f);
			// Celestial angle == 0 at zenith.
			float celestialAngleRadians = world.getCelestialAngleRadians(1.0f);
			if(celestialAngleRadians > Math.PI)
				celestialAngleRadians = (float) (2 * Math.PI - celestialAngleRadians);
			
			mSunIntensity = multiplicator * MathHelper.cos(celestialAngleRadians / displacement);
			mSunIntensity = Math.max(0, mSunIntensity);
			mSunIntensity = Math.min(1, mSunIntensity);
			
			if(mSunIntensity > 0)
			{
				if(world.isRaining())
				{
					mSunIntensity *= ModConfiguration.getRainGenerationFactor();
				}
				if(world.isThundering())
				{
					mSunIntensity *= ModConfiguration.getThunderGenerationFactor();
				}
			}
		} else
		{
			mSunIntensity = 0;
		}
	}
	
	protected void generateEnergy()
	{
		if(mCurrentEnergyGeneration > 0)
		{
			getEnergyStorage().receiveEnergy(mCurrentEnergyGeneration, false);
		}
	}
	
	/**
	 * Goes through the inventory and counts the number of each upgrade.
	 */
	private void refreshUpgradeCache()
	{
		mUpgradeCache.clear();
		for(int i = 0; i < getSizeInventory(); ++i)
		{
			ItemStack itemStack = getStackInSlot(i);
			if(itemStack != null && itemStack.getItem() instanceof UpgradeItem)
			{
				if(mUpgradeCache.containsKey(itemStack.getItem()))
				{
					mUpgradeCache.put(itemStack.getItem(), itemStack.getCount() + mUpgradeCache.get(itemStack.getItem()));
				} else
				{
					mUpgradeCache.put(itemStack.getItem(), itemStack.getCount());
				}
			}
		}
		
		// Do we have any upgrade for traversal?
		if(getUpgradeCount(ItemsSFR.mUpgradeTraversal) > 0 && mEnergyDispenserModule instanceof SimpleEnergyDispenserModule)
		{
			mEnergyDispenserModule = new TraversalEnergyDispenserModule(this);
		} else if(getUpgradeCount(ItemsSFR.mUpgradeTraversal) == 0 && mEnergyDispenserModule instanceof TraversalEnergyDispenserModule)
		{
			mEnergyDispenserModule = new SimpleEnergyDispenserModule(this);
		}
		
		// Apply effect of transfer rate upgrade.
		getEnergyStorage().setMaxTransfer((int) (getTransfer() * (1 + ModConfiguration.getTransferRateUpgradeIncrease() * Math.pow(getUpgradeCount(ItemsSFR.mUpgradeTransferRate), ModConfiguration.getTransferRateUpgradeReturnsToScale()))));
		
		// Apply effect of capacity upgrade
		getEnergyStorage().setMaxEnergyStored((int) (getCapacity() * (1 + ModConfiguration.getCapacityUpgradeIncrease() * Math.pow(getUpgradeCount(ItemsSFR.mUpgradeCapacity), ModConfiguration.getCapacityUpgradeReturnsToScale()))));
	}
	
	public int getTransfer()
	{
		if(this instanceof AbstractSolarPanelTileEntity)
			return ((AbstractSolarPanelTileEntity) this).transfer;
		return RemoteConfigs.getTierConfiguration(mTierIndex).getMaximumEnergyTransfer();
	}
	
	public int getMaxGen()
	{
		if(this instanceof AbstractSolarPanelTileEntity)
			return ((AbstractSolarPanelTileEntity) this).getMaximumEnergyGeneration();
		return RemoteConfigs.getTierConfiguration(mTierIndex).getMaximumEnergyGeneration();
	}
	
	public int getCapacity()
	{
		if(this instanceof AbstractSolarPanelTileEntity)
			return ((AbstractSolarPanelTileEntity) this).cap;
		return RemoteConfigs.getTierConfiguration(mTierIndex).getCapacity();
	}
	
	public int getTotalUpgradeInstalled()
	{
		int count = 0;
		for(int value : mUpgradeCache.values())
			count += value;
		return count;
	}
	
	/**
	 * Returns the number of upgrade that can still be added.
	 */
	public int additionalUpgradeAllowed(ItemStack pItemStack)
	{
		if(pItemStack != null)
		{
			Item item = pItemStack.getItem();
			if(item instanceof UpgradeItem)
			{
				UpgradeItem upgrade = (UpgradeItem) item;
				return upgrade.getMaximumPerSolarPanel() - getUpgradeCount(upgrade);
			}
		}
		return 0;
	}
	
	public int getUpgradeCount(Item pItem)
	{
		if(pItem != null)
		{
			Integer count = mUpgradeCache.get(pItem);
			return count == null ? 0 : count;
		}
		return 0;
	}
	
	protected void loadDataFromNBT(NBTTagCompound pNBT)
	{
		mTierIndex = pNBT.getInteger(NBTConstants.TIER_INDEX);
		mEnergyStorage.setMaxEnergyStored(getCapacity());
		mEnergyStorage.setMaxTransfer(getTransfer());
		mInventory.readFromNBT(pNBT);
		refreshUpgradeCache();
		mEnergyStorage.readFromNBT(pNBT);
		mCurrentEnergyGeneration = pNBT.getInteger("ÑurrentGen");
		
		if(this instanceof AbstractSolarPanelTileEntity)
		{
			AbstractSolarPanelTileEntity t = (AbstractSolarPanelTileEntity) this;
			t.maxGen = pNBT.getInteger("MaxGen");
			t.transfer = pNBT.getInteger("MaxTransfer");
			t.cap = pNBT.getInteger("MaxCap");
		}
	}
	
	protected void addDataToNBT(NBTTagCompound pNBT)
	{
		pNBT.setInteger(NBTConstants.TIER_INDEX, mTierIndex);
		mInventory.writeToNBT(pNBT);
		mEnergyStorage.writeToNBT(pNBT);
	}
	
	public void setEnergyStored(int pEnergy)
	{
		getEnergyStorage().setEnergyStored(pEnergy);
	}
	
	public double getScaledEnergyStoredFraction(int pScale)
	{
		double v = pScale;
		v *= (double) getEnergyStored();
		v /= (double) getMaxEnergyStored();
		return v;
	}
	
	public int getPercentageEnergyStored()
	{
		return (int) getScaledEnergyStoredFraction(100);
	}
	
	@Override
	public int getEnergyStored()
	{
		return getEnergyStorage().getEnergyStored();
	}
	
	@Override
	public int getMaxEnergyStored()
	{
		return getEnergyStorage().getMaxEnergyStored();
	}
	
	@Override
	public int getSizeInventory()
	{
		return mInventory.getSizeInventory();
	}
	
	@Override
	public ItemStack getStackInSlot(int pSlotIndex)
	{
		if(pSlotIndex < 0 || pSlotIndex >= mInventory.getSizeInventory())
			return InterItemStack.NULL_STACK;
		return mInventory.getStackInSlot(pSlotIndex);
	}
	
	@Override
	public ItemStack decrStackSize(int pSlotIndex, int pDecrementAmount)
	{
		return mInventory.decrStackSize(pSlotIndex, pDecrementAmount);
	}
	
	@Override
	public void setInventorySlotContents(int pSlotIndex, ItemStack pItemStack)
	{
		mInventory.setInventorySlotContents(pSlotIndex, pItemStack);
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return mInventory.getInventoryStackLimit();
	}
	
	@Override
	public boolean isUsableByPlayer(EntityPlayer pEntityPlayer)
	{
		return mInventory.isUsableByPlayer(pEntityPlayer, pos);
	}
	
	@Override
	public boolean isItemValidForSlot(int pSlotIndex, ItemStack pItemStack)
	{
		return pItemStack.getItem() instanceof UpgradeItem && additionalUpgradeAllowed(pItemStack) >= pItemStack.getCount();
	}
	
	@Override
	public void markDirty()
	{
		super.markDirty();
		refreshUpgradeCache();
	}
	
	@Override
	public ITextComponent getDisplayName()
	{
		return mInventory.getDisplayName();
	}
	
	@Override
	public String getName()
	{
		return mInventory.getName();
	}
	
	@Override
	public boolean hasCustomName()
	{
		return mInventory.hasCustomName();
	}
	
	@Override
	public void clear()
	{
		mInventory.clear();
	}
	
	@Override
	public void closeInventory(EntityPlayer arg0)
	{
		mInventory.closeInventory(arg0);
	}
	
	@Override
	public int getField(int arg0)
	{
		return mInventory.getField(arg0);
	}
	
	@Override
	public int getFieldCount()
	{
		return mInventory.getFieldCount();
	}
	
	@Override
	public void openInventory(EntityPlayer arg0)
	{
		mInventory.openInventory(arg0);
	}
	
	@Override
	public ItemStack removeStackFromSlot(int arg0)
	{
		return mInventory.removeStackFromSlot(arg0);
	}
	
	@Override
	public void setField(int arg0, int arg1)
	{
		mInventory.setField(arg0, arg1);
	}
	
	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		loadDataFromNBT(nbt);
	}
	
	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		nbt.setInteger(NBTConstants.TIER_INDEX, mTierIndex);
		mInventory.writeToNBT(nbt);
		mEnergyStorage.writeToNBT(nbt);
		nbt.setInteger("ÑurrentGen", mCurrentEnergyGeneration);
		if(this instanceof AbstractSolarPanelTileEntity)
		{
			AbstractSolarPanelTileEntity t = (AbstractSolarPanelTileEntity) this;
			nbt.setInteger("MaxGen", t.getMaxGen());
			nbt.setInteger("MaxTransfer", t.transfer);
			nbt.setInteger("MaxCap", t.cap);
		}
	}
	
	public String getTopResource()
	{
		return InfoSFR.MOD_ID + ":blocks/solar" + mTierIndex + "_top";
	}
	
	public String getBaseResource()
	{
		return InfoSFR.MOD_ID + ":blocks/solar" + mTierIndex + "_base";
	}
	
	@Override
	public boolean isEmpty()
	{
		return getInventory().isEmpty();
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if((capability == CapabilityEnergy.ENERGY || capability == CapabilityEJ.ENERGY) && facing != EnumFacing.UP)
			return true;
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if((capability == CapabilityEnergy.ENERGY || capability == CapabilityEJ.ENERGY) && facing != EnumFacing.UP)
			return (T) this;
		return super.getCapability(capability, facing);
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		return 0;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return getEnergyStorage().extractEnergy(maxExtract, simulate);
	}
	
	@Override
	public boolean canExtract()
	{
		return true;
	}
	
	@Override
	public boolean canReceive()
	{
		return false;
	}
	
	@Override
	public void createDrop(EntityPlayer player, World world, BlockPos pos)
	{
		WorldLocation loc = new WorldLocation(world, pos);
		if(player != null && !player.capabilities.isCreativeMode && loc.getBlock() instanceof SolarPanelBlock)
			((SolarPanelBlock) loc.getBlock()).dismantleBlock(world, pos);
	}
	
	@Override
	public int getEnergyStored(EnumFacing from)
	{
		return from != EnumFacing.UP ? getEnergyStored() : 0;
	}
	
	@Override
	public int getMaxEnergyStored(EnumFacing from)
	{
		return from != EnumFacing.UP ? getMaxEnergyStored() : 0;
	}
	
	@Override
	public boolean canConnectEnergy(EnumFacing from)
	{
		return from != EnumFacing.UP;
	}
	
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate)
	{
		return from != EnumFacing.UP ? extractEnergy(maxExtract, simulate) : 0;
	}
	
	@Override
	public boolean isEngineSupported(eTooltipEngine engine)
	{
		return engine == eTooltipEngine.WAILA;
	}
	
	@Override
	public void getTextTooltip(List<String> list, EntityPlayer player)
	{
		list.add(Lang.localise("energy.capacity") + ": " + NumShortening.shorten(getMaxEnergyStored(), 4) + " RF");
		list.add(Lang.localise("energy.generation") + ": " + NumShortening.shorten(getCurrentEnergyGeneration(), 4) + " RF/t");
	}
	
	@Override
	public ProgressBar[] getProgressBars(EntityPlayer player)
	{
		ProgressBar stored = ProgressBarColors.makeEnergyBar(getEnergyStored(), getCapacity(), ProgressBarColors.client_getPrefferedEnergyUnit(), Lang.localise("energy.stored") + ": " + NumShortening.shorten(getEnergyStored(), 2));
		stored.suffix = " RF";
		
		long prog = Math.round(getCurrentEnergyGeneration() / (double) getMaximumEnergyGeneration() * 100);
		ProgressBar efficiency = new ProgressBar(100).setProgress(prog);
		efficiency.prefix = Lang.localise("energy.efficiency") + ": " + prog + "%";
		efficiency.numberFormat = eNumberFormat.NONE;
		
		ProgressBarColors.YELLOW_BAR.apply(efficiency);
		
		return new ProgressBar[] { stored, efficiency };
	}
	
	@Override
	public boolean hasProgressBars(EntityPlayer player)
	{
		return true;
	}
}