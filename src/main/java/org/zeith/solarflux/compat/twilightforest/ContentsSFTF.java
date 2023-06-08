package org.zeith.solarflux.compat.twilightforest;

import net.minecraft.world.item.Item;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.solarflux.compat.twilightforest.items.TwiLightUpgrade;

import static org.zeith.solarflux.init.ItemsSF.newMaterial;

public interface ContentsSFTF
{
	@RegistryName("twilight_cell_1")
	Item TWILIGHT_CELL_1 = newMaterial();
	
	@RegistryName("twilight_cell_2")
	Item TWILIGHT_CELL_2 = newMaterial();
	
	@RegistryName("twilight_upgrade")
	TwiLightUpgrade TWI_LIGHT_UPGRADE = new TwiLightUpgrade();
}