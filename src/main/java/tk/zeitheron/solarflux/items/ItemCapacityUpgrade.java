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
	
	public static final UUID CAPACITY_ATTRIBUTE_UUID = UUID.fromString("a85f5b2f-35e2b-e364f-338af-837a6b8f5a8f");
	
	@Override
	public void update(SolarPanelTile tile, ItemStack stack, int amount)
	{
		amount = Math.min(amount, 10);
		tile.capacity.applyModifier(new AttributeModMultiply(1F + (amount * .1F)), CAPACITY_ATTRIBUTE_UUID);
	}
}