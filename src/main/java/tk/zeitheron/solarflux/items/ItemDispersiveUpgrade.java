package tk.zeitheron.solarflux.items;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.block.SolarPanelTile;
import tk.zeitheron.solarflux.util.charging.ItemChargeHelper;
import tk.zeitheron.solarflux.util.charging.fe.FECharge;

public class ItemDispersiveUpgrade extends UpgradeItem
{
	public ItemDispersiveUpgrade()
	{
		super(1);
		setRegistryName(InfoSF.MOD_ID, "dispersive_upgrade");
	}
	
	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		for(ServerPlayerEntity mp : tile.getWorld().getEntitiesWithinAABB(ServerPlayerEntity.class, new AxisAlignedBB(tile.getPos()).grow(16)))
		{
			float mod = Math.max(0, 1F - (float) (mp.getDistanceSq(tile.getPos().getX() + 0.5F, tile.getPos().getY() + 0.5F, tile.getPos().getZ() + 0.5F) / 256));
			tile.transfer.setBaseValue(tile.getInstance().transfer);
			int transfer = Math.round(tile.transfer.getValueI() * mod);
			int sent = Math.min(Math.round(tile.energy * mod), transfer);
			int fe = sent - ItemChargeHelper.chargePlayer(mp, new FECharge(sent), false).FE;
			tile.energy -= fe;
		}
	}
}