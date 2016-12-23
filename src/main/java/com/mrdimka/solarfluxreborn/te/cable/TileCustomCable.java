package com.mrdimka.solarfluxreborn.te.cable;

import com.mrdimka.solarfluxreborn.reference.Reference;

import net.minecraft.nbt.NBTTagCompound;

public class TileCustomCable extends TileAbstractCable
{
	public double capacityAdded;
	public int tier;
	
	public TileCustomCable() {}
	public TileCustomCable(double cap, int tier) { this.tier = tier; capacityAdded = cap; }
	
	@Override
	public double getCapacityAddedToNet()
	{
		return capacityAdded;
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt)
	{
		super.writeCustomNBT(nbt);
		nbt.setDouble("CapacityAdded", capacityAdded);
		nbt.setInteger("CapTier", tier);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt)
	{
		super.readCustomNBT(nbt);
		capacityAdded = nbt.getDouble("CapacityAdded");
		tier = nbt.getInteger("CapTier");
	}
	
	@Override
	public String getResourceConnection()
	{
		return Reference.MOD_ID.toLowerCase() + ":blocks/wire_" + (tier - 1);
	}
}