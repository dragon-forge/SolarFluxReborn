package org.zeith.solarflux.panels;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.util.cfg.ConfigFile;
import org.zeith.hammerlib.util.cfg.entries.ConfigEntryCategory;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.block.SolarPanelBlock;
import org.zeith.solarflux.block.SolarPanelTile;

import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Stream;

@SimplyRegister
public class SolarPanels
{
	public static final Map<String, SolarPanel> PANELS = new HashMap<>();
	
	@RegistryName("solar_panel")
	public static final TileEntityType<SolarPanelTile> SOLAR_PANEL_TYPE = new TileEntityType<SolarPanelTile>(SolarPanelTile::new, new HashSet<>(), null)
	{
		@Override
		public boolean isValid(Block blockIn)
		{
			return blockIn instanceof SolarPanelBlock;
		}
	};
	
	@SimplyRegister
	public static void registerPanels(Consumer<Block> registry)
	{
		listPanelBlocks().forEach(registry);
	}
	
	public static double LOOSE_ENERGY;
	
	public static float RAIN_MULTIPLIER = 0.6F, THUNDER_MULTIPLIER = 0.4F;
	
	public static final SolarPanel[] CORE_PANELS = new SolarPanel[8];
	
	public static File CONFIG_DIR;
	
	public static Stream<SolarPanel> listPanels()
	{
		return PANELS.values().stream();
	}
	
	public static Stream<SolarPanelBlock> listPanelBlocks()
	{
		return listPanels().map(SolarPanel::getBlock);
	}
	
	public static final List<ResourceLocation> RECIPE_KEYS = new ArrayList<>();
	private static final List<ResourceLocation> ENABLED_RECIPES = new ArrayList<>();
	
	public static void init()
	{
		File solarflux = CONFIG_DIR = FMLPaths.CONFIGDIR.get().resolve("solarflux").toFile();
		
		if(!solarflux.isDirectory())
			solarflux.mkdirs();
		new File(solarflux, "compat").mkdirs();
		
		int[] generations = new int[] {
				1,
				8,
				32,
				128,
				512,
				2048,
				8192,
				32768
		};
		int[] transfers = new int[] {
				8,
				64,
				256,
				1024,
				4096,
				16348,
				65536,
				262144
		};
		int[] capacities = new int[] {
				25000,
				125000,
				425000,
				2000000,
				8000000,
				32000000,
				64000000,
				128000000
		};
		
		ConfigFile cfgs = new ConfigFile(new File(solarflux, "main.hlc"));
		
		cfgs.setComment("Main configuration file for Solar Flux Reborn!\nTo implement custom panels, look for the custom_panels.js file!");
		
		ConfigEntryCategory spc = cfgs.getCategory("Solar Panels");
		
		LOOSE_ENERGY = spc.getFloatEntry("Pickup Energy Loss", 5, 0, 100).setDescription("How much energy (percent) will get lost while picking up the solar panel?").getValue();
		
		for(int i = 0; i < CORE_PANELS.length; ++i)
		{
			ConfigEntryCategory spsc = spc.getCategory("Solar Panel " + (i + 1));
			
			long gen = spsc.getLongEntry("Generation Rate", generations[i], 1, Long.MAX_VALUE).getValue();
			long transfer = spsc.getLongEntry("Transfer Rate", transfers[i], 1, Long.MAX_VALUE).getValue();
			long capacity = spsc.getLongEntry("Capacity", capacities[i], 1, Long.MAX_VALUE).getValue();
			
			CORE_PANELS[i] = SolarPanel.builder().name(Integer.toString(i + 1)).generation(gen).transfer(transfer).capacity(capacity).buildAndRegister();
		}
		
		ConfigEntryCategory main = cfgs.getCategory("Main");
		
		RAIN_MULTIPLIER = main.getFloatEntry("Rain Multiplier", 0.6F, 0F, 1F).setDescription("How much energy should be generated when it is raining? 0 - nothing, 1 - full power.").getValue();
		THUNDER_MULTIPLIER = main.getFloatEntry("Thunder Multiplier", 0.4F, 0F, 1F).setDescription("How much energy should be generated when it is thundering? 0 - nothing, 1 - full power.").getValue();
		
		if(cfgs.hasChanged())
			cfgs.save();
		
		File textures = new File(solarflux, "textures");
		if(!textures.isDirectory())
		{
			textures.mkdirs();
			
			File blocks = new File(textures, "blocks");
			
			if(!blocks.isDirectory())
			{
				blocks.mkdirs();
				
				int r;
				byte[] buf = new byte[768];
				
				try(FileOutputStream out = new FileOutputStream(new File(blocks, "example_base.png")); InputStream in = SolarFlux.class.getResourceAsStream("/assets/solarflux/textures/blocks/sp_example_base.png"))
				{
					while((r = in.read(buf)) > 0)
						out.write(buf, 0, r);
				} catch(IOException e)
				{
					throw new RuntimeException(e);
				}
				
				try(FileOutputStream out = new FileOutputStream(new File(blocks, "example_top.png")); InputStream in = SolarFlux.class.getResourceAsStream("/assets/solarflux/textures/blocks/sp_example_top.png"))
				{
					
					while((r = in.read(buf)) > 0)
						out.write(buf, 0, r);
				} catch(IOException e)
				{
					throw new RuntimeException(e);
				}
			}
			
			File items = new File(textures, "items");
			
			if(!items.isDirectory())
			{
				items.mkdirs();
				
				int r;
				byte[] buf = new byte[768];
				
				try(FileOutputStream out = new FileOutputStream(new File(items, "example.png")); InputStream in = SolarFlux.class.getResourceAsStream("/assets/solarflux/textures/items/_example.png"))
				{
					while((r = in.read(buf)) > 0)
						out.write(buf, 0, r);
				} catch(IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		} else // Migration code
		{
			File blocks = new File(textures, "blocks");
			
			if(!blocks.isDirectory())
			{
				blocks.mkdirs();
				move(textures, blocks);
			}
			
			File items = new File(textures, "items");
			
			if(!items.isDirectory())
			{
				items.mkdirs();
				
				int r;
				byte[] buf = new byte[768];
				
				try(FileOutputStream out = new FileOutputStream(new File(items, "example.png")); InputStream in = SolarFlux.class.getResourceAsStream("/assets/solarflux/textures/items/_example.png"))
				{
					while((r = in.read(buf)) > 0)
						out.write(buf, 0, r);
				} catch(IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		
		File custom_panels = new File(solarflux, "custom_panels.js");
		if(!custom_panels.isFile())
			try(FileOutputStream fos = new FileOutputStream(custom_panels); InputStream in = SolarFlux.class.getResourceAsStream("/__custom_panels.js"))
			{
				int r;
				byte[] buf = new byte[768];
				while((r = in.read(buf)) > 0)
					fos.write(buf, 0, r);
			} catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		
		try
		{
			SolarScriptEngine engine = new SolarScriptEngine(Files.readAllLines(custom_panels.toPath(), StandardCharsets.UTF_8).stream());
			engine.callFunction("init");
		} catch(IOException | ScriptException | ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static void setupPanel(ConfigEntryCategory cat, String name, AtomicLong generation, AtomicLong transfer, AtomicLong capacity)
	{
		ConfigEntryCategory spsc = cat.getCategory("Solar Panel " + name);
		generation.set(spsc.getLongEntry("Generation Rate", generation.get(), 1, Long.MAX_VALUE).getValue());
		transfer.set(spsc.getLongEntry("Transfer Rate", transfer.get(), 1, Long.MAX_VALUE).getValue());
		capacity.set(spsc.getLongEntry("Capacity", capacity.get(), 1, Long.MAX_VALUE).getValue());
	}
	
	public static long[] setupPanel(ConfigEntryCategory cat, String name, long generation, long transfer, long capacity)
	{
		long[] out = new long[3];
		ConfigEntryCategory spsc = cat.getCategory("Solar Panel " + name);
		out[0] = spsc.getLongEntry("Generation Rate", generation, 1, Long.MAX_VALUE).getValue();
		out[1] = spsc.getLongEntry("Transfer Rate", transfer, 1, Long.MAX_VALUE).getValue();
		out[2] = spsc.getLongEntry("Capacity", capacity, 1, Long.MAX_VALUE).getValue();
		return out;
	}
	
	public static long[] setupPanel(ConfigEntryCategory cat, String name, SolarPanel copyFrom)
	{
		return setupPanel(cat, name, copyFrom.delegateData.generation, copyFrom.delegateData.transfer, copyFrom.delegateData.capacity);
	}
	
	private static void move(File dir, File to)
	{
		if(dir.isDirectory() && to.isDirectory() && !dir.getAbsolutePath().startsWith(to.getAbsolutePath()))  // we are not supposed to move a file into itself
			for(File f : dir.listFiles())
				if(!f.getAbsolutePath().startsWith(to.getAbsolutePath())) // same but with subfiles
				{
					if(f.isFile())
						f.renameTo(new File(to, f.getName()));
					else if(f.isDirectory())
					{
						File nd = new File(to, f.getName());
						if(!nd.isDirectory()) nd.mkdirs();
						move(f, nd);
						f.delete();
					}
				}
	}
	
	public static void refreshConfigs()
	{
		ConfigFile panels = new ConfigFile(new File(CONFIG_DIR, "panels.hlc"));
		
		listPanels().forEach(i ->
		{
			ConfigEntryCategory cat;
			if(i.isCustom) cat = panels.getCategory("Solar Flux: Custom");
			else if(i.getCompatMod() == null) cat = panels.getCategory("Solar Flux");
			else
				cat = panels.getCategory(ModList.get().getMods().stream().filter(m -> m.getModId().equals(i.getCompatMod())).findFirst().map(ModInfo::getDisplayName).orElse("Unknown"));
			i.configureBase(cat.getCategory(i.name));
		});
		
		if(panels.hasChanged()) panels.save();
	}
	
	public static void refreshRecipes()
	{
		ConfigFile recipes = new ConfigFile(new File(CONFIG_DIR, "recipes.hlc"));
		ConfigEntryCategory cat = recipes.getCategory("recipes")
				.setDescription("List of all the recipes that Solar Flux adds.\nDefaulted to true, but you may disable any of the recipes listed.\nAfter you're done changing, you may apply the code right away, using /reload command");
		RECIPE_KEYS.forEach(recipe ->
		{
			boolean enabled = cat.getBooleanEntry(recipe.toString(), true).getValue();
			if(enabled) ENABLED_RECIPES.add(recipe);
		});
		recipes.save();
	}
	
	public static boolean isRecipeActive(ResourceLocation id)
	{
		return ENABLED_RECIPES.contains(id);
	}
	
	public static void indexRecipes(ResourceLocation... ids)
	{
		RECIPE_KEYS.addAll(Arrays.asList(ids));
	}
	
	public static Ingredient getGeneratingSolars(long generation)
	{
		return Ingredient.of(listPanels().filter(sp -> sp.delegateData.generation == generation).map(SolarPanel::getBlock).map(ItemStack::new));
	}
	
	public static Ingredient getGeneratingSolars(SolarPanel baseGeneration)
	{
		return getGeneratingSolars(baseGeneration.delegateData.generation);
	}
}