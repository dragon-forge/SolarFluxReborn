package tk.zeitheron.solarflux.block;

import java.util.List;

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
import tk.zeitheron.solarflux.util.SimpleInventory;

public class SolarPanelBlockItem extends BlockItem
{
	public final SolarPanelBlock panelBlock;
	
	public SolarPanelBlockItem(SolarPanelBlock blockIn, Properties builder)
	{
		super(blockIn, builder);
		setRegistryName(blockIn.getRegistryName());
		this.panelBlock = blockIn;
	}
	
	SimpleInventory inventory5 = new SimpleInventory(5);
	SimpleInventory inventory1 = new SimpleInventory(1);
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(new StringTextComponent(I18n.format("info.solarflux.energy.generation", panelBlock.panel.getPanelData().generation)).applyTextStyle(TextFormatting.GRAY));
		tooltip.add(new StringTextComponent(I18n.format("info.solarflux.energy.transfer", panelBlock.panel.getPanelData().transfer)).applyTextStyle(TextFormatting.GRAY));
		tooltip.add(new StringTextComponent(I18n.format("info.solarflux.energy.capacity", panelBlock.panel.getPanelData().capacity)).applyTextStyle(TextFormatting.GRAY));
		
		if(stack.hasTag() && (stack.getTag().contains("Upgrades") || stack.getTag().contains("Energy") || stack.getTag().contains("Chargeable")))
		{
			CompoundNBT tag = stack.getTag();
			
			{
				tooltip.add(new StringTextComponent(I18n.format("info.solarflux.contentretained")).applyTextStyles(TextFormatting.ITALIC, TextFormatting.DARK_GREEN));
			}
			
			if(Screen.hasShiftDown())
			{
				if(tag.contains("Energy", NBT.TAG_LONG))
					tooltip.add(new StringTextComponent(I18n.format("info.solarflux.energy.stored1", tag.getLong("Energy"))).applyTextStyle(TextFormatting.YELLOW));
				
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
					tooltip.add(new StringTextComponent(I18n.format("info.solarflux.upgrades.installed") + ":").applyTextStyle(TextFormatting.GRAY));
					inventory5.stream().filter(s -> !s.isEmpty()).forEach(uStack ->
					{
						List<ITextComponent> comps = uStack.getTooltip(Minecraft.getInstance().player, flagIn);
						tooltip.add(new StringTextComponent(" - ").appendSibling(comps.get(0).appendText(" x" + uStack.getCount()).applyTextStyle(TextFormatting.GRAY)).applyTextStyle(TextFormatting.DARK_GRAY));
						for(int i = 1; i < comps.size(); ++i)
							tooltip.add(new StringTextComponent("    ").appendSibling(comps.get(i)).applyTextStyle(TextFormatting.DARK_GRAY));
					});
				}
			} else
				tooltip.add(new StringTextComponent(I18n.format("info.solarflux.hold.for.info", TextFormatting.YELLOW + I18n.format("info.solarflux.shift") + TextFormatting.DARK_GRAY)).applyTextStyle(TextFormatting.DARK_GRAY));
		}
	}
}