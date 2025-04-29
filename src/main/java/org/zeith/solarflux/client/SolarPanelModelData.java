package org.zeith.solarflux.client;

import com.google.common.base.MoreObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.data.*;

public record SolarPanelModelData(
		boolean west, boolean east, boolean north, boolean south,
		boolean westNorth, boolean westSouth, boolean eastNorth, boolean eastSouth
)
{
	public static final ModelProperty<SolarPanelModelData> PROPERTY = new ModelProperty<>();
	
	public static final SolarPanelModelData ITEM = new SolarPanelModelData(
			false, false, false, false,
			false, false, false, false
	);
	
	public static SolarPanelModelData findOrItem(ModelData data)
	{
		return MoreObjects.firstNonNull(data.get(PROPERTY), ITEM);
	}
	
	public static SolarPanelModelData gather(BlockGetter world, BlockPos pos, Block block)
	{
		return new SolarPanelModelData(
				world.getBlockState(pos.west()).getBlock() == block,
				world.getBlockState(pos.east()).getBlock() == block,
				world.getBlockState(pos.north()).getBlock() == block,
				world.getBlockState(pos.south()).getBlock() == block,
				
				world.getBlockState(pos.west().north()).getBlock() == block,
				world.getBlockState(pos.west().south()).getBlock() == block,
				world.getBlockState(pos.east().north()).getBlock() == block,
				world.getBlockState(pos.east().south()).getBlock() == block
		);
	}
}