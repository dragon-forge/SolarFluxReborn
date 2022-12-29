package org.zeith.solarflux.items.upgrades;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.zeith.hammerlib.util.charging.IChargeHandler;
import org.zeith.hammerlib.util.charging.ItemChargeHelper;
import org.zeith.hammerlib.util.charging.fe.FECharge;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.items.upgrades._base.UpgradeItem;

public class ItemDispersiveUpgrade
		extends UpgradeItem
{
	public ItemDispersiveUpgrade()
	{
		super(1);
	}
	
	@Override
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
		for(ServerPlayer mp : tile.level().getEntitiesOfClass(ServerPlayer.class, new AABB(tile.pos()).inflate(16)))
		{
			float mod = Math.max(0, 1F - (float) (mp.distanceToSqr(tile.pos().getX() + 0.5F, tile.pos().getY() + 0.5F, tile.pos().getZ() + 0.5F) / 256));
			tile.transfer().setBaseValue(tile.getInstance().transfer);
			int transfer = Math.round(tile.transfer().getValueI() * mod);
			int sent = Math.min(Math.round(tile.energy() * mod), transfer);
			int fe = sent - ItemChargeHelper.chargePlayer(mp, new FECharge(sent), IChargeHandler.ChargeAction.EXECUTE).FE;
			tile.energy(tile.energy() - fe);
		}
	}
	
	@Override
	protected Object[] hoverTextData(ItemStack stack)
	{
		return new Object[] { 16 };
	}
}