package org.zeith.solarflux.api;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public interface IFurnaceBlockEntity
{
	/**
	 * This method is called every upgrade tick to perform activation of a furnace.
	 * <p>
	 * If you wish to use this interface, return true only when your entity should consume energy.
	 */
	void activateWithSolarPanel(ServerLevel level, ISolarPanelTile solar);
	
	Direction getSideForSolarPanel();
}