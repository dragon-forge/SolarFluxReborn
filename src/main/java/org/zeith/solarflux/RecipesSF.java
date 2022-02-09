package org.zeith.solarflux;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import org.zeith.hammerlib.core.init.TagsHL;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.solarflux.items.ItemsSF;
import org.zeith.solarflux.panels.SolarPanels;

public class RecipesSF
{
	public static final ResourceLocation MIRROR = new ResourceLocation(InfoSF.MOD_ID, "mirror");
	public static final ResourceLocation BLAZING_COATING = new ResourceLocation(InfoSF.MOD_ID, "blazing_coating");
	public static final ResourceLocation EMERALD_GLASS = new ResourceLocation(InfoSF.MOD_ID, "emerald_glass");
	public static final ResourceLocation ENDER_GLASS = new ResourceLocation(InfoSF.MOD_ID, "ender_glass");
	public static final ResourceLocation PHOTOVOLTAIC_CELL_1 = new ResourceLocation(InfoSF.MOD_ID, "photovoltaic_cell_1");
	public static final ResourceLocation PHOTOVOLTAIC_CELL_2 = new ResourceLocation(InfoSF.MOD_ID, "photovoltaic_cell_2");
	public static final ResourceLocation PHOTOVOLTAIC_CELL_3 = new ResourceLocation(InfoSF.MOD_ID, "photovoltaic_cell_3");
	public static final ResourceLocation PHOTOVOLTAIC_CELL_4 = new ResourceLocation(InfoSF.MOD_ID, "photovoltaic_cell_4");
	public static final ResourceLocation PHOTOVOLTAIC_CELL_5 = new ResourceLocation(InfoSF.MOD_ID, "photovoltaic_cell_5");
	public static final ResourceLocation PHOTOVOLTAIC_CELL_6 = new ResourceLocation(InfoSF.MOD_ID, "photovoltaic_cell_6");
	public static final ResourceLocation BLANK_UPGRADE = new ResourceLocation(InfoSF.MOD_ID, "blank_upgrade");

	public static final ResourceLocation SOLAR_PANEL_1 = new ResourceLocation(InfoSF.MOD_ID, "solar_panel_1");
	public static final ResourceLocation SOLAR_PANEL_2 = new ResourceLocation(InfoSF.MOD_ID, "solar_panel_2");
	public static final ResourceLocation SOLAR_PANEL_3 = new ResourceLocation(InfoSF.MOD_ID, "solar_panel_3");
	public static final ResourceLocation SOLAR_PANEL_4 = new ResourceLocation(InfoSF.MOD_ID, "solar_panel_4");
	public static final ResourceLocation SOLAR_PANEL_5 = new ResourceLocation(InfoSF.MOD_ID, "solar_panel_5");
	public static final ResourceLocation SOLAR_PANEL_6 = new ResourceLocation(InfoSF.MOD_ID, "solar_panel_6");
	public static final ResourceLocation SOLAR_PANEL_7 = new ResourceLocation(InfoSF.MOD_ID, "solar_panel_7");
	public static final ResourceLocation SOLAR_PANEL_8 = new ResourceLocation(InfoSF.MOD_ID, "solar_panel_8");

	public static final ResourceLocation EFFICIENCY_UPGRADE = new ResourceLocation(InfoSF.MOD_ID, "efficiency_upgrade");
	public static final ResourceLocation TRANSFER_RATE_UPGRADE = new ResourceLocation(InfoSF.MOD_ID, "transfer_rate_upgrade");
	public static final ResourceLocation TRAVERSAL_UPGRADE = new ResourceLocation(InfoSF.MOD_ID, "traversal_upgrade");
	public static final ResourceLocation DISPERSIVE_UPGRADE = new ResourceLocation(InfoSF.MOD_ID, "dispersive_upgrade");
	public static final ResourceLocation BLOCK_CHARGING_UPGRADE = new ResourceLocation(InfoSF.MOD_ID, "block_charging_upgrade");
	public static final ResourceLocation FURNACE_UPGRADE = new ResourceLocation(InfoSF.MOD_ID, "furnace_upgrade");
	public static final ResourceLocation CAPACITY_UPGRADE = new ResourceLocation(InfoSF.MOD_ID, "capacity_upgrade");

	public static void indexRecipes()
	{
		SolarPanels.indexRecipes(MIRROR);
		SolarPanels.indexRecipes(BLAZING_COATING, EMERALD_GLASS, ENDER_GLASS);
		SolarPanels.indexRecipes(PHOTOVOLTAIC_CELL_1, PHOTOVOLTAIC_CELL_2, PHOTOVOLTAIC_CELL_3, PHOTOVOLTAIC_CELL_4, PHOTOVOLTAIC_CELL_5, PHOTOVOLTAIC_CELL_6);
		SolarPanels.indexRecipes(BLANK_UPGRADE);
		SolarPanels.indexRecipes(SOLAR_PANEL_1, SOLAR_PANEL_2, SOLAR_PANEL_3, SOLAR_PANEL_4, SOLAR_PANEL_5, SOLAR_PANEL_6, SOLAR_PANEL_7, SOLAR_PANEL_8);
		SolarPanels.indexRecipes(EFFICIENCY_UPGRADE, TRANSFER_RATE_UPGRADE, TRAVERSAL_UPGRADE, DISPERSIVE_UPGRADE, BLOCK_CHARGING_UPGRADE, FURNACE_UPGRADE, CAPACITY_UPGRADE);
	}

	public static void addRecipes(RegisterRecipesEvent $)
	{
		indexRecipes();
		SolarPanels.refreshRecipes();

		//		helper.addKeyShaped(MIRROR, new ItemStack(ItemsSF.MIRROR, 3), "ggg", " i ", 'g', "blockGlass", 'i', "ingotIron");
		//		helper.addKeyShaped(PHOTOVOLTAIC_CELL_1, new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_1), "ggg", "lll", "mmm", 'g', "blockGlass", 'l', "gemLapis", 'm', ItemsSF.MIRROR);
		//		helper.addKeyShaped(PHOTOVOLTAIC_CELL_2, new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_2), "clc", "lcl", "msm", 'c', Items.CLAY_BALL, 'l', "gemLapis", 'm', ItemsSF.MIRROR, 's', ItemsSF.PHOTOVOLTAIC_CELL_1);
		//		helper.addKeyShaped(PHOTOVOLTAIC_CELL_3, new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_3), "ggg", "lll", "oco", 'g', "blockGlass", 'l', "dustGlowstone", 'o', "obsidian", 'c', ItemsSF.PHOTOVOLTAIC_CELL_2);
		//		helper.addKeyShaped(PHOTOVOLTAIC_CELL_4, new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_4), "bbb", "gdg", "qcq", 'b', Items.BLAZE_POWDER, 'g', "dustGlowstone", 'd', "gemDiamond", 'q', "blockQuartz", 'c', ItemsSF.PHOTOVOLTAIC_CELL_3);
		//		helper.addKeyShaped(PHOTOVOLTAIC_CELL_5, new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_5), "bbb", "gdg", "qcq", 'b', Items.BLAZE_ROD, 'g', Blocks.GLOWSTONE, 'd', "blockDiamond", 'q', "blockQuartz", 'c', new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_4));
		//		helper.addKeyShaped(PHOTOVOLTAIC_CELL_6, new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_6), "bbb", "gdg", "qcq", 'b', "gemEmerald", 'g', Blocks.GLOWSTONE, 'd', "blockDiamond", 'q', "blockQuartz", 'c', new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_5));
		//		helper.addKeyShaped(BLANK_UPGRADE, new ItemStack(ItemsSF.BLANK_UPGRADE), " c ", "cmc", " c ", 'c', "cobblestone", 'm', ItemsSF.MIRROR);

		$.shaped()
				.id(MIRROR)
				.result(ItemsSF.MIRROR, 3)
				.shape("ggg", " i ")
				.map('g', Tags.Items.GLASS)
				.map('i', Tags.Items.INGOTS_IRON)
				.registerIf(SolarPanels::isRecipeActive);

		$.shaped()
				.id(BLAZING_COATING)
				.result(ItemsSF.BLAZING_COATING, 2)
				.shape("m", "b", "m")
				.map('m', ItemsSF.MIRROR)
				.map('b', Items.BLAZE_POWDER)
				.registerIf(SolarPanels::isRecipeActive);

		$.shaped()
				.id(EMERALD_GLASS)
				.result(ItemsSF.EMERALD_GLASS, 2)
				.shape("m", "e", "m")
				.map('m', ItemsSF.MIRROR)
				.map('e', Tags.Items.GEMS_EMERALD)
				.registerIf(SolarPanels::isRecipeActive);

		$.shaped()
				.id(ENDER_GLASS)
				.result(ItemsSF.ENDER_GLASS, 3)
				.shape("eee", "pyp")
				.map('p', Tags.Items.ENDER_PEARLS)
				.map('y', Items.ENDER_EYE)
				.map('e', ItemsSF.EMERALD_GLASS)
				.registerIf(SolarPanels::isRecipeActive);

		{
			$.shaped()
					.id(PHOTOVOLTAIC_CELL_1)
					.result(ItemsSF.PHOTOVOLTAIC_CELL_1)
					.shape("ggg", "lll", "mmm")
					.map('g', Tags.Items.GLASS)
					.map('l', Tags.Items.GEMS_LAPIS)
					.map('m', ItemsSF.MIRROR)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(PHOTOVOLTAIC_CELL_2)
					.result(ItemsSF.PHOTOVOLTAIC_CELL_2)
					.shape("clc", "lcl", "msm")
					.map('c', Items.CLAY_BALL)
					.map('l', Tags.Items.GEMS_LAPIS)
					.map('m', ItemsSF.MIRROR)
					.map('s', ItemsSF.PHOTOVOLTAIC_CELL_1)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(PHOTOVOLTAIC_CELL_3)
					.result(ItemsSF.PHOTOVOLTAIC_CELL_3)
					.shape("ggg", "lll", "oco")
					.map('g', Tags.Items.GLASS)
					.map('l', Tags.Items.DUSTS_GLOWSTONE)
					.map('o', Tags.Items.OBSIDIAN)
					.map('c', ItemsSF.PHOTOVOLTAIC_CELL_2)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(PHOTOVOLTAIC_CELL_4)
					.result(ItemsSF.PHOTOVOLTAIC_CELL_4)
					.shape("bbb", "gdg", "qcq")
					.map('b', ItemsSF.BLAZING_COATING)
					.map('g', Tags.Items.DUSTS_GLOWSTONE)
					.map('d', Tags.Items.GEMS_DIAMOND)
					.map('q', Tags.Items.STORAGE_BLOCKS_QUARTZ)
					.map('c', ItemsSF.PHOTOVOLTAIC_CELL_3)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(PHOTOVOLTAIC_CELL_5)
					.result(ItemsSF.PHOTOVOLTAIC_CELL_5, 2)
					.shape("bbb", "gdg", "cqc")
					.map('b', ItemsSF.EMERALD_GLASS)
					.map('g', TagsHL.Items.STORAGE_BLOCKS_GLOWSTONE)
					.map('d', Tags.Items.STORAGE_BLOCKS_DIAMOND)
					.map('q', Tags.Items.STORAGE_BLOCKS_QUARTZ)
					.map('c', ItemsSF.PHOTOVOLTAIC_CELL_4)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(PHOTOVOLTAIC_CELL_6)
					.result(ItemsSF.PHOTOVOLTAIC_CELL_6, 2)
					.shape("bbb", "gdg", "cqc")
					.map('b', ItemsSF.ENDER_GLASS)
					.map('g', TagsHL.Items.STORAGE_BLOCKS_GLOWSTONE)
					.map('d', Tags.Items.STORAGE_BLOCKS_EMERALD)
					.map('q', Tags.Items.STORAGE_BLOCKS_QUARTZ)
					.map('c', ItemsSF.PHOTOVOLTAIC_CELL_5)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(BLANK_UPGRADE)
					.result(ItemsSF.BLANK_UPGRADE)
					.shape(" c ", "cmc", " c ")
					.map('c', Tags.Items.COBBLESTONE)
					.map('m', ItemsSF.MIRROR)
					.registerIf(SolarPanels::isRecipeActive);
		}

		{
			$.shaped()
					.id(SOLAR_PANEL_1)
					.result(SolarPanels.CORE_PANELS[0].getBlock())
					.shape("mmm", "prp", "ppp")
					.map('m', ItemsSF.MIRROR)
					.map('p', ItemTags.PLANKS)
					.map('r', Tags.Items.DUSTS_REDSTONE)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(SOLAR_PANEL_2)
					.result(SolarPanels.CORE_PANELS[1].getBlock())
					.shape("sss", "sps", "sss")
					.map('s', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[0]))
					.map('p', TagsHL.Items.PISTONS)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(SOLAR_PANEL_3)
					.result(SolarPanels.CORE_PANELS[2].getBlock(), 2)
					.shape("ppp", "scs", "sbs")
					.map('s', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[1]))
					.map('p', ItemsSF.PHOTOVOLTAIC_CELL_1)
					.map('c', Items.REPEATER)
					.map('b', Tags.Items.STORAGE_BLOCKS_IRON)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(SOLAR_PANEL_4)
					.result(SolarPanels.CORE_PANELS[3].getBlock(), 2)
					.shape("ppp", "scs", "sbs")
					.map('s', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[2]))
					.map('p', ItemsSF.PHOTOVOLTAIC_CELL_2)
					.map('c', Items.CLOCK)
					.map('b', Tags.Items.STORAGE_BLOCKS_IRON)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(SOLAR_PANEL_5)
					.result(SolarPanels.CORE_PANELS[4].getBlock(), 2)
					.shape("ppp", "scs", "sbs")
					.map('s', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[3]))
					.map('p', ItemsSF.PHOTOVOLTAIC_CELL_3)
					.map('c', TagsHL.Items.STORAGE_BLOCKS_GLOWSTONE)
					.map('b', Tags.Items.STORAGE_BLOCKS_GOLD)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(SOLAR_PANEL_6)
					.result(SolarPanels.CORE_PANELS[5].getBlock(), 2)
					.shape("ppp", "scs", "sbs")
					.map('s', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[4]))
					.map('p', ItemsSF.PHOTOVOLTAIC_CELL_4)
					.map('c', Blocks.REDSTONE_LAMP)
					.map('b', Tags.Items.STORAGE_BLOCKS_DIAMOND)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(SOLAR_PANEL_7)
					.result(SolarPanels.CORE_PANELS[6].getBlock(), 2)
					.shape("ppp", "scs", "scs")
					.map('s', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[5]))
					.map('p', ItemsSF.PHOTOVOLTAIC_CELL_5)
					.map('c', Items.DRAGON_BREATH)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(SOLAR_PANEL_8)
					.result(SolarPanels.CORE_PANELS[7].getBlock(), 2)
					.shape("ppp", "scs", "scs")
					.map('s', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[6]))
					.map('p', ItemsSF.PHOTOVOLTAIC_CELL_6)
					.map('c', Items.DRAGON_EGG)
					.registerIf(SolarPanels::isRecipeActive);
		}

		{
			$.shaped()
					.id(EFFICIENCY_UPGRADE)
					.result(ItemsSF.EFFICIENCY_UPGRADE)
					.shape(" m ", "mum", " c ")
					.map('m', ItemsSF.MIRROR)
					.map('u', ItemsSF.BLANK_UPGRADE)
					.map('c', ItemsSF.PHOTOVOLTAIC_CELL_1)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(TRANSFER_RATE_UPGRADE)
					.result(ItemsSF.TRANSFER_RATE_UPGRADE)
					.shape("rrr", "gug", "rrr")
					.map('u', ItemsSF.BLANK_UPGRADE)
					.map('r', Tags.Items.DUSTS_REDSTONE)
					.map('g', Tags.Items.INGOTS_GOLD)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(TRAVERSAL_UPGRADE)
					.result(ItemsSF.TRAVERSAL_UPGRADE)
					.shape("ipi", "rur", "ipi")
					.map('i', Tags.Items.INGOTS_IRON)
					.map('p', TagsHL.Items.PISTONS)
					.map('u', ItemsSF.BLANK_UPGRADE)
					.map('r', Tags.Items.DUSTS_REDSTONE)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(DISPERSIVE_UPGRADE)
					.result(ItemsSF.DISPERSIVE_UPGRADE)
					.shape("geg", "eue", "geg")
					.map('g', Tags.Items.DUSTS_GLOWSTONE)
					.map('e', Items.ENDER_EYE)
					.map('u', ItemsSF.BLANK_UPGRADE)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(BLOCK_CHARGING_UPGRADE)
					.result(ItemsSF.BLOCK_CHARGING_UPGRADE)
					.shape("geg", "eue", "geg")
					.map('g', Tags.Items.ENDER_PEARLS)
					.map('e', Tags.Items.STORAGE_BLOCKS_REDSTONE)
					.map('u', ItemsSF.DISPERSIVE_UPGRADE)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(FURNACE_UPGRADE)
					.result(ItemsSF.FURNACE_UPGRADE)
					.shape("ccc", "cuc", "bfb")
					.map('u', ItemsSF.BLANK_UPGRADE)
					.map('c', Items.COAL)
					.map('f', Blocks.FURNACE)
					.map('b', ItemsSF.BLAZING_COATING)
					.registerIf(SolarPanels::isRecipeActive);

			$.shaped()
					.id(CAPACITY_UPGRADE)
					.result(ItemsSF.CAPACITY_UPGRADE)
					.shape(" r ", "rur", "rcr")
					.map('u', ItemsSF.BLANK_UPGRADE)
					.map('r', Tags.Items.STORAGE_BLOCKS_REDSTONE)
					.map('c', Tags.Items.STORAGE_BLOCKS_DIAMOND)
					.registerIf(SolarPanels::isRecipeActive);
		}

		SolarPanels.listPanels().forEach(sp -> sp.recipes($));
	}
}