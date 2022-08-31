package org.zeith.solarflux.panels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraftforge.common.util.INBTSerializable;
import org.zeith.solarflux.block.SolarPanelTile;
import org.zeith.solarflux.init.SolarPanelsSF;

public class SolarPanelInstance
		implements INBTSerializable<CompoundTag>
{
	public SolarPanel infoDelegate;
	public String delegate;
	public long gen, cap;
	public long transfer;
	public boolean valid = false;

	public SolarPanel getDelegate()
	{
		if(infoDelegate == null)
			return infoDelegate = SolarPanelsSF.PANELS.get(this.delegate);
		return infoDelegate;
	}

	public float computeSunIntensity(SolarPanelTile solar)
	{
		if(getDelegate() != null)
			return infoDelegate.computeSunIntensity(solar);

		// If delegate cannot be found for odd reason:

		if(!solar.doesSeeSky())
			return 0F;

		float celestialAngleRadians = solar.getLevel().getSunAngle(1F);
		if(celestialAngleRadians > Math.PI)
			celestialAngleRadians = (float) (2 * Math.PI - celestialAngleRadians);
		int lowLightCount = 0;
		float multiplicator = 1.5F - (lowLightCount * .122F);
		float displacement = 1.2F + (lowLightCount * .08F);

		return Mth.clamp(multiplicator * Mth.cos(celestialAngleRadians / displacement), 0, 1);
	}

	public boolean isValid()
	{
		return valid;
	}

	public void reset()
	{
		SolarPanel info = getDelegate();
		valid = info != null;
		if(valid)
			info.accept(this);
	}

	@Override
	public CompoundTag serializeNBT()
	{
		CompoundTag nbt = new CompoundTag();
		nbt.putString("Delegate", delegate.toString());
		return nbt;
	}

	public static SolarPanelInstance deserialize(CompoundTag nbt)
	{
		SolarPanelInstance inst = new SolarPanelInstance();
		inst.deserializeNBT(nbt);
		return inst;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		delegate = nbt.getString("Delegate");
		reset();
	}
}