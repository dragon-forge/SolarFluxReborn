package tk.zeitheron.solarflux.items;

import java.util.UUID;

import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.api.attribute.AttributeModMultiply;
import tk.zeitheron.solarflux.block.tile.TileBaseSolar;

import net.minecraft.item.ItemStack;

public class ItemCapacityUpgrade extends ItemUpgrade
{
	public ItemCapacityUpgrade()
	{
		setRegistryName(InfoSF.MOD_ID, "capacity_upgrade");
	}
	
	@Override
	public int getMaxUpgrades()
	{
		return 10;
	}
	
	public static final UUID CAPACITY_ATTRIBUTE_UUID = UUID.fromString("a85f5b2f-35e2b-e364f-338af-837a6b8f5a8f");
	
	@Override
	public void update(TileBaseSolar tile, ItemStack stack, int amount)
	{
		amount = Math.min(amount, 10);
		tile.capacity.applyModifier(new AttributeModMultiply(1F + (amount * .1F)), CAPACITY_ATTRIBUTE_UUID);
	}
}