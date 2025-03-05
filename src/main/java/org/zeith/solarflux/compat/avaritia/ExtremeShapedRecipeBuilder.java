package org.zeith.solarflux.compat.avaritia;

import committee.nova.mods.avaritia.common.crafting.recipe.*;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import org.zeith.hammerlib.core.RecipeHelper;
import org.zeith.hammerlib.core.adapter.recipe.*;
import org.zeith.hammerlib.util.mcf.itf.IRecipeRegistrationEvent;

import java.util.*;

public class ExtremeShapedRecipeBuilder
		extends RecipeBuilder<ExtremeShapedRecipeBuilder>
{
	private final Map<Character, Ingredient> dictionary = new HashMap<>();
	private RecipeShape shape;
	
	public ExtremeShapedRecipeBuilder(IRecipeRegistrationEvent<Recipe<?>> event)
	{
		super(event);
	}
	
	public ExtremeShapedRecipeBuilder shape(int width, int height, String... shapeKeys)
	{
		this.shape = new RecipeShape(event.getItemLookup(), width, height, shapeKeys);
		return this;
	}
	
	public ExtremeShapedRecipeBuilder shape(String... shapeKeys)
	{
		this.shape = new RecipeShape(event.getItemLookup(), shapeKeys);
		return this;
	}
	
	public ExtremeShapedRecipeBuilder map(char c, Object ingredient)
	{
		dictionary.put(c, RecipeHelper.fromComponent(event.getItemLookup(), ingredient));
		return this;
	}
	
	@Override
	protected void validate()
	{
		super.validate();
		if(shape == null)
			throw new IllegalStateException(getClass().getSimpleName() + " does not have a defined shape!");
		if(dictionary.isEmpty())
			throw new IllegalStateException(getClass().getSimpleName() + " does not have any defined ingredients!");
	}
	
	@Override
	protected Recipe<?> createRecipe()
	{
		return new ShapedExtremeCraftingRecipe(group,
				new ShapedExtremePattern(shape.width, shape.height,
						NonNullList.copyOf(shape
								.createIngredientMap(dictionary)
								.stream()
								.map(o -> o.orElse(Ingredient.of(Items.AIR)))
								.toList()),
						Optional.empty()
				),
				result
		);
	}
}