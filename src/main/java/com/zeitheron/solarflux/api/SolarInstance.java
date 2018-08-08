package com.zeitheron.solarflux.api;

import com.zeitheron.solarflux.block.tile.TileBaseSolar;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SolarInstance implements INBTSerializable<NBTTagCompound>
{
	public ResourceLocation delegate;
	public int gen, transfer, cap;
	public boolean valid = false;
	
	public SolarInfo getDelegate()
	{
		return SolarFluxAPI.SOLAR_PANELS.getValue(this.delegate);
	}
	
	public float computeSunIntensity(TileBaseSolar solar)
	{
		if(!solar.getWorld().canBlockSeeSky(solar.getPos()))
			return 0F;
		
		float celestialAngleRadians = solar.getWorld().getCelestialAngleRadians(1F);
		if(celestialAngleRadians > Math.PI)
			celestialAngleRadians = (float) (2 * Math.PI - celestialAngleRadians);
		int lowLightCount = 0;
		float multiplicator = 1.5F - (lowLightCount * .122F);
		float displacement = 1.2F + (lowLightCount * .08F);
		
		return MathHelper.clamp(multiplicator * MathHelper.cos(celestialAngleRadians / displacement), 0, 1);
	}
	
	public boolean isValid()
	{
		return valid;
	}
	
	public void reset()
	{
		SolarInfo info = getDelegate();
		valid = info != null;
		if(valid)
			info.accept(this);
	}
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("Delegate", delegate.toString());
		return nbt;
	}
	
	public static SolarInstance deserialize(NBTTagCompound nbt)
	{
		SolarInstance inst = new SolarInstance();
		inst.deserializeNBT(nbt);
		return inst;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		delegate = new ResourceLocation(nbt.getString("Delegate"));
		reset();
	}
}