package tk.zeitheron.solarflux.items;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.api.attribute.AttributeModMultiply;
import tk.zeitheron.solarflux.block.SolarPanelTile;

public class ItemTransferRateUpgrade extends UpgradeItem
{
	public ItemTransferRateUpgrade()
	{
		super(10);
		setRegistryName(InfoSF.MOD_ID, "transfer_rate_upgrade");
	}
	
	public static final UUID TRANSFER_RATE_ATTRIBUTE_UUID = UUID.fromString("28575922-b562a-c364d-788af-337a6b8f5a8a");
	
	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		amount = Math.min(amount, 10);
		tile.transfer.applyModifier(new AttributeModMultiply(1F + (amount * .15F)), TRANSFER_RATE_ATTRIBUTE_UUID);
	}
}