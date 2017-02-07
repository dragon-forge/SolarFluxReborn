package com.mrdimka.solarfluxreborn.init;

import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRecipeAPI;
import com.brandon3055.draconicevolution.api.fusioncrafting.SimpleFusionRecipe;
import com.mrdimka.solarfluxreborn.config.DraconicEvolutionConfigs;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FusionRecipes
{
	public static void register()
	{
		if(DraconicEvolutionConfigs.chaoticSolar && DraconicEvolutionConfigs.draconicSolar && DraconicEvolutionConfigs.useFusionForChaotic)
		FusionRecipeAPI.addRecipe(new SimpleFusionRecipe(new ItemStack(ModBlocks.chaoticSolar, 3), new ItemStack(Item.REGISTRY.getObject(new ResourceLocation("draconicevolution", "chaos_shard"))), 128000000, 3, ModBlocks.draconicSolar, Item.REGISTRY.getObject(new ResourceLocation("draconicevolution", "awakened_core")), ModBlocks.draconicSolar, Item.REGISTRY.getObject(new ResourceLocation("draconicevolution", "awakened_core")), ModBlocks.draconicSolar, Item.REGISTRY.getObject(new ResourceLocation("draconicevolution", "awakened_core")), ModBlocks.draconicSolar, Item.REGISTRY.getObject(new ResourceLocation("draconicevolution", "awakened_core"))));
	}
}
