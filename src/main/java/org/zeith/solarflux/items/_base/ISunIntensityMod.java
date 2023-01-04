package org.zeith.solarflux.items._base;

import org.zeith.solarflux.api.ISolarPanelTile;

public interface ISunIntensityMod
{
	default float applySunIntensityModifier(ISolarPanelTile tile, float value)
	{
		return value;
	}
}