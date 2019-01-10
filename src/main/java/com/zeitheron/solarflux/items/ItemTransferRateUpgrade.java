package com.zeitheron.solarflux.items;

import java.util.UUID;

import com.zeitheron.solarflux.InfoSF;
import com.zeitheron.solarflux.api.attribute.AttributeModMultiply;
import com.zeitheron.solarflux.block.tile.TileBaseSolar;

public class ItemTransferRateUpgrade extends ItemUpgrade
{
	public ItemTransferRateUpgrade()
	{
		setRegistryName(InfoSF.MOD_ID, "transfer_rate_upgrade");
	}
	
	@Override
	public int getMaxUpgrades()
	{
		return 10;
	}
	
	public static final UUID TRANSFER_RATE_ATTRIBUTE_UUID = UUID.fromString("28575922-b562a-c364d-788af-337a6b8f5a8a");
	
	@Override
	public void update(TileBaseSolar tile, int amount)
	{
		amount = Math.min(amount, 10);
		tile.transfer.applyModifier(new AttributeModMultiply(1F + (amount * .15F)), TRANSFER_RATE_ATTRIBUTE_UUID);
	}
}