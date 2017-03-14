package com.mrdimka.solarfluxreborn.init;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.mrdimka.solarfluxreborn.blocks.SolarPanelBlock;
import com.mrdimka.solarfluxreborn.config.BlackHoleStorageConfigs;
import com.mrdimka.solarfluxreborn.config.DraconicEvolutionConfigs;
import com.mrdimka.solarfluxreborn.config.ModConfiguration;
import com.mrdimka.solarfluxreborn.items.CraftingItem;
import com.mrdimka.solarfluxreborn.utility.ArrayHashSet;

public class RecipeIO
{
	private static final ArrayHashSet<FurnaceRecipe> fr = new ArrayHashSet<FurnaceRecipe>();
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
			
			bbb: if(sor != null)
			{
				r.add(sor);
				
				if(i < 3) break bbb;
				
				SolarPanelBlock spb = (SolarPanelBlock) ModBlocks.getSolarPanels().get(i);
				CraftingItem unprepared = ModItems.getUnpreparedForPanel(spb);
				
				if(spb != null && unprepared != null)
				{
					FurnaceRecipe fr0 = new FurnaceRecipe(new ItemStack(unprepared), new ItemStack(spb));
					
					if(BlackHoleStorageConfigs.canIntegrate && BlackHoleStorageConfigs.unpreparedSolarsNeedAT)
						fr0 = toBHSRecipe(fr0, spb.getCapacity() / (Math.abs(spb.getTierIndex()) + 1));
					
					fr.add(fr0);
				}
			}
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
	private static FurnaceRecipe toBHSRecipe(FurnaceRecipe r, long rf) { return SFRAtomicTransformerRecipes.toAtomic(r, rf); }
	
	private static ShapedOreRecipe recipe(int solar)
	{
		if(solar == 0)
		{
			lastTier = 0;
			return new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(solar)), "mmm", "prp", "ppp", 'm', ModItems.mirror, 'p', "plankWood", 'r', "dustRedstone");
		}
		else if(solar == 1 && ModBlocks.getSolarPanels().size() > solar)
		{
			lastTier = 1;
			return new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(solar)), "sss", "sps", "sss", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', Blocks.PISTON);
		}
		else if(solar == 2 && ModBlocks.getSolarPanels().size() > solar)
		{
			lastTier = 2;
			return new ShapedOreRecipe(new ItemStack(ModBlocks.getSolarPanels().get(solar), 2), "ppp", "scs", "sbs", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', ModItems.solarCell1, 'c', Items.REPEATER, 'b', "blockIron");
		}
		else if(solar == 3 && ModBlocks.getSolarPanels().size() > solar)
		{
			lastTier = 3;
			SolarPanelBlock spb = (SolarPanelBlock) ModBlocks.getSolarPanels().get(solar);
			ItemStack result = new ItemStack(ModConfiguration.addUnprepared ? ModItems.getUnpreparedForPanel(spb) : Item.getItemFromBlock(spb), 2);
			return new ShapedOreRecipe(result, "ppp", "scs", "sbs", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', ModItems.solarCell2, 'c', Items.CLOCK, 'b', "blockIron");
		}
		else if(solar == 4 && ModBlocks.getSolarPanels().size() > solar)
		{
			lastTier = 4;
			SolarPanelBlock spb = (SolarPanelBlock) ModBlocks.getSolarPanels().get(solar);
			ItemStack result = new ItemStack(ModConfiguration.addUnprepared ? ModItems.getUnpreparedForPanel(spb) : Item.getItemFromBlock(spb), 2);
			return new ShapedOreRecipe(result, "ppp", "scs", "sbs", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', ModItems.solarCell3, 'c', Blocks.GLOWSTONE, 'b', "blockGold");
		}
		else if(solar == 5 && ModBlocks.getSolarPanels().size() > solar)
		{
			lastTier = 5;
			SolarPanelBlock spb = (SolarPanelBlock) ModBlocks.getSolarPanels().get(solar);
			ItemStack result = new ItemStack(ModConfiguration.addUnprepared ? ModItems.getUnpreparedForPanel(spb) : Item.getItemFromBlock(spb), 2);
			return new ShapedOreRecipe(result, "ppp", "scs", "sbs", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', ModItems.solarCell4, 'c', Blocks.REDSTONE_LAMP, 'b', "blockDiamond");
		}
		else if(solar == 6 && ModBlocks.getSolarPanels().size() > solar)
		{
			lastTier = 6;
			SolarPanelBlock spb = (SolarPanelBlock) ModBlocks.getSolarPanels().get(solar);
			ItemStack result = new ItemStack(ModConfiguration.addUnprepared ? ModItems.getUnpreparedForPanel(spb) : Item.getItemFromBlock(spb), 2);
			return new ShapedOreRecipe(result, "ppp", "scs", "scs", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', ModItems.solarCell4, 'c', Items.DRAGON_BREATH);
		}
		else if(solar == 7 && ModBlocks.getSolarPanels().size() > solar)
		{
			lastTier = 7;
			SolarPanelBlock spb = (SolarPanelBlock) ModBlocks.getSolarPanels().get(solar);
			ItemStack result = new ItemStack(ModConfiguration.addUnprepared ? ModItems.getUnpreparedForPanel(spb) : Item.getItemFromBlock(spb), 2);
			return new ShapedOreRecipe(result, "ppp", "scs", "scs", 's', ModBlocks.getSolarPanels().get(solar - 1), 'p', ModItems.solarCell4, 'c', Blocks.DRAGON_EGG);
		}
		
		return null;
	}
	
	public static void reload()
	{
		for(FurnaceRecipe r : fr) r.removeRecipe();
		fr.clear();
		
		r2();
		
		for(FurnaceRecipe r : fr) r.addRecipe();
		
		CraftingManager.getInstance().getRecipeList().removeAll(recipesReg);
		recipesReg.clear();
		
		CraftingManager.getInstance().getRecipeList().addAll(recipes);
		CraftingManager.getInstance().getRecipeList().addAll(r);
		recipesReg.addAll(recipes);
		recipesReg.addAll(r);
	}
	
	public static class FurnaceRecipe
	{
		private final ItemStack in, out;
		private final float xp;
		
		public FurnaceRecipe(ItemStack in, ItemStack out) { this(in, out, 0); } // no xp
		public FurnaceRecipe(ItemStack in, ItemStack out, float xp)
		{
			this.in = in;
			this.out = out;
			this.xp = xp;
		}
		
		public void addRecipe()
		{
			FurnaceRecipes.instance().addSmeltingRecipe(in, out, xp);
		}
		
		public void removeRecipe()
		{
			Map<ItemStack, ItemStack> smeltingList = null;
		    Map<ItemStack, Float> experienceList = null;
		    
		    Field[] fs = FurnaceRecipes.class.getDeclaredFields();
		    
		    Field f_smeltingList = fs[1];
		    f_smeltingList.setAccessible(true);
		    
		    Field f_experienceList = fs[2];
		    f_experienceList.setAccessible(true);
		    
		    try { smeltingList = (Map<ItemStack, ItemStack>) f_smeltingList.get(FurnaceRecipes.instance()); } catch(Throwable err) { smeltingList = FurnaceRecipes.instance().getSmeltingList(); }
		    try { experienceList = (Map<ItemStack, Float>) f_experienceList.get(FurnaceRecipes.instance()); } catch(Throwable err) {}
		    
		    smeltingList.remove(in);
		    if(experienceList != null) experienceList.remove(out);
		}
		
		public ItemStack getIn()
		{
			return in;
		}
		
		public ItemStack getOut()
		{
			return out;
		}
		
		public float getXp()
		{
			return xp;
		}
	}
}