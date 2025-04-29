package org.zeith.solarflux.block;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;

public class SolarBlockEntityType
		extends BlockEntityType<SolarPanelTile>
{
	public SolarBlockEntityType()
	{
		super(SolarPanelTile::new, new HashSet<>(), null);
	}
	
	@Override
	public boolean isValid(BlockState state)
	{
		return state.getBlock() instanceof SolarPanelBlock;
	}
}
