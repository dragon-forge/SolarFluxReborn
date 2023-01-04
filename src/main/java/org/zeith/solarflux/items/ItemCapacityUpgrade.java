package org.zeith.solarflux.items;

import net.minecraft.item.ItemStack;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.attribute.AttributeModMultiply;
import org.zeith.solarflux.items._base.UpgradeItem;

import java.util.UUID;

@SimplyRegister
public class ItemCapacityUpgrade
		extends UpgradeItem
{
	@RegistryName("capacity_upgrade")
	public static final ItemCapacityUpgrade CAPACITY_UPGRADE = new ItemCapacityUpgrade();
	
	public ItemCapacityUpgrade()
	{
		super(10);
	}
	
	public static final UUID CAPACITY_ATTRIBUTE_UUID = new UUID(-6314227893548403121L, 4084627948862134927L);
	
	@Override
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
		amount = Math.min(amount, 10);
		tile.capacity().applyModifier(new AttributeModMultiply(1F + (amount * .1F)), CAPACITY_ATTRIBUTE_UUID);
	}
	
	@Override
	protected Object[] hoverTextData(ItemStack stack)
	{
		return new Object[] { 10F };
	}
}