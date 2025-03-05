package org.zeith.solarflux.panels;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.zeith.hammerlib.util.mcf.Resources;
import org.zeith.solarflux.items.JSItem;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class JSHelper
{
	private static final List<JSItem.FutureJSGenerator> ITEMS2REG = new ArrayList<>();
	private static final List<JSItem> JS_MATERIALS_INTERNAL = new ArrayList<>();
	public static final List<JSItem> JS_MATERIALS = Collections.unmodifiableList(JS_MATERIALS_INTERNAL);
	
	public static ItemLike newJSItem(String name)
	{
		var gen = new JSItem.FutureJSGenerator(name);
		ITEMS2REG.add(gen);
		return gen;
	}
	
	public static void generateItems(BiConsumer<ResourceLocation, Item> handler)
	{
		ITEMS2REG.forEach(f ->
		{
			var jsi = f.create();
			handler.accept(jsi.getRegistryName(), jsi);
			JS_MATERIALS_INTERNAL.add(jsi);
		});
	}
	
	public static ItemLike item(String id)
	{
		return () -> BuiltInRegistries.ITEM.getValue(Resources.location(id));
	}
	
	public static ItemLike item(String mod, String id)
	{
		return () -> BuiltInRegistries.ITEM.getValue(Resources.location(mod, id));
	}
	
	public static Supplier<TagKey<Item>> tag(String id)
	{
		return () -> ItemTags.create(Resources.location(id));
	}
	
	public static Supplier<TagKey<Item>> tag(String mod, String id)
	{
		return () -> ItemTags.create(Resources.location(mod, id));
	}
}