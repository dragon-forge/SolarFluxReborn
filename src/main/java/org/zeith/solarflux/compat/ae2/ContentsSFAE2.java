package org.zeith.solarflux.compat.ae2;

import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.solarflux.compat.ae2.items.ItemAE2EnergyUpgrade;

public interface ContentsSFAE2
{
	@RegistryName("ae2/energy_upgrade")
	ItemAE2EnergyUpgrade ENERGY_UPGRADE = new ItemAE2EnergyUpgrade();
}