package com.zeitheron.solarfluxreborn.te.cable;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;
import com.zeitheron.hammercore.internal.TeslaAPI;
import com.zeitheron.hammercore.internal.capabilities.CapabilityEJ;
import com.zeitheron.hammercore.utils.math.vec.Cuboid6;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileCustomCable extends TileAbstractCable implements Predicate<EnumFacing>
{
	public double capacityAdded;
	
	public Cuboid6[] cubs = null;
	
	public TileCustomCable()
	{
	}
	
	public TileCustomCable(double cap)
	{
		capacityAdded = cap;
	}
	
	private int connectionTimer = 0;
	
	@Override
	public void tick()
	{
		super.tick();
		if(connectionTimer++ % 20 == 0)
			cubs = bake();
	}
	
	@Override
	public double getCapacityAddedToNet()
	{
		return capacityAdded;
	}
	
	@Override
	public void writeNBT(NBTTagCompound nbt)
	{
		super.writeNBT(nbt);
		nbt.setDouble("CapacityAdded", capacityAdded);
	}
	
	@Override
	public void readNBT(NBTTagCompound nbt)
	{
		super.readNBT(nbt);
		capacityAdded = nbt.getDouble("CapacityAdded");
	}
	
	public Cuboid6[] bake()
	{
		List<Cuboid6> c = new ArrayList<>();
		
		c.add(new Cuboid6(6D / 16D, 6D / 16D, 6D / 16D, 10D / 16D, 10D / 16D, 10D / 16D));
		
		for(EnumFacing f : EnumFacing.VALUES)
		{
			boolean conn = false;
			TileEntity te = world.getTileEntity(pos.offset(f));
			if(te == null)
				continue;
			if(te != null && TileCustomCable.class.isAssignableFrom(te.getClass()))
				conn = true;
			else if(te.hasCapability(CapabilityEnergy.ENERGY, f.getOpposite()))
				conn = true;
			else if(TeslaAPI.isTeslaConsumer(te))
				conn = true;
			
			if(conn)
			{
				if(f == EnumFacing.UP)
					c.add(new Cuboid6(6D / 16D, 10D / 16D, 6D / 16D, 10D / 16D, 1D, 10D / 16D));
				if(f == EnumFacing.DOWN)
					c.add(new Cuboid6(6D / 16D, 0, 6D / 16D, 10D / 16D, 6D / 16D, 10D / 16D));
				if(f == EnumFacing.EAST)
					c.add(new Cuboid6(10D / 16D, 6D / 16D, 6D / 16D, 1D, 10D / 16D, 10D / 16D));
				if(f == EnumFacing.WEST)
					c.add(new Cuboid6(0D, 6D / 16D, 6D / 16D, 6D / 16D, 10D / 16D, 10D / 16D));
				if(f == EnumFacing.SOUTH)
					c.add(new Cuboid6(6D / 16D, 6D / 16D, 10D / 16D, 10D / 16D, 10D / 16D, 1D));
				if(f == EnumFacing.NORTH)
					c.add(new Cuboid6(6D / 16D, 6D / 16D, 0D, 10D / 16D, 10D / 16D, 6D / 16D));
			}
		}
		
		return c.toArray(new Cuboid6[c.size()]);
	}
	
	public Cuboid6[] getCuboids()
	{
		if(cubs == null)
			cubs = bake();
		return cubs;
	}
	
	@Override
	public boolean apply(EnumFacing input)
	{
		TileEntity tt = world.getTileEntity(pos.offset(input));
		if(tt == null)
			return false;
		if(tt.hasCapability(CapabilityEnergy.ENERGY, input.getOpposite()) || tt.hasCapability(CapabilityEJ.ENERGY, input.getOpposite()))
			return true;
		else if(TeslaAPI.isTeslaConsumer(tt))
			return true;
		return false;
	}
}