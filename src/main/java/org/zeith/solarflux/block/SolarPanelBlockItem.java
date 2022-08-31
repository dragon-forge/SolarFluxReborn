package org.zeith.solarflux.block;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.inv.SimpleInventory;

import java.util.List;

public class SolarPanelBlockItem
		extends BlockItem
{
	public final SolarPanelBlock panelBlock;
	
	public SolarPanelBlockItem(SolarPanelBlock blockIn, Properties builder)
	{
		super(blockIn, builder);
		this.panelBlock = blockIn;
	}
	
	SimpleInventory inventory5 = new SimpleInventory(5);
	SimpleInventory inventory1 = new SimpleInventory(1);
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		tooltip.add(Component.literal(I18n.get("info.solarflux.energy.generation", panelBlock.panel.getPanelData().generation)).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.literal(I18n.get("info.solarflux.energy.transfer", panelBlock.panel.getPanelData().transfer)).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.literal(I18n.get("info.solarflux.energy.capacity", panelBlock.panel.getPanelData().capacity)).withStyle(ChatFormatting.GRAY));
		
		if(stack.hasTag() && (stack.getTag().contains("Upgrades") || stack.getTag().contains("Energy") || stack.getTag().contains("Chargeable")))
		{
			CompoundTag tag = stack.getTag();
			
			{
				tooltip.add(Component.literal(I18n.get("info.solarflux.contentretained")).withStyle(s -> s.withItalic(true).withColor(0x009BB4)));
				tooltip.add(Component.literal(I18n.get("info.solarflux.resetcontent")).withStyle(s -> s.withItalic(true).withColor(0x01D7C7)));
			}
			
			if(Screen.hasShiftDown())
			{
				if(tag.contains("Energy", Tag.TAG_LONG))
					tooltip.add(Component.literal(I18n.get("info.solarflux.energy.stored1", tag.getLong("Energy"))).withStyle(ChatFormatting.YELLOW));
				
				inventory5.readFromNBT(tag, "Upgrades");
				boolean empty = true;
				for(ItemStack uStack : inventory5)
					if(!uStack.isEmpty())
					{
						empty = false;
						break;
					}
				if(!empty)
				{
					tooltip.add(Component.literal(I18n.get("info.solarflux.upgrades.installed") + ":").withStyle(ChatFormatting.GRAY));
					inventory5.stream().filter(s -> !s.isEmpty()).forEach(uStack ->
					{
						List<Component> comps = uStack.getTooltipLines(Minecraft.getInstance().player, flagIn);
						tooltip.add(Component.literal(" - ").append(comps.get(0).copy().append(" x" + uStack.getCount()).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
						for(int i = 1; i < comps.size(); ++i)
							tooltip.add(Component.literal("    ").append(comps.get(i)).withStyle(ChatFormatting.DARK_GRAY));
					});
				}
			} else
				tooltip.add(Component.literal(I18n.get("info.solarflux.hold.for.info", ChatFormatting.YELLOW + I18n.get("info.solarflux.shift") + ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.DARK_GRAY));
		}
	}
}