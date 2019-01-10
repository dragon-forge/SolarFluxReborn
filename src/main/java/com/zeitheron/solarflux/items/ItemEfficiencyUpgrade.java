package com.zeitheron.solarflux.items;

import java.util.UUID;

import com.zeitheron.solarflux.InfoSF;
import com.zeitheron.solarflux.api.attribute.AttributeModMultiply;
import com.zeitheron.solarflux.block.tile.TileBaseSolar;

public class ItemEfficiencyUpgrade extends ItemUpgrade
{
	public ItemEfficiencyUpgrade()
	{
		setRegistryName(InfoSF.MOD_ID, "efficiency_upgrade");
	}
	
	@Override
	public int getMaxUpgrades()
	{
		return 20;
	}
	
	public static final UUID EFFICIENCY_ATTRIBUTE_UUID = UUID.fromString("28575920-a562b-e364d-388af-837a6b8f5a8c");
	
	@Override
	public void update(TileBaseSolar tile, int amount)
	{
		amount = Math.min(amount, 20);
		tile.generation.applyModifier(new AttributeModMultiply(1F + (amount * .05F)), EFFICIENCY_ATTRIBUTE_UUID);
	}
}