package tk.zeitheron.solarflux.panels;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.block.SolarPanelBlock;
import tk.zeitheron.solarflux.block.SolarPanelTile;
import tk.zeitheron.solarflux.shaded.hammerlib.cfg.ConfigEntryCategory;
import tk.zeitheron.solarflux.shaded.hammerlib.cfg.Configuration;

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
	public static final TileEntityType<SolarPanelTile> SOLAR_PANEL_TYPE = new TileEntityType<SolarPanelTile>(SolarPanelTile::new, new HashSet<>(), null)
	{
		{
			setRegistryName("solarflux", "solar_panel");
		}

		@Override
		public boolean isValidBlock(Block blockIn)
		{
			return blockIn instanceof SolarPanelBlock;
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

		Configuration cfgs = new Configuration(new File(solarflux, "main.hlc"));

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

			int r;
			byte[] buf = new byte[768];

			try(FileOutputStream out = new FileOutputStream(new File(textures, "example_base.png")); InputStream in = SolarFlux.class.getResourceAsStream("/assets/solarflux/textures/blocks/sp_example_base.png"))
			{
				while((r = in.read(buf)) > 0)
					out.write(buf, 0, r);
			} catch(IOException e)
			{
				throw new RuntimeException(e);
			}

			try(FileOutputStream out = new FileOutputStream(new File(textures, "example_top.png")); InputStream in = SolarFlux.class.getResourceAsStream("/assets/solarflux/textures/blocks/sp_example_top.png"))
			{

				while((r = in.read(buf)) > 0)
					out.write(buf, 0, r);
			} catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}

		File custom_panels = new File(solarflux, "custom_panels.js");
		if(!custom_panels.isFile())
			try(FileOutputStream fos = new FileOutputStream(custom_panels))
			{
				fos.write(Base64.getMimeDecoder().decode("LyoNCiogVGhpcyBKYXZhU2NyaXB0IGZpbGUgY2FuIGJlIHVzZWQgdG8gaW5pdGlhbGl6ZSB5b3VyIG93biBzb2xhciBwYW5lbHMuDQoqIEZpcnN0IG9mZiwgYWxsIG1ldGhvZHMgaGF2ZSByZXR1cm4gdHlwZXMgKHRoZXkgYXJlIHNwZWNpZmllZCBhZnRlciB0aGUgIj0+IikNCiogSG93LXRvOiAob3Igd2F0Y2ggdGhlIHR1dG9yaWFsIGh0dHBzOi8veW91dHUuYmUvV1ZyNi0zRTdsQTggOzMpDQoqIDEuIFRvIGNyZWF0ZSBhIG5ldyBwYW5lbCwgeW91IG5lZWQgdG8gbWFrZSBhIGJ1aWxkZXIsIGNhbGwgcGFuZWwoKT0+U29sYXJQYW5lbEJ1aWxkZXIgdG8gYmVnaW4gdGhlIGJ1aWxkZXIgY2hhaW4uDQoqIDIuIENoYWluIGVsZW1lbnRzOg0KKiAgICAtIC5uYW1lKCJ5b3VybmFtZSIpPT5Tb2xhclBhbmVsQnVpbGRlciAvLyBtYW5kYXRvcnkNCiogICAgLSAuaGVpZ2h0KGZsb2F0KT0+U29sYXJQYW5lbEJ1aWxkZXIgLy8gb3B0aW9uYWwsIGZsb2F0IHZhbHVlIGlzIGJldHdlZW4gWzA7MV0NCiogICAgLSAuZ2VuZXJhdGlvbigiYW1vdW50Iik9PlNvbGFyUGFuZWxCdWlsZGVyIC8vIG1hbmRhdG9yeSwgcGFzcyB0aGUgbnVtYmVyIGFzIGEgc3RyaW5nDQoqICAgIC0gLmNhcGFjaXR5KCJhbW91bnQiKT0+U29sYXJQYW5lbEJ1aWxkZXIgLy8gbWFuZGF0b3J5LCBwYXNzIHRoZSBudW1iZXIgYXMgYSBzdHJpbmcNCiogICAgLSAudHJhbnNmZXIoImFtb3VudCIpPT5Tb2xhclBhbmVsQnVpbGRlciAvLyBtYW5kYXRvcnksIHBhc3MgdGhlIG51bWJlciBhcyBhIHN0cmluZw0KKiAzLiBBdCB0aGUgZW5kIG9mIHRoZSBjaGFpbiwgY2FsbCAuYnVpbGQoKT0+U29sYXJQYW5lbCAoYWx0ZXJuYXRpdmVseSwgLmJ1aWxkQW5kUmVnaXN0ZXIoKT0+U29sYXJQYW5lbCwgdG8gc2tpcCBzdGVwICM1KQ0KKiA0LiBMYW5ndWFnZXM6IGNhbGwgYWZ0ZXIgYnVpbGQgY2hhaW4gZW5kIChvcGVyYXRlIG9uIHBhbmVsKSwgc3RhcnQgbGFuZ3VhZ2UgY2hhaW4gd2l0aCAubGFuZ0J1aWxkZXIoKT0+TGFuZ3VhZ2VCdWlsZGVyDQoqICAgIC0gLnB1dCgiZW5fdXMiLCAiWW91ciBTb2xhciBQYW5lbCBOYW1lIik9Pkxhbmd1YWdlQnVpbGRlcg0KKiAgICBBZnRlciB0aGF0LCBjYWxsIGFzIG1hbnkgbGFuZyBhc3NpZ25zIGFzIHlvdSB3YW50Og0KKiAgICAtIC5wdXQoImxhbmciLCAiWW91ciBTb2xhciBQYW5lbCBOYW1lIik9Pkxhbmd1YWdlQnVpbGRlcg0KKiAgICBFbmQgY2hhaW4gd2l0aCAuYnVpbGQoKT0+U29sYXJQYW5lbA0KKiA1LiBSZWNpcGVzOiBjYWxsIGFmdGVyIGJ1aWxkIGNoYWluIGVuZCAob3BlcmF0ZSBvbiBwYW5lbCksIHN0YXJ0IHJlY2lwZSBjaGFpbiB3aXRoIC5yZWNpcGVCdWlsZGVyKCk9PlJlY2lwZUJ1aWxkZXINCiogICAgLSAuc2hhcGUoc3RyaW5nLi4uKT0+UmVjaXBlQnVpbGRlciAvLyBTcGVjaWZ5IHRoZSBuZWVkZWQgc3RyaW5nIGFtb3VudCAoMSBzdHJpbmcgPSAxIHJvdykNCiogICAgQWZ0ZXIgeW91IHNwZWNpZmllZCB0aGUgcmVjaXBlIHNoYXBlLCBiaW5kIGFsbCBpbmdyZWRpZW50czoNCiogICAgLSAuYmluZCgnYycsIGl0ZW0oIm1vZGlkIiwgIml0ZW1fbmFtZSIpKT0+UmVjaXBlQnVpbGRlcg0KKiAgICBFbmQgY2hhaW4gd2l0aCAuYnVpbGQoQU1PVU5UKT0+U29sYXJQYW5lbCAvLyBBTU9VTlQgaXMgdGhlIGludCB2YWx1ZSAoMDs2NF0gb2YgaXRlbXMgaW4gdGhlIHJlY2lwZSBvdXRwdXQsIGlmIG9taXR0ZWQsIHdpbGwgYmUgZGVmYXVsdGVkIHRvIDEuDQoqIDYuIFRvIHJlZ2lzdGVyIHRoZSBwYW5lbCwgYXBwZW5kIC5yZWdpc3RlcigpPT5Tb2xhclBhbmVsIGFmdGVyIGVuZGluZyB0aGUgY2hhaW4uDQoqIDcuIFRleHR1cmluZzogKGFsbCB0ZXh0dXJlcyBhcmUgc3RvcmVkIGluICJ0ZXh0dXJlcyIgZm9sZGVyKQ0KKiAgICAgICJ5b3VybmFtZV9iYXNlLnBuZyIsIG9wdGlvbmFsbHkgd2l0aCAieW91cm5hbWVfYmFzZS5tY21ldGEiIChmb3IgYW5pbWF0aW9ucykNCiogICAgICAieW91cm5hbWVfdG9wLnBuZyIsIG9wdGlvbmFsbHkgd2l0aCAieW91cm5hbWVfdG9wLm1jbWV0YSIgKGZvciBhbmltYXRpb25zKQ0KKiANCiogQWRkaXRpb25hbCBtZXRob2RzICYgZmVhdHVyZXM6DQoqICAgLSBpc01vZExvYWRlZCgibW9kaWQiKT0+Ym9vbGVhbiAvLyByZXR1cm5zIGlmIHRoZSBzcGVjaWZpZWQgbW9kIGlzIGxvYWRlZC4gQ291bGQgYmUgdXNlZnVsIGZvciBzZXR0aW5nIHVwIG1vZC1kZXBlbmRlbnQgc29sYXIgcGFuZWxzLg0KKiAgIC0geW91IGNhbiBoYXZlIGEgbGluZSAiaW1wb3J0IHBhdGgudG8uQ2xhc3M7IiAgdG8gYXZvaWQgdXNpbmcgSmF2YS50eXBlKCJwYXRoLnRvLkNsYXNzIikgc3R1ZmYuIENyZWF0ZWQgb3V0c2lkZSBvZiBhbnkgZnVuY3Rpb25zLCBkZWNsYXJlcyBhIG5ldyB2YXJpYWJsZSB3aXRoIHRoZSBzaW1wbGUgY2xhc3MgbmFtZS4NCiogICAtIHlvdSBjYW4gaGF2ZSBhIGxpbmUgImRlZmluZSBhX2tleSAhdmFsdWUhIiB0byBtYWtlIHRoZSBjb21waWxlciByZXBsYWNlIGFsbCBhX2tleSB3aXRoICF2YWx1ZSEgYXQgcnVudGltZS4NCiovDQoNCmRlZmluZSBmdW5jIGZ1bmN0aW9uDQpkZWZpbmUgZW5nbGlzaCAiZW5fdXMiDQoNCi8qKiBUaGlzIGZ1bmN0aW9uIGlzIGNhbGxlZCB3aGVuIG1vZCBpcyBiZWluZyBjb25zdHJ1Y3RlZCAqLw0KZnVuYyBpbml0KCkNCnsNCgkvLyBFeGFtcGxlOiAodGV4dHVyZXMgYXJlIGV4dHJhY3RlZCBpbiAvdGV4dHVyZXMvIGJ5IGRlZmF1bHQpLCB1bmNvbW1lbnQgdG8gdHJ5IGl0IG91dCEgKFJlcXVpcmVzIGdhbWUgcmVzdGFydCkNCgkvKg0KCXBhbmVsKCkNCgkJLm5hbWUoImV4YW1wbGUiKQ0KCQkuaGVpZ2h0KDggLyAxNi4wKQ0KCQkuZ2VuZXJhdGlvbigiODM4ODYwOCIpDQoJCS5jYXBhY2l0eSgiMzM1NTQ0MzIwMCIpDQoJCS50cmFuc2ZlcigiNTAzMzE2NDgiKQ0KCS5idWlsZEFuZFJlZ2lzdGVyKCkNCgkJLmxhbmdCdWlsZGVyKCkNCgkJCS5wdXQoZW5nbGlzaCwgIkV4YW1wbGUgU29sYXIgUGFuZWwiKQ0KCQkJLmJ1aWxkKCkNCgkJLnJlY2lwZUJ1aWxkZXIoKQ0KCQkJLnNoYXBlKCJwcHAiLCAiOGM4IiwgIjhoOCIpDQoJCQkuYmluZCgncCcsIGl0ZW0oInNvbGFyZmx1eCIsICJwaG90b3ZvbHRhaWNfY2VsbF82IikpDQoJCQkuYmluZCgnOCcsIGl0ZW0oInNvbGFyZmx1eDpzcF84IikpDQoJCQkuYmluZCgnYycsIGl0ZW0oImNob3J1c19mcnVpdCIpKQ0KCQkJLmJpbmQoJ2gnLCBpdGVtKCJkcmFnb25faGVhZCIpKQ0KCQkuYnVpbGQoMik7DQoJKi8NCn0"));
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

	public static void refreshConfigs()
	{
		Configuration panels = new Configuration(new File(CONFIG_DIR, "panels.hlc"));

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
		Configuration recipes = new Configuration(new File(CONFIG_DIR, "recipes.hlc"));
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
		return Ingredient.fromItemListStream(listPanels().filter(sp -> sp.delegateData.generation == generation).map(SolarPanel::getBlock).map(ItemStack::new).map(Ingredient.SingleItemList::new));
	}
}