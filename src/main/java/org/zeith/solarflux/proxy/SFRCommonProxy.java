package org.zeith.solarflux.proxy;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SFRCommonProxy
{
	@OnlyIn(Dist.CLIENT)
	public void clientSetup()
	{
	}
	
	public void commonSetup()
	{
	}
}