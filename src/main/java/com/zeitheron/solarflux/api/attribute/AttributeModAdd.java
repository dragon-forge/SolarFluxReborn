package com.zeitheron.solarflux.api.attribute;

public class AttributeModAdd implements IAttributeMod
{
	protected float value;
	
	public AttributeModAdd(float val)
	{
		this.value = val;
	}
	
	@Override
	public float operate(float given)
	{
		return given + value;
	}
	
	@Override
	public float getValue()
	{
		return value;
	}
}