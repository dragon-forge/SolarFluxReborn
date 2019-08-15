package com.zeitheron.solarflux.proxy;

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
	
	default void updateWindow(int window, int key, long val)
	{
	}
}