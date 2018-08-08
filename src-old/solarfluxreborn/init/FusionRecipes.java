package com.zeitheron.solarfluxreborn.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRecipeAPI;
import com.brandon3055.draconicevolution.api.fusioncrafting.SimpleFusionRecipe;
import com.zeitheron.solarfluxreborn.config.DraconicEvolutionConfigs;

public class FusionRecipes
{
	public static void register()
	{
		IForgeRegistry<Item> items = GameRegistry.findRegistry(Item.class);
		
		Item awakened_core = items.getValue(new ResourceLocation("draconicevolution", "awakened_core"));
		Item chaos_shard = items.getValue(new ResourceLocation("draconicevolution", "chaos_shard"));
		
		if(DraconicEvolutionConfigs.chaoticSolar && DraconicEvolutionConfigs.draconicSolar)
			FusionRecipeAPI.addRecipe(new SimpleFusionRecipe(new ItemStack(BlocksSFR.chaoticSolar, 3), new ItemStack(chaos_shard), 256000000, 3, BlocksSFR.draconicSolar, awakened_core, BlocksSFR.draconicSolar, awakened_core, BlocksSFR.draconicSolar, awakened_core, BlocksSFR.draconicSolar, awakened_core));
	}
}