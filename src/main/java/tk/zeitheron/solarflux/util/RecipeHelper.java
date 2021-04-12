package tk.zeitheron.solarflux.util;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import net.minecraftforge.registries.IForgeRegistryEntry;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.shaded.hammerlib.api.OreDict;

import java.util.*;

public class RecipeHelper
{
	private static final List<IRecipe<?>> recipes = new ArrayList<>();

	final String modid;

	public RecipeHelper(String modid)
	{
		this.modid = modid;
	}

	static void addRecipes(RecipeManager mgr)
	{
		recipes.forEach(r ->
		{
			Map<ResourceLocation, IRecipe<?>> map = mgr.recipes.computeIfAbsent(r.getType(), t -> new HashMap<>());
			IRecipe<?> old = map.get(r.getId());
			if(old == null)
			{
				r.getIngredients().stream().filter(i -> i instanceof TagIngredient).map(i -> (TagIngredient) i).forEach(TagIngredient::redefine);
				map.put(r.getId(), r);
			}
		});
		SolarFlux.LOG.info("Registered {} additional recipes.", recipes.size());
	}

	public static void mutableManager(RecipeManager mgr)
	{
		mgr.recipes = new HashMap<>(mgr.recipes);
		for(IRecipeType<?> type : mgr.recipes.keySet())
		{
			mgr.recipes.put(type, new HashMap<>(mgr.recipes.get(type)));
		}
	}

	public static void reload(RecipeManager mgr, IReloadableResourceManager rel)
	{
		rel.addReloadListener(RunnableReloader.of(() ->
		{
			mutableManager(mgr);
			addRecipes(mgr);
		}));
	}

	public static void addRecipe(IRecipe<?> rec)
	{
		synchronized(recipes)
		{
			if(rec == null)
			{
				SolarFlux.LOG.error("Attempted to add null recipe, this is invalid behavior.");
				Thread.dumpStack();
			}
			recipes.add(rec);
		}
	}

	public void addShapeless(Object output, Object... inputs)
	{
		ItemStack out = makeStack(output);
		addRecipe(new ShapelessRecipe(nextId(out.getItem()), modid, out, createInput(false, inputs)));
	}

	public void addKeyShaped(Object output, Object... recipeComponents)
	{
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;

		if(recipeComponents[i] instanceof String[])
		{
			String[] astring = ((String[]) recipeComponents[i++]);

			for(String s2 : astring)
			{
				++k;
				j = s2.length();
				s = s + s2;
			}
		} else
		{
			while(recipeComponents[i] instanceof String)
			{
				String s1 = (String) recipeComponents[i++];
				++k;
				j = s1.length();
				s = s + s1;
			}
		}

		Map<Character, Ingredient> map;

		for(map = Maps.newHashMap(); i < recipeComponents.length; i += 2)
		{
			Character character = (Character) recipeComponents[i];
			Ingredient ingr = fromComponent(recipeComponents[i + 1]);
			if(ingr != null)
				map.put(character, ingr);
			else
				SolarFlux.LOG.warn("Unable to parse ingredient!!! Ingredient being parsed: " + recipeComponents[i + 1]);
		}

		NonNullList<Ingredient> input = NonNullList.withSize(j * k, Ingredient.EMPTY);

		for(int l = 0; l < j * k; ++l)
		{
			char c0 = s.charAt(l);
			if(map.containsKey(c0))
				input.set(l, map.get(c0));
		}

		ItemStack out = makeStack(output);
		addRecipe(new ShapedRecipe(nextId(out.getItem()), "", j, k, input, out));
	}

	public static Ingredient fromComponent(Object comp)
	{
		Ingredient ingr = null;
		if(comp instanceof IItemProvider)
			ingr = Ingredient.fromItems((IItemProvider) comp);
		else if(comp instanceof ItemStack)
			ingr = Ingredient.fromStacks(((ItemStack) comp).copy());
		else if(comp instanceof String || comp instanceof ResourceLocation)
		{
			String k = comp.toString();
			ResourceLocation od;
			if((od = OreDict.get(k)) != null)
				return new TagIngredient(od.toString());
		} else if(comp instanceof ItemStack[])
		{
			ItemStack[] items = ((ItemStack[]) comp).clone();
			for(int l = 0; l < items.length; ++l)
				items[l] = items[l].copy();
			ingr = Ingredient.fromStacks(items);
		} else if(comp instanceof Ingredient)
			ingr = (Ingredient) comp;
		return ingr;
	}

	public void addShaped(Object output, Object... input)
	{
		addShaped(output, 3, 3, input);
	}

	public void addShaped(Object output, int width, int height, Object... input)
	{
		addRecipe(genShaped(makeStack(output), width, height, input));
	}

	public ShapedRecipe genShaped(ItemStack output, int l, int w, Object... input)
	{
		if(l * w != input.length)
			throw new UnsupportedOperationException("Attempted to add invalid shaped recipe.  Complain to the author of " + modid);
		return new ShapedRecipe(nextId(output.getItem()), modid, l, w, createInput(true, input), output);
	}

	@SuppressWarnings("unchecked")
	public NonNullList<Ingredient> createInput(boolean allowEmpty, Object... input)
	{
		NonNullList<Ingredient> inputL = NonNullList.create();
		for(int i = 0; i < input.length; i++)
		{
			Object k = input[i];
			ResourceLocation od;
			if(k instanceof String && (od = OreDict.get(k.toString())) != null)
				inputL.add(i, new TagIngredient(od.toString()));
			else if(k instanceof ItemStack && !((ItemStack) k).isEmpty())
				inputL.add(i, CachedIngredient.create((ItemStack) k));
			else if(k instanceof IForgeRegistryEntry) inputL.add(i, CachedIngredient.create(makeStack(k)));
			else if(k instanceof Ingredient) inputL.add(i, (Ingredient) k);
			else if(allowEmpty) inputL.add(i, Ingredient.EMPTY);
			else
				throw new UnsupportedOperationException("Attempted to add invalid recipe. Complain to the author of " + modid + ". (Input " + k + " not allowed.)");
		}
		return inputL;
	}

	public static ItemStack makeStack(Object thing, int size)
	{
		if(thing instanceof ItemStack) return (ItemStack) thing;
		if(thing instanceof Item) return new ItemStack((Item) thing, size);
		if(thing instanceof Block) return new ItemStack((Block) thing, size);
		throw new IllegalArgumentException("Attempted to create an ItemStack from something that cannot be converted: " + thing);
	}

	public static ItemStack makeStack(Object thing)
	{
		return makeStack(thing, 1);
	}

	public static class CachedIngredient
			extends Ingredient
	{

		private static Int2ObjectMap<CachedIngredient> ingredients = new Int2ObjectOpenHashMap<>();

		private CachedIngredient(ItemStack... matches)
		{
			super(Arrays.stream(matches).map(s -> new SingleItemList(s)));
			if(matches.length == 1) ingredients.put(RecipeItemHelper.pack(matches[0]), this);
		}

		public static CachedIngredient create(ItemStack... matches)
		{
			synchronized(ingredients)
			{
				if(matches.length == 1)
				{
					CachedIngredient coi = ingredients.get(RecipeItemHelper.pack(matches[0]));
					return coi != null ? coi : new CachedIngredient(matches);
				} else return new CachedIngredient(matches);
			}
		}

		@Override
		public IIngredientSerializer<? extends Ingredient> getSerializer()
		{
			return VanillaIngredientSerializer.INSTANCE;
		}

	}

	static int lastRecipeID;

	private static ResourceLocation nextId(Item item)
	{
		ResourceLocation rl = item.getRegistryName();
		return new ResourceLocation(rl.getNamespace(), (++lastRecipeID) + "/" + rl.getPath());
	}
}