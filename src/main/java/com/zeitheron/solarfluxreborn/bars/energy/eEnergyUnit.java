package com.zeitheron.solarfluxreborn.bars.energy;

import com.zeitheron.hammercore.tile.tooltip.ProgressBar;

public enum eEnergyUnit
{
	RF(1, ProgressBarColors.ENERGY_RF_BAR), //
	FE(1, ProgressBarColors.ENERGY_FE_BAR), //
	TESLA(1, ProgressBarColors.ENERGY_TESLA_BAR), //
	QF(8, ProgressBarColors.ENERGY_QF_BAR), //
	EU(4, ProgressBarColors.ENERGY_EU_BAR);
	
	public final float pi;
	private final ProgressBarColors colors;
	
	private eEnergyUnit(float points, ProgressBarColors colors)
	{
		this.pi = points;
		this.colors = colors;
	}
	
	public ProgressBar apply(ProgressBar bar)
	{
		return colors.apply(bar);
	}
	
	public float convert(float e, eEnergyUnit unit)
	{
		return (e * unit.pi) / pi;
	}
}