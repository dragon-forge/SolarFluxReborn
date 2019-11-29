package tk.zeitheron.solarflux.panels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Stream;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.block.SolarPanelBlock;
import tk.zeitheron.solarflux.block.SolarPanelTile;
import tk.zeitheron.solarflux.shaded.hammerlib.cfg.ConfigEntryCategory;
import tk.zeitheron.solarflux.shaded.hammerlib.cfg.Configuration;

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
	
	public static void init()
	{
		File solarflux = CONFIG_DIR = new File(FMLPaths.CONFIGDIR.get().toFile(), "solarflux");
		
		if(!solarflux.isDirectory())
			solarflux.mkdirs();
		
		int[] generations = new int[] { 1, 8, 32, 128, 512, 2048, 8192, 32768 };
		int[] transfers = new int[] { 8, 64, 256, 1024, 4096, 16348, 65536, 262144 };
		int[] capacities = new int[] { 25000, 125000, 425000, 2000000, 8000000, 32000000, 64000000, 128000000 };
		
		Configuration cfgs = new Configuration(new File(solarflux, "main.hlc"));
		
		cfgs.setComment("Main configurration file fur Solar Flux Reborn!\nTo implement custom panels, look for the custom_panels.js file!");
		
		ConfigEntryCategory spc = cfgs.getCategory("Solar Panels");
		
		for(int i = 0; i < CORE_PANELS.length; ++i)
		{
			ConfigEntryCategory spsc = spc.getCategory("Solar Panel " + (i + 1));
			
			long gen = spsc.getLongEntry("Generation Rate", generations[i], 1, Long.MAX_VALUE).getValue();
			long transfer = spsc.getLongEntry("Transfer Rate", transfers[i], 1, Long.MAX_VALUE).getValue();
			long capacity = spsc.getLongEntry("Capacity", capacities[i], 1, Long.MAX_VALUE).getValue();
			
			CORE_PANELS[i] = SolarPanel.builder().name(Integer.toString(i + 1)).generation(gen).transfer(transfer).capacity(capacity).buildAndRegister();
		}
		
		if(cfgs.hasChanged())
			cfgs.save();
		
		File textures = new File(solarflux, "textures");
		if(!textures.isDirectory())
		{
			textures.mkdirs();
			
			int r;
			byte[] buf = new byte[768];
			
			try(FileOutputStream out = new FileOutputStream(new File(textures, "example_base.png"));InputStream in = SolarFlux.class.getResourceAsStream("/assets/solarflux/textures/blocks/sp_example_base.png"))
			{
				while((r = in.read(buf)) > 0)
					out.write(buf, 0, r);
			} catch(IOException e)
			{
				throw new RuntimeException(e);
			}
			
			try(FileOutputStream out = new FileOutputStream(new File(textures, "example_top.png"));InputStream in = SolarFlux.class.getResourceAsStream("/assets/solarflux/textures/blocks/sp_example_top.png"))
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
				fos.write(Base64.getMimeDecoder().decode("LyoNCiogVGhpcyBKYXZhU2NyaXB0IGZpbGUgY2FuIGJlIHVzZWQgdG8gaW5pdGlhbGl6ZSB5b3VyIG93biBzb2xhciBwYW5lbHMuDQoqIEZpcnN0IG9mZiwgYWxsIG1ldGhvZHMgaGF2ZSByZXR1cm4gdHlwZXMgKHRoZXkgYXJlIHNwZWNpZmllZCBhZnRlciB0aGUgIj0+IikNCiogSG93LXRvOiAob3Igd2F0Y2ggdGhlIHR1dG9yaWFsIGh0dHBzOi8veW91dHUuYmUvV1ZyNi0zRTdsQTggOzMpDQoqIDEuIFRvIGNyZWF0ZSBhIG5ldyBwYW5lbCwgeW91IG5lZWQgdG8gbWFrZSBhIGJ1aWxkZXIsIGNhbGwgcGFuZWwoKT0+U29sYXJQYW5lbEJ1aWxkZXIgdG8gYmVnaW4gdGhlIGJ1aWxkZXIgY2hhaW4uDQoqIDIuIENoYWluIGVsZW1lbnRzOg0KKiAgICAtIC5uYW1lKCJ5b3VybmFtZSIpPT5Tb2xhclBhbmVsQnVpbGRlciAvLyBtYW5kYXRvcnkNCiogICAgLSAuaGVpZ2h0KGZsb2F0KT0+U29sYXJQYW5lbEJ1aWxkZXIgLy8gb3B0aW9uYWwsIGZsb2F0IHZhbHVlIGlzIGJldHdlZW4gWzA7MV0NCiogICAgLSAuZ2VuZXJhdGlvbigiYW1vdW50Iik9PlNvbGFyUGFuZWxCdWlsZGVyIC8vIG1hbmRhdG9yeSwgcGFzcyB0aGUgbnVtYmVyIGFzIGEgc3RyaW5nDQoqICAgIC0gLmNhcGFjaXR5KCJhbW91bnQiKT0+U29sYXJQYW5lbEJ1aWxkZXIgLy8gbWFuZGF0b3J5LCBwYXNzIHRoZSBudW1iZXIgYXMgYSBzdHJpbmcNCiogICAgLSAudHJhbnNmZXIoImFtb3VudCIpPT5Tb2xhclBhbmVsQnVpbGRlciAvLyBtYW5kYXRvcnksIHBhc3MgdGhlIG51bWJlciBhcyBhIHN0cmluZw0KKiAzLiBBdCB0aGUgZW5kIG9mIHRoZSBjaGFpbiwgY2FsbCAuYnVpbGQoKT0+U29sYXJQYW5lbCAoYWx0ZXJuYXRpdmVseSwgLmJ1aWxkQW5kUmVnaXN0ZXIoKT0+U29sYXJQYW5lbCwgdG8gc2tpcCBzdGVwICM1KQ0KKiA0LiBMYW5ndWFnZXM6IGNhbGwgYWZ0ZXIgYnVpbGQgY2hhaW4gZW5kIChvcGVyYXRlIG9uIHBhbmVsKSwgc3RhcnQgbGFuZ3VhZ2UgY2hhaW4gd2l0aCAubGFuZ0J1aWxkZXIoKT0+TGFuZ3VhZ2VCdWlsZGVyDQoqICAgIC0gLnB1dCgiZW5fdXMiLCAiWW91ciBTb2xhciBQYW5lbCBOYW1lIik9Pkxhbmd1YWdlQnVpbGRlcg0KKiAgICBBZnRlciB0aGF0LCBjYWxsIGFzIG1hbnkgbGFuZyBhc3NpZ25zIGFzIHlvdSB3YW50Og0KKiAgICAtIC5wdXQoImxhbmciLCAiWW91ciBTb2xhciBQYW5lbCBOYW1lIik9Pkxhbmd1YWdlQnVpbGRlcg0KKiAgICBFbmQgY2hhaW4gd2l0aCAuYnVpbGQoKT0+U29sYXJQYW5lbA0KKiA1LiBSZWNpcGVzOiBjYWxsIGFmdGVyIGJ1aWxkIGNoYWluIGVuZCAob3BlcmF0ZSBvbiBwYW5lbCksIHN0YXJ0IHJlY2lwZSBjaGFpbiB3aXRoIC5yZWNpcGVCdWlsZGVyKCk9PlJlY2lwZUJ1aWxkZXINCiogICAgLSAuc2hhcGUoc3RyaW5nLi4uKT0+UmVjaXBlQnVpbGRlciAvLyBTcGVjaWZ5IHRoZSBuZWVkZWQgc3RyaW5nIGFtb3VudCAoMSBzdHJpbmcgPSAxIHJvdykNCiogICAgQWZ0ZXIgeW91IHNwZWNpZmllZCB0aGUgcmVjaXBlIHNoYXBlLCBiaW5kIGFsbCBpbmdyZWRpZW50czoNCiogICAgLSAuYmluZCgnYycsIGl0ZW0oIm1vZGlkIiwgIml0ZW1fbmFtZSIpKT0+UmVjaXBlQnVpbGRlcg0KKiAgICBFbmQgY2hhaW4gd2l0aCAuYnVpbGQoQU1PVU5UKT0+U29sYXJQYW5lbCAvLyBBTU9VTlQgaXMgdGhlIGludCB2YWx1ZSAoMDs2NF0gb2YgaXRlbXMgaW4gdGhlIHJlY2lwZSBvdXRwdXQsIGlmIG9taXR0ZWQsIHdpbGwgYmUgZGVmYXVsdGVkIHRvIDEuDQoqIDYuIFRvIHJlZ2lzdGVyIHRoZSBwYW5lbCwgYXBwZW5kIC5yZWdpc3RlcigpPT5Tb2xhclBhbmVsIGFmdGVyIGVuZGluZyB0aGUgY2hhaW4uDQoqIDcuIFRleHR1cmluZzogKGFsbCB0ZXh0dXJlcyBhcmUgc3RvcmVkIGluICJ0ZXh0dXJlcyIgZm9sZGVyKQ0KKiAgICAgICJ5b3VybmFtZV9iYXNlLnBuZyIsIG9wdGlvbmFsbHkgd2l0aCAieW91cm5hbWVfYmFzZS5tY21ldGEiIChmb3IgYW5pbWF0aW9ucykNCiogICAgICAieW91cm5hbWVfdG9wLnBuZyIsIG9wdGlvbmFsbHkgd2l0aCAieW91cm5hbWVfdG9wLm1jbWV0YSIgKGZvciBhbmltYXRpb25zKQ0KKiANCiogQWRkaXRpb25hbCBtZXRob2RzICYgZmVhdHVyZXM6DQoqICAgLSBpc01vZExvYWRlZCgibW9kaWQiKT0+Ym9vbGVhbiAvLyByZXR1cm5zIGlmIHRoZSBzcGVjaWZpZWQgbW9kIGlzIGxvYWRlZC4gQ291bGQgYmUgdXNlZnVsIGZvciBzZXR0aW5nIHVwIG1vZC1kZXBlbmRlbnQgc29sYXIgcGFuZWxzLg0KKiAgIC0geW91IGNhbiBoYXZlIGEgbGluZSAiaW1wb3J0IHBhdGgudG8uQ2xhc3M7IiAgdG8gYXZvaWQgdXNpbmcgSmF2YS50eXBlKCJwYXRoLnRvLkNsYXNzIikgc3R1ZmYuIENyZWF0ZWQgb3V0c2lkZSBvZiBhbnkgZnVuY3Rpb25zLCBkZWNsYXJlcyBhIG5ldyB2YXJpYWJsZSB3aXRoIHRoZSBzaW1wbGUgY2xhc3MgbmFtZS4NCiovDQoNCi8qKiBUaGlzIGZ1bmN0aW9uIGlzIGNhbGxlZCB3aGVuIG1vZCBpcyBiZWluZyBjb25zdHJ1Y3RlZCAqLw0KZnVuY3Rpb24gaW5pdCgpDQp7DQoJLy8gRXhhbXBsZTogKHRleHR1cmVzIGFyZSBleHRyYWN0ZWQgaW4gL3RleHR1cmVzLyBieSBkZWZhdWx0KSwgdW5jb21tZW50IHRvIHRyeSBpdCBvdXQhIChSZXF1aXJlcyBnYW1lIHJlc3RhcnQpDQoJLyoNCglwYW5lbCgpDQoJCS5uYW1lKCJleGFtcGxlIikNCgkJLmhlaWdodCg4IC8gMTYuMCkNCgkJLmdlbmVyYXRpb24oIjgzODg2MDgiKQ0KCQkuY2FwYWNpdHkoIjMzNTU0NDMyMDAiKQ0KCQkudHJhbnNmZXIoIjUwMzMxNjQ4IikNCgkuYnVpbGRBbmRSZWdpc3RlcigpDQoJCS5sYW5nQnVpbGRlcigpDQoJCQkucHV0KCJlbl91cyIsICJFeGFtcGxlIFNvbGFyIFBhbmVsIikNCgkJCS5idWlsZCgpDQoJCS5yZWNpcGVCdWlsZGVyKCkNCgkJCS5zaGFwZSgicHBwIiwgIjhjOCIsICI4aDgiKQ0KCQkJLmJpbmQoJ3AnLCBpdGVtKCJzb2xhcmZsdXgiLCAicGhvdG92b2x0YWljX2NlbGxfNiIpKQ0KCQkJLmJpbmQoJzgnLCBpdGVtKCJzb2xhcmZsdXg6c3BfOCIpKQ0KCQkJLmJpbmQoJ2MnLCBpdGVtKCJjaG9ydXNfZnJ1aXQiKSkNCgkJCS5iaW5kKCdoJywgaXRlbSgiZHJhZ29uX2hlYWQiKSkNCgkJLmJ1aWxkKDIpOw0KCSovDQp9"));
			} catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		
		try
		{
			StringBuilder content = new StringBuilder();
			Files.readAllLines(custom_panels.toPath(), StandardCharsets.UTF_8).forEach(ln ->
			{
				if(ln.startsWith("import ") && ln.endsWith(";"))
				{
					String clazz = ln.substring(7, ln.length() - 1);
					ln = "var " + clazz.substring(clazz.lastIndexOf('.') + 1) + " = Java.type(\"" + clazz + "\");";
				}
				content.append(ln + "\n");
			});
			
			ScriptEngine se = newEngine();
			Invocable inv = (Invocable) se;
			se.eval(content.toString());
			inv.invokeFunction("init");
		} catch(IOException | ScriptException | ReflectiveOperationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public static ScriptEngine newEngine()
	{
		ScriptEngine se = new ScriptEngineManager(null).getEngineByName("Nashorn");
		
		try
		{
			se.put("panel", se.eval("function(){return Java.type('" + SolarPanel.class.getName() + "').customBuilder();}"));
			se.put("item", se.eval("function(mod, id){var js=Java.type('" + JSHelper.class.getName() + "');if(!id){return js.item(mod);}else{return js.item(mod,id);}}"));
			se.put("isModLoaded", se.eval("function(mod){return Java.type('" + ModList.class.getName() + "').get().isLoaded(mod);}"));
		} catch(ScriptException e)
		{
			e.printStackTrace();
		}
		
		return se;
	}
	
	public static Ingredient getGeneratingSolars(long generation)
	{
		return Ingredient.fromItemListStream(listPanels().filter(sp -> sp.delegateData.generation == generation).map(SolarPanel::getBlock).map(ItemStack::new).map(Ingredient.SingleItemList::new));
	}
}