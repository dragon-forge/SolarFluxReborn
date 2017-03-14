package com.mrdimka.solarfluxreborn.te.cable;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

import com.mrdimka.common.utils.CommonTileEntity_SFR;
import com.mrdimka.hammercore.ext.TeslaAPI;
import com.mrdimka.solarfluxreborn.network.energy.cable.CableNetwork;
import com.mrdimka.solarfluxreborn.reference.Reference;

public class TileAbstractCable extends CommonTileEntity_SFR implements IEnergyReceiver, IEnergyProvider, IEnergyStorage
{
	public CableNetwork network;
	public double internal;
	
	@Override
	public void update()
	{
		if(network == null)
		{
			Set<CableNetwork> nets = new HashSet<CableNetwork>();
			for(EnumFacing f : EnumFacing.VALUES)
			{
				TileEntity t = world.getTileEntity(pos.offset(f));
				if(t instanceof TileAbstractCable)
				{
					TileAbstractCable w = (TileAbstractCable) t;
					if(w.network != null)
						nets.add(w.network);
				}
			}
			
			if(nets.size() > 0)
			{
				CableNetwork netC = null;
				int size = 0;
				
				for(CableNetwork n : nets.toArray(new CableNetwork[0]))
				{
					if(n.wires.size() > size)
					{
						size = n.wires.size();
						netC = n;
					}else if(n.wires.size() == size) netC = n;
				}
				
				network = netC;
			}
			
			if(network == null) network = new CableNetwork();
			
			network.connect(this);
		}else
		{
			network.connect(this);
			network.markTicked(this);
			
			for(EnumFacing f : EnumFacing.VALUES)
			{
				TileEntity t = world.getTileEntity(pos.offset(f));
				if(t instanceof TileAbstractCable)
				{
					TileAbstractCable w = (TileAbstractCable) t;
					if(w.network != null && w.network != network)
					{
						if(w.network.wires.size() > network.wires.size())
							w.network.merge(network);
						else network.merge(w.network);
					}
				}
			}
		}
		
		BlockPos p;
		if(network != null && network.energy > 0) for(EnumFacing f : EnumFacing.VALUES)
		{
			p = getPos().offset(f);
			TileEntity t = world.getTileEntity(p);
			if(t instanceof TileAbstractCable || t == null) continue;
			
			if(t instanceof IEnergyReceiver)
			{
				IEnergyReceiver r = (IEnergyReceiver) t;
				
				if(r.canConnectEnergy(f.getOpposite()))
				{
					int sent = r.receiveEnergy(f.getOpposite(), Math.min((int) getCapacityAddedToNet(), (int) network.energy), false);
					network.energy -= sent;
				}
			}else if(t.hasCapability(CapabilityEnergy.ENERGY, f.getOpposite()))
			{
				IEnergyStorage r = t.getCapability(CapabilityEnergy.ENERGY, f.getOpposite());
				
				int sent = r.receiveEnergy(Math.min((int) getCapacityAddedToNet(), (int) network.energy), false);
				network.energy -= sent;
			}else if(TeslaAPI.isTeslaConsumer(t))
			{
				int sent = (int) TeslaAPI.givePowerToConsumer(t, Math.min((int) getCapacityAddedToNet(), (int) network.energy), false);
				network.energy -= sent;
			}
		}
	}
	
	public double getCapacityAddedToNet()
	{
		return 80D;
	}
	
	@Override
	public boolean canConnectEnergy(EnumFacing facing)
	{
		return true;
	}

	@Override
	public int getEnergyStored(EnumFacing facing)
	{
		if(network == null) return 0;
		return (int) network.energy;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing facing)
	{
		if(network == null) return 0;
		return (int) network.capacity;
	}

	@Override
	public int receiveEnergy(EnumFacing facing, int maxReceive, boolean simulate)
	{
		if(network == null) return 0;
		int energyReceived = (int) Math.min(network.capacity - network.energy, maxReceive);
        if(!simulate)
        	network.energy += energyReceived;
        return energyReceived;
	}
	
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate)
	{
		if(network == null) return 0;
		int energyGiven = (int) Math.min(network.energy, maxExtract);
        if(!simulate) network.energy -= energyGiven;
        return energyGiven;
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt)
	{
		internal = nbt.getDouble("EnergyStored");
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt)
	{
		nbt.setDouble("EnergyStored", internal);
	}
	
	boolean wasUnloaded = false;
	
	@Override
	public void onChunkUnload()
	{
		super.onChunkUnload();
		wasUnloaded = true;
		
		if(network != null)
		{
			network.splitEnergyTo1Wire(this);
			network.disconnect(this);
		}
	}
	
	@Override
	public void invalidate()
	{
		super.invalidate();
		
		if(!wasUnloaded && network != null)
		{
			CableNetwork network = this.network;
			network.disconnect(this);
			network.energy += internal;
		}
	}
	
	public String getResourceConnection()
	{
		return Reference.MOD_ID + ":blocks/wire_1";
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY) return true;
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY) return (T) this;
		return super.getCapability(capability, facing);
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		return receiveEnergy(EnumFacing.DOWN, maxReceive, simulate);
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return extractEnergy(EnumFacing.DOWN, maxExtract, simulate);
	}
	
	@Override
	public int getEnergyStored()
	{
		return getEnergyStored(EnumFacing.DOWN);
	}
	
	@Override
	public int getMaxEnergyStored()
	{
		return getMaxEnergyStored(EnumFacing.DOWN);
	}
	
	@Override
	public boolean canExtract()
	{
		return true;
	}
	
	@Override
	public boolean canReceive()
	{
		return true;
	}
}