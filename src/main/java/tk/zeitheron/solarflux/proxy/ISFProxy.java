package tk.zeitheron.solarflux.proxy;

import tk.zeitheron.solarflux.api.SolarInfo;
import net.minecraft.item.Item;

public interface ISFProxy
{
	default void construct()
	{
	}
	
	default void preInit()
	{
	}
	
	default void init()
	{
	}
	
	default void postInit()
	{
	}
	
	default void render(Item item)
	{
	}

	default void onPanelRegistered(SolarInfo info)
	{
	}
	
	default void updateWindow(int window, int key, long val)
	{
	}
}