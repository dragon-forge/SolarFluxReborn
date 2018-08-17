package com.zeitheron.solarflux.proxy;

import net.minecraft.item.Item;

public interface ISFProxy
{
	default void init()
	{
	}
	
	default void render(Item item)
	{
	}
	
	default void updateWindow(int window, int key, int val)
	{
	}
}