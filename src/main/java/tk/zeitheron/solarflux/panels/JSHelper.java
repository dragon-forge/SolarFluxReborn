package tk.zeitheron.solarflux.panels;

import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

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
}