package tk.zeitheron.solarflux.items;

import java.util.ArrayList;
import java.util.List;

import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.block.tile.TileBaseSolar;
import tk.zeitheron.solarflux.utils.BlockPosFace;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ItemTraversalUpgrade extends ItemUpgrade
{
	public ItemTraversalUpgrade()
	{
		setRegistryName(InfoSF.MOD_ID, "traversal_upgrade");
	}
	
	@Override
	public int getMaxUpgrades()
	{
		return 1;
	}
	
	static List<BlockPos> cache = new ArrayList<>();
	
	@Override
	public void update(TileBaseSolar tile, ItemStack stack, int amount)
	{
		if(tile.getWorld().getTotalWorldTime() % 20L == 0L)
		{
			cache.clear();
			tile.traversal.clear();
			cache.add(tile.getPos());
			findMachines(tile, cache, tile.traversal);
		}
	}
	
	public static void findMachines(TileBaseSolar tile, List<BlockPos> cache, List<BlockPosFace> acceptors)
	{
		for(int i = 0; i < cache.size(); ++i)
		{
			BlockPos pos = cache.get(i);
			for(EnumFacing face : EnumFacing.VALUES)
			{
				BlockPos p = pos.offset(face);
				if(p.distanceSq(cache.get(0)) > 25D)
					continue;
				TileEntity t = tile.getWorld().getTileEntity(p);
				IEnergyStorage e;
				if(t != null && t.hasCapability(CapabilityEnergy.ENERGY, face.getOpposite()) && (e = t.getCapability(CapabilityEnergy.ENERGY, face.getOpposite())).canReceive())
				{
					if(!cache.contains(p))
					{
						cache.add(p);
						BlockPosFace bpf = new BlockPosFace(p, face.getOpposite());
						acceptors.add(bpf);
					}
				}
			}
		}
	}
}