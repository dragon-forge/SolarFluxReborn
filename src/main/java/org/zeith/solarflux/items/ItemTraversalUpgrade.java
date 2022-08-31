package org.zeith.solarflux.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.zeith.solarflux.block.SolarPanelTile;
import org.zeith.solarflux.util.BlockPosFace;

import java.util.ArrayList;
import java.util.List;

public class ItemTraversalUpgrade
		extends UpgradeItem
{
	public ItemTraversalUpgrade()
	{
		super(1);
	}

	static List<BlockPos> cache = new ArrayList<>();

	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		if(tile.getLevel().getDayTime() % 20L == 0L)
		{
			cache.clear();
			tile.traversal.clear();
			cache.add(tile.getBlockPos());
			findMachines(tile, cache, tile.traversal);
		}
	}

	// We really don't need to make a copy of all values every tick, so this constant is here to save the day.
	private static final Direction[] DIRECTIONS = Direction.values();

	public static void findMachines(SolarPanelTile tile, List<BlockPos> cache, List<BlockPosFace> acceptors)
	{
		for(int i = 0; i < cache.size(); ++i)
		{
			BlockPos pos = cache.get(i);
			for(Direction face : DIRECTIONS)
			{
				BlockPos p = pos.relative(face);
				if(p.distSqr(cache.get(0)) > 25D)
					continue;
				BlockEntity t = tile.getLevel().getBlockEntity(p);
				if(t != null)
					t.getCapability(CapabilityEnergy.ENERGY, face.getOpposite()).filter(IEnergyStorage::canReceive).ifPresent(e ->
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
}