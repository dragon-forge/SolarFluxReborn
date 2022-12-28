package org.zeith.solarflux.compat.draconicevolution;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.util.java.Cast;

import java.util.Collection;

/**
 * Provides utility methods for interacting with Draconic Evolution's Fusion Recipe API.
 *
 * @author Zeith
 */
public class FusionRecipeAPI
{
	/**
	 * Registers a Fusion Recipe.
	 *
	 * @param id
	 * 		the recipe identifier
	 * @param recipe
	 * 		the Fusion Recipe to register
	 * @param e
	 * 		the event object
	 */
	public static void register(ResourceLocation id, FusionRecipe recipe, RegisterRecipesEvent e)
	{
		e.register(id, Cast.cast(recipe));
	}
	
	/**
	 * Creates a new Fusion Recipe.
	 *
	 * @param id
	 * 		the recipe identifier
	 * @param result
	 * 		the resulting ItemStack
	 * @param catalyst
	 * 		the catalyst Ingredient
	 * @param totalEnergy
	 * 		the required energy
	 * @param techLevel
	 * 		the required Tech Level
	 * @param ingredients
	 * 		the Fusion Ingredient list
	 *
	 * @return the new Fusion Recipe
	 *
	 * @throws ReflectiveOperationException
	 * 		if there is an error creating the recipe
	 */
	public static FusionRecipe create(ResourceLocation id, ItemStack result, Ingredient catalyst, long totalEnergy, TechLevel techLevel, Collection<FusionRecipe.FusionIngredient> ingredients) throws ReflectiveOperationException
	{
		@SuppressWarnings("JavaReflectionMemberAccess") // We compiled with DE 1.16.5, thus ItemStack and other things are not visible.
		var ctor = FusionRecipe.class.getDeclaredConstructor(ResourceLocation.class, ItemStack.class, Ingredient.class, long.class, TechLevel.class, Collection.class);
		ctor.setAccessible(true);
		return ctor.newInstance(id, result, catalyst, totalEnergy, techLevel, ingredients);
	}
	
	/**
	 * Creates a new Fusion Ingredient with the specified ItemStack and default "consume" value of true.
	 *
	 * @param stack
	 * 		the ItemStack
	 *
	 * @return the new Fusion Ingredient
	 *
	 * @throws ReflectiveOperationException
	 * 		if there is an error creating the Fusion Ingredient
	 */
	public static FusionRecipe.FusionIngredient ingr(ItemStack stack) throws ReflectiveOperationException
	{
		return ingr(stack, true);
	}
	
	/**
	 * Creates a new Fusion Ingredient with the specified ItemStack and "consume" value.
	 *
	 * @param stack
	 * 		the ItemStack
	 * @param consume
	 * 		whether the ItemStack should be consumed
	 *
	 * @return the new Fusion Ingredient
	 *
	 * @throws ReflectiveOperationException
	 * 		if there is an error creating the Fusion Ingredient
	 */
	public static FusionRecipe.FusionIngredient ingr(ItemStack stack, boolean consume) throws ReflectiveOperationException
	{
		@SuppressWarnings("JavaReflectionMemberAccess") // We compiled with DE 1.16.5, thus ItemStack and other things are not visible.
		var ctor = FusionRecipe.FusionIngredient.class.getDeclaredConstructor(Ingredient.class, boolean.class);
		ctor.setAccessible(true);
		return ctor.newInstance(Ingredient.of(stack), consume);
	}
}