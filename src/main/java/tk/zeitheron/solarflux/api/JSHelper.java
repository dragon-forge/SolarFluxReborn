package tk.zeitheron.solarflux.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.function.Supplier;

public class JSHelper
{
	public static Supplier<ItemStack> item(String id)
	{
		return () -> new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(id)));
	}

	public static Supplier<ItemStack> item(String mod, String id)
	{
		return () -> new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(mod, id)));
	}

	public static Supplier<ItemStack> item(String id, int meta)
	{
		return () -> new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(id)), 1, meta);
	}
}