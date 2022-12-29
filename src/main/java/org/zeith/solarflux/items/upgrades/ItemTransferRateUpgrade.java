package org.zeith.solarflux.items.upgrades;

import net.minecraft.world.item.ItemStack;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.attribute.AttributeModMultiply;
import org.zeith.solarflux.items.upgrades._base.UpgradeItem;

import java.util.UUID;

import static org.zeith.solarflux.init.SolarPanelsSF.TRANSFER_RATE_UPGRADE_INCREASE;

public class ItemTransferRateUpgrade
		extends UpgradeItem
{
	public ItemTransferRateUpgrade()
	{
		super(10);
	}
	
	public static final UUID TRANSFER_RATE_ATTRIBUTE_UUID = new UUID(2906890127155279437L, -8597596562743403894L);
	
	@Override
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
		amount = Math.min(amount, 10);
		tile.transfer().applyModifier(new AttributeModMultiply(1F + (amount * TRANSFER_RATE_UPGRADE_INCREASE)), TRANSFER_RATE_ATTRIBUTE_UUID);
	}
	
	@Override
	protected Object[] hoverTextData(ItemStack stack)
	{
		return new Object[] { TRANSFER_RATE_UPGRADE_INCREASE * 100F };
	}
}