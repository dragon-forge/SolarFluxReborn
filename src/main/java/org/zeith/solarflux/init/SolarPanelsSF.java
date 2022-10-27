package org.zeith.solarflux.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.util.configured.ConfiguredLib;
import org.zeith.hammerlib.util.configured.data.DecimalValueRange;
import org.zeith.hammerlib.util.configured.data.IntValueRange;
import org.zeith.hammerlib.util.configured.types.ConfigCategory;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.block.SolarPanelBlock;
import org.zeith.solarflux.block.SolarPanelTile;
import org.zeith.solarflux.panels.SolarPanel;
import org.zeith.solarflux.panels.SolarScriptEngine;

import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@SimplyRegister
public class SolarPanelsSF
{
	public static final Map<String, SolarPanel> PANELS = new HashMap<>();
	@RegistryName("solar_panel")
	public static final BlockEntityType<SolarPanelTile> SOLAR_PANEL_TYPE = new BlockEntityType<SolarPanelTile>(SolarPanelTile::new, new HashSet<>(), null)
	{
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
	
	@SimplyRegister
	public static void registerPanels(BiConsumer<ResourceLocation, Block> reg)
	{
		listPanelBlocks()
				.forEach(panel ->
						reg.accept(panel.getRegistryName(), panel)
				);
	}
	
	public static final List<ResourceLocation> RECIPE_KEYS = new ArrayList<>();
	private static final List<ResourceLocation> ENABLED_RECIPES = new ArrayList<>();
	
	public static void init()
	{
		File solarflux = CONFIG_DIR = new File(FMLPaths.CONFIGDIR.get().toFile(), "solarflux");
		
		if(!solarflux.isDirectory())
			solarflux.mkdirs();
		
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
		
		var cfgs = ConfiguredLib.create(new File(solarflux, "main.cfg"), true);
		
		cfgs.withComment("Main configuration file for Solar Flux Reborn!\nTo implement custom panels, look for the custom_panels.js file!");
		
		var spc = cfgs.setupCategory("Solar Panels");
		
		LOOSE_ENERGY = spc.getElement(ConfiguredLib.DECIMAL, "Pickup Energy Loss")
				.withRange(DecimalValueRange.rangeClosed(0, 100))
				.withDefault(5)
				.withComment("How much energy (percent) will get lost while picking up the solar panel?")
				.getValue()
				.floatValue();
		
		for(int i = 0; i < CORE_PANELS.length; ++i)
		{
			var spsc = spc.setupSubCategory("Solar Panel " + (i + 1));
			
			var generation = spsc.getElement(ConfiguredLib.INT, "Generation Rate")
					.withRange(IntValueRange.rangeClosed(1, Long.MAX_VALUE))
					.withDefault(generations[i])
					.withComment("How much FE does this solar panel produce per tick?")
					.getValue()
					.longValue();
			
			var transfer = spsc.getElement(ConfiguredLib.INT, "Transfer Rate")
					.withRange(IntValueRange.rangeClosed(1, Long.MAX_VALUE))
					.withDefault(transfers[i])
					.withComment("How much FE does this solar panel emit to other blocks, per tick?")
					.getValue()
					.longValue();
			
			var capacity = spsc.getElement(ConfiguredLib.INT, "Capacity")
					.withRange(IntValueRange.rangeClosed(1, Long.MAX_VALUE))
					.withDefault(capacities[i])
					.withComment("How much FE does this solar panel store?")
					.getValue()
					.longValue();
			
			CORE_PANELS[i] = SolarPanel.builder()
					.name(Integer.toString(i + 1))
					.generation(generation)
					.transfer(transfer)
					.capacity(capacity)
					.buildAndRegister();
		}
		
		var main = cfgs.setupCategory("Main");
		
		RAIN_MULTIPLIER = spc.getElement(ConfiguredLib.DECIMAL, "Rain Multiplier")
				.withRange(DecimalValueRange.rangeClosed(0, 1))
				.withDefault(0.6F)
				.withComment("How much energy should be generated when it is raining? 0 - nothing, 1 - full power.")
				.getValue()
				.floatValue();
		
		THUNDER_MULTIPLIER = spc.getElement(ConfiguredLib.DECIMAL, "Thunder Multiplier")
				.withRange(DecimalValueRange.rangeClosed(0, 1))
				.withDefault(0.4F)
				.withComment("How much energy should be generated when it is thundering? 0 - nothing, 1 - full power.")
				.getValue()
				.floatValue();
		
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
		var panels = ConfiguredLib.create(new File(CONFIG_DIR, "panels.cfg"), true);
		
		listPanels().forEach(i ->
		{
			ConfigCategory cat;
			if(i.isCustom) cat = panels.setupCategory("Solar Flux: Custom");
			else if(i.getCompatMod() == null) cat = panels.setupCategory("Solar Flux");
			else
				cat = panels.setupCategory(
						ModList.get().getMods()
								.stream()
								.filter(m -> m.getModId().equals(i.getCompatMod()))
								.findFirst()
								.map(IModInfo::getDisplayName)
								.orElse("Unknown")
				);
			i.configureBase(cat.setupSubCategory(i.name));
		});
		
		if(panels.hasChanged()) panels.save();
		
		refreshRecipes();
	}
	
	public static void refreshRecipes()
	{
		ENABLED_RECIPES.clear();
		
		var recipes = ConfiguredLib.create(new File(CONFIG_DIR, "recipes.cfg"), true);
		var cat = recipes.setupCategory("recipes");
		RECIPE_KEYS.forEach(recipe ->
		{
			boolean enabled = cat.getElement(ConfiguredLib.BOOLEAN, recipe.toString())
					.withDefault(true)
					.withComment("Enable this recipe?")
					.getValue();
			
			if(enabled)
				ENABLED_RECIPES.add(recipe);
		});
		if(recipes.hasChanged())
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