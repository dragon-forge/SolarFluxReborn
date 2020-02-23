package tk.zeitheron.solarflux.api;

import tk.zeitheron.solarflux.block.tile.TileBaseSolar;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;

public class SolarInstance implements INBTSerializable<NBTTagCompound>
{
	public SolarInfo infoDelegate;
	public ResourceLocation delegate;
	public long gen, cap, transfer;
	public boolean valid = false;
	
	public SolarInfo getDelegate()
	{
		if(infoDelegate == null)
			return infoDelegate = SolarFluxAPI.SOLAR_PANELS.getValue(this.delegate);
		return infoDelegate;
	}
	
	public float computeSunIntensity(TileBaseSolar solar)
	{
		if(getDelegate() != null)
			return infoDelegate.computeSunIntensity(solar);
		
		// If delegate cannot be found for odd reason:
		
		if(!solar.doesSeeSky())
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