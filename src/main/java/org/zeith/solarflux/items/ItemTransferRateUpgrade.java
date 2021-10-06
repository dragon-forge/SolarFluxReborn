package org.zeith.solarflux.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.solarflux.attribute.AttributeModMultiply;
import org.zeith.solarflux.block.SolarPanelTile;

import java.util.UUID;

@SimplyRegister
public class ItemTransferRateUpgrade
		extends UpgradeItem
{
	@RegistryName("transfer_rate_upgrade")
	public static final Item TRANSFER_RATE_UPGRADE = new ItemTransferRateUpgrade();

	public ItemTransferRateUpgrade()
	{
		super(10);
	}

	public static final UUID TRANSFER_RATE_ATTRIBUTE_UUID = new UUID(2906890127155279437L, -8597596562743403894L);

	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		amount = Math.min(amount, 10);
		tile.transfer.applyModifier(new AttributeModMultiply(1F + (amount * .15F)), TRANSFER_RATE_ATTRIBUTE_UUID);
	}
}