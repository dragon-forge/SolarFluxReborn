package com.mrdimka.solarfluxreborn.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.mrdimka.solarfluxreborn.config.BlackHoleStorageConfigs;
import com.mrdimka.solarfluxreborn.config.DraconicEvolutionConfigs;
import com.mrdimka.solarfluxreborn.utility.ArrayHashSet;

public class RecipeIO
{
	private static final ArrayHashSet<IRecipe> r = new ArrayHashSet<IRecipe>();
	public static final ArrayHashSet<IRecipe> recipes = new ArrayHashSet<IRecipe>();
	private static final ArrayHashSet<IRecipe> recipesReg = new ArrayHashSet<IRecipe>();
	
	private static int lastTier = 0;
	
	private static void r2()
	{
		r.clear();
		r.add(new ShapedOreRecipe(new ItemStack(ModItems.mirror, 2), "ggg", " i ", 'g', "blockGlass", 'i', "ingotIron"));
		r.add(new ShapedOreRecipe(new ItemStack(ModItems.solarCell1), "ggg", "lll", "mmm", 'g', "blockGlass", 'l', "gemLapis", 'm', ModItems.mirror));
		r.add(new ShapedOreRecipe(new ItemStack(ModItems.solarCell2), "clc", "lcl", "msm", 'c', Items.CLAY_BALL, 'l', "gemLapis", 'm', ModItems.mirror, 's', ModItems.solarCell1));
		r.add(new ShapedOreRecipe(new ItemStack(ModItems.solarCell3), "ggg", "lll", "oco", 'g', "blockGlass", 'l', "dustGlowstone", 'o', Blocks.OBSIDIAN, 'c', ModItems.solarCell2));
		r.add(new ShapedOreRecipe(new ItemStack(ModItems.solarCell4), "bbb", "gdg", "qcq", 'b', Items.BLAZE_ROD, 'g', "dustGlowstone", 'd', "blockDiamond", 'q', "blockQuartz", 'c', ModItems.solarCell3));
		
		int i = 0;
		while(true)
		{
			ShapedOreRecipe sor = recipe(i);
			if(sor != null) r.add(sor);
			else break;
			++i;
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
		r.add(new ShapedOreRecipe(new ItemStack(ModBlocks.cable3, 2), "ddd", "geg", "ddd", 'd', "blockDiamond", 'g', "dustGlowstone", 'e', Items.ENDER_EYE));
		
		if(DraconicEvolutionConfigs.canIntegrate)
		{
			if(DraconicEvolutionConfigs.draconicSolar)
				r.add(new ShapedOreRecipe(new ItemStack(ModBlocks.draconicSolar, 2), "scs", "cec", "scs", 's', ModBlocks.getSolarPanels().get(lastTier), 'c', Item.REGISTRY.getObject(new ResourceLocation("draconicevolution", "wyvern_core")), 'e', Item.REGISTRY.getObject(new ResourceLocation("draconicevolution", "draconic_energy_core"))));
			if(DraconicEvolutionConfigs.chaoticSolar && DraconicEvolutionConfigs.draconicSolar && !DraconicEvolutionConfigs.useFusionForChaotic)
				r.add(new ShapedOreRecipe(new ItemStack(ModBlocks.chaoticSolar, 2), "scs", "coc", "scs", 's', ModBlocks.draconicSolar, 'c', Item.REGISTRY.getObject(new ResourceLocation("draconicevolution", "awakened_core")), 'o', Item.REGISTRY.getObject(new ResourceLocation("draconicevolution", "chaos_shard"))));
			loadDEFRecipes();
		}
		
		if(BlackHoleStorageConfigs.canIntegrate)
		{
			boolean dmsc = BlackHoleStorageConfigs.solarcellDM;
			if(dmsc) r.add(new ShapedOreRecipe(ModItems.solarcelldarkmatter, "ppp", "bmb", "gcg", 'p', Blocks.SEA_LANTERN, 'g', "glowstone", 'm', "matterDark", 'c', ModItems.solarCell4, 'b', Items.BLAZE_ROD));
			if(BlackHoleStorageConfigs.darkMatterSolar)
			{
				ItemStack out = ItemStack.EMPTY;
				if(BlackHoleStorageConfigs.DMSolarRequiresTransformation) out = new ItemStack(ModItems.unprepareddmsolar, 2);
				else out = new ItemStack(ModBlocks.darkMatterSolar, 2);
				r.add(new ShapedOreRecipe(out, "ccc", "sds", "sds", 'c', dmsc ? ModItems.solarcelldarkmatter : ModItems.solarCell4, 's', ModBlocks.getSolarPanels().get(lastTier), 'd', "matterDark"));
			}
			
			loadBHSRecipes();
		}
	}
	
	private static void loadDEFRecipes() { FusionRecipes.register(); }
	private static void loadBHSRecipes() { SFRAtomicTransformerRecipes.register(); }
	
	private static ShapedOreRecipe recipe(int solar)
	{
		if(solar == 0)
		{
			lastTier = 0;
			return new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(solar)), "mmm", "prp", "ppp", 'm', ModItems.mirror, 'p', "plankWood", 'r', "dustRedstone");
		}
		else if(solar == 1 && ModBlocks.getSolarPanels().size() >= 2)
		{
			lastTier = 1;
			return new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(solar)), "sss", "sps", "sss", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', Blocks.PISTON);
		}
		else if(solar == 2 && ModBlocks.getSolarPanels().size() >= 3)
		{
			lastTier = 2;
			return new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(solar), 2), "ppp", "scs", "sbs", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', ModItems.solarCell1, 'c', Items.REPEATER, 'b', "blockIron");
		}
		else if(solar == 3 && ModBlocks.getSolarPanels().size() >= 4)
		{
			lastTier = 3;
			return new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(solar), 2), "ppp", "scs", "sbs", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', ModItems.solarCell2, 'c', Items.CLOCK, 'b', "blockIron");
		}
		else if(solar == 4 && ModBlocks.getSolarPanels().size() >= 5)
		{
			lastTier = 4;
			return new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(solar), 2), "ppp", "scs", "sbs", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', ModItems.solarCell3, 'c', Blocks.GLOWSTONE, 'b', "blockGold");
		}
		else if(solar == 5 && ModBlocks.getSolarPanels().size() >= 6)
		{
			lastTier = 5;
			return new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(solar), 2), "ppp", "scs", "sbs", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', ModItems.solarCell4, 'c', Blocks.REDSTONE_LAMP, 'b', "blockDiamond");
		}
		else if(solar == 6 && ModBlocks.getSolarPanels().size() >= 7)
		{
			lastTier = 6;
			return new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(solar), 2), "ppp", "scs", "scs", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', ModItems.solarCell4, 'c', Items.DRAGON_BREATH);
		}
		else if(solar == 7 && ModBlocks.getSolarPanels().size() >= 8)
		{
			lastTier = 7;
			return new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(solar), 2), "ppp", "scs", "scs", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', ModItems.solarCell4, 'c', Blocks.DRAGON_EGG);
		}
		
		return null;
	}
	
	public static void reload()
	{
		r2();
		
		CraftingManager.getInstance().getRecipeList().removeAll(recipesReg);
		CraftingManager.getInstance().getRecipeList().addAll(recipes);
		recipesReg.addAll(recipes);
		CraftingManager.getInstance().getRecipeList().addAll(r);
		recipesReg.addAll(r);
	}
}