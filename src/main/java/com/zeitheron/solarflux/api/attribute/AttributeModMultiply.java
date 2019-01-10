package com.zeitheron.solarflux.api.attribute;

import net.minecraft.nbt.NBTTagFloat;

public class AttributeModMultiply implements IAttributeMod
{
	protected float value;
	
	public AttributeModMultiply(float val)
	{
		this.value = val;
	}
	
	@Override
	public float operate(float given)
	{
		return given * value;
	}
	
	@Override
	public EnumAttributeLayer getLayer()
	{
		return EnumAttributeLayer.THREE;
	}
	
	@Override
	public float getValue()
	{
		return value;
	}
}