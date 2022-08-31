package org.zeith.solarflux.items;

import net.minecraft.world.item.ItemStack;
import org.zeith.solarflux.attribute.AttributeModMultiply;
import org.zeith.solarflux.block.SolarPanelTile;

import java.util.UUID;

public class ItemEfficiencyUpgrade
		extends UpgradeItem
{
	public ItemEfficiencyUpgrade()
	{
		super(20);
	}

	public static final UUID EFFICIENCY_ATTRIBUTE_UUID = new UUID(2906890122860377677L, -8597508601813181812L);

	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		amount = Math.min(amount, 20);
		tile.generation.applyModifier(new AttributeModMultiply(1F + (amount * .05F)), EFFICIENCY_ATTRIBUTE_UUID);
	}
}