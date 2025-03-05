package org.zeith.solarflux.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import org.zeith.hammerlib.util.CommonMessages;

import java.util.List;

public class ItemMaterial
		extends Item
{
	public ItemMaterial()
	{
		super(new Properties());
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag)
	{
		tooltip.add(CommonMessages.CRAFTING_MATERIAL);
	}
}