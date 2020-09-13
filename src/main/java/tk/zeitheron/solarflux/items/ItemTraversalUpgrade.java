package tk.zeitheron.solarflux.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import tk.zeitheron.solarflux.block.SolarPanelTile;
import tk.zeitheron.solarflux.util.BlockPosFace;

public class ItemTraversalUpgrade extends UpgradeItem
{
	public ItemTraversalUpgrade()
	{
		super(1);
		setRegistryName("traversal_upgrade");
	}
	
	static List<BlockPos> cache = new ArrayList<>();
	
	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		if(tile.getWorld().getDayTime() % 20L == 0L)
		{
			cache.clear();
			tile.traversal.clear();
			cache.add(tile.getPos());
			findMachines(tile, cache, tile.traversal);
		}
	}
	
	public static void findMachines(SolarPanelTile tile, List<BlockPos> cache, List<BlockPosFace> acceptors)
	{
		for(int i = 0; i < cache.size(); ++i)
		{
			BlockPos pos = cache.get(i);
			for(Direction face : Direction.values())
			{
				BlockPos p = pos.offset(face);
				if(p.distanceSq(cache.get(0)) > 25D)
					continue;
				TileEntity t = tile.getWorld().getTileEntity(p);
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