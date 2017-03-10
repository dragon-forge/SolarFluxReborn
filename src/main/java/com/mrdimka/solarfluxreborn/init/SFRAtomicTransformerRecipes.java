package com.mrdimka.solarfluxreborn.init;

import net.minecraft.item.ItemStack;

import com.mrdimka.holestorage.api.atomictransformer.AtomicTransformerRecipes;
import com.mrdimka.holestorage.api.atomictransformer.SimpleTransformerRecipe;
import com.mrdimka.solarfluxreborn.config.BlackHoleStorageConfigs;

public class SFRAtomicTransformerRecipes
{
	public static void register()
	{
		if(BlackHoleStorageConfigs.DMSolarRequiresTransformation && BlackHoleStorageConfigs.darkMatterSolar)
		{
			AtomicTransformerRecipes.register(new ItemStack(ModItems.unprepareddmsolar), new ItemStack(ModBlocks.darkMatterSolar), 4000000000L);
		}
	}
}