package org.zeith.solarflux.panels;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.api.items.IIngredientProvider;

import java.util.stream.Stream;

public class JSHelper
{
	public static IItemProvider item(String id)
	{
		return () -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
	}

	public static IItemProvider item(String mod, String id)
	{
		return () -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(mod, id));
	}

	public static IIngredientProvider tag(String id)
	{
		return () ->
		{
			ITag<Item> itag = TagCollectionManager.getInstance().getItems().getTag(new ResourceLocation(id));
			return Ingredient.fromValues(Stream.of(new Ingredient.TagList(itag)));
		};
	}

	public static IIngredientProvider tag(String mod, String id)
	{
		return () ->
		{
			ITag<Item> itag = TagCollectionManager.getInstance().getItems().getTag(new ResourceLocation(mod, id));
			return Ingredient.fromValues(Stream.of(new Ingredient.TagList(itag)));
		};
	}
}