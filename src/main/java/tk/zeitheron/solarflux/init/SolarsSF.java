package tk.zeitheron.solarflux.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.api.SolarFluxAPI;
import tk.zeitheron.solarflux.api.SolarInfo;
import tk.zeitheron.solarflux.api.SolarScriptEngine;
import tk.zeitheron.solarflux.block.BlockBaseSolar;
import tk.zeitheron.solarflux.block.ItemBlockBaseSolar;
import tk.zeitheron.solarflux.shaded.hammerlib.cfg.ConfigEntryCategory;
import tk.zeitheron.solarflux.shaded.hammerlib.cfg.Configuration;

import javax.script.ScriptException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

public class SolarsSF
{
	public static final SolarInfo[] CORE_PANELS = new SolarInfo[8];

	private static File CONFIG_DIR;
	public static double LOOSE_ENERGY;

	public static File getConfigDir()
	{
		return CONFIG_DIR;
	}

	public static Ingredient getGeneratingSolars(long gen)
	{
		return Ingredient.fromStacks(
				SolarFluxAPI.SOLAR_PANELS.getValuesCollection()
						.stream()
						.filter(s -> s.getGeneration() == gen)
						.map(SolarInfo::getBlock)
						.map(ItemStack::new)
						.toArray(ItemStack[]::new));
	}

	public static final List<SolarInfo> modSolars = new ArrayList<>();

	public static void preInit(File solarflux)
	{
		CONFIG_DIR = solarflux;
		if(!CONFIG_DIR.isDirectory()) CONFIG_DIR.mkdirs();

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
				16384,
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

		IForgeRegistry<Block> blocks = ForgeRegistries.BLOCKS;
		IForgeRegistry<Item> items = ForgeRegistries.ITEMS;
		IForgeRegistry<SolarInfo> solars = SolarFluxAPI.SOLAR_PANELS;

		Configuration cfgs = new Configuration(new File(solarflux, "main.hlc"));
		cfgs.setComment("Main configuration file fur Solar Flux Reborn!\nTo implement custom panels, look for the custom_panels.js file!");
		ConfigEntryCategory spc = cfgs.getCategory("Solar Panels");
		LOOSE_ENERGY = spc.getFloatEntry("Pickup Energy Loss", 5, 0, 100).setDescription("How much energy (percent) will get lost while picking up the solar panel?").getValue();
		for(int i = 0; i < CORE_PANELS.length; ++i)
		{
			long gen = generations[i];
			long transfer = transfers[i];
			long capacity = capacities[i];
			CORE_PANELS[i] = SolarInfo.builder().name(Integer.toString(i + 1)).generation(gen).transfer(transfer).capacity(capacity).buildAndRegister();
		}
		if(cfgs.hasChanged())
			cfgs.save();

		File textures = new File(CONFIG_DIR, "textures");
		if(!textures.isDirectory())
		{
			textures.mkdirs();

			int r;
			byte[] buf = new byte[768];

			try(FileOutputStream out = new FileOutputStream(new File(textures, "example_base.png")); InputStream in = SolarFlux.class.getResourceAsStream("/assets/solarflux/textures/blocks/solar_panel_example_base.png"))
			{
				while((r = in.read(buf)) > 0)
					out.write(buf, 0, r);
			} catch(IOException e)
			{
				throw new RuntimeException(e);
			}

			try(FileOutputStream out = new FileOutputStream(new File(textures, "example_top.png")); InputStream in = SolarFlux.class.getResourceAsStream("/assets/solarflux/textures/blocks/solar_panel_example_top.png"))
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
				fos.write(Base64.getMimeDecoder().decode("LyoNCiogVGhpcyBKYXZhU2NyaXB0IGZpbGUgY2FuIGJlIHVzZWQgdG8gaW5pdGlhbGl6ZSB5b3VyIG93biBzb2xhciBwYW5lbHMuDQoqIEZpcnN0IG9mZiwgYWxsIG1ldGhvZHMgaGF2ZSByZXR1cm4gdHlwZXMgKHRoZXkgYXJlIHNwZWNpZmllZCBhZnRlciB0aGUgIj0+IikNCiogSG93LXRvOiAob3Igd2F0Y2ggdGhlIHR1dG9yaWFsIGh0dHBzOi8veW91dHUuYmUvV1ZyNi0zRTdsQTggOzMpDQoqIDEuIFRvIGNyZWF0ZSBhIG5ldyBwYW5lbCwgeW91IG5lZWQgdG8gbWFrZSBhIGJ1aWxkZXIsIGNhbGwgcGFuZWwoKT0+U29sYXJQYW5lbEJ1aWxkZXIgdG8gYmVnaW4gdGhlIGJ1aWxkZXIgY2hhaW4uDQoqIDIuIENoYWluIGVsZW1lbnRzOg0KKiAgICAtIC5uYW1lKCJ5b3VybmFtZSIpPT5Tb2xhclBhbmVsQnVpbGRlciAvLyBtYW5kYXRvcnkNCiogICAgLSAuaGVpZ2h0KGZsb2F0KT0+U29sYXJQYW5lbEJ1aWxkZXIgLy8gb3B0aW9uYWwsIGZsb2F0IHZhbHVlIGlzIGJldHdlZW4gWzA7MV0NCiogICAgLSAuZ2VuZXJhdGlvbigiYW1vdW50Iik9PlNvbGFyUGFuZWxCdWlsZGVyIC8vIG1hbmRhdG9yeSwgcGFzcyB0aGUgbnVtYmVyIGFzIGEgc3RyaW5nDQoqICAgIC0gLmNhcGFjaXR5KCJhbW91bnQiKT0+U29sYXJQYW5lbEJ1aWxkZXIgLy8gbWFuZGF0b3J5LCBwYXNzIHRoZSBudW1iZXIgYXMgYSBzdHJpbmcNCiogICAgLSAudHJhbnNmZXIoImFtb3VudCIpPT5Tb2xhclBhbmVsQnVpbGRlciAvLyBtYW5kYXRvcnksIHBhc3MgdGhlIG51bWJlciBhcyBhIHN0cmluZw0KKiAzLiBBdCB0aGUgZW5kIG9mIHRoZSBjaGFpbiwgY2FsbCAuYnVpbGQoKT0+U29sYXJQYW5lbCAoYWx0ZXJuYXRpdmVseSwgLmJ1aWxkQW5kUmVnaXN0ZXIoKT0+U29sYXJQYW5lbCwgdG8gc2tpcCBzdGVwICM1KQ0KKiA0LiBMYW5ndWFnZXM6IGNhbGwgYWZ0ZXIgYnVpbGQgY2hhaW4gZW5kIChvcGVyYXRlIG9uIHBhbmVsKSwgc3RhcnQgbGFuZ3VhZ2UgY2hhaW4gd2l0aCAubGFuZ0J1aWxkZXIoKT0+TGFuZ3VhZ2VCdWlsZGVyDQoqICAgIC0gLnB1dCgiZW5fdXMiLCAiWW91ciBTb2xhciBQYW5lbCBOYW1lIik9Pkxhbmd1YWdlQnVpbGRlcg0KKiAgICBBZnRlciB0aGF0LCBjYWxsIGFzIG1hbnkgbGFuZyBhc3NpZ25zIGFzIHlvdSB3YW50Og0KKiAgICAtIC5wdXQoImxhbmciLCAiWW91ciBTb2xhciBQYW5lbCBOYW1lIik9Pkxhbmd1YWdlQnVpbGRlcg0KKiAgICBFbmQgY2hhaW4gd2l0aCAuYnVpbGQoKT0+U29sYXJQYW5lbA0KKiA1LiBSZWNpcGVzOiBjYWxsIGFmdGVyIGJ1aWxkIGNoYWluIGVuZCAob3BlcmF0ZSBvbiBwYW5lbCksIHN0YXJ0IHJlY2lwZSBjaGFpbiB3aXRoIC5yZWNpcGVCdWlsZGVyKCk9PlJlY2lwZUJ1aWxkZXINCiogICAgLSAuc2hhcGUoc3RyaW5nLi4uKT0+UmVjaXBlQnVpbGRlciAvLyBTcGVjaWZ5IHRoZSBuZWVkZWQgc3RyaW5nIGFtb3VudCAoMSBzdHJpbmcgPSAxIHJvdykNCiogICAgQWZ0ZXIgeW91IHNwZWNpZmllZCB0aGUgcmVjaXBlIHNoYXBlLCBiaW5kIGFsbCBpbmdyZWRpZW50czoNCiogICAgLSAuYmluZCgnYycsIGl0ZW0oIm1vZGlkIiwgIml0ZW1fbmFtZSIpKT0+UmVjaXBlQnVpbGRlcg0KKiAgICBFbmQgY2hhaW4gd2l0aCAuYnVpbGQoQU1PVU5UKT0+U29sYXJQYW5lbCAvLyBBTU9VTlQgaXMgdGhlIGludCB2YWx1ZSAoMDs2NF0gb2YgaXRlbXMgaW4gdGhlIHJlY2lwZSBvdXRwdXQsIGlmIG9taXR0ZWQsIHdpbGwgYmUgZGVmYXVsdGVkIHRvIDEuDQoqIDYuIFRvIHJlZ2lzdGVyIHRoZSBwYW5lbCwgYXBwZW5kIC5yZWdpc3RlcigpPT5Tb2xhclBhbmVsIGFmdGVyIGVuZGluZyB0aGUgY2hhaW4uDQoqIDcuIFRleHR1cmluZzogKGFsbCB0ZXh0dXJlcyBhcmUgc3RvcmVkIGluICJ0ZXh0dXJlcyIgZm9sZGVyKQ0KKiAgICAgICJ5b3VybmFtZV9iYXNlLnBuZyIsIG9wdGlvbmFsbHkgd2l0aCAieW91cm5hbWVfYmFzZS5tY21ldGEiIChmb3IgYW5pbWF0aW9ucykNCiogICAgICAieW91cm5hbWVfdG9wLnBuZyIsIG9wdGlvbmFsbHkgd2l0aCAieW91cm5hbWVfdG9wLm1jbWV0YSIgKGZvciBhbmltYXRpb25zKQ0KKiANCiogQWRkaXRpb25hbCBtZXRob2RzICYgZmVhdHVyZXM6DQoqICAgLSBpc01vZExvYWRlZCgibW9kaWQiKT0+Ym9vbGVhbiAvLyByZXR1cm5zIGlmIHRoZSBzcGVjaWZpZWQgbW9kIGlzIGxvYWRlZC4gQ291bGQgYmUgdXNlZnVsIGZvciBzZXR0aW5nIHVwIG1vZC1kZXBlbmRlbnQgc29sYXIgcGFuZWxzLg0KKiAgIC0geW91IGNhbiBoYXZlIGEgbGluZSAiaW1wb3J0IHBhdGgudG8uQ2xhc3M7IiAgdG8gYXZvaWQgdXNpbmcgSmF2YS50eXBlKCJwYXRoLnRvLkNsYXNzIikgc3R1ZmYuIENyZWF0ZWQgb3V0c2lkZSBvZiBhbnkgZnVuY3Rpb25zLCBkZWNsYXJlcyBhIG5ldyB2YXJpYWJsZSB3aXRoIHRoZSBzaW1wbGUgY2xhc3MgbmFtZS4NCiogICAtIHlvdSBjYW4gaGF2ZSBhIGxpbmUgImRlZmluZSBhX2tleSAhdmFsdWUhIiB0byBtYWtlIHRoZSBjb21waWxlciByZXBsYWNlIGFsbCBhX2tleSB3aXRoICF2YWx1ZSEgYXQgcnVudGltZS4NCiovDQoNCmRlZmluZSBmdW5jIGZ1bmN0aW9uDQpkZWZpbmUgZW5nbGlzaCAiZW5fdXMiDQoNCi8qKiBUaGlzIGZ1bmN0aW9uIGlzIGNhbGxlZCB3aGVuIG1vZCBpcyBiZWluZyBjb25zdHJ1Y3RlZCAqLw0KZnVuYyBpbml0KCkNCnsNCgkvLyBFeGFtcGxlOiAodGV4dHVyZXMgYXJlIGV4dHJhY3RlZCBpbiAvdGV4dHVyZXMvIGJ5IGRlZmF1bHQpLCB1bmNvbW1lbnQgdG8gdHJ5IGl0IG91dCEgKFJlcXVpcmVzIGdhbWUgcmVzdGFydCkNCgkNCgkvKg0KCXBhbmVsKCkNCgkJLm5hbWUoImV4YW1wbGUiKQ0KCQkuaGVpZ2h0KDggLyAxNi4wKQ0KCQkuZ2VuZXJhdGlvbigiODM4ODYwOCIpDQoJCS5jYXBhY2l0eSgiMzM1NTQ0MzIwMCIpDQoJCS50cmFuc2ZlcigiNTAzMzE2NDgiKQ0KCS5idWlsZEFuZFJlZ2lzdGVyKCkNCgkJLmxhbmdCdWlsZGVyKCkNCgkJCS5wdXQoZW5nbGlzaCwgIkV4YW1wbGUgU29sYXIgUGFuZWwiKQ0KCQkJLmJ1aWxkKCkNCgkJLnJlY2lwZUJ1aWxkZXIoKQ0KCQkJLnNoYXBlKCJwcHAiLCAiOGM4IiwgIjhoOCIpDQoJCQkuYmluZCgncCcsIGl0ZW0oInNvbGFyZmx1eCIsICJwaG90b3ZvbHRhaWNfY2VsbF82IikpDQoJCQkuYmluZCgnOCcsIGl0ZW0oInNvbGFyZmx1eDpzb2xhcl9wYW5lbF84IikpDQoJCQkuYmluZCgnYycsIGl0ZW0oImNob3J1c19mcnVpdCIpKQ0KCQkJLmJpbmQoJ2gnLCBpdGVtKCJza3VsbCIsIDUpKQ0KCQkuYnVpbGQoMik7DQoJKi8NCn0"));
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

		for(SolarInfo si : modSolars)
			try
			{
				solars.register(si);
				BlockBaseSolar block = si.getBlock();
				blocks.register(block);
				Item ib = new ItemBlockBaseSolar(block);
				ib.setRegistryName(block.getRegistryName());
				ib.setCreativeTab(SolarFluxAPI.tab);
				items.register(ib);
				SolarFluxAPI.renderRenderer.accept(ib);
				SolarFlux.proxy.onPanelRegistered(si);
			} catch(Throwable err)
			{
				err.printStackTrace();
			}
	}

	public static void refreshConfigs()
	{
		Configuration panels = new Configuration(new File(SolarFlux.CONFIG_DIR, "panels.hlc"));

		SolarFluxAPI.SOLAR_PANELS.forEach(i ->
		{
			ConfigEntryCategory cat;
			if(i.isCustom) cat = panels.getCategory("Solar Flux: Custom");
			else if(i.getCompatMod() == null) cat = panels.getCategory("Solar Flux");
			else cat = panels.getCategory(Loader.instance().getIndexedModList().get(i.getCompatMod()).getName());
			i.configureBase(cat.getCategory(i.getRegistryName().toString()));
		});

		if(panels.hasChanged()) panels.save();
	}

	public static Collection<SolarInfo> listPanels()
	{
		return SolarFluxAPI.SOLAR_PANELS.getValuesCollection();
	}
}