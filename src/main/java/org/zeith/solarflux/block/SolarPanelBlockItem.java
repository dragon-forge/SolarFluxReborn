package org.zeith.solarflux.block;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.zeith.hammerlib.api.items.ITabItem;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.items.data.PanelDataComponent;

import java.util.List;

public class SolarPanelBlockItem
		extends BlockItem
		implements ITabItem
{
	public final SolarPanelBlock panelBlock;
	
	public SolarPanelBlockItem(SolarPanelBlock blockIn, Properties builder)
	{
		super(blockIn, builder
				.component(PanelDataComponent.TYPE.get(), PanelDataComponent.EMPTY)
		);
		this.panelBlock = blockIn;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn)
	{
		tooltip.add(Component.literal(I18n.get("info.solarflux.energy.generation", panelBlock.panel.getPanelData().generation)).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.literal(I18n.get("info.solarflux.energy.transfer", panelBlock.panel.getPanelData().transfer)).withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.literal(I18n.get("info.solarflux.energy.capacity", panelBlock.panel.getPanelData().capacity)).withStyle(ChatFormatting.GRAY));
		
		PanelDataComponent com = stack.get(PanelDataComponent.TYPE.get());
		if(com == null || com.isEmpty()) return;
		
		tooltip.add(Component.literal(I18n.get("info.solarflux.contentretained")).withStyle(s -> s.withItalic(true).withColor(0x009BB4)));
		tooltip.add(Component.literal(I18n.get("info.solarflux.resetcontent")).withStyle(s -> s.withItalic(true).withColor(0x01D7C7)));
		
		if(Screen.hasShiftDown())
		{
			if(com.energy() > 0L)
				tooltip.add(Component.literal(I18n.get("info.solarflux.energy.stored1", com.energy())).withStyle(ChatFormatting.YELLOW));
			
			boolean empty = true;
			for(ItemStack uStack : com.upgrades())
				if(!uStack.isEmpty())
				{
					empty = false;
					break;
				}
			if(!empty)
			{
				tooltip.add(Component.literal(I18n.get("info.solarflux.upgrades.installed") + ":").withStyle(ChatFormatting.GRAY));
				com.upgrades().stream()
						.filter(s -> !s.isEmpty())
						.<Component>mapMulti((uStack, line) ->
						{
							List<Component> comps = uStack.getTooltipLines(context, Minecraft.getInstance().player, flagIn);
							if(comps.isEmpty()) return;
							
							line.accept(Component.literal(" - ")
									.append(comps.getFirst().copy().append(" x" + uStack.getCount()).withStyle(ChatFormatting.GRAY))
									.withStyle(ChatFormatting.DARK_GRAY)
							);
							
							for(int i = 1; i < comps.size(); ++i)
								line.accept(Component.literal("    ").append(comps.get(i)).withStyle(ChatFormatting.DARK_GRAY));
						}).forEach(tooltip::add);
			}
		} else
			tooltip.add(Component.literal(I18n.get("info.solarflux.hold.for.info", ChatFormatting.YELLOW + I18n.get("info.solarflux.shift") + ChatFormatting.DARK_GRAY)).withStyle(ChatFormatting.DARK_GRAY));
	}
	
	@Override
	public CreativeModeTab getItemCategory()
	{
		return SolarFlux.ITEM_GROUP.tab();
	}
}