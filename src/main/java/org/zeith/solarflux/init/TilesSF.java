package org.zeith.solarflux.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.solarflux.block.SolarPanelBlock;
import org.zeith.solarflux.block.SolarPanelTile;

import java.util.HashSet;

@SimplyRegister
public interface TilesSF
{
	@RegistryName("solar_panel")
	BlockEntityType<SolarPanelTile> SOLAR_PANEL = new BlockEntityType<>(SolarPanelTile::new, new HashSet<>(), null)
	{
		@Override
		public boolean isValid(BlockState state)
		{
			return state.getBlock() instanceof SolarPanelBlock;
		}
	};
}