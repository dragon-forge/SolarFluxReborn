package org.zeith.solarflux.items._base;

import com.google.common.base.Suppliers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.api.ISolarPanelTile;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public abstract class UpgradeItem
		extends Item
{
	public UpgradeItem(int stackSize)
	{
		this(new Item.Properties().stacksTo(stackSize));
	}
	
	public UpgradeItem(Properties props)
	{
		super(props.tab(SolarFlux.ITEM_GROUP));
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
	
	public boolean canStayInPanel(ISolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return canInstall(tile, stack, upgradeInv);
	}
	
	public boolean canInstall(ISolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return true;
	}
	
	protected Supplier<String> tooltipId = Suppliers.memoize(() -> Util.makeDescriptionId("info", ForgeRegistries.ITEMS.getKey(this)));
	
	protected Object[] hoverTextData(ItemStack stack)
	{
		return new Object[0];
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World level, List<ITextComponent> tooltip, ITooltipFlag context)
	{
		try
		{
			StringTextComponent txt = new StringTextComponent(String.format(new TranslationTextComponent(tooltipId.get()).getString(), hoverTextData(stack)));
			txt.setStyle(txt.getStyle().withColor(Color.fromRgb(0x666666)));
			tooltip.add(txt);
		} catch(IllegalFormatException e)
		{
			tooltip.add(new TranslationTextComponent(e.getMessage()));
		}
		
		super.appendHoverText(stack, level, tooltip, context);
	}
	
	public int getMaxUpgradesInstalled(ISolarPanelTile tile)
	{
		return getDefaultInstance().getMaxStackSize();
	}
	
	public <T> Optional<T> findAbility(Ability<T> ability)
	{
		return ability.findIn();
	}
}