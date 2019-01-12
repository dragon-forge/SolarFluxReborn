package com.zeitheron.solarflux.init;

import com.zeitheron.solarflux.InfoSF;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

public class RecipesSF
{
	public static void register(IForgeRegistry<IRecipe> registry)
	{
		registry.register(shaped(new ItemStack(ItemsSF.MIRROR, 3), "ggg", " i ", 'g', "blockGlass", 'i', "ingotIron").setRegistryName(InfoSF.MOD_ID, "mirror"));
		registry.register(shaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_1), "ggg", "lll", "mmm", 'g', "blockGlass", 'l', "gemLapis", 'm', ItemsSF.MIRROR).setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_1"));
		registry.register(shaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_2), "clc", "lcl", "msm", 'c', Items.CLAY_BALL, 'l', "gemLapis", 'm', ItemsSF.MIRROR, 's', ItemsSF.PHOTOVOLTAIC_CELL_1).setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_2"));
		registry.register(shaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_3), "ggg", "lll", "oco", 'g', "blockGlass", 'l', "dustGlowstone", 'o', Blocks.OBSIDIAN, 'c', ItemsSF.PHOTOVOLTAIC_CELL_2).setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_3"));
		registry.register(shaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_4), "bbb", "gdg", "qcq", 'b', Items.BLAZE_POWDER, 'g', "dustGlowstone", 'd', "gemDiamond", 'q', "blockQuartz", 'c', ItemsSF.PHOTOVOLTAIC_CELL_3).setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_4"));
		registry.register(shaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_5), "bbb", "gdg", "qcq", 'b', Items.BLAZE_ROD, 'g', "glowstone", 'd', "blockDiamond", 'q', "blockQuartz", 'c', new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_4)).setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_5"));
		registry.register(shaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_6), "bbb", "gdg", "qcq", 'b', "gemEmerald", 'g', "glowstone", 'd', "blockDiamond", 'q', "blockQuartz", 'c', new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_5)).setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_6"));
		registry.register(shaped(new ItemStack(ItemsSF.BLANK_UPGRADE), " c ", "cmc", " c ", 'c', "cobblestone", 'm', ItemsSF.MIRROR).setRegistryName(InfoSF.MOD_ID, "blank_upgrade"));
		
		Item dragon_egg = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "dragon_egg"));
		
		registry.register(shaped(new ItemStack(SolarsSF.SOLAR_1.getBlock()), "mmm", "prp", "ppp", 'm', ItemsSF.MIRROR, 'p', "plankWood", 'r', "dustRedstone").setRegistryName(InfoSF.MOD_ID, "solar_panel_1"));
		registry.register(shaped(new ItemStack(SolarsSF.SOLAR_2.getBlock()), "sss", "sps", "sss", 's', SolarsSF.getGeneratingSolars(SolarsSF.SOLAR_1.maxGeneration), 'p', Blocks.PISTON).setRegistryName(InfoSF.MOD_ID, "solar_panel_2"));
		registry.register(shaped(new ItemStack(SolarsSF.SOLAR_3.getBlock(), 2), "ppp", "scs", "sbs", 's', SolarsSF.getGeneratingSolars(SolarsSF.SOLAR_2.maxGeneration), 'p', ItemsSF.PHOTOVOLTAIC_CELL_1, 'c', Items.REPEATER, 'b', "blockIron").setRegistryName(InfoSF.MOD_ID, "solar_panel_3"));
		registry.register(shaped(new ItemStack(SolarsSF.SOLAR_4.getBlock(), 2), "ppp", "scs", "sbs", 's', SolarsSF.getGeneratingSolars(SolarsSF.SOLAR_3.maxGeneration), 'p', ItemsSF.PHOTOVOLTAIC_CELL_2, 'c', Items.CLOCK, 'b', "blockIron").setRegistryName(InfoSF.MOD_ID, "solar_panel_4"));
		registry.register(shaped(new ItemStack(SolarsSF.SOLAR_5.getBlock(), 2), "ppp", "scs", "sbs", 's', SolarsSF.getGeneratingSolars(SolarsSF.SOLAR_4.maxGeneration), 'p', ItemsSF.PHOTOVOLTAIC_CELL_3, 'c', "dustGlowstone", 'b', "blockGold").setRegistryName(InfoSF.MOD_ID, "solar_panel_5"));
		registry.register(shaped(new ItemStack(SolarsSF.SOLAR_6.getBlock(), 2), "ppp", "scs", "sbs", 's', SolarsSF.getGeneratingSolars(SolarsSF.SOLAR_5.maxGeneration), 'p', ItemsSF.PHOTOVOLTAIC_CELL_4, 'c', Blocks.REDSTONE_LAMP, 'b', "blockDiamond").setRegistryName(InfoSF.MOD_ID, "solar_panel_6"));
		registry.register(shaped(new ItemStack(SolarsSF.SOLAR_7.getBlock(), 2), "ppp", "scs", "scs", 's', SolarsSF.getGeneratingSolars(SolarsSF.SOLAR_6.maxGeneration), 'p', ItemsSF.PHOTOVOLTAIC_CELL_5, 'c', new ItemStack(Items.DRAGON_BREATH)).setRegistryName(InfoSF.MOD_ID, "solar_panel_7"));
		registry.register(shaped(new ItemStack(SolarsSF.SOLAR_8.getBlock(), 2), "ppp", "scs", "scs", 's', SolarsSF.getGeneratingSolars(SolarsSF.SOLAR_7.maxGeneration), 'p', ItemsSF.PHOTOVOLTAIC_CELL_6, 'c', new ItemStack(dragon_egg)).setRegistryName(InfoSF.MOD_ID, "solar_panel_8"));
		registry.register(shaped(new ItemStack(ItemsSF.EFFICIENCY_UPGRADE), " m ", "mum", " c ", 'm', ItemsSF.MIRROR, 'u', ItemsSF.BLANK_UPGRADE, 'c', ItemsSF.PHOTOVOLTAIC_CELL_1).setRegistryName(InfoSF.MOD_ID, "efficiency_upgrade"));
		registry.register(shaped(new ItemStack(ItemsSF.TRANSFER_RATE_UPGRADE), "rrr", "gug", "rrr", 'u', ItemsSF.BLANK_UPGRADE, 'r', "dustRedstone", 'g', "ingotGold").setRegistryName(InfoSF.MOD_ID, "transfer_rate_upgrade"));
		registry.register(shaped(new ItemStack(ItemsSF.TRAVERSAL_UPGRADE), "ipi", "rur", "ipi", 'i', "ingotIron", 'p', Ingredient.fromItems(Item.getItemFromBlock(Blocks.PISTON), Item.getItemFromBlock(Blocks.STICKY_PISTON)), 'u', ItemsSF.BLANK_UPGRADE, 'r', "dustRedstone").setRegistryName(InfoSF.MOD_ID, "traversal_upgrade"));
		registry.register(shaped(new ItemStack(ItemsSF.DISPERSIVE_UPGRADE), "geg", "eue", "geg", 'g', "dustGlowstone", 'e', Items.ENDER_EYE, 'u', ItemsSF.BLANK_UPGRADE).setRegistryName(InfoSF.MOD_ID, "dispersive_upgrade"));
		registry.register(shaped(new ItemStack(ItemsSF.BLOCK_CHARGING_UPGRADE), "geg", "eue", "geg", 'g', "enderpearl", 'e', "blockRedstone", 'u', ItemsSF.DISPERSIVE_UPGRADE).setRegistryName(InfoSF.MOD_ID, "block_charging_upgrade"));
		registry.register(shaped(new ItemStack(ItemsSF.FURNACE_UPGRADE), "ccc", "cuc", "cfc", 'u', ItemsSF.BLANK_UPGRADE, 'c', Items.COAL, 'f', Blocks.FURNACE).setRegistryName(InfoSF.MOD_ID, "furnace_upgrade"));
		registry.register(shaped(new ItemStack(ItemsSF.CAPACITY_UPGRADE), " r ", "rur", "rcr", 'u', ItemsSF.BLANK_UPGRADE, 'r', "dustRedstone", 'c', "blockDiamond").setRegistryName(InfoSF.MOD_ID, "capacity_upgrade"));
	}
	
	public static ShapedOreRecipe shaped(Item result, Object... recipe)
	{
		return new ShapedOreRecipe(new ResourceLocation(InfoSF.MOD_ID), result, recipe);
	}
	
	public static ShapedOreRecipe shaped(Block result, Object... recipe)
	{
		return new ShapedOreRecipe(new ResourceLocation(InfoSF.MOD_ID), result, recipe);
	}
	
	public static ShapedOreRecipe shaped(ItemStack result, Object... recipe)
	{
		return new ShapedOreRecipe(new ResourceLocation(InfoSF.MOD_ID), result, recipe);
	}
}