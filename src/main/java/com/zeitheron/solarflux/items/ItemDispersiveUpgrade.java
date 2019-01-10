package com.zeitheron.solarflux.items;

import com.zeitheron.solarflux.InfoSF;
import com.zeitheron.solarflux.block.tile.TileBaseSolar;
import com.zeitheron.solarflux.utils.charging.ItemChargeHelper;
import com.zeitheron.solarflux.utils.charging.fe.FECharge;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.AxisAlignedBB;

public class ItemDispersiveUpgrade extends ItemUpgrade
{
	public ItemDispersiveUpgrade()
	{
		setRegistryName(InfoSF.MOD_ID, "dispersive_upgrade");
	}
	
	@Override
	public int getMaxUpgrades()
	{
		return 1;
	}
	
	@Override
	public void update(TileBaseSolar tile, int amount)
	{
		for(EntityPlayerMP mp : tile.getWorld().getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(tile.getPos()).grow(16)))
		{
			float mod = Math.max(0, 1F - (float) (mp.getDistanceSqToCenter(tile.getPos()) / 256));
			tile.transfer.setBaseValue(tile.instance.transfer);
			int transfer = Math.round(tile.transfer.getValue() * mod);
			int sent = Math.min(Math.round(tile.energy * mod), transfer);
			int fe = sent - ItemChargeHelper.chargePlayer(mp, new FECharge(sent), false).FE;
			tile.energy -= fe;
		}
	}
}