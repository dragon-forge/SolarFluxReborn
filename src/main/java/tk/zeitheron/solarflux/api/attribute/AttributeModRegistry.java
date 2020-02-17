package tk.zeitheron.solarflux.api.attribute;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class AttributeModRegistry
{
	private static final Map<String, Function<Float, ? extends IAttributeMod>> registry = new HashMap<>();
	private static final BiMap<String, Class<? extends IAttributeMod>> typeRegistry = HashBiMap.create();
	
	static
	{
		register("add", AttributeModAdd.class, AttributeModAdd::new);
		register("mult", AttributeModMultiply.class, AttributeModMultiply::new);
	}
	
	public static <T extends IAttributeMod> void register(String id, Class<T> type, Function<Float, T> generator)
	{
		id = id.toLowerCase();
		if(registry.containsKey(id))
			throw new IllegalArgumentException("Duplicate entry for '" + id + "'!");
		registry.put(id, generator);
		typeRegistry.put(id, type);
	}
	
	public static String getId(IAttributeMod mod)
	{
		if(mod == null)
			return null;
		return typeRegistry.inverse().get(mod.getClass());
	}
	
	public static IAttributeMod create(String id, float value)
	{
		id = id.toLowerCase();
		return registry.containsKey(id) ? registry.get(id).apply(value) : null;
	}
}