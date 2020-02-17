package tk.zeitheron.solarflux;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;
import tk.zeitheron.solarflux.items.ItemsSF;
import tk.zeitheron.solarflux.panels.SolarPanel;
import tk.zeitheron.solarflux.panels.SolarPanels;
import tk.zeitheron.solarflux.shaded.hammerlib.api.OreDict;

@EventBusSubscriber
public class RecipesSF
{
	public static void addRecipes(List<IRecipe<?>> r)
	{
		r.add(parseShaped(new ItemStack(ItemsSF.MIRROR, 3), "ggg", " i ", 'g', "blockGlass", 'i', "ingotIron"));
		r.add(parseShaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_1), "ggg", "lll", "mmm", 'g', "blockGlass", 'l', "gemLapis", 'm', ItemsSF.MIRROR));
		r.add(parseShaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_2), "clc", "lcl", "msm", 'c', Items.CLAY_BALL, 'l', "gemLapis", 'm', ItemsSF.MIRROR, 's', ItemsSF.PHOTOVOLTAIC_CELL_1));
		r.add(parseShaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_3), "ggg", "lll", "oco", 'g', "blockGlass", 'l', "dustGlowstone", 'o', "obsidian", 'c', ItemsSF.PHOTOVOLTAIC_CELL_2));
		r.add(parseShaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_4), "bbb", "gdg", "qcq", 'b', Items.BLAZE_POWDER, 'g', "dustGlowstone", 'd', "gemDiamond", 'q', "blockQuartz", 'c', ItemsSF.PHOTOVOLTAIC_CELL_3));
		r.add(parseShaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_5), "bbb", "gdg", "qcq", 'b', Items.BLAZE_ROD, 'g', Blocks.GLOWSTONE, 'd', "blockDiamond", 'q', "blockQuartz", 'c', new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_4)));
		r.add(parseShaped(new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_6), "bbb", "gdg", "qcq", 'b', "gemEmerald", 'g', Blocks.GLOWSTONE, 'd', "blockDiamond", 'q', "blockQuartz", 'c', new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_5)));
		r.add(parseShaped(new ItemStack(ItemsSF.BLANK_UPGRADE), " c ", "cmc", " c ", 'c', "cobblestone", 'm', ItemsSF.MIRROR));
		
		Item dragon_egg = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", "dragon_egg"));
		
		r.add(parseShaped(new ItemStack(SolarPanels.CORE_PANELS[0].getBlock()), "mmm", "prp", "ppp", 'm', ItemsSF.MIRROR, 'p', "plankWood", 'r', "dustRedstone"));
		r.add(parseShaped(new ItemStack(SolarPanels.CORE_PANELS[1].getBlock()), "sss", "sps", "sss", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[0].delegateData.generation), 'p', Blocks.PISTON));
		r.add(parseShaped(new ItemStack(SolarPanels.CORE_PANELS[2].getBlock(), 2), "ppp", "scs", "sbs", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[1].delegateData.generation), 'p', ItemsSF.PHOTOVOLTAIC_CELL_1, 'c', Items.REPEATER, 'b', "blockIron"));
		r.add(parseShaped(new ItemStack(SolarPanels.CORE_PANELS[3].getBlock(), 2), "ppp", "scs", "sbs", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[2].delegateData.generation), 'p', ItemsSF.PHOTOVOLTAIC_CELL_2, 'c', Items.CLOCK, 'b', "blockIron"));
		r.add(parseShaped(new ItemStack(SolarPanels.CORE_PANELS[4].getBlock(), 2), "ppp", "scs", "sbs", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[3].delegateData.generation), 'p', ItemsSF.PHOTOVOLTAIC_CELL_3, 'c', "dustGlowstone", 'b', "blockGold"));
		r.add(parseShaped(new ItemStack(SolarPanels.CORE_PANELS[5].getBlock(), 2), "ppp", "scs", "sbs", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[4].delegateData.generation), 'p', ItemsSF.PHOTOVOLTAIC_CELL_4, 'c', Blocks.REDSTONE_LAMP, 'b', "blockDiamond"));
		r.add(parseShaped(new ItemStack(SolarPanels.CORE_PANELS[6].getBlock(), 2), "ppp", "scs", "scs", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[5].delegateData.generation), 'p', ItemsSF.PHOTOVOLTAIC_CELL_5, 'c', new ItemStack(Items.DRAGON_BREATH)));
		r.add(parseShaped(new ItemStack(SolarPanels.CORE_PANELS[7].getBlock(), 2), "ppp", "scs", "scs", 's', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[6].delegateData.generation), 'p', ItemsSF.PHOTOVOLTAIC_CELL_6, 'c', new ItemStack(dragon_egg)));
		
		r.add(parseShaped(new ItemStack(ItemsSF.EFFICIENCY_UPGRADE), " m ", "mum", " c ", 'm', ItemsSF.MIRROR, 'u', ItemsSF.BLANK_UPGRADE, 'c', ItemsSF.PHOTOVOLTAIC_CELL_1));
		r.add(parseShaped(new ItemStack(ItemsSF.TRANSFER_RATE_UPGRADE), "rrr", "gug", "rrr", 'u', ItemsSF.BLANK_UPGRADE, 'r', "dustRedstone", 'g', "ingotGold"));
		r.add(parseShaped(new ItemStack(ItemsSF.TRAVERSAL_UPGRADE), "ipi", "rur", "ipi", 'i', "ingotIron", 'p', Ingredient.fromItems(Blocks.PISTON, Blocks.STICKY_PISTON), 'u', ItemsSF.BLANK_UPGRADE, 'r', "dustRedstone"));
		r.add(parseShaped(new ItemStack(ItemsSF.DISPERSIVE_UPGRADE), "geg", "eue", "geg", 'g', "dustGlowstone", 'e', Items.ENDER_EYE, 'u', ItemsSF.BLANK_UPGRADE));
		r.add(parseShaped(new ItemStack(ItemsSF.BLOCK_CHARGING_UPGRADE), "geg", "eue", "geg", 'g', "enderpearl", 'e', "blockRedstone", 'u', ItemsSF.DISPERSIVE_UPGRADE));
		r.add(parseShaped(new ItemStack(ItemsSF.FURNACE_UPGRADE), "ccc", "cuc", "cfc", 'u', ItemsSF.BLANK_UPGRADE, 'c', Items.COAL, 'f', Blocks.FURNACE));
		r.add(parseShaped(new ItemStack(ItemsSF.CAPACITY_UPGRADE), " r ", "rur", "rcr", 'u', ItemsSF.BLANK_UPGRADE, 'r', "dustRedstone", 'c', "blockDiamond"));
		
		SolarPanels.listPanels().flatMap(SolarPanel::recipes).forEach(r::add);
	}
	
	@SubscribeEvent
	public static void worldTick(WorldTickEvent e)
	{
		if(e.phase == Phase.START)
		{
			RecipeManager mgr = e.world.getRecipeManager();
			Field f = RecipeManager.class.getDeclaredFields()[2];
			f.setAccessible(true);
			try
			{
				Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes = Map.class.cast(f.get(mgr));
				
				if(recipes instanceof ImmutableMap)
				{
					recipes = new HashMap<>(recipes);
					f.set(mgr, recipes);
				}
				
				if(!recipes.containsKey(SFRRecipeType.INSTANCE))
				{
					addRecipes(recipes);
					recipes.put(SFRRecipeType.INSTANCE, new HashMap<>());
				}
			} catch(IllegalArgumentException | IllegalAccessException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	
	public static void addRecipes(Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes)
	{
		List<IRecipe<?>> shaped = new ArrayList<>();
		
		addRecipes(shaped);
		
		shaped.forEach(recipe ->
		{
			IRecipeType<?> type = recipe.getType();
			Map<ResourceLocation, IRecipe<?>> map = recipes.get(type);
			if(map instanceof ImmutableMap)
			{
				map = new HashMap<>(map);
				recipes.put(type, map);
			}
			map.put(recipe.getId(), recipe);
		});
	}
	
	static int lastRecipeID;
	
	private static ResourceLocation nextId(Item item)
	{
		ResourceLocation rl = item.getRegistryName();
		return new ResourceLocation(rl.getNamespace(), (++lastRecipeID) + "/" + rl.getPath());
	}
	
	public static ShapedRecipe parseShaped(ItemStack output, Object... recipeComponents)
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
		
		for(map = Maps.<Character, Ingredient> newHashMap(); i < recipeComponents.length; i += 2)
		{
			Character character = (Character) recipeComponents[i];
			Ingredient ingr = fromComponent(recipeComponents[i + 1]);
			if(ingr != null)
				map.put(character, ingr);
			else
				SolarFlux.LOG.warn("Unable to parse ingredient!!! Ingredient being parsed: " + recipeComponents[i + 1]);
		}
		
		NonNullList<Ingredient> aitemstack = NonNullList.withSize(j * k, Ingredient.EMPTY);
		
		for(int l = 0; l < j * k; ++l)
		{
			char c0 = s.charAt(l);
			if(map.containsKey(c0))
				aitemstack.set(l, map.get(c0));
		}
		
		return new ShapedRecipe(nextId(output.getItem()), "", j, k, aitemstack, output);
	}
	
	public static Ingredient fromComponent(Object comp)
	{
		Ingredient ingr = null;
		
		if(comp instanceof IItemProvider)
			ingr = Ingredient.fromItems((IItemProvider) comp);
		else if(comp instanceof ItemStack)
			ingr = Ingredient.fromStacks(((ItemStack) comp).copy());
		else if(comp instanceof Tag)
			ingr = Ingredient.fromTag((Tag<Item>) comp);
		else if(comp instanceof Tag[])
			ingr = fromTags((Tag<Item>[]) comp);
		else if(comp instanceof String || comp instanceof ResourceLocation)
		{
			String st = comp.toString();
			ResourceLocation tag = null;
			ResourceLocation odConv = OreDict.get(st);
			if(odConv != null)
				tag = odConv;
			else
				tag = new ResourceLocation(st.contains(":") ? st : ("forge:" + st));
			ingr = Ingredient.fromTag(ItemTags.getCollection().getOrCreate(tag));
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
	
	public static Ingredient fromTags(Tag<Item>... tags)
	{
		List<Ingredient.TagList> list = new ArrayList<>();
		for(Tag<Item> t : tags)
			list.add(new Ingredient.TagList(t));
		return Ingredient.fromItemListStream(list.stream());
	}
	
	public static Ingredient fromTags(Collection<Tag<Item>> tags)
	{
		List<Ingredient.TagList> list = new ArrayList<>();
		for(Tag<Item> t : tags)
			list.add(new Ingredient.TagList(t));
		return Ingredient.fromItemListStream(list.stream());
	}
	
	private static class SFRRecipeType implements IRecipeType<IRecipe<?>>
	{
		public static final SFRRecipeType INSTANCE = new SFRRecipeType();
		
		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof SFRRecipeType;
		}
	}
}