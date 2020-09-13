package tk.zeitheron.solarflux.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.MathHelper;
import tk.zeitheron.solarflux.RecipesSF;
import tk.zeitheron.solarflux.block.SolarPanelBlock;
import tk.zeitheron.solarflux.block.SolarPanelTile;
import tk.zeitheron.solarflux.shaded.hammerlib.cfg.ConfigEntryCategory;

public class SolarPanel implements IItemProvider
{
	private final SolarPanelData delegateDataBase;
	public final String name;
	public SolarPanelData delegateData;
	public SolarPanelData networkData;
	private String compatMod;
	
	public List<Supplier<IRecipe<?>>> recipes = new ArrayList<>();
	
	public final boolean isCustom;
	
	private SolarPanelBlock block;

	private LanguageData langs;

	public SolarPanel(String name, SolarPanelData data, boolean isCustom)
	{
		this.delegateData = this.delegateDataBase = networkData = data;
		this.name = (isCustom ? "custom_" : "") + name;
		this.isCustom = isCustom;
	}

	public SolarPanelData getPanelData()
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

	public SolarPanel setCompatMod(String compatMod)
	{
		this.compatMod = compatMod;
		return this;
	}

	public String getCompatMod()
	{
		return compatMod;
	}

	public void configureBase(ConfigEntryCategory category)
	{
		this.delegateData = new SolarPanelData(category, this);
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

	@Override
	public Item asItem()
	{
		return getBlock().asItem();
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

	public RecipeBuilder recipeBuilder()
	{
		return new RecipeBuilder(this);
	}

	public float computeSunIntensity(SolarPanelTile solar)
	{
		if(!solar.doesSeeSky())
			return 0F;

		float celestialAngleRadians = solar.getWorld().getCelestialAngleRadians(1F);
		if(celestialAngleRadians > Math.PI)
			celestialAngleRadians = (float) (2 * Math.PI - celestialAngleRadians);
		int lowLightCount = 0;
		float multiplicator = 1.5F - (lowLightCount * .122F);
		float displacement = 1.2F + (lowLightCount * .08F);

		return MathHelper.clamp(multiplicator * MathHelper.cos(celestialAngleRadians / displacement), 0, 1);
	}

	public Stream<IRecipe<?>> recipes()
	{
		return recipes == null ? Stream.empty() : recipes.stream().map(Supplier::get);
	}

	public static class Builder
	{
		String name;
		Long generation, capacity, transfer;
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
			if(n instanceof Long)
			{
				this.generation = (Long) n;
				return this;
			}
			return generation(Long.toString(n.longValue()));
		}
		
		public Builder generation(String s)
		{
			this.generation = new Long(s);
			return this;
		}
		
		public Builder capacity(Number n)
		{
			if(n instanceof Long)
			{
				this.capacity = (Long) n;
				return this;
			}
			return capacity(Long.toString(n.longValue()));
		}
		
		public Builder capacity(String s)
		{
			this.capacity = new Long(s);
			return this;
		}
		
		public Builder transfer(Number n)
		{
			if(n instanceof Long)
			{
				this.transfer = (Long) n;
				return this;
			}
			return transfer(Long.toString(n.longValue()));
		}
		
		public Builder transfer(String s)
		{
			this.transfer = new Long(s);
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
	
	public static class RecipeBuilder
	{
		final SolarPanel panel;
		
		List<Object> args = new ArrayList<>();
		
		public RecipeBuilder(SolarPanel panel)
		{
			this.panel = panel;
		}
		
		public RecipeBuilder shape(String... strings)
		{
			args.addAll(Arrays.asList(strings));
			return this;
		}
		
		public RecipeBuilder bind(String ch, Object output)
		{
			if(ch.length() != 1)
				throw new IllegalArgumentException(ch + " is not a single character!");
			args.add(ch.charAt(0));
			args.add(output);
			return this;
		}
		
		public SolarPanel build()
		{
			return build(1);
		}
		
		public SolarPanel build(int amount)
		{
			this.panel.recipes.add(() -> RecipesSF.parseShaped(new ItemStack(panel), args.toArray(new Object[args.size()])));
			return this.panel;
		}
	}
	
	public static class SolarPanelData
	{
		public final long generation, capacity, transfer;
		public final float height;
		
		public SolarPanelData(PacketBuffer buf)
		{
			this.generation = buf.readLong();
			this.capacity = buf.readLong();
			this.transfer = buf.readLong();
			this.height = buf.readFloat();
		}
		
		public SolarPanelData(long generation, long capacity, long transfer, float height)
		{
			this.generation = generation;
			this.capacity = capacity;
			this.transfer = transfer;
			this.height = height;
		}

		public SolarPanelData(ConfigEntryCategory cat, SolarPanel base)
		{
			this.generation = cat.getLongEntry("Generation Rate", base.delegateDataBase.generation, 1, Long.MAX_VALUE).setDescription("How much FE does this solar panel produce per tick?").getValue();
			this.transfer = cat.getLongEntry("Transfer Rate", base.delegateDataBase.transfer, 1, Long.MAX_VALUE).setDescription("How much FE does this solar panel emit to other blocks, per tick?").getValue();
			this.capacity = cat.getLongEntry("Capacity", base.delegateDataBase.capacity, 1, Long.MAX_VALUE).setDescription("How much FE does this solar panel store?").getValue();
			this.height = cat.getFloatEntry("Height", base.delegateDataBase.height * 16F, 0, 16).setDescription("How high is this solar panel?").getValue() / 16F;
		}

		public void write(PacketBuffer buf)
		{
			buf.writeLong(generation);
			buf.writeLong(capacity);
			buf.writeLong(transfer);
			buf.writeFloat(height);
		}
	}
	
	public SolarPanelInstance createInstance(SolarPanelTile tile)
	{
		SolarPanelInstance inst = new SolarPanelInstance();
		inst.delegate = name;
		inst.infoDelegate = this;
		inst.reset();
		return inst;
	}
	
	public void accept(SolarPanelInstance t)
	{
		SolarPanelData data = getPanelData();
		t.gen = data.generation;
		t.cap = data.capacity;
		t.transfer = data.transfer;
		t.delegate = name;
	}
}