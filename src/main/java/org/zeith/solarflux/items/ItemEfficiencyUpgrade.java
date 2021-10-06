package org.zeith.solarflux.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.solarflux.attribute.AttributeModMultiply;
import org.zeith.solarflux.block.SolarPanelTile;

import java.util.UUID;

@SimplyRegister
public class ItemEfficiencyUpgrade
		extends UpgradeItem
{
	@RegistryName("efficiency_upgrade")
	public static final Item EFFICIENCY_UPGRADE = new ItemEfficiencyUpgrade();

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