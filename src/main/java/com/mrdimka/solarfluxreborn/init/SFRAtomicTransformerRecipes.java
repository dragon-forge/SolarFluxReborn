package com.mrdimka.solarfluxreborn.init;

import net.minecraft.item.ItemStack;

import com.mrdimka.holestorage.api.atomictransformer.AtomicTransformerRecipes;
import com.mrdimka.holestorage.api.atomictransformer.SimpleTransformerRecipe;
import com.mrdimka.solarfluxreborn.config.BlackHoleStorageConfigs;
import com.mrdimka.solarfluxreborn.init.RecipeIO.FurnaceRecipe;

public class SFRAtomicTransformerRecipes
{
	public static void register()
	{
		if(BlackHoleStorageConfigs.DMSolarRequiresTransformation && BlackHoleStorageConfigs.darkMatterSolar)
			AtomicTransformerRecipes.register(new ItemStack(ModItems.unprepareddmsolar), new ItemStack(ModBlocks.darkMatterSolar), 4000000000L);
	}
	
	public static void register(FurnaceRecipe recipe, long rf)
	{
		AtomicTransformerRecipes.register(recipe.getIn(), recipe.getOut(), rf);
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
			AtomicTransformerRecipes.register(recipe);
		}
		
		@Override
		public void removeRecipe()
		{
			AtomicTransformerRecipes.getRecipes().remove(recipe);
		}
	}
}