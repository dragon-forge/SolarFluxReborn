package tk.zeitheron.solarflux.block;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SolarPanelBlockItem extends BlockItem
{
	public final SolarPanelBlock panelBlock;
	
	public SolarPanelBlockItem(SolarPanelBlock blockIn, Properties builder)
	{
		super(blockIn, builder);
		setRegistryName(blockIn.getRegistryName());
		this.panelBlock = blockIn;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(new StringTextComponent(I18n.format("info.solarflux.energy.generation", panelBlock.panel.getPanelData().generation)));
		tooltip.add(new StringTextComponent(I18n.format("info.solarflux.energy.transfer", panelBlock.panel.getPanelData(). transfer)));
		tooltip.add(new StringTextComponent(I18n.format("info.solarflux.energy.capacity", panelBlock.panel.getPanelData().capacity)));
	}
}