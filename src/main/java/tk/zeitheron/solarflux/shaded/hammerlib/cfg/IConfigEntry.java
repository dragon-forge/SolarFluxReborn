package tk.zeitheron.solarflux.shaded.hammerlib.cfg;

public interface IConfigEntry
{
	String getDescription();
	
	String getName();
	
	ConfigEntrySerializer<?> getSerializer();
	
	default String getSerializedName()
	{
		return getName().replaceAll("=", "\u2248");
	}
}