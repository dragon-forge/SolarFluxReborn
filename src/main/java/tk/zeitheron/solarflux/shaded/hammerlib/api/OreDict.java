package tk.zeitheron.solarflux.shaded.hammerlib.api;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import tk.zeitheron.solarflux.SolarFlux;

/**
 * WARNING: THIS IS NOT AN ACTUAL ORE DICTIONARY!!!<br>
 * IT IS USED TO ADAPT OLD NAMES TO ITEM TAGS!
 */
public class OreDict
{
	private static final Map<String, ResourceLocation> MAPPING = new HashMap<>();
	private static boolean hasInit;
	
	public static void register(String key, String value)
	{
		register(key, new ResourceLocation(value.contains(":") ? value : ("forge:" + value)));
	}
	
	public static void register(String key, Tag tag)
	{
		if(tag != null)
			register(key, tag.getId());
	}
	
	public static void register(String key, ResourceLocation value)
	{
		MAPPING.put(key, value);
	}
	
	@Nullable
	public static ResourceLocation get(String od)
	{
		if(!MAPPING.containsKey(od))
		{
			if(od.startsWith("ingot"))
				MAPPING.put(od, new ResourceLocation("forge", "ingots/" + od.substring(5).toLowerCase()));
			if(od.startsWith("ore"))
				MAPPING.put(od, new ResourceLocation("forge", "ores/" + od.substring(3).toLowerCase()));
			if(od.startsWith("gem"))
				MAPPING.put(od, new ResourceLocation("forge", "gems/" + od.substring(3).toLowerCase()));
			if(od.startsWith("block"))
				MAPPING.put(od, new ResourceLocation("forge", "storage_blocks/" + od.substring(5).toLowerCase()));
			if(od.startsWith("dust"))
				MAPPING.put(od, new ResourceLocation("forge", "dusts/" + od.substring(4).toLowerCase()));
			if(od.startsWith("gear"))
				MAPPING.put(od, new ResourceLocation("forge", "gears/" + od.substring(4).toLowerCase()));
			if(od.startsWith("plate"))
				MAPPING.put(od, new ResourceLocation("forge", "plates/" + od.substring(5).toLowerCase()));
			
			if(MAPPING.containsKey(od))
				SolarFlux.LOG.debug("Generated mapping for " + od + ": " + MAPPING.get(od));
		}
		
		return MAPPING.get(od);
	}
	
	static
	{
		initVanillaEntries();
	}
	
	public static void initVanillaEntries()
	{
		if(!hasInit)
		{
			// tree- and wood-related things
			register("logWood", ItemTags.LOGS);
			register("plankWood", ItemTags.PLANKS);
			register("slabWood", ItemTags.WOODEN_SLABS);
			register("stairWood", ItemTags.WOODEN_STAIRS);
			register("fenceWood", Tags.Items.FENCES_WOODEN);
			register("fenceGateWood", Tags.Items.FENCE_GATES_WOODEN);
			register("doorWood", ItemTags.WOODEN_DOORS);
			register("stickWood", Tags.Items.RODS_WOODEN);
			register("treeSapling", ItemTags.SAPLINGS);
			register("treeLeaves", ItemTags.LEAVES);
			
			// Ores
			register("oreGold", Tags.Items.ORES_GOLD);
			register("oreIron", Tags.Items.ORES_IRON);
			register("oreLapis", Tags.Items.ORES_LAPIS);
			register("oreDiamond", Tags.Items.ORES_DIAMOND);
			register("oreRedstone", Tags.Items.ORES_REDSTONE);
			register("oreEmerald", Tags.Items.ORES_EMERALD);
			register("oreQuartz", Tags.Items.ORES_QUARTZ);
			register("oreCoal", Tags.Items.ORES_COAL);
			
			// Ingots
			register("ingotIron", Tags.Items.INGOTS_IRON);
			register("ingotGold", Tags.Items.INGOTS_GOLD);
			register("ingotBrick", Tags.Items.INGOTS_BRICK);
			register("ingotBrickNether", Tags.Items.INGOTS_NETHER_BRICK);
			
			// Nugget
			register("nuggetIron", Tags.Items.NUGGETS_IRON);
			register("nuggetIron", Tags.Items.NUGGETS_GOLD);
			
			// Gems
			register("gemDiamond", Tags.Items.GEMS_DIAMOND);
			register("gemEmerald", Tags.Items.GEMS_EMERALD);
			register("gemQuartz", Tags.Items.GEMS_QUARTZ);
			register("gemPrismarine", Tags.Items.GEMS_PRISMARINE);
			register("gemLapis", Tags.Items.GEMS_LAPIS);
			register("enderpearl", Tags.Items.ENDER_PEARLS);
			
			// Dusts
			register("dustPrismarine", Tags.Items.DUSTS_PRISMARINE);
			register("dustRedstone", Tags.Items.DUSTS_REDSTONE);
			register("dustGlowstone", Tags.Items.DUSTS_GLOWSTONE);
			
			// Storage Blocks
			register("blockGold", Tags.Items.STORAGE_BLOCKS_GOLD);
			register("blockIron", Tags.Items.STORAGE_BLOCKS_IRON);
			register("blockLapis", Tags.Items.STORAGE_BLOCKS_LAPIS);
			register("blockDiamond", Tags.Items.STORAGE_BLOCKS_DIAMOND);
			register("blockRedstone", Tags.Items.STORAGE_BLOCKS_REDSTONE);
			register("blockEmerald", Tags.Items.STORAGE_BLOCKS_EMERALD);
			register("blockQuartz", Tags.Items.STORAGE_BLOCKS_QUARTZ);
			register("blockCoal", Tags.Items.STORAGE_BLOCKS_COAL);
			register("blockGlass", Tags.Items.GLASS);
			
			// Misc
			register("dye", Tags.Items.DYES);
			register("musicDisc", Tags.Items.MUSIC_DISCS);
			
			// Blocks
			register("stone", Tags.Items.STONE);
			register("obsidian", Tags.Items.OBSIDIAN);
			register("sand", ItemTags.SAND);
			register("brickStone", ItemTags.STONE_BRICKS);
			
			// Chests
			register("chest", Tags.Items.CHESTS);
			register("chestWood", Tags.Items.CHESTS_WOODEN);
			register("chestTrapped", Tags.Items.CHESTS_TRAPPED);
			register("chestEnder", Tags.Items.CHESTS_ENDER);
		}
		hasInit = true;
	}
}