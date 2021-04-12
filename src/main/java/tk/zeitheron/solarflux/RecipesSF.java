package tk.zeitheron.solarflux;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import tk.zeitheron.solarflux.items.ItemsSF;
import tk.zeitheron.solarflux.panels.SolarPanel;
import tk.zeitheron.solarflux.panels.SolarPanels;
import tk.zeitheron.solarflux.util.RecipeHelper;

public class RecipesSF
{
	public static final RecipeHelper helper = new RecipeHelper("solarflux");

	public static void addRecipes()
	{
		helper.addKeyShaped(new ItemStack(ItemsSF.MIRROR, 3), "ggg", " i ", 'g', "blockGlass", 'i', "ingotIron");
		helper.addKeyShaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_1), "ggg", "lll", "mmm", 'g', "blockGlass", 'l', "gemLapis", 'm', ItemsSF.MIRROR);
		helper.addKeyShaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_2), "clc", "lcl", "msm", 'c', Items.CLAY_BALL, 'l', "gemLapis", 'm', ItemsSF.MIRROR, 's', ItemsSF.PHOTOVOLTAIC_CELL_1);
		helper.addKeyShaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_3), "ggg", "lll", "oco", 'g', "blockGlass", 'l', "dustGlowstone", 'o', "obsidian", 'c', ItemsSF.PHOTOVOLTAIC_CELL_2);
		helper.addKeyShaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_4), "bbb", "gdg", "qcq", 'b', Items.BLAZE_POWDER, 'g', "dustGlowstone", 'd', "gemDiamond", 'q', "blockQuartz", 'c', ItemsSF.PHOTOVOLTAIC_CELL_3);
		helper.addKeyShaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_5), "bbb", "gdg", "qcq", 'b', Items.BLAZE_ROD, 'g', Blocks.GLOWSTONE, 'd', "blockDiamond", 'q', "blockQuartz", 'c', new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_4));
		helper.addKeyShaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_6), "bbb", "gdg", "qcq", 'b', "gemEmerald", 'g', Blocks.GLOWSTONE, 'd', "blockDiamond", 'q', "blockQuartz", 'c', new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_5));
		helper.addKeyShaped(new ItemStack(ItemsSF.BLANK_UPGRADE), " c ", "cmc", " c ", 'c', "cobblestone", 'm', ItemsSF.MIRROR);

		Item dragon_egg = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "dragon_egg"));

		helper.addKeyShaped(new ItemStack(SolarPanels.CORE_PANELS[0].getBlock()), "mmm", "prp", "ppp", 'm', ItemsSF.MIRROR, 'p', "plankWood", 'r', "dustRedstone");
		helper.addKeyShaped(new ItemStack(SolarPanels.CORE_PANELS[1].getBlock()), "sss", "sps", "sss", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[0].delegateData.generation), 'p', Blocks.PISTON);
		helper.addKeyShaped(new ItemStack(SolarPanels.CORE_PANELS[2].getBlock(), 2), "ppp", "scs", "sbs", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[1].delegateData.generation), 'p', ItemsSF.PHOTOVOLTAIC_CELL_1, 'c', Items.REPEATER, 'b', "blockIron");
		helper.addKeyShaped(new ItemStack(SolarPanels.CORE_PANELS[3].getBlock(), 2), "ppp", "scs", "sbs", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[2].delegateData.generation), 'p', ItemsSF.PHOTOVOLTAIC_CELL_2, 'c', Items.CLOCK, 'b', "blockIron");
		helper.addKeyShaped(new ItemStack(SolarPanels.CORE_PANELS[4].getBlock(), 2), "ppp", "scs", "sbs", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[3].delegateData.generation), 'p', ItemsSF.PHOTOVOLTAIC_CELL_3, 'c', "dustGlowstone", 'b', "blockGold");
		helper.addKeyShaped(new ItemStack(SolarPanels.CORE_PANELS[5].getBlock(), 2), "ppp", "scs", "sbs", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[4].delegateData.generation), 'p', ItemsSF.PHOTOVOLTAIC_CELL_4, 'c', Blocks.REDSTONE_LAMP, 'b', "blockDiamond");
		helper.addKeyShaped(new ItemStack(SolarPanels.CORE_PANELS[6].getBlock(), 2), "ppp", "scs", "scs", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[5].delegateData.generation), 'p', ItemsSF.PHOTOVOLTAIC_CELL_5, 'c', new ItemStack(Items.DRAGON_BREATH));
		helper.addKeyShaped(new ItemStack(SolarPanels.CORE_PANELS[7].getBlock(), 2), "ppp", "scs", "scs", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[6].delegateData.generation), 'p', ItemsSF.PHOTOVOLTAIC_CELL_6, 'c', new ItemStack(dragon_egg));

		helper.addKeyShaped(new ItemStack(ItemsSF.EFFICIENCY_UPGRADE), " m ", "mum", " c ", 'm', ItemsSF.MIRROR, 'u', ItemsSF.BLANK_UPGRADE, 'c', ItemsSF.PHOTOVOLTAIC_CELL_1);
		helper.addKeyShaped(new ItemStack(ItemsSF.TRANSFER_RATE_UPGRADE), "rrr", "gug", "rrr", 'u', ItemsSF.BLANK_UPGRADE, 'r', "dustRedstone", 'g', "ingotGold");
		helper.addKeyShaped(new ItemStack(ItemsSF.TRAVERSAL_UPGRADE), "ipi", "rur", "ipi", 'i', "ingotIron", 'p', Ingredient.fromItems(Blocks.PISTON, Blocks.STICKY_PISTON), 'u', ItemsSF.BLANK_UPGRADE, 'r', "dustRedstone");
		helper.addKeyShaped(new ItemStack(ItemsSF.DISPERSIVE_UPGRADE), "geg", "eue", "geg", 'g', "dustGlowstone", 'e', Items.ENDER_EYE, 'u', ItemsSF.BLANK_UPGRADE);
		helper.addKeyShaped(new ItemStack(ItemsSF.BLOCK_CHARGING_UPGRADE), "geg", "eue", "geg", 'g', "enderpearl", 'e', "blockRedstone", 'u', ItemsSF.DISPERSIVE_UPGRADE);
		helper.addKeyShaped(new ItemStack(ItemsSF.FURNACE_UPGRADE), "ccc", "cuc", "cfc", 'u', ItemsSF.BLANK_UPGRADE, 'c', Items.COAL, 'f', Blocks.FURNACE);
		helper.addKeyShaped(new ItemStack(ItemsSF.CAPACITY_UPGRADE), " r ", "rur", "rcr", 'u', ItemsSF.BLANK_UPGRADE, 'r', "dustRedstone", 'c', "blockDiamond");

		SolarPanels.listPanels().forEach(sp -> sp.recipes(helper));
	}
}