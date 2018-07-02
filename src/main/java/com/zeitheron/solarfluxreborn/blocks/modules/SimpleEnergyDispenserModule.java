package com.zeitheron.solarfluxreborn.blocks.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.zeitheron.hammercore.internal.TeslaAPI;
import com.zeitheron.solarfluxreborn.blocks.StatefulEnergyStorage;
import com.zeitheron.solarfluxreborn.config.ModConfiguration;
import com.zeitheron.solarfluxreborn.init.ItemsSFR;
import com.zeitheron.solarfluxreborn.te.AbstractSolarPanelTileEntity;
import com.zeitheron.solarfluxreborn.te.SolarPanelTileEntity;

import net.minecraft.block.BlockFurnace;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Module used to distribute energy to neighbor blocks.
 */
public class SimpleEnergyDispenserModule extends AbstractSolarPanelModule
{
	private final List<BlockPos> mTargets = Lists.newArrayList();
	private final Map<BlockPos, EnumFacing> mFacings = new HashMap<BlockPos, EnumFacing>();
	private int mTargetStartingIndex;
	private int mFurnaceEnergyBuffer;
	
	public SimpleEnergyDispenserModule(SolarPanelTileEntity pSolarPanelTileEntity)
	{
		super(pSolarPanelTileEntity);
	}
	
	@Override
	public void tick()
	{
		sendEnergyToTargets();
		if(atRate(getTargetRefreshRate()))
		{
			searchTargets();
		}
	}
	
	protected int getTargetRefreshRate()
	{
		// TODO Should this be a config option?
		return 20;
	}
	
	protected void searchTargets()
	{
		mTargets.clear();
		BlockPos position = getTileEntity().getPos();
		for(EnumFacing direction : EnumFacing.VALUES)
		{
			BlockPos neighbor = position.offset(direction);
			if(isValidTarget(neighbor, direction))
			{
				mTargets.add(neighbor);
				mFacings.put(neighbor, direction);
			}
		}
	}
	
	protected List<BlockPos> getTargets()
	{
		return mTargets;
	}
	
	protected Map<BlockPos, EnumFacing> getmFacings()
	{
		return mFacings;
	}
	
	protected void sendEnergyToTargets()
	{
		if(mTargets.size() > 0 && getTileEntity().getEnergyStored() > 0)
		{
			for(int i = 0; i < mTargets.size(); ++i)
			{
				BlockPos pos = mTargets.get((mTargetStartingIndex + i) % mTargets.size());
				sendEnergyTo(pos, mFacings.get(pos));
			}
			mTargetStartingIndex = (mTargetStartingIndex + 1) % mTargets.size();
		}
	}
	
	protected boolean isValidTarget(BlockPos pos, EnumFacing to)
	{
		TileEntity tile = getTileEntity().getWorld().getTileEntity(pos);
		if(tile == null)
			return false;
		if(tile.hasCapability(CapabilityEnergy.ENERGY, to.getOpposite()))
			return true;
		if(TeslaAPI.isTeslaConsumer(tile))
			return true;
		return getTileEntity().getUpgradeCount(ItemsSFR.mUpgradeFurnace) > 0 && tile instanceof TileEntityFurnace;
	}
	
	protected void sendEnergyTo(BlockPos pos, EnumFacing to)
	{
		TileEntity tile = getTileEntity().getWorld().getTileEntity(pos);
		if(tile == null)
			return;
		
		if(tile.hasCapability(CapabilityEnergy.ENERGY, to.getOpposite()))
			sendEnergyToFE(tile, to);
		else if(getTileEntity().getUpgradeCount(ItemsSFR.mUpgradeFurnace) > 0 && tile instanceof TileEntityFurnace)
			sendEnergyToFurnace((TileEntityFurnace) tile);
		else if(TeslaAPI.isTeslaConsumer(tile))
			sendEnergyToTeslaReceiver(tile, to.getOpposite());
	}
	
	protected void sendEnergyToReceiver(IEnergyStorage pEnergyReceiver)
	{
		getTileEntity().getEnergyStorage().sendMaxTo(pEnergyReceiver);
	}
	
	protected void sendEnergyToTeslaReceiver(TileEntity t, EnumFacing pFrom)
	{
		if(TeslaAPI.isTeslaConsumer(t))
		{
			StatefulEnergyStorage storage = getTileEntity().getEnergyStorage();
			storage.extractEnergy((int) TeslaAPI.givePowerToConsumer(t, Math.min(storage.getMaxExtract(), storage.getEnergyStored()), false), false);
		}
	}
	
	protected void sendEnergyToFE(TileEntity t, EnumFacing pFrom)
	{
		if(t != null && t.hasCapability(CapabilityEnergy.ENERGY, pFrom.getOpposite()))
		{
			StatefulEnergyStorage storage = getTileEntity().getEnergyStorage();
			IEnergyStorage ies = t.getCapability(CapabilityEnergy.ENERGY, pFrom.getOpposite());
			storage.extractEnergy(ies.receiveEnergy(Math.min(storage.getMaxExtract(), storage.getEnergyStored()), false), false);
		}
	}
	
	protected void sendEnergyToFurnace(TileEntityFurnace pFurnace)
	{
		final int FURNACE_COOKING_TICKS = 200;
		final int FURNACE_COOKING_ENERGY = FURNACE_COOKING_TICKS * ModConfiguration.getFurnaceUpgradeHeatingConsumption();
		
		if(mFurnaceEnergyBuffer < FURNACE_COOKING_ENERGY)
			mFurnaceEnergyBuffer += getTileEntity().getEnergyStorage().extractEnergy(FURNACE_COOKING_ENERGY - mFurnaceEnergyBuffer, false);
		
		SolarPanelTileEntity solar = getTileEntity();
		if((solar.getTier() > 0 || solar instanceof AbstractSolarPanelTileEntity) && pFurnace.isBurning())
		{
			int tier = solar.getTier();
			
			if(solar instanceof AbstractSolarPanelTileEntity)
				tier = 100;
			
			if(pFurnace.getField(2) < 200)
				pFurnace.setField(2, pFurnace.getField(2) + tier);
			if(pFurnace.getField(2) >= 200)
			{
				pFurnace.smeltItem();
				pFurnace.setField(2, 0);
			}
		}
		
		// Is there anything to smelt?
		if(pFurnace.getStackInSlot(0) != null && pFurnace.getField(0) < FURNACE_COOKING_TICKS)
		{
			int burnTicksAvailable = mFurnaceEnergyBuffer / ModConfiguration.getFurnaceUpgradeHeatingConsumption();
			if(burnTicksAvailable >= FURNACE_COOKING_TICKS)
			{
				if(pFurnace.getField(0) == 0)
				{
					// Add 1 as first tick is not counted in the burning
					// process.
					pFurnace.setField(0, pFurnace.getField(0) + 1);
					BlockFurnace.setState(pFurnace.getField(0) > 0, pFurnace.getWorld(), pFurnace.getPos());
				}
				pFurnace.setField(0, pFurnace.getField(0) + FURNACE_COOKING_TICKS);
				mFurnaceEnergyBuffer -= FURNACE_COOKING_TICKS * ModConfiguration.getFurnaceUpgradeHeatingConsumption();
			}
		}
	}
}
