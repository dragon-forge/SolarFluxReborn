package org.zeith.solarflux.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import org.zeith.hammerlib.annotations.*;
import org.zeith.solarflux.block.*;

@SimplyRegister
public interface TilesSF
{
	@RegistryName("solar_panel")
	BlockEntityType<SolarPanelTile> SOLAR_PANEL = new SolarBlockEntityType();
}