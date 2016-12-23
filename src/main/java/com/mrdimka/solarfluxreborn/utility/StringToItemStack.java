package com.mrdimka.solarfluxreborn.utility;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class StringToItemStack
{
	public static ItemStack toItemStack(String origin, String nbt)
	{
		try
		{
			String rawId = origin.substring(0, origin.lastIndexOf(":"));
			int otpMeta = Integer.parseInt(origin.substring(origin.lastIndexOf(":") + 1, origin.lastIndexOf("@")));
			int otpStackSize = Integer.parseInt(origin.substring(origin.lastIndexOf("@") + 1, origin.length()));
			return GameRegistry.makeItemStack(rawId, otpMeta, otpStackSize, nbt);
		}
		catch(Throwable err) {}
		return null;
	}
}