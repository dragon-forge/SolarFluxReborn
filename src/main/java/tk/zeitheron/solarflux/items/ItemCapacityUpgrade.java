package tk.zeitheron.solarflux.items;

import java.util.UUID;

import net.minecraft.item.ItemStack;
import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.api.attribute.AttributeModMultiply;
import tk.zeitheron.solarflux.block.SolarPanelTile;

public class ItemCapacityUpgrade extends UpgradeItem
{
	public ItemCapacityUpgrade()
	{
		super(10);
		setRegistryName(InfoSF.MOD_ID, "capacity_upgrade");
	}

	public static final UUID CAPACITY_ATTRIBUTE_UUID = new UUID(-6314227893548403121L, 4084627948862134927L);
	
	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		amount = Math.min(amount, 10);
		tile.capacity.applyModifier(new AttributeModMultiply(1F + (amount * .1F)), CAPACITY_ATTRIBUTE_UUID);
	}
}