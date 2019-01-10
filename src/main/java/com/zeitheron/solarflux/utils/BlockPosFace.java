package com.zeitheron.solarflux.utils;

import java.util.Objects;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class BlockPosFace
{
	public final BlockPos pos;
	public final EnumFacing face;
	
	public BlockPosFace(BlockPos pos, EnumFacing face)
	{
		this.pos = pos;
		this.face = face;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof BlockPosFace)
		{
			BlockPosFace bpf = (BlockPosFace) obj;
			return Objects.equals(pos, bpf.pos) && Objects.equals(face, bpf.face);
		}
		return false;
	}
}