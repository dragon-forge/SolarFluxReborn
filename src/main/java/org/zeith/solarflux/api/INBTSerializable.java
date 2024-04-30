package org.zeith.solarflux.api;

import net.minecraft.nbt.CompoundTag;

public interface INBTSerializable
{
	CompoundTag serializeNBT();
	
	void deserializeNBT(CompoundTag nbt);
}