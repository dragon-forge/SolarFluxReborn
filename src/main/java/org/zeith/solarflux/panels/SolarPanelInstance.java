package org.zeith.solarflux.panels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraftforge.common.util.INBTSerializable;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.init.SolarPanelsSF;

/**
 * A SolarPanel instance represents a single instance of a SolarPanel block in the world. (wrapped in {@link ISolarPanelTile})
 * It stores the SolarPanel info delegate for the block, as well as its own generation rate, capacity, and transfer rate.
 *
 * @author Zeith
 */
public class SolarPanelInstance
		implements INBTSerializable<CompoundTag>
{
	/**
	 * The {@link ISolarPanelTile} info delegate for this instance.
	 */
	public SolarPanel infoDelegate;
	/**
	 * The string identifier for the {@link ISolarPanelTile} info delegate for this instance.
	 */
	public String delegate;
	/**
	 * The generation rate of this {@link ISolarPanelTile} instance, in FE per tick.
	 */
	public long gen;
	/**
	 * The capacity of this {@link ISolarPanelTile} instance, in FE.
	 */
	public long cap;
	/**
	 * The transfer rate of this {@link ISolarPanelTile} instance, in FE per tick.
	 */
	public long transfer;
	/**
	 * Whether this {@link ISolarPanelTile} instance is valid.
	 */
	public boolean valid = false;
	
	/**
	 * Returns the SolarPanel info delegate for this instance.
	 * If the info delegate has not been set, it will attempt to retrieve it using the delegate string identifier.
	 *
	 * @return the SolarPanel info delegate for this instance
	 */
	public SolarPanel getDelegate()
	{
		if(infoDelegate == null)
			return infoDelegate = SolarPanelsSF.PANELS.get(this.delegate);
		return infoDelegate;
	}
	
	/**
	 * Computes the sun intensity for this SolarPanel instance.
	 * This is used to determine how much energy the SolarPanel should generate.
	 *
	 * @param solar
	 * 		the {@link ISolarPanelTile} representing this SolarPanel instance in the world
	 *
	 * @return a float value between 0 and 1 representing the sun intensity for this SolarPanel instance
	 */
	public float computeSunIntensity(ISolarPanelTile solar)
	{
		if(getDelegate() != null)
			return infoDelegate.computeSunIntensity(solar);
		
		// If delegate cannot be found for odd reason:
		
		if(!solar.doesSeeSky())
			return 0F;
		
		float celestialAngleRadians = solar.level().getSunAngle(1F);
		if(celestialAngleRadians > Math.PI)
			celestialAngleRadians = (float) (2 * Math.PI - celestialAngleRadians);
		int lowLightCount = 0;
		float multiplicator = 1.5F - (lowLightCount * .122F);
		float displacement = 1.2F + (lowLightCount * .08F);
		
		return Mth.clamp(multiplicator * Mth.cos(celestialAngleRadians / displacement), 0, 1);
	}
	
	/**
	 * Returns whether this {@link ISolarPanelTile} instance is valid.
	 * An instance is considered valid if it has a valid SolarPanel info delegate.
	 *
	 * @return whether this SolarPanel instance is valid
	 */
	public boolean isValid()
	{
		return valid;
	}
	
	/**
	 * Resets this SolarPanel instance by setting the valid flag and applying the settings from the info delegate.
	 */
	public void reset()
	{
		SolarPanel info = getDelegate();
		valid = info != null;
		if(valid)
			info.accept(this);
	}
	
	/**
	 * Deserializes a SolarPanelInstance from a CompoundTag.
	 *
	 * @param nbt
	 * 		the CompoundTag to deserialize from
	 *
	 * @return the deserialized SolarPanelInstance
	 */
	public static SolarPanelInstance deserialize(CompoundTag nbt)
	{
		SolarPanelInstance inst = new SolarPanelInstance();
		inst.deserializeNBT(nbt);
		return inst;
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag nbt = new CompoundTag();
		nbt.putString("Delegate", delegate.toString());
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		delegate = nbt.getString("Delegate");
		reset();
	}
}