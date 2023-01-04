package org.zeith.solarflux.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.attribute.AttributeModMultiply;
import org.zeith.solarflux.items._base.UpgradeItem;

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
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
		amount = Math.min(amount, 20);
		tile.generation().applyModifier(new AttributeModMultiply(1F + (amount * .05F)), EFFICIENCY_ATTRIBUTE_UUID);
	}
	
	@Override
	protected Object[] hoverTextData(ItemStack stack)
	{
		return new Object[] { 5F };
	}
}