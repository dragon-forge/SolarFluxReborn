package org.zeith.solarflux.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.items._base.UpgradeItem;
import org.zeith.solarflux.util.BlockPosFace;

import java.util.ArrayList;
import java.util.List;

@SimplyRegister
public class ItemTraversalUpgrade
		extends UpgradeItem
{
	@RegistryName("traversal_upgrade")
	public static final Item TRAVERSAL_UPGRADE = new ItemTraversalUpgrade();
	
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
	
	public static void findMachines(ISolarPanelTile tile, List<BlockPos> cache, List<BlockPosFace> acceptors)
	{
		for(int i = 0; i < cache.size(); ++i)
		{
			BlockPos pos = cache.get(i);
			for(Direction face : Direction.values())
			{
				BlockPos p = pos.relative(face);
				if(p.distSqr(cache.get(0)) > 25D)
					continue;
				TileEntity t = tile.level().getBlockEntity(p);
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
	
	@Override
	protected Object[] hoverTextData(ItemStack stack)
	{
		return new Object[] { Math.round((float) Math.sqrt(25)) };
	}
}