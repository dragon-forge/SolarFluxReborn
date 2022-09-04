package org.zeith.solarflux.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.zeith.hammerlib.core.adapter.recipe.RecipeBuilder;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.solarflux.block.SolarPanelBlock;

public class RecipeClearSolarPanel
		extends ShapelessRecipe
{
	public RecipeClearSolarPanel(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> input)
	{
		super(id, group, result, input);
	}
	
	@Override
	public boolean isSpecial()
	{
		return true;
	}
	
	public static RecipeClearSolarPanelBuilder builder(RegisterRecipesEvent $)
	{
		return new RecipeClearSolarPanelBuilder($);
	}
	
	public static class RecipeClearSolarPanelBuilder
			extends RecipeBuilder<RecipeClearSolarPanelBuilder, Recipe<?>>
	{
		private final NonNullList<Ingredient> ingredients = NonNullList.create();
		
		public RecipeClearSolarPanelBuilder(RegisterRecipesEvent event)
		{
			super(event);
		}
		
		public RecipeClearSolarPanelBuilder set(SolarPanelBlock ingredient)
		{
			result(ingredient).ingredients.add(Ingredient.of(ingredient));
			return this;
		}
		
		@Override
		public void register()
		{
			validate();
			if(ingredients.isEmpty())
				throw new IllegalStateException(getClass().getSimpleName() + " does not have any defined ingredients!");
			event.register(getIdentifier(), new RecipeClearSolarPanel(getIdentifier(), group, result, ingredients));
		}
	}
}