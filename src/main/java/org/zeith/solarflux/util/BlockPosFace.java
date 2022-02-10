package org.zeith.solarflux.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Objects;

public class BlockPosFace
{
	public final BlockPos pos;
	public final Direction face;
	public final float rate;

	public BlockPosFace(BlockPos pos, Direction face)
	{
		this(pos, face, 1F);
	}

	public BlockPosFace(BlockPos pos, Direction face, float rate)
	{
		this.pos = pos;
		this.face = face;
		this.rate = rate;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof BlockPosFace bpf)
			return Objects.equals(pos, bpf.pos)
					&& Objects.equals(face, bpf.face)
					&& rate == bpf.rate;
		return false;
	}
}