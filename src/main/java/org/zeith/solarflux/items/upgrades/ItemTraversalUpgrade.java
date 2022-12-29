package org.zeith.solarflux.items.upgrades;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.items.upgrades._base.UpgradeItem;
import org.zeith.solarflux.util.BlockPosFace;

import java.util.ArrayList;
import java.util.List;

import static org.zeith.solarflux.init.SolarPanelsSF.TRAVERSAL_UPGRADE_RANGE;

public class ItemTraversalUpgrade
		extends UpgradeItem
{
	public ItemTraversalUpgrade()
	{
		super(1);
	}
	
	static List<BlockPos> cache = new ArrayList<>();
	
	@Override
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
		if(tile.level().getDayTime() % 20L == 0L)
		{
			cache.clear();
			tile.traversal().clear();
			cache.add(tile.pos());
			findMachines(tile, cache, tile.traversal());
		}
	}
	
	// We really don't need to make a copy of all values every tick, so this constant is here to save the day.
	private static final Direction[] DIRECTIONS = Direction.values();
	
	public static void findMachines(ISolarPanelTile tile, List<BlockPos> cache, List<BlockPosFace> acceptors)
	{
		for(int i = 0; i < cache.size(); ++i)
		{
			var pos = cache.get(i);
			for(var face : DIRECTIONS)
			{
				var p = pos.relative(face);
				if(p.distSqr(cache.get(0)) > TRAVERSAL_UPGRADE_RANGE)
					continue;
				BlockEntity t = tile.level().getBlockEntity(p);
				if(t != null)
					t.getCapability(ForgeCapabilities.ENERGY, face.getOpposite())
							.filter(IEnergyStorage::canReceive)
							.ifPresent(e ->
							{
								if(!cache.contains(p))
								{
									cache.add(p);
									BlockPosFace bpf = new BlockPosFace(p, face.getOpposite());
									acceptors.add(bpf);
								}
							});
			}
		}
	}
	
	@Override
	protected Object[] hoverTextData(ItemStack stack)
	{
		return new Object[] { Math.round((float) Math.sqrt(TRAVERSAL_UPGRADE_RANGE)) };
	}
}