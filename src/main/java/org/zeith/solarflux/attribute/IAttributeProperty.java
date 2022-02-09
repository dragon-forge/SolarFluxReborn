package org.zeith.solarflux.attribute;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public interface IAttributeProperty
		extends INBTSerializable<CompoundTag>
{
	double getValue();

	default long getValueL()
	{
		return Math.round(getValue());
	}

	default int getValueI()
	{
		return (int) Math.min(getValueL(), (long) Integer.MAX_VALUE);
	}

	double getBaseValue();

	void setBaseValue(double value);

	IAttributeMod getModifier(UUID uuid);

	IAttributeMod removeModifier(UUID uuid);

	void removeModifier(IAttributeMod mod);

	void applyModifier(IAttributeMod mod, UUID uuid);

	double recalculateValue();

	void clearAttributes();
}