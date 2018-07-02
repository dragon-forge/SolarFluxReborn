package com.zeitheron.solarfluxreborn.blocks.modules;

import com.zeitheron.solarfluxreborn.te.SolarPanelTileEntity;

public abstract class AbstractSolarPanelModule extends AbstractTileEntityModule<SolarPanelTileEntity>
{
	protected AbstractSolarPanelModule(SolarPanelTileEntity pSolarPanelTileEntity)
	{
		super(pSolarPanelTileEntity);
	}
	
	protected boolean atRate(int pDesiredTickRate)
	{
		return getTileEntity().atTickRate(pDesiredTickRate);
	}
}
