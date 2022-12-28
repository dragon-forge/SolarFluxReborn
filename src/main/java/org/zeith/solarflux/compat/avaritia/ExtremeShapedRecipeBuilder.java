package org.zeith.solarflux.compat.avaritia;

import morph.avaritia.recipe.ExtremeShapedRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.core.adapter.recipe.RecipeBuilder;
import org.zeith.hammerlib.core.adapter.recipe.RecipeShape;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;

import java.util.HashMap;
import java.util.Map;

public class ExtremeShapedRecipeBuilder
		extends RecipeBuilder<ExtremeShapedRecipeBuilder, Recipe<?>>
{
	private final Map<Character, Ingredient> dictionary = new HashMap<>();
	private RecipeShape shape;
	
	public ExtremeShapedRecipeBuilder(IRecipeRegistrationEvent<Recipe<?>> event)
	{
		super(event);
	}
	
	public ExtremeShapedRecipeBuilder shape(int width, int height, String... shapeKeys)
	{
		this.shape = new RecipeShape(width, height, shapeKeys);
		return this;
	}
	
	public ExtremeShapedRecipeBuilder shape(String... shapeKeys)
	{
		this.shape = new RecipeShape(shapeKeys);
		return this;
	}
	
	public ExtremeShapedRecipeBuilder map(char c, Object ingredient)
	{
		dictionary.put(c, RecipeHelper.fromComponent(ingredient));
		return this;
	}
	
	@Override
	public void register()
	{
		validate();
		if(shape == null)
			throw new IllegalStateException(getClass().getSimpleName() + " does not have a defined shape!");
		if(dictionary.isEmpty())
			throw new IllegalStateException(getClass().getSimpleName() + " does not have any defined ingredients!");
		var id = getIdentifier();
		event.register(id, new ExtremeShapedRecipe(id, group, shape.width, shape.height, shape.createIngredientMap(dictionary), result));
	}
}