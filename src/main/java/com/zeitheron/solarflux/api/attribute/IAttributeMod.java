package com.zeitheron.solarflux.api.attribute;

public interface IAttributeMod
{
	float operate(float given);
	
	float getValue();
	
	default EnumAttributeLayer getLayer()
	{
		return EnumAttributeLayer.ONE;
	}
}