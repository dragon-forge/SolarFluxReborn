package com.mrdimka.solarfluxreborn.init;

import com.mrdimka.solarfluxreborn.utility.ArrayHashSet;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class RecipeIO
{
	private static final ArrayHashSet<IRecipe> r = new ArrayHashSet<IRecipe>();
	public static final ArrayHashSet<IRecipe> recipes = new ArrayHashSet<IRecipe>();
	private static final ArrayHashSet<IRecipe> recipesReg = new ArrayHashSet<IRecipe>();
	
	private static void r2()
	{
		r.clear();
		r.add(new ShapedOreRecipe(new ItemStack(ModItems.mirror, 2), "ggg", " i ", 'g', "blockGlass", 'i', "ingotIron"));
		r.add(new ShapedOreRecipe(new ItemStack(ModItems.solarCell1), "ggg", "lll", "mmm", 'g', "blockGlass", 'l', "gemLapis", 'm', ModItems.mirror));
		r.add(new ShapedOreRecipe(new ItemStack(ModItems.solarCell2), "clc", "lcl", "msm", 'c', Items.CLAY_BALL, 'l', "gemLapis", 'm', ModItems.mirror, 's', ModItems.solarCell1));
		r.add(new ShapedOreRecipe(new ItemStack(ModItems.solarCell3), "ggg", "lll", "oco", 'g', "blockGlass", 'l', "dustGlowstone", 'o', Blocks.OBSIDIAN, 'c', ModItems.solarCell2));
		r.add(new ShapedOreRecipe(new ItemStack(ModItems.solarCell4), "bbb", "gdg", "qcq", 'b', Items.BLAZE_ROD, 'g', "dustGlowstone", 'd', "blockDiamond", 'q', "blockQuartz", 'c', ModItems.solarCell3));
		
		for(int i = 0; i < ModBlocks.getSolarPanels().size(); ++i)
		{
			r.add(recipe(i));
			if(i >= 5) break;
		}
		
		if(ModItems.mUpgradeBlank != null) r.add(new ShapedOreRecipe(new ItemStack(ModItems.mUpgradeBlank), " c ", "cmc", " c ", 'c', "cobblestone", 'm', ModItems.mirror));
		if(ModItems.mUpgradeEfficiency != null) r.add(new ShapedOreRecipe(new ItemStack(ModItems.mUpgradeEfficiency), " c ", "cuc", " s ", 'c', ModItems.solarCell1, 'u', ModItems.mUpgradeBlank, 's', ModItems.solarCell2));
		if(ModItems.mUpgradeLowLight != null) r.add(new ShapedOreRecipe(new ItemStack(ModItems.mUpgradeLowLight), "ggg", "lul", "ggg", 'g', "blockGlass", 'u', ModItems.mUpgradeBlank, 'l', "dustGlowstone"));
		if(ModItems.mUpgradeTraversal != null) r.add(new ShapedOreRecipe(new ItemStack(ModItems.mUpgradeTraversal), "i i", "rur", "i i", 'i', "ingotIron", 'u', ModItems.mUpgradeBlank, 'r', "dustRedstone"));
		if(ModItems.mUpgradeTransferRate != null) r.add(new ShapedOreRecipe(new ItemStack(ModItems.mUpgradeTransferRate), "rrr", "gug", "rrr", 'u', ModItems.mUpgradeBlank, 'r', "dustRedstone", 'g', "ingotGold"));
		if(ModItems.mUpgradeCapacity != null) r.add(new ShapedOreRecipe(new ItemStack(ModItems.mUpgradeCapacity), " r ", "rur", "rcr", 'u', ModItems.mUpgradeBlank, 'r', "dustRedstone", 'c', "blockDiamond"));
		if(ModItems.mUpgradeFurnace != null) r.add(new ShapedOreRecipe(new ItemStack(ModItems.mUpgradeFurnace), "ccc", "cuc", "cfc", 'u', ModItems.mUpgradeBlank, 'r', "dustRedstone", 'c', Items.COAL, 'f', Blocks.FURNACE));
		r.add(new ShapedOreRecipe(new ItemStack(ModBlocks.cable1, 6), "ggg", "rrr", "ggg", 'r', "dustRedstone", 'g', "blockGlass"));
		r.add(new ShapedOreRecipe(new ItemStack(ModBlocks.cable2, 6), "ggg", "rrr", "ggg", 'r', "dustRedstone", 'g', "ingotIron"));
	}
	
	private static ShapedOreRecipe recipe(int solar)
	{
		if(solar == 0) return (new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(0)), "mmm", "prp", "ppp", 'm', ModItems.mirror, 'p', "plankWood", 'r', "dustRedstone"));
		else if(solar == 1) return (new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(1)), "sss", "sps", "sss", 's', ModBlocks.getSolarPanels().get(0), 'p', Blocks.PISTON));
		else if(solar == 2) return (new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(2), 2), "ppp", "scs", "sbs", 's', ModBlocks.getSolarPanels().get(1), 'p', ModItems.solarCell1, 'c', Items.REPEATER, 'b', "blockIron"));
		else if(solar == 3) return (new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(3), 2), "ppp", "scs", "sbs", 's', ModBlocks.getSolarPanels().get(2), 'p', ModItems.solarCell2, 'c', Items.CLOCK, 'b', "blockIron"));
		else if(solar == 4) return (new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(4), 2), "ppp", "scs", "sbs", 's', ModBlocks.getSolarPanels().get(3), 'p', ModItems.solarCell3, 'c', Blocks.GLOWSTONE, 'b', "blockGold"));
		else if(solar == 5) return (new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(5), 2), "ppp", "scs", "sbs", 's', ModBlocks.getSolarPanels().get(4), 'p', ModItems.solarCell4, 'c', Blocks.REDSTONE_LAMP, 'b', "blockDiamond"));
		return null;
	}
	
	public static void reload()
	{
		r2();
		
		for(Object o : recipesReg.toArray()) CraftingManager.getInstance().getRecipeList().remove(o);
		
		CraftingManager.getInstance().getRecipeList().addAll(recipes);
		recipesReg.addAll(recipes);
		
		CraftingManager.getInstance().getRecipeList().addAll(r);
		recipesReg.addAll(r);
	}
}