package com.mrdimka.solarfluxreborn.intr.waila;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;

public class WailaIntegrar
{
	public static void registerWAIA(IWailaRegistrar reg)
	{
		WDataPump pump = new WDataPump();
		reg.registerBodyProvider(pump, Block.class);
		reg.registerHeadProvider(pump, Block.class);
		reg.registerTailProvider(pump, Block.class);
	}
}