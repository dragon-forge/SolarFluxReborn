package org.zeith.solarflux.compat.ae2.tile;

import org.zeith.solarflux.api.ISolarPanelTile;

public interface IAE2SolarPanelTile
		extends ISolarPanelTile
{
	void onReady();
	
	void setConnectedToAENetwork(boolean connected);
}