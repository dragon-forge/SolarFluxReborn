package tk.zeitheron.solarflux.block;

import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.utils.InventoryDummy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockBaseSolar
		extends ItemBlock
{
	public final BlockBaseSolar panelBlock;

	public ItemBlockBaseSolar(BlockBaseSolar block)
	{
		super(block);
		panelBlock = block;
	}

	InventoryDummy inventory5 = new InventoryDummy(5);
	InventoryDummy inventory1 = new InventoryDummy(1);

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(I18n.format("info." + InfoSF.MOD_ID + ".energy.generation", panelBlock.getPanelData().getGeneration()));
		tooltip.add(I18n.format("info." + InfoSF.MOD_ID + ".energy.transfer", panelBlock.getPanelData().getTransfer()));
		tooltip.add(I18n.format("info." + InfoSF.MOD_ID + ".energy.capacity", panelBlock.getPanelData().getCapacity()));

		if(stack.hasTagCompound() && (stack.getTagCompound().hasKey("Upgrades") || stack.getTagCompound().hasKey("Energy") || stack.getTagCompound().hasKey("Chargeable")))
		{
			tooltip.add(TextFormatting.DARK_GREEN.toString() + TextFormatting.ITALIC.toString() + I18n.format("info." + InfoSF.MOD_ID + ".contentretained"));

			if(GuiScreen.isShiftKeyDown())
			{
				NBTTagCompound tag = stack.getTagCompound();

				if(tag.hasKey("Energy", Constants.NBT.TAG_LONG))
					tooltip.add(TextFormatting.YELLOW + I18n.format("info." + InfoSF.MOD_ID + ".energy.stored1", tag.getLong("Energy")));

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
					tooltip.add(I18n.format("info." + InfoSF.MOD_ID + ".upgrades.installed") + ":");
					inventory5.stream().filter(s -> !s.isEmpty()).forEach(uStack ->
					{
						List<String> comps = uStack.getTooltip(Minecraft.getMinecraft().player, flagIn);
						tooltip.add(TextFormatting.DARK_GRAY + " - " + TextFormatting.GRAY + comps.get(0) + (" x" + uStack.getCount()));
						for(int i = 1; i < comps.size(); ++i)
							tooltip.add(TextFormatting.DARK_GRAY + "    " + comps.get(i));
					});
				}
			} else
				tooltip.add(TextFormatting.DARK_GRAY + I18n.format("info." + InfoSF.MOD_ID + ".hold.for.info", TextFormatting.YELLOW + I18n.format("info." + InfoSF.MOD_ID + ".shift") + TextFormatting.DARK_GRAY));
		} else tooltip.add(TextFormatting.DARK_GRAY + I18n.format("info." + InfoSF.MOD_ID + ".pickuptip"));
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		super.getSubItems(tab, items);
	}
}