package com.zeitheron.solarfluxreborn.bars.energy;

import com.zeitheron.hammercore.client.HCClientOptions;
import com.zeitheron.hammercore.tile.tooltip.ProgressBar;
import com.zeitheron.hammercore.tile.tooltip.eNumberFormat;
import com.zeitheron.hammercore.utils.color.ColorHelper;

public class ProgressBarColors
{
	public static final ProgressBarColors RED_BAR = new ProgressBarColors(0x55006699, 0xff555555, 0xffdd0000, 0xff430000);
	public static final ProgressBarColors GREEN_BAR = new ProgressBarColors(0x55006699, 0xff555555, 0xff00dd00, 0xff004300);
	public static final ProgressBarColors BLUE_BAR = new ProgressBarColors(0x55006699, 0xff555555, 0xff0000dd, 0xff000043);
	public static final ProgressBarColors ORANGE_BAR = RED_BAR.interpolate(GREEN_BAR, .25F);
	public static final ProgressBarColors YELLOW_BAR = RED_BAR.interpolate(GREEN_BAR, .5F);
	public static final ProgressBarColors CYAN_BAR = BLUE_BAR.interpolate(GREEN_BAR, .5F);
	
	public static final ProgressBarColors ENERGY_QF_BAR = new ProgressBarColors(0x55006699, 0xff555555, 0xffdddd00, 0xff333333);
	public static final ProgressBarColors ENERGY_FE_BAR = RED_BAR.interpolate(GREEN_BAR, .4F);
	public static final ProgressBarColors ENERGY_TESLA_BAR = BLUE_BAR.interpolate(GREEN_BAR, .3F);
	public static final ProgressBarColors ENERGY_EU_BAR = BLUE_BAR.interpolate(GREEN_BAR, .5F);
	public static final ProgressBarColors ENERGY_RF_BAR = RED_BAR;
	
	public int backgroundColor, borderColor;
	public int filledMainColor, filledAlternateColor;
	
	public ProgressBarColors(int a, int b, int c, int d)
	{
		this.backgroundColor = a;
		this.borderColor = b;
		this.filledMainColor = c;
		this.filledAlternateColor = d;
	}
	
	public static eEnergyUnit client_getPrefferedEnergyUnit()
	{
		if(HCClientOptions.getOptions().getData() == null)
			return eEnergyUnit.RF;
		return eEnergyUnit.values()[HCClientOptions.getOptions().getData().optInt("PrefEnergyUnit", eEnergyUnit.RF.ordinal()) % eEnergyUnit.values().length];
	}
	
	public static void client_setPrefferedEnergyUnit(eEnergyUnit u)
	{
		try
		{
			HCClientOptions.getOptions().getData().put("PrefEnergyUnit", u.ordinal());
			HCClientOptions.getOptions().save();
		} catch(Throwable e)
		{
		}
	}
	
	public static ProgressBar makeEnergyBar(long energy, long maxEnergy, eEnergyUnit unit, String text)
	{
		ProgressBar bar = new ProgressBar(maxEnergy).setProgress(energy);
		bar.prefix = text;
		bar.numberFormat = eNumberFormat.NONE;
		return unit.apply(bar);
	}
	
	public ProgressBarColors interpolate(ProgressBarColors other, float mixRate)
	{
		int a = ColorHelper.interpolate(backgroundColor, other.backgroundColor, mixRate);
		int b = ColorHelper.interpolate(borderColor, other.borderColor, mixRate);
		int c = ColorHelper.interpolate(filledMainColor, other.filledMainColor, mixRate);
		int d = ColorHelper.interpolate(filledAlternateColor, other.filledAlternateColor, mixRate);
		return new ProgressBarColors(a, b, c, d);
	}
	
	public ProgressBar apply(ProgressBar bar)
	{
		bar.backgroundColor = backgroundColor;
		bar.borderColor = borderColor;
		bar.filledMainColor = filledMainColor;
		bar.filledAlternateColor = filledAlternateColor;
		return bar;
	}
}