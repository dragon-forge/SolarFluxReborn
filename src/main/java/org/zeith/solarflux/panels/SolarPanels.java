package org.zeith.solarflux.panels;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import org.zeith.hammerlib.util.cfg.ConfigFile;
import org.zeith.hammerlib.util.cfg.entries.ConfigEntryCategory;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.block.SolarPanelBlock;
import org.zeith.solarflux.block.SolarPanelTile;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public class SolarPanels
{
	public static final Map<String, SolarPanel> PANELS = new HashMap<>();
	public static final BlockEntityType<SolarPanelTile> SOLAR_PANEL_TYPE = new BlockEntityType<SolarPanelTile>(SolarPanelTile::new, new HashSet<>(), null)
	{
		{
			setRegistryName("solarflux", "solar_panel");
		}

		@Override
		public boolean isValid(BlockState state)
		{
			return state.getBlock() instanceof SolarPanelBlock;
		}
	};

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
		File solarflux = CONFIG_DIR = new File(FMLPaths.CONFIGDIR.get().toFile(), "solarflux");

		if(!solarflux.isDirectory())
			solarflux.mkdirs();

		int[] generations = new int[]{
				1,
				8,
				32,
				128,
				512,
				2048,
				8192,
				32768
		};
		int[] transfers = new int[]{
				8,
				64,
				256,
				1024,
				4096,
				16348,
				65536,
				262144
		};
		int[] capacities = new int[]{
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
				cat = panels.getCategory(
						ModList.get().getMods()
								.stream()
								.filter(m -> m.getModId().equals(i.getCompatMod()))
								.findFirst()
								.map(IModInfo::getDisplayName)
								.orElse("Unknown")
				);
			i.configureBase(cat.getCategory(i.name));
		});

		if(panels.hasChanged()) panels.save();
	}

	public static void refreshRecipes()
	{
		ConfigFile recipes = new ConfigFile(new File(CONFIG_DIR, "recipes.hlc"));
		ConfigEntryCategory cat = recipes.getCategory("recipes");
		RECIPE_KEYS.forEach(recipe ->
		{
			boolean enabled = cat.getBooleanEntry(recipe.toString(), true).setDescription("Enable this recipe?").getValue();
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
		return Ingredient.fromValues(listPanels().filter(sp -> sp.delegateData.generation == generation).map(SolarPanel::getBlock).map(ItemStack::new).map(Ingredient.ItemValue::new));
	}

	public static Ingredient getGeneratingSolars(SolarPanel baseGeneration)
	{
		return getGeneratingSolars(baseGeneration.delegateData.generation);
	}
}