package org.zeith.solarflux.items;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.zeith.hammerlib.util.charging.IChargeHandler;
import org.zeith.hammerlib.util.charging.ItemChargeHelper;
import org.zeith.hammerlib.util.charging.fe.FECharge;
import org.zeith.solarflux.block.SolarPanelTile;

public class ItemDispersiveUpgrade
		extends UpgradeItem
{
	public ItemDispersiveUpgrade()
	{
		super(1);
	}

	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		for(ServerPlayer mp : tile.getLevel().getEntitiesOfClass(ServerPlayer.class, new AABB(tile.getBlockPos()).inflate(16)))
		{
			float mod = Math.max(0, 1F - (float) (mp.distanceToSqr(tile.getBlockPos().getX() + 0.5F, tile.getBlockPos().getY() + 0.5F, tile.getBlockPos().getZ() + 0.5F) / 256));
			tile.transfer.setBaseValue(tile.getInstance().transfer);
			int transfer = Math.round(tile.transfer.getValueI() * mod);
			int sent = Math.min(Math.round(tile.energy * mod), transfer);
			int fe = sent - ItemChargeHelper.chargePlayer(mp, new FECharge(sent), IChargeHandler.ChargeAction.EXECUTE).FE;
			tile.energy -= fe;
		}
	}
}