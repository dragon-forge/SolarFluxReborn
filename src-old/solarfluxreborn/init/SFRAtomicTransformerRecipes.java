package com.zeitheron.solarfluxreborn.init;

import com.zeitheron.holestorage.api.atomictransformer.RecipesAtomicTransformer;
import com.zeitheron.holestorage.api.atomictransformer.SimpleTransformerRecipe;
import com.zeitheron.solarfluxreborn.config.BlackHoleStorageConfigs;
import com.zeitheron.solarfluxreborn.init.RecipeIO.FurnaceRecipe;

import net.minecraft.item.ItemStack;

public class SFRAtomicTransformerRecipes
{
	public static void register()
	{
		if(BlackHoleStorageConfigs.DMSolarRequiresTransformation && BlackHoleStorageConfigs.darkMatterSolar)
			RecipesAtomicTransformer.register(new ItemStack(ItemsSFR.unprepareddmsolar), new ItemStack(BlocksSFR.darkMatterSolar), 4000000000L);
	}
	
	public static void register(FurnaceRecipe recipe, long rf)
	{
		RecipesAtomicTransformer.register(recipe.getIn(), recipe.getOut(), rf);
	}
	
	public static FurnaceRecipe toAtomic(FurnaceRecipe r, long rf)
	{
		return new AtomicFurnaceRecipe(r.getIn(), r.getOut(), rf);
	}
	
	public static class AtomicFurnaceRecipe extends FurnaceRecipe
	{
		private final SimpleTransformerRecipe recipe;
		
		public AtomicFurnaceRecipe(ItemStack in, ItemStack out, long rf)
		{
			super(in, out);
			recipe = new SimpleTransformerRecipe(in, out, rf);
		}
		
		@Override
		public void addRecipe()
		{
			RecipesAtomicTransformer.register(recipe);
		}
		
		@Override
		public void removeRecipe()
		{
			RecipesAtomicTransformer.getRecipes().remove(recipe);
		}
	}
}