package org.zeith.solarflux.compat._abilities;

import org.zeith.hammerlib.compat.base.Ability;
import org.zeith.solarflux.items.upgrades._base.ISunIntensityMod;

/**
 * A class that defines abilities provided by the SolarFlux mod.
 *
 * <p>This class contains constants for abilities that can be provided by other compatibilities to extend the
 * functionality of the SolarFlux mod.
 */
public class SFAbilities
{
	/**
	 * An ability that allows querying which solar panels were added by a compatibility into the SolarFlux Reborn mod.
	 */
	public static final Ability<AddedSolarPanels> ADDED_SOLAR_PANELS = new Ability<>(AddedSolarPanels.class);
	
	public static final Ability<ISunIntensityMod> ITEM_UPGRADE_SUN_INTENSITY_MUL = new Ability<>(ISunIntensityMod.class);
}