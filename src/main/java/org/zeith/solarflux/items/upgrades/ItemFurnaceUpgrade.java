package org.zeith.solarflux.items.upgrades;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.zeith.solarflux.api.IFurnaceBlockEntity;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.items.upgrades._base.UpgradeItem;
import org.zeith.solarflux.util.BlockPosFace;

import java.util.HashSet;
import java.util.Set;

public class ItemFurnaceUpgrade
		extends UpgradeItem
{
	public ItemFurnaceUpgrade()
	{
		super(1);
	}
	
	@Override
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
		Level lvl = tile.level();
		
		Set<BlockPos> updated = new HashSet<>();
		
		for(var d : Direction.values())
		{
			var pos = tile.pos().relative(d.getOpposite());
			if(updateFurnaceAt(tile, lvl, pos, d))
				updated.add(pos);
		}
		
		for(BlockPosFace face : tile.traversal())
			if(!updated.contains(face.pos))
				updateFurnaceAt(tile, lvl, face.pos, face.face);
	}
	
	public boolean updateFurnaceAt(ISolarPanelTile solar, Level lvl, BlockPos pos, Direction dir)
	{
		if(lvl.getBlockEntity(pos) instanceof IFurnaceBlockEntity fbe && fbe.getSideForSolarPanel() == dir)
		{
			fbe.activateWithSolarPanel(solar);
			return true;
		}
		return false;
	}
}