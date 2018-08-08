package com.zeitheron.solarfluxreborn.blocks.modules;

import com.zeitheron.solarfluxreborn.te.SolarPanelTileEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class EnergySharingModule extends AbstractSolarPanelModule
{
	private static final short DISTRIBUTION_TICK_RATE = 5 * 20;
	
	public EnergySharingModule(SolarPanelTileEntity pTileEntity)
	{
		super(pTileEntity);
	}
	
	@Override
	public void tick()
	{
		if(atRate(1))
		{
			SolarPanelTileEntity me = getTileEntity();
			IBlockState state = me.getWorld().getBlockState(me.getPos());
			tryAutoBalanceEnergyAt(me.getPos().add(1, 0, 0));
			tryAutoBalanceEnergyAt(me.getPos().add(0, 0, 1));
		}
	}
	
	private void tryAutoBalanceEnergyAt(BlockPos pos)
	{
		TileEntity tile = getTileEntity().getWorld().getTileEntity(pos);
		if(tile instanceof SolarPanelTileEntity)
		{
			SolarPanelTileEntity neighbor = (SolarPanelTileEntity) tile;
			getTileEntity().getEnergyStorage().autoBalanceEnergy(neighbor.getEnergyStorage(), DISTRIBUTION_TICK_RATE);
		}
	}
}
