package org.zeith.solarflux.panels;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class JSHelper
{
	public static ItemLike item(String id)
	{
		return () -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
	}

	public static ItemLike item(String mod, String id)
	{
		return () -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(mod, id));
	}

	public static Supplier<Tag.Named<Item>> tag(String id)
	{
		return () -> ForgeTagHandler.makeWrapperTag(ForgeRegistries.ITEMS, new ResourceLocation(id));
	}

	public static Supplier<Tag.Named<Item>> tag(String mod, String id)
	{
		return () -> ForgeTagHandler.makeWrapperTag(ForgeRegistries.ITEMS, new ResourceLocation(mod, id));
	}
}