package org.zeith.solarflux.attribute;

public class AttributeModAdd
		implements IAttributeMod
{
	protected double value;

	public AttributeModAdd(double val)
	{
		this.value = val;
	}

	@Override
	public double operate(double given)
	{
		return given + value;
	}

	@Override
	public double getValue()
	{
		return value;
	}
}