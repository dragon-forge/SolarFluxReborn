package org.zeith.solarflux.items.upgrades._base;

import com.google.common.base.Suppliers;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.compat.base.Ability;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.api.ISolarPanelTile;

import java.util.*;
import java.util.function.Supplier;

public abstract class UpgradeItem
		extends Item
{
	public UpgradeItem(int stackSize)
	{
		super(new Item.Properties().stacksTo(stackSize));
	}
	
	public UpgradeItem(Item.Properties props)
	{
		super(props);
	}
	
	{
		SolarFlux.ITEM_GROUP.add(this);
	}
	
	public void update(ISolarPanelTile tile, ItemStack stack, int amount)
	{
	}
	
	public void onInstalled(ISolarPanelTile tile, int prevCount, int newCount)
	{
	}
	
	public void onRemoved(ISolarPanelTile tile, int prevCount, int newCount)
	{
	}
	
	protected Supplier<String> tooltipId = Suppliers.memoize(() -> Util.makeDescriptionId("info", ForgeRegistries.ITEMS.getKey(this)));
	
	protected Object[] hoverTextData(ItemStack stack)
	{
		return new Object[0];
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag context)
	{
		try
		{
			var txt = Component.literal(Component.translatable(tooltipId.get()).getString().formatted(hoverTextData(stack)));
			tooltip.add(txt.withStyle(Style.EMPTY.withColor(0x666666)));
		} catch(IllegalFormatException e)
		{
			tooltip.add(Component.translatable(e.getMessage()));
		}
	}
	
	public boolean canStayInPanel(ISolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return true;
	}
	
	public boolean canInstall(ISolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return true;
	}
	
	public int getMaxUpgradesInstalled(ISolarPanelTile tile)
	{
		return getMaxStackSize(getDefaultInstance());
	}
	
	public <T> Optional<T> findAbility(Ability<T> ability)
	{
		return ability.findIn();
	}
}