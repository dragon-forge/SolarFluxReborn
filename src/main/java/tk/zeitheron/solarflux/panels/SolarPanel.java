package tk.zeitheron.solarflux.panels;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import tk.zeitheron.solarflux.block.SolarPanelBlock;

public class SolarPanel
{
	public final String name;
	public final SolarPanelData delegateData;
	public SolarPanelData networkData;
	
	public final boolean isCustom;
	
	private SolarPanelBlock block;
	
	private LanguageData langs;
	
	public SolarPanel(String name, SolarPanelData data, boolean isCustom)
	{
		this.delegateData = networkData = data;
		this.name = (isCustom ? "custom_" : "") + name;
		this.isCustom = isCustom;
	}
	
	@OnlyIn(Dist.CLIENT)
	public SolarPanelData getClientPanelData()
	{
		return networkData != null ? networkData : delegateData;
	}
	
	public SolarPanel register()
	{
		if(SolarPanels.PANELS.containsKey(name))
			throw new IllegalArgumentException("Solar panel with id " + name + " already exists.");
		SolarPanels.PANELS.put(name, this);
		return this;
	}
	
	protected SolarPanelBlock createBlock()
	{
		return new SolarPanelBlock(this);
	}
	
	public SolarPanelBlock getBlock()
	{
		if(block == null)
		{
			block = createBlock();
			block.setRegistryName("sp_" + name);
		}
		return block;
	}
	
	public static Builder builder()
	{
		return new Builder();
	}
	
	public static Builder customBuilder()
	{
		Builder b = new Builder();
		b.custom = true;
		return b;
	}
	
	public LanguageData langBuilder()
	{
		return new LanguageData(this);
	}
	
	public boolean hasLang()
	{
		return langs != null;
	}
	
	public LanguageData getLang()
	{
		return langs;
	}
	
	public static class Builder
	{
		String name;
		BigInteger generation, capacity, transfer;
		float height = 6 / 16F;
		boolean custom = false;
		
		public Builder name(String s)
		{
			this.name = s;
			return this;
		}
		
		public Builder height(float f)
		{
			this.height = f;
			return this;
		}
		
		public Builder generation(Number n)
		{
			if(n instanceof BigInteger)
			{
				this.generation = (BigInteger) n;
				return this;
			}
			return generation(Long.toString(n.longValue()));
		}
		
		public Builder generation(String s)
		{
			this.generation = new BigInteger(s);
			return this;
		}
		
		public Builder capacity(Number n)
		{
			if(n instanceof BigInteger)
			{
				this.capacity = (BigInteger) n;
				return this;
			}
			return capacity(Long.toString(n.longValue()));
		}
		
		public Builder capacity(String s)
		{
			this.capacity = new BigInteger(s);
			return this;
		}
		
		public Builder transfer(Number n)
		{
			if(n instanceof BigInteger)
			{
				this.transfer = (BigInteger) n;
				return this;
			}
			return transfer(Long.toString(n.longValue()));
		}
		
		public Builder transfer(String s)
		{
			this.transfer = new BigInteger(s);
			return this;
		}
		
		public SolarPanel build()
		{
			if(name == null)
				throw new NullPointerException("name == null");
			if(generation == null)
				throw new NullPointerException("generation == null");
			if(capacity == null)
				throw new NullPointerException("capacity == null");
			if(transfer == null)
				throw new NullPointerException("transfer == null");
			return new SolarPanel(name, new SolarPanelData(generation, capacity, transfer, height), custom);
		}
		
		public SolarPanel buildAndRegister()
		{
			return build().register();
		}
	}
	
	public static class LanguageData
	{
		public final Map<String, String> langToName = new HashMap<>();
		public String def;
		
		final SolarPanel panel;
		
		public LanguageData(SolarPanel panel)
		{
			this.panel = panel;
		}
		
		public LanguageData put(String lang, String loc)
		{
			lang = lang.toLowerCase();
			if(lang.equals("en_us"))
				def = loc;
			langToName.put(lang, loc);
			return this;
		}
		
		public String getName(String lang)
		{
			return langToName.getOrDefault(lang, def);
		}
		
		public SolarPanel build()
		{
			if(def == null)
				throw new RuntimeException("Unable to apply languages: no 'en_us' value found!");
			panel.langs = this;
			return panel;
		}
	}
	
	public static class SolarPanelData
	{
		public final BigInteger generation, capacity, transfer;
		public final float height;
		
		public SolarPanelData(PacketBuffer buf)
		{
			this.generation = new BigInteger(buf.readByteArray());
			this.capacity = new BigInteger(buf.readByteArray());
			this.transfer = new BigInteger(buf.readByteArray());
			this.height = buf.readFloat();
		}
		
		public SolarPanelData(BigInteger generation, BigInteger capacity, BigInteger transfer, float height)
		{
			this.generation = generation;
			this.capacity = capacity;
			this.transfer = transfer;
			this.height = height;
		}
		
		public void write(PacketBuffer buf)
		{
			buf.writeByteArray(generation.toByteArray());
			buf.writeByteArray(capacity.toByteArray());
			buf.writeByteArray(transfer.toByteArray());
			buf.writeFloat(height);
		}
	}
}