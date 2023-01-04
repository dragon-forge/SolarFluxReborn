package org.zeith.solarflux.compat.twilightforest;

import net.minecraft.item.Item;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.solarflux.compat.twilightforest.items.TwiLightUpgrade;

import static org.zeith.solarflux.items.ItemsSF.newItem;

public interface ContentsSFTF
{
	@RegistryName("twilightforest/twilight_cell_1")
	Item TWILIGHT_CELL_1 = newItem();
	
	@RegistryName("twilightforest/twilight_cell_2")
	Item TWILIGHT_CELL_2 = newItem();
	
	@RegistryName("twilightforest/twilight_upgrade")
	TwiLightUpgrade TWI_LIGHT_UPGRADE = new TwiLightUpgrade();
}