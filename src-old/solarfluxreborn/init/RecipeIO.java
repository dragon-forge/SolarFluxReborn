package com.zeitheron.solarfluxreborn.init;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.zeitheron.hammercore.internal.SimpleRegistration;
import com.zeitheron.solarfluxreborn.blocks.SolarPanelBlock;
import com.zeitheron.solarfluxreborn.config.BlackHoleStorageConfigs;
import com.zeitheron.solarfluxreborn.config.DraconicEvolutionConfigs;
import com.zeitheron.solarfluxreborn.config.ModConfiguration;
import com.zeitheron.solarfluxreborn.items.CraftingItem;
import com.zeitheron.solarfluxreborn.reference.InfoSFR;
import com.zeitheron.solarfluxreborn.utility.ArrayHashSet;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RecipeIO
{
	private static final ArrayHashSet<FurnaceRecipe> fr = new ArrayHashSet<FurnaceRecipe>();
	
	private static int lastTier = 0;
	
	private static void r2()
	{
		shaped(new ItemStack(ItemsSFR.mirror, 2), "ggg", " i ", 'g', "blockGlass", 'i', "ingotIron");
		shaped(new ItemStack(ItemsSFR.solarCell1), "ggg", "lll", "mmm", 'g', "blockGlass", 'l', "gemLapis", 'm', ItemsSFR.mirror);
		shaped(new ItemStack(ItemsSFR.solarCell2), "clc", "lcl", "msm", 'c', Items.CLAY_BALL, 'l', "gemLapis", 'm', ItemsSFR.mirror, 's', ItemsSFR.solarCell1);
		shaped(new ItemStack(ItemsSFR.solarCell3), "ggg", "lll", "oco", 'g', "blockGlass", 'l', "dustGlowstone", 'o', Blocks.OBSIDIAN, 'c', ItemsSFR.solarCell2);
		shaped(new ItemStack(ItemsSFR.solarCell4), "bbb", "gdg", "qcq", 'b', Items.BLAZE_ROD, 'g', "dustGlowstone", 'd', "blockDiamond", 'q', "blockQuartz", 'c', ItemsSFR.solarCell3);
		
		int i = 0;
		while(true)
		{
			boolean sor = recipe(i);
			
			bbb: if(sor)
			{
				if(i < 3)
					break bbb;
				
				SolarPanelBlock spb = (SolarPanelBlock) BlocksSFR.getSolarPanels().get(i);
				CraftingItem unprepared = ItemsSFR.getUnpreparedForPanel(spb);
				
				if(spb != null && unprepared != null)
				{
					FurnaceRecipe fr0 = new FurnaceRecipe(new ItemStack(unprepared), new ItemStack(spb));
					
					if(BlackHoleStorageConfigs.canIntegrate && BlackHoleStorageConfigs.unpreparedSolarsNeedAT)
						fr0 = toBHSRecipe(fr0, spb.getCapacity() / (Math.abs(spb.getTierIndex()) + 1));
					
					fr.add(fr0);
				}
			} else
				break;
			++i;
		}
		
		// if(ItemsSFR.mUpgradeBlank != null) r.add(new ShapedOreRecipe(new
		// ItemStack(ItemsSFR.mUpgradeBlank), " c ", "cmc", " c ", 'c',
		// "cobblestone", 'm', ItemsSFR.mirror));
		// if(ItemsSFR.mUpgradeEfficiency != null) r.add(new ShapedOreRecipe(new
		// ItemStack(ItemsSFR.mUpgradeEfficiency), " c ", "cuc", " s ", 'c',
		// ItemsSFR.solarCell1, 'u', ItemsSFR.mUpgradeBlank, 's',
		// ItemsSFR.solarCell2));
		// if(ItemsSFR.mUpgradeLowLight != null) r.add(new ShapedOreRecipe(new
		// ItemStack(ItemsSFR.mUpgradeLowLight), "ggg", "lul", "ggg", 'g',
		// "blockGlass", 'u', ItemsSFR.mUpgradeBlank, 'l', "dustGlowstone"));
		// if(ItemsSFR.mUpgradeTraversal != null) r.add(new ShapedOreRecipe(new
		// ItemStack(ItemsSFR.mUpgradeTraversal), "i i", "rur", "i i", 'i',
		// "ingotIron", 'u', ItemsSFR.mUpgradeBlank, 'r', "dustRedstone"));
		// if(ItemsSFR.mUpgradeTransferRate != null) r.add(new
		// ShapedOreRecipe(new ItemStack(ItemsSFR.mUpgradeTransferRate), "rrr",
		// "gug", "rrr", 'u', ItemsSFR.mUpgradeBlank, 'r', "dustRedstone", 'g',
		// "ingotGold"));
		// if(ItemsSFR.mUpgradeCapacity != null) r.add(new ShapedOreRecipe(new
		// ItemStack(ItemsSFR.mUpgradeCapacity), " r ", "rur", "rcr", 'u',
		// ItemsSFR.mUpgradeBlank, 'r', "dustRedstone", 'c', "blockDiamond"));
		// if(ItemsSFR.mUpgradeFurnace != null) r.add(new ShapedOreRecipe(new
		// ItemStack(ItemsSFR.mUpgradeFurnace), "ccc", "cuc", "cfc", 'u',
		// ItemsSFR.mUpgradeBlank, 'r', "dustRedstone", 'c', Items.COAL, 'f',
		// Blocks.FURNACE));
		shaped(new ItemStack(BlocksSFR.cable1, 6), "ggg", "rrr", "ggg", 'r', "dustRedstone", 'g', "blockGlass");
		shaped(new ItemStack(BlocksSFR.cable2, 6), "ggg", "rrr", "ggg", 'r', "dustRedstone", 'g', "ingotIron");
		shaped(new ItemStack(BlocksSFR.cable3, 2), "ddd", "geg", "ddd", 'd', "blockDiamond", 'g', "dustGlowstone", 'e', Items.ENDER_EYE);
		
		if(DraconicEvolutionConfigs.canIntegrate)
		{
			if(DraconicEvolutionConfigs.draconicSolar)
				shaped(new ItemStack(BlocksSFR.draconicSolar, 2), "scs", "cec", "scs", 's', BlocksSFR.getSolarPanels().get(lastTier), 'c', Item.REGISTRY.getObject(new ResourceLocation("draconicevolution", "wyvern_core")), 'e', Item.REGISTRY.getObject(new ResourceLocation("draconicevolution", "draconic_energy_core")));
			// if(DraconicEvolutionConfigs.chaoticSolar &&
			// DraconicEvolutionConfigs.draconicSolar &&
			// !DraconicEvolutionConfigs.useFusionForChaotic)
			// shaped(new ItemStack(BlocksSFR.chaoticSolar, 2), "scs", "coc",
			// "scs", 's', BlocksSFR.draconicSolar, 'c',
			// Item.REGISTRY.getObject(new ResourceLocation("draconicevolution",
			// "awakened_core")), 'o', Item.REGISTRY.getObject(new
			// ResourceLocation("draconicevolution", "chaos_shard")));
			loadDEFRecipes();
		}
		
		if(BlackHoleStorageConfigs.canIntegrate)
		{
			boolean dmsc = BlackHoleStorageConfigs.solarcellDM;
			if(dmsc)
				shaped(ItemsSFR.solarcelldarkmatter, "ppp", "bmb", "gcg", 'p', Blocks.SEA_LANTERN, 'g', "glowstone", 'm', "matterDark", 'c', ItemsSFR.solarCell4, 'b', Items.BLAZE_ROD);
			if(BlackHoleStorageConfigs.darkMatterSolar)
			{
				ItemStack out = ItemStack.EMPTY;
				if(BlackHoleStorageConfigs.DMSolarRequiresTransformation)
					out = new ItemStack(ItemsSFR.unprepareddmsolar, 2);
				else
					out = new ItemStack(BlocksSFR.darkMatterSolar, 2);
				shaped(out, "ccc", "sds", "sds", 'c', dmsc ? ItemsSFR.solarcelldarkmatter : ItemsSFR.solarCell4, 's', BlocksSFR.getSolarPanels().get(lastTier), 'd', "matterDark");
			}
			
			loadBHSRecipes();
		}
	}
	
	private static void loadDEFRecipes()
	{
		FusionRecipes.register();
	}
	
	private static void loadBHSRecipes()
	{
		SFRAtomicTransformerRecipes.register();
	}
	
	private static FurnaceRecipe toBHSRecipe(FurnaceRecipe r, long rf)
	{
		return SFRAtomicTransformerRecipes.toAtomic(r, rf);
	}
	
	private static boolean recipe(int solar)
	{
		if(solar == 0)
		{
			lastTier = 0;
			shaped(new ItemStack(BlocksSFR.getSolarPanels().get(solar)), "mmm", "prp", "ppp", 'm', ItemsSFR.mirror, 'p', "plankWood", 'r', "dustRedstone");
		} else if(solar == 1 && BlocksSFR.getSolarPanels().size() > solar)
		{
			lastTier = 1;
			shaped(new ItemStack(BlocksSFR.getSolarPanels().get(solar)), "sss", "sps", "sss", 's', BlocksSFR.getSolarPanels().get(solar - 1), 'p', Blocks.PISTON);
		} else if(solar == 2 && BlocksSFR.getSolarPanels().size() > solar)
		{
			lastTier = 2;
			shaped(new ItemStack(BlocksSFR.getSolarPanels().get(solar), 2), "ppp", "scs", "sbs", 's', BlocksSFR.getSolarPanels().get(solar - 1), 'p', ItemsSFR.solarCell1, 'c', Items.REPEATER, 'b', "blockIron");
		} else if(solar == 3 && BlocksSFR.getSolarPanels().size() > solar)
		{
			lastTier = 3;
			SolarPanelBlock spb = (SolarPanelBlock) BlocksSFR.getSolarPanels().get(solar);
			ItemStack result = new ItemStack(ModConfiguration.addUnprepared ? ItemsSFR.getUnpreparedForPanel(spb) : Item.getItemFromBlock(spb), 2);
			shaped(result, "ppp", "scs", "sbs", 's', BlocksSFR.getSolarPanels().get(solar - 1), 'p', ItemsSFR.solarCell2, 'c', Items.CLOCK, 'b', "blockIron");
		} else if(solar == 4 && BlocksSFR.getSolarPanels().size() > solar)
		{
			lastTier = 4;
			SolarPanelBlock spb = (SolarPanelBlock) BlocksSFR.getSolarPanels().get(solar);
			ItemStack result = new ItemStack(ModConfiguration.addUnprepared ? ItemsSFR.getUnpreparedForPanel(spb) : Item.getItemFromBlock(spb), 2);
			shaped(result, "ppp", "scs", "sbs", 's', BlocksSFR.getSolarPanels().get(solar - 1), 'p', ItemsSFR.solarCell3, 'c', Blocks.GLOWSTONE, 'b', "blockGold");
		} else if(solar == 5 && BlocksSFR.getSolarPanels().size() > solar)
		{
			lastTier = 5;
			SolarPanelBlock spb = (SolarPanelBlock) BlocksSFR.getSolarPanels().get(solar);
			ItemStack result = new ItemStack(ModConfiguration.addUnprepared ? ItemsSFR.getUnpreparedForPanel(spb) : Item.getItemFromBlock(spb), 2);
			shaped(result, "ppp", "scs", "sbs", 's', BlocksSFR.getSolarPanels().get(solar - 1), 'p', ItemsSFR.solarCell4, 'c', Blocks.REDSTONE_LAMP, 'b', "blockDiamond");
		} else if(solar == 6 && BlocksSFR.getSolarPanels().size() > solar)
		{
			lastTier = 6;
			SolarPanelBlock spb = (SolarPanelBlock) BlocksSFR.getSolarPanels().get(solar);
			ItemStack result = new ItemStack(ModConfiguration.addUnprepared ? ItemsSFR.getUnpreparedForPanel(spb) : Item.getItemFromBlock(spb), 2);
			shaped(result, "ppp", "scs", "scs", 's', BlocksSFR.getSolarPanels().get(solar - 1), 'p', ItemsSFR.solarCell4, 'c', Items.DRAGON_BREATH);
		} else if(solar == 7 && BlocksSFR.getSolarPanels().size() > solar)
		{
			lastTier = 7;
			SolarPanelBlock spb = (SolarPanelBlock) BlocksSFR.getSolarPanels().get(solar);
			ItemStack result = new ItemStack(ModConfiguration.addUnprepared ? ItemsSFR.getUnpreparedForPanel(spb) : Item.getItemFromBlock(spb), 2);
			Item egg = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation("minecraft", "dragon_egg"));
			shaped(result, "ppp", "scs", "scs", 's', BlocksSFR.getSolarPanels().get(solar - 1), 'p', ItemsSFR.solarCell4, 'c', egg);
		}
		
		return solar == lastTier;
	}
	
	public static ItemStack enchantedBook(Enchantment ench, int lvl)
	{
		ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
		Map<Enchantment, Integer> enchs = new HashMap<>();
		enchs.put(ench, lvl);
		EnchantmentHelper.setEnchantments(enchs, book);
		return book;
	}
	
	private static final List<IRecipe> recipes = new ArrayList<>();
	
	public static Collection<IRecipe> collect()
	{
		for(FurnaceRecipe r : fr)
			r.removeRecipe();
		r2();
		for(FurnaceRecipe r : fr)
			r.addRecipe();
		HashSet<IRecipe> rs = new HashSet<>(recipes);
		recipes.clear();
		return rs;
	}
	
	private static void smelting(ItemStack in, ItemStack out)
	{
		smelting(in, out, 0);
	}
	
	private static void smelting(Item in, ItemStack out, float xp)
	{
		FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(in), out, xp);
	}
	
	private static void smelting(ItemStack in, ItemStack out, float xp)
	{
		FurnaceRecipes.instance().addSmeltingRecipe(in, out, xp);
	}
	
	private static void shaped(ItemStack out, Object... recipeComponents)
	{
		recipes.add(SimpleRegistration.parseShapedRecipe(out, recipeComponents).setRegistryName(InfoSFR.MOD_ID, "recipes." + recipes.size()));
	}
	
	private static void shaped(Item out, Object... recipeComponents)
	{
		recipes.add(SimpleRegistration.parseShapedRecipe(new ItemStack(out), recipeComponents).setRegistryName(InfoSFR.MOD_ID, "recipes." + recipes.size()));
	}
	
	private static void shaped(Block out, Object... recipeComponents)
	{
		recipes.add(SimpleRegistration.parseShapedRecipe(new ItemStack(out), recipeComponents).setRegistryName(InfoSFR.MOD_ID, "recipes." + recipes.size()));
	}
	
	private static void shapeless(ItemStack out, Object... recipeComponents)
	{
		recipes.add(SimpleRegistration.parseShapelessRecipe(out, recipeComponents).setRegistryName(InfoSFR.MOD_ID, "recipes." + recipes.size()));
	}
	
	private static void shapeless(Item out, Object... recipeComponents)
	{
		recipes.add(SimpleRegistration.parseShapelessRecipe(new ItemStack(out), recipeComponents).setRegistryName(InfoSFR.MOD_ID, "recipes." + recipes.size()));
	}
	
	private static void recipe(IRecipe recipe)
	{
		recipes.add(recipe);
	}
	
	private static void shapeless(Block out, Object... recipeComponents)
	{
		recipes.add(SimpleRegistration.parseShapelessRecipe(new ItemStack(out), recipeComponents).setRegistryName(InfoSFR.MOD_ID, "recipes." + recipes.size()));
	}
	
	public static class FurnaceRecipe
	{
		private final ItemStack in, out;
		private final float xp;
		
		public FurnaceRecipe(ItemStack in, ItemStack out)
		{
			this(in, out, 0);
		}
		
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
			
			try
			{
				smeltingList = (Map<ItemStack, ItemStack>) f_smeltingList.get(FurnaceRecipes.instance());
			} catch(Throwable err)
			{
				smeltingList = FurnaceRecipes.instance().getSmeltingList();
			}
			try
			{
				experienceList = (Map<ItemStack, Float>) f_experienceList.get(FurnaceRecipes.instance());
			} catch(Throwable err)
			{
			}
			
			smeltingList.remove(in);
			if(experienceList != null)
				experienceList.remove(out);
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