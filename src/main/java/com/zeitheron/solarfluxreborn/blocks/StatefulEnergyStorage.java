package com.zeitheron.solarfluxreborn.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.IEnergyStorage;

import com.zeitheron.solarfluxreborn.reference.NBTConstants;

import cofh.redstoneflux.api.IEnergyReceiver;

public class StatefulEnergyStorage implements IEnergyStorage
{
	protected int energy;
	protected int capacity;
	protected int maxTransferReceive;
	protected int maxTransferExtract;
	
	public StatefulEnergyStorage(int pCapacity)
	{
		this(pCapacity, pCapacity, pCapacity);
	}
	
	public StatefulEnergyStorage(int pCapacity, int pMaxTransfer)
	{
		this(pCapacity, pMaxTransfer, pMaxTransfer);
	}
	
	public StatefulEnergyStorage(int pCapacity, int pMaxTransferReceive, int pMaxTransferExtract)
	{
		capacity = pCapacity;
		maxTransferReceive = pMaxTransferReceive;
		maxTransferExtract = pMaxTransferExtract;
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		setEnergyStored(nbt.getInteger(NBTConstants.ENERGY));
	}
	
	public void writeToNBT(NBTTagCompound pNbt)
	{
		pNbt.setInteger(NBTConstants.ENERGY, getEnergyStored());
	}
	
	/**
	 * Returns the maximum amount of energy the storage can receive.
	 */
	public int getMaxReceive()
	{
		return Math.min(capacity - energy, maxTransferReceive);
	}
	
	/**
	 * Returns the maximum amount of energy that can be extracted from the
	 * storage.
	 */
	public int getMaxExtract()
	{
		return Math.min(energy, maxTransferExtract);
	}
	
	@Override
	public int receiveEnergy(int pMaxReceive, boolean pSimulate)
	{
		int energyReceived = Math.min(getMaxReceive(), Math.max(pMaxReceive, 0));
		if(!pSimulate)
		{
			energy += energyReceived;
		}
		return energyReceived;
	}
	
	@Override
	public int extractEnergy(int pMaxExtract, boolean pSimulate)
	{
		int energyExtracted = Math.min(getMaxExtract(), Math.max(pMaxExtract, 0));
		if(!pSimulate)
		{
			energy -= energyExtracted;
		}
		return energyExtracted;
	}
	
	/**
	 * Sends the maximum amount of energy possible to the
	 * {@link IEnergyReceiver}
	 */
	public int sendMaxTo(IEnergyStorage pEnergyReceiver)
	{
		return extractEnergy(pEnergyReceiver.receiveEnergy(getMaxExtract(), false), false);
	}
	
	/**
	 * Tries to balance the amount of energy between the two
	 * {@link StatefulEnergyStorage}
	 */
	public int autoBalanceEnergy(StatefulEnergyStorage pOtherEnergyStorage)
	{
		int delta = getEnergyStored() - pOtherEnergyStorage.getEnergyStored();
		if(delta < 0)
		{
			return pOtherEnergyStorage.autoBalanceEnergy(this);
		} else if(delta > 0 && !pOtherEnergyStorage.isFull())
		{
			return extractEnergy(pOtherEnergyStorage.receiveEnergy(delta / 2, false), false);
		}
		return 0;
	}
	
	public int autoBalanceEnergy(StatefulEnergyStorage pOtherEnergyStorage, int pTransferSpeed)
	{
		maxTransferExtract *= pTransferSpeed;
		maxTransferReceive *= pTransferSpeed;
		pOtherEnergyStorage.maxTransferExtract *= pTransferSpeed;
		pOtherEnergyStorage.maxTransferReceive *= pTransferSpeed;
		int result = autoBalanceEnergy(pOtherEnergyStorage);
		maxTransferExtract /= pTransferSpeed;
		maxTransferReceive /= pTransferSpeed;
		pOtherEnergyStorage.maxTransferExtract /= pTransferSpeed;
		pOtherEnergyStorage.maxTransferReceive /= pTransferSpeed;
		return result;
	}
	
	@Override
	public int getEnergyStored()
	{
		return energy;
	}
	
	public void setEnergyStored(int pEnergy)
	{
		energy = pEnergy;
		if(energy > capacity)
		{
			energy = capacity;
		} else if(energy < 0)
		{
			energy = 0;
		}
	}
	
	@Override
	public int getMaxEnergyStored()
	{
		return capacity;
	}
	
	public void setMaxEnergyStored(int pCapacity)
	{
		capacity = pCapacity;
		if(energy > capacity)
		{
			energy = capacity;
		}
	}
	
	public boolean isFull()
	{
		return getEnergyStored() == getMaxEnergyStored();
	}
	
	public int getMaxTransferReceive()
	{
		return maxTransferReceive;
	}
	
	public void setMaxTransferReceive(int pMaxTransferReceive)
	{
		maxTransferReceive = pMaxTransferReceive;
	}
	
	public int getMaxTransferExtract()
	{
		return maxTransferExtract;
	}
	
	public void setMaxTransferExtract(int pMaxTransferExtract)
	{
		maxTransferExtract = pMaxTransferExtract;
	}
	
	public void setMaxTransfer(int pMaxTransfer)
	{
		setMaxTransferReceive(pMaxTransfer);
		setMaxTransferExtract(pMaxTransfer);
	}
	
	@Override
	public boolean canExtract()
	{
		return maxTransferExtract > 0;
	}
	
	@Override
	public boolean canReceive()
	{
		return maxTransferReceive > 0;
	}
}
