package tk.zeitheron.solarflux.panels;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
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
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.loading.FMLPaths;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.block.SolarPanelBlock;
import tk.zeitheron.solarflux.block.SolarPanelTile;
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
		
		if(cfgs.hasChanged())
			cfgs.save();
		
		for(int i = 0; i < CORE_PANELS.length; ++i)
			CORE_PANELS[i] = SolarPanel.builder().name(Integer.toString(i + 1)).generation(generations[i]).transfer(transfers[i]).capacity(capacities[i]).buildAndRegister();
		
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
				fos.write(Base64.getMimeDecoder().decode("LyoNCiogVGhpcyBKYXZhU2NyaXB0IGZpbGUgY2FuIGJlIHVzZWQgdG8gaW5pdGlhbGl6ZSB5b3VyIG93biBzb2xhciBwYW5lbHMuDQoqIEZpcnN0IG9mZiwgYWxsIG1ldGhvZHMgaGF2ZSByZXR1cm4gdHlwZXMgKHRoZXkgYXJlIHNwZWNpZmllZCBhZnRlciB0aGUgIj0+IikNCiogSG93LXRvOg0KKiAxLiBUbyBjcmVhdGUgYSBuZXcgcGFuZWwsIHlvdSBuZWVkIHRvIG1ha2UgYSBidWlsZGVyLCBjYWxsICAgcGFuZWwoKT0+U29sYXJQYW5lbEJ1aWxkZXIgICB0byBiZWdpbiB0aGUgYnVpbGRlciBjaGFpbi4NCiogMi4gQ2hhaW4gZWxlbWVudHM6DQoqICAgIC0gLm5hbWUoInlvdXJuYW1lIik9PlNvbGFyUGFuZWxCdWlsZGVyIC8vIG1hbmRhdG9yeQ0KKiAgICAtIC5oZWlnaHQoZmxvYXQpPT5Tb2xhclBhbmVsQnVpbGRlciAvLyBvcHRpb25hbCwgZmxvYXQgdmFsdWUgaXMgYmV0d2VlbiBbMDsxXQ0KKiAgICAtIC5nZW5lcmF0aW9uKCJhbW91bnQiKT0+U29sYXJQYW5lbEJ1aWxkZXIgLy8gbWFuZGF0b3J5LCBwYXNzIHRoZSBudW1iZXIgYXMgYSBzdHJpbmcNCiogICAgLSAuY2FwYWNpdHkoImFtb3VudCIpPT5Tb2xhclBhbmVsQnVpbGRlciAvLyBtYW5kYXRvcnksIHBhc3MgdGhlIG51bWJlciBhcyBhIHN0cmluZw0KKiAgICAtIC50cmFuc2ZlcigiYW1vdW50Iik9PlNvbGFyUGFuZWxCdWlsZGVyIC8vIG1hbmRhdG9yeSwgcGFzcyB0aGUgbnVtYmVyIGFzIGEgc3RyaW5nDQoqIDMuIEF0IHRoZSBlbmQgb2YgdGhlIGNoYWluLCBjYWxsIC5idWlsZCgpPT5Tb2xhclBhbmVsIChhbHRlcm5hdGl2ZWx5LCAuYnVpbGRBbmRSZWdpc3RlcigpPT5Tb2xhclBhbmVsLCB0byBza2lwIHN0ZXAgIzUpDQoqIDQuIExhbmd1YWdlczogY2FsbCBhZnRlciBidWlsZCBjaGFpbiBlbmQgKG9wZXJhdGUgb24gcGFuZWwpLCBzdGFydCBsYW5ndWFnZSBjaGFpbiB3aXRoIC5sYW5nQnVpbGRlcigpPT5MYW5ndWFnZUJ1aWxkZXINCiogICAgLSAucHV0KCJlbl91cyIsICJZb3VyIFNvbGFyIFBhbmVsIE5hbWUiKT0+TGFuZ3VhZ2VCdWlsZGVyDQoqICAgIEFmdGVyIHRoYXQsIGNhbGwgYXMgbWFueSBsYW5nIGFzc2lnbnMgYXMgeW91IHdhbnQ6DQoqICAgIC0gLnB1dCgibGFuZyIsICJZb3VyIFNvbGFyIFBhbmVsIE5hbWUiKT0+TGFuZ3VhZ2VCdWlsZGVyDQoqICAgIEVuZCBjaGFpbiB3aXRoIC5idWlsZCgpPT5Tb2xhclBhbmVsDQoqIDUuIFRvIHJlZ2lzdGVyIHRoZSBwYW5lbCwgYXBwZW5kIC5yZWdpc3RlcigpPT5Tb2xhclBhbmVsIGFmdGVyIGVuZGluZyB0aGUgY2hhaW4uDQoqIDYuIFRleHR1cmluZzogKGFsbCB0ZXh0dXJlcyBhcmUgc3RvcmVkIGluICJ0ZXh0dXJlcyIgZm9sZGVyKQ0KKiAgICAgICJ5b3VybmFtZV9iYXNlLnBuZyIsIG9wdGlvbmFsbHkgd2l0aCAieW91cm5hbWVfYmFzZS5tY21ldGEiIChmb3IgYW5pbWF0aW9ucykNCiogICAgICAieW91cm5hbWVfdG9wLnBuZyIsIG9wdGlvbmFsbHkgd2l0aCAieW91cm5hbWVfdG9wLm1jbWV0YSIgKGZvciBhbmltYXRpb25zKQ0KKi8NCg0KLyoqIFRoaXMgZnVuY3Rpb24gaXMgY2FsbGVkIHdoZW4gbW9kIGlzIGJlaW5nIGNvbnN0cnVjdGVkICovDQpmdW5jdGlvbiBpbml0KCkNCnsNCgkvLyBFeGFtcGxlOiAodGV4dHVyZXMgYXJlIGV4dHJhY3RlZCBpbiAvdGV4dHVyZXMvIGJ5IGRlZmF1bHQpLCB1bmNvbW1lbnQgdG8gdHJ5IGl0IG91dCEgKFJlcXVpcmVzIGdhbWUgcmVzdGFydCkNCgkvLyB2YXIgZXhhbXBsZVNvbGFyUGFuZWwgPSBwYW5lbCgpLm5hbWUoImV4YW1wbGUiKS5oZWlnaHQoMC41KS5nZW5lcmF0aW9uKCI4Mzg4NjA4IikuY2FwYWNpdHkoIjMzNTU0NDMyMDAiKS50cmFuc2ZlcigiNTAzMzE2NDgiKS5idWlsZEFuZFJlZ2lzdGVyKCkubGFuZ0J1aWxkZXIoKS5wdXQoImVuX3VzIiwgIkV4YW1wbGUgU29sYXIgUGFuZWwiKS5idWlsZCgpOw0KfQ"));
			} catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		
		try(Reader isr = new InputStreamReader(new FileInputStream(custom_panels), StandardCharsets.UTF_8))
		{
			ScriptEngine se = newEngine();
			Invocable inv = (Invocable) se;
			se.eval(isr);
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
			se.put("panel", se.eval("function() { return Java.type('" + SolarPanel.class.getName() + "').customBuilder(); }"));
		} catch(ScriptException e)
		{
			e.printStackTrace();
		}
		
		return se;
	}
}