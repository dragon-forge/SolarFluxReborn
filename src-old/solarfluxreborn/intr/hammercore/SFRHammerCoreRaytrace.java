package com.zeitheron.solarfluxreborn.intr.hammercore;

import java.util.Objects;

import com.zeitheron.hammercore.api.mhb.IRayCubeRegistry;
import com.zeitheron.hammercore.api.mhb.IRayRegistry;
import com.zeitheron.hammercore.api.mhb.RaytracePlugin;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.solarfluxreborn.blocks.BlockAbstractCable;

import net.minecraft.block.Block;

@RaytracePlugin
public class SFRHammerCoreRaytrace implements IRayRegistry
{
	@Override
	public void registerCubes(IRayCubeRegistry cube)
	{
		Block.REGISTRY.getKeys().stream().map(Block.REGISTRY::getObject).map(b -> WorldUtil.cast(b, BlockAbstractCable.class)).filter(Objects::nonNull).forEach(ab -> cube.bindBlockCubeManager(ab, ab));
	}
}