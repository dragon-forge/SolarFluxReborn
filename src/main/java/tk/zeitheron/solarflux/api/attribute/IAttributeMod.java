package tk.zeitheron.solarflux.api.attribute;

public interface IAttributeMod
{
	double operate(double given);
	
	double getValue();
	
	default EnumAttributeLayer getLayer()
	{
		return EnumAttributeLayer.ONE;
	}
}