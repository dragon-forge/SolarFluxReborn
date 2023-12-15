package org.zeith.solarflux.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
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
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(new StringTextComponent(I18n.get("info.solarflux.energy.generation", panelBlock.panel.getPanelData().generation)).withStyle(TextFormatting.GRAY));
		tooltip.add(new StringTextComponent(I18n.get("info.solarflux.energy.transfer", panelBlock.panel.getPanelData().transfer)).withStyle(TextFormatting.GRAY));
		tooltip.add(new StringTextComponent(I18n.get("info.solarflux.energy.capacity", panelBlock.panel.getPanelData().capacity)).withStyle(TextFormatting.GRAY));

		if(stack.hasTag() && (stack.getTag().contains("Upgrades") || stack.getTag().contains("Energy") || stack.getTag().contains("Chargeable")))
		{
			CompoundNBT tag = stack.getTag();

			{
				tooltip.add(new StringTextComponent(I18n.get("info.solarflux.contentretained")).withStyle(TextFormatting.ITALIC, TextFormatting.DARK_GREEN));
			}

			if(Screen.hasShiftDown())
			{
				if(tag.contains("Energy", NBT.TAG_LONG))
					tooltip.add(new StringTextComponent(I18n.get("info.solarflux.energy.stored1", tag.getLong("Energy"))).withStyle(TextFormatting.YELLOW));

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
					tooltip.add(new StringTextComponent(I18n.get("info.solarflux.upgrades.installed") + ":").withStyle(TextFormatting.GRAY));
					inventory5.stream().filter(s -> !s.isEmpty()).forEach(uStack ->
					{
						List<ITextComponent> comps = uStack.getTooltipLines(Minecraft.getInstance().player, flagIn);
						tooltip.add(new StringTextComponent(" - ").append(comps.get(0).copy().append(" x" + uStack.getCount()).withStyle(TextFormatting.GRAY)).withStyle(TextFormatting.DARK_GRAY));
						for(int i = 1; i < comps.size(); ++i)
							tooltip.add(new StringTextComponent("    ").append(comps.get(i)).withStyle(TextFormatting.DARK_GRAY));
					});
				}
			} else
				tooltip.add(new StringTextComponent(I18n.get("info.solarflux.hold.for.info", TextFormatting.YELLOW + I18n.get("info.solarflux.shift") + TextFormatting.DARK_GRAY)).withStyle(TextFormatting.DARK_GRAY));
		}
	}
}