package tk.zeitheron.solarflux.api.attribute;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants.NBT;

public class SimpleAttributeProperty implements IAttributeProperty
{
	public static final Logger LOG = LogManager.getLogger("SolarFlux");
	
	protected double value;
	protected double base;
	
	protected boolean dirty = true;
	
	protected final EnumMap<EnumAttributeLayer, List<IAttributeMod>> mods = new EnumMap<>(EnumAttributeLayer.class);
	protected final BiMap<UUID, IAttributeMod> modsById = HashBiMap.create();
	
	{
		for(EnumAttributeLayer l : EnumAttributeLayer.values())
			mods.put(l, new ArrayList<>());
	}
	
	@Override
	public double getValue()
	{
		if(dirty)
			return recalculateValue();
		return value;
	}
	
	@Override
	public double getBaseValue()
	{
		return base;
	}
	
	@Override
	public void setBaseValue(double value)
	{
		base = value;
		dirty = true;
	}
	
	public void setValue(double value)
	{
		this.value = value;
		this.dirty = false;
	}
	
	@Override
	public IAttributeMod getModifier(UUID uuid)
	{
		return modsById.get(uuid);
	}
	
	@Override
	public IAttributeMod removeModifier(UUID uuid)
	{
		IAttributeMod mod = modsById.remove(uuid);
		removeModifier(mod);
		return mod;
	}
	
	@Override
	public void removeModifier(IAttributeMod mod)
	{
		if(mod != null)
		{
			mods.get(mod.getLayer()).remove(mod);
			modsById.inverse().remove(mod);
			dirty = true;
		}
	}
	
	@Override
	public void applyModifier(IAttributeMod mod, UUID uuid)
	{
		if(mod != null)
		{
			IAttributeMod mod2 = modsById.get(uuid);
			if(mod2 != null)
				throw new IllegalArgumentException("Duplicate attribute modifier with id '" + uuid + "'!");
			List<IAttributeMod> mods = this.mods.get(mod.getLayer());
			if(mods.contains(mod))
				throw new IllegalArgumentException("Attribute modifier '" + mod + "' is already present!");
			
			mods.add(mod);
			modsById.put(uuid, mod);
			dirty = true;
		}
	}
	
	@Override
	public double recalculateValue()
	{
		value = getBaseValue();
		for(EnumAttributeLayer l : EnumAttributeLayer.values())
			for(IAttributeMod mod : mods.get(l))
				value = mod.operate(value);
		dirty = false;
		return value;
	}
	
	@Override
	public CompoundNBT serializeNBT()
	{
		CompoundNBT nbt = new CompoundNBT();
		nbt.putDouble("Base", getBaseValue());
		ListNBT attrs = new ListNBT();
		for(IAttributeMod mod : modsById.values())
		{
			String id = AttributeModRegistry.getId(mod);
			if(id == null)
			{
				LOG.info("Found not registered attribute: " + mod + ". Don't know how to handle, skipping.");
				continue;
			}
			
			CompoundNBT tag = new CompoundNBT();
			tag.putUniqueId("UUID", modsById.inverse().get(mod));
			tag.putDouble("Val", mod.getValue());
			tag.putString("Id", id);
			attrs.add(tag);
		}
		nbt.put("Modifiers", attrs);
		return nbt;
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt)
	{
		setBaseValue(nbt.getFloat("Base"));
		ListNBT attrs = nbt.getList("Modifiers", NBT.TAG_COMPOUND);
		for(int i = 0; i < attrs.size(); ++i)
		{
			CompoundNBT tag = attrs.getCompound(i);
			IAttributeMod mod = AttributeModRegistry.create(tag.getString("Id"), tag.getFloat("Val"));
			if(mod == null)
			{
				LOG.info("Found not registered attribute with id '" + tag.getString("Id") + "'. Don't know how to handle, skipping.");
				continue;
			}
			applyModifier(mod, tag.getUniqueId("UUID"));
		}
	}
	
	@Override
	public void clearAttributes()
	{
		for(EnumAttributeLayer l : EnumAttributeLayer.values())
			mods.get(l).clear();
		modsById.clear();
		value = getBaseValue();
		dirty = false;
	}
}