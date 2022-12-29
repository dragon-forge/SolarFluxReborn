package org.zeith.solarflux.items.upgrades;

import net.minecraft.world.item.ItemStack;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.attribute.AttributeModMultiply;
import org.zeith.solarflux.items.upgrades._base.UpgradeItem;

import java.util.UUID;

import static org.zeith.solarflux.init.SolarPanelsSF.CAPACITY_UPGRADE_INCREASE;

public class ItemCapacityUpgrade
		extends UpgradeItem
{
	public ItemCapacityUpgrade()
	{
		super(10);
	}
	
	public static final UUID CAPACITY_ATTRIBUTE_UUID = new UUID(-6314227893548403121L, 4084627948862134927L);
	
	@Override
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
		amount = Math.min(amount, 10);
		tile.capacity().applyModifier(new AttributeModMultiply(1F + (amount * CAPACITY_UPGRADE_INCREASE)), CAPACITY_ATTRIBUTE_UUID);
	}
	
	@Override
	protected Object[] hoverTextData(ItemStack stack)
	{
		return new Object[] { CAPACITY_UPGRADE_INCREASE * 100F };
	}
}