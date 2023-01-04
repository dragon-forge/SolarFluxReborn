package org.zeith.solarflux.items;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.util.charging.IChargeHandler;
import org.zeith.hammerlib.util.charging.ItemChargeHelper;
import org.zeith.hammerlib.util.charging.fe.FECharge;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.items._base.UpgradeItem;

@SimplyRegister
public class ItemDispersiveUpgrade
		extends UpgradeItem
{
	@RegistryName("dispersive_upgrade")
	public static final ItemDispersiveUpgrade DISPERSIVE_UPGRADE = new ItemDispersiveUpgrade();
	
	public ItemDispersiveUpgrade()
	{
		super(1);
	}
	
	@Override
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
		for(ServerPlayerEntity mp : tile.level().getEntitiesOfClass(ServerPlayerEntity.class, new AxisAlignedBB(tile.pos()).inflate(16)))
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