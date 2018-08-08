package com.zeitheron.solarfluxreborn.utility;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileMapper
{
	private static Map<EnumFacing, Boolean> conns = new HashMap<EnumFacing, Boolean>();
	
	public static Map<EnumFacing, Boolean> request(World w, BlockPos pos, Class<?> tileclass)
	{
		conns.clear();
		
		for(EnumFacing f : EnumFacing.VALUES)
		{
			TileEntity te = w.getTileEntity(pos.offset(f));
			if(te == null && tileclass == null)
				conns.put(f, true);
			else if(te != null && tileclass != null && te.getClass().isAssignableFrom(tileclass))
				conns.put(f, true);
		}
		
		return conns;
	}
}