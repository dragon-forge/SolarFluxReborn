package org.zeith.solarflux.items.upgrades;

import net.minecraft.world.item.ItemStack;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.attribute.AttributeModMultiply;
import org.zeith.solarflux.items.upgrades._base.UpgradeItem;

import java.util.UUID;

import static org.zeith.solarflux.init.SolarPanelsSF.EFFICIENCY_UPGRADE_INCREASE;

public class ItemEfficiencyUpgrade
		extends UpgradeItem
{
	public ItemEfficiencyUpgrade()
	{
		super(20);
	}
	
	public static final UUID EFFICIENCY_ATTRIBUTE_UUID = new UUID(2906890122860377677L, -8597508601813181812L);
	
	@Override
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
		amount = Math.min(amount, 20);
		tile.generation().applyModifier(new AttributeModMultiply(1F + (amount * EFFICIENCY_UPGRADE_INCREASE)), EFFICIENCY_ATTRIBUTE_UUID);
	}
	
	@Override
	protected Object[] hoverTextData(ItemStack stack)
	{
		return new Object[] { EFFICIENCY_UPGRADE_INCREASE * 100F };
	}
}