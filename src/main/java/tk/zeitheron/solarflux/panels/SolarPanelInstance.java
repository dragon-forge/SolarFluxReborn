package tk.zeitheron.solarflux.panels;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;
import tk.zeitheron.solarflux.block.SolarPanelTile;

public class SolarPanelInstance implements INBTSerializable<CompoundNBT>
{
	public SolarPanel infoDelegate;
	public String delegate;
	public long gen, cap;
	public long transfer;
	public boolean valid = false;
	
	public SolarPanel getDelegate()
	{
		if(infoDelegate == null)
			return infoDelegate = SolarPanels.PANELS.get(this.delegate);
		return infoDelegate;
	}
	
	public float computeSunIntensity(SolarPanelTile solar)
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
		SolarPanel info = getDelegate();
		valid = info != null;
		if(valid)
			info.accept(this);
	}
	
	@Override
	public CompoundNBT serializeNBT()
	{
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("Delegate", delegate.toString());
		return nbt;
	}
	
	public static SolarPanelInstance deserialize(CompoundNBT nbt)
	{
		SolarPanelInstance inst = new SolarPanelInstance();
		inst.deserializeNBT(nbt);
		return inst;
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt)
	{
		delegate = nbt.getString("Delegate");
		reset();
	}
}