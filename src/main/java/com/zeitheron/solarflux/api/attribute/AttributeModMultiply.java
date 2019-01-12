package com.zeitheron.solarflux.api.attribute;

import net.minecraft.nbt.NBTTagFloat;

public class AttributeModMultiply implements IAttributeMod
{
	protected double value;
	
	public AttributeModMultiply(double val)
	{
		this.value = val;
	}
	
	@Override
	public double operate(double given)
	{
		return given * value;
	}
	
	@Override
	public EnumAttributeLayer getLayer()
	{
		return EnumAttributeLayer.THREE;
	}
	
	@Override
	public double getValue()
	{
		return value;
	}
}