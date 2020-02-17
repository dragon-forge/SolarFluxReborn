package tk.zeitheron.solarflux.api.attribute;

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