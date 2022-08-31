package org.zeith.solarflux.items;

import net.minecraft.world.item.ItemStack;
import org.zeith.solarflux.attribute.AttributeModMultiply;
import org.zeith.solarflux.block.SolarPanelTile;

import java.util.UUID;

public class ItemCapacityUpgrade
		extends UpgradeItem
{
	public ItemCapacityUpgrade()
	{
		super(10);
	}

	public static final UUID CAPACITY_ATTRIBUTE_UUID = new UUID(-6314227893548403121L, 4084627948862134927L);

	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		amount = Math.min(amount, 10);
		tile.capacity.applyModifier(new AttributeModMultiply(1F + (amount * .1F)), CAPACITY_ATTRIBUTE_UUID);
	}
}