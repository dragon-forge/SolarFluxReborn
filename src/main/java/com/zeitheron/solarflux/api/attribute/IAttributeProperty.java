package com.zeitheron.solarflux.api.attribute;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IAttributeProperty extends INBTSerializable<NBTTagCompound>
{
	float getValue();
	
	float getBaseValue();
	
	void setBaseValue(float value);
	
	IAttributeMod getModifier(UUID uuid);
	
	IAttributeMod removeModifier(UUID uuid);
	
	void removeModifier(IAttributeMod mod);
	
	void applyModifier(IAttributeMod mod, UUID uuid);
	
	float recalculateValue();
	
	void clearAttributes();
}