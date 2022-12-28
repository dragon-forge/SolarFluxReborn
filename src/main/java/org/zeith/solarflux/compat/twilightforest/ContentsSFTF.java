package org.zeith.solarflux.compat.twilightforest;

import net.minecraft.world.item.Item;
import org.zeith.hammerlib.annotations.RegistryName;

import static org.zeith.solarflux.init.ItemsSF.newItem;

public interface ContentsSFTF
{
	@RegistryName("twilight_cell_1")
	Item TWILIGHT_CELL_1 = newItem();
	
	@RegistryName("twilight_cell_2")
	Item TWILIGHT_CELL_2 = newItem();
}