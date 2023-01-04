package org.zeith.solarflux.compat.ae2.items;

import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.compat.ae2.tile.IAE2SolarPanelTile;
import org.zeith.solarflux.items._base.UpgradeItem;

public class ItemAE2EnergyUpgrade
		extends UpgradeItem
{
	public ItemAE2EnergyUpgrade()
	{
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	}
	
	@Override
	public void onInstalled(ISolarPanelTile tile, int prevCount, int newCount)
	{
		boolean had = prevCount > 0;
		boolean has = newCount > 0;
		
		if(had != has && !tile.level().isClientSide() && tile instanceof IAE2SolarPanelTile)
		{
			((IAE2SolarPanelTile) tile).setConnectedToAENetwork(has);
		}
	}
	
	@Override
	public void onRemoved(ISolarPanelTile tile, int prevCount, int newCount)
	{
		boolean had = prevCount > 0;
		boolean has = newCount > 0;
		
		if(had != has && !tile.level().isClientSide() && tile instanceof IAE2SolarPanelTile)
		{
			((IAE2SolarPanelTile) tile).setConnectedToAENetwork(has);

//			for(var dir : Direction.values())
//			{
//				var node = GridHelper.getNodeHost(tile.level(), tile.pos().relative(dir));
//				if(node != null)
//				{
//					var be = tile.level().getBlockEntity(tile.pos().relative(dir));
//					if(be != null)
//						ServerListener.syncTileEntity(be);
//				}
//			}
		}
	}
}