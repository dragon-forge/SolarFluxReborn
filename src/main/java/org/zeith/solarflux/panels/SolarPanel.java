package org.zeith.solarflux.panels;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.zeith.hammerlib.core.adapter.recipe.ShapedRecipeBuilder;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.util.configured.ConfiguredLib;
import org.zeith.hammerlib.util.configured.data.DecimalValueRange;
import org.zeith.hammerlib.util.configured.data.IntValueRange;
import org.zeith.hammerlib.util.configured.types.ConfigCategory;
import org.zeith.solarflux.InfoSF;
import org.zeith.solarflux.block.SolarPanelBlock;
import org.zeith.solarflux.block.SolarPanelTile;
import org.zeith.solarflux.init.SolarPanelsSF;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SolarPanel
		implements ItemLike
{
	private final SolarPanelData delegateDataBase;
	public final String name;
	public SolarPanelData delegateData;
	public SolarPanelData networkData;
	private String compatMod;
	
	public List<Consumer<RegisterRecipesEvent>> recipes = new ArrayList<>();
	
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
		if(SolarPanelsSF.PANELS.containsKey(name))
			throw new IllegalArgumentException("Solar panel with id " + name + " already exists.");
		SolarPanelsSF.PANELS.put(name, this);
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
	
	public void configureBase(ConfigCategory category)
	{
		this.delegateData = new SolarPanelData(category, this);
	}
	
	protected SolarPanelBlock createBlock()
	{
		return new SolarPanelBlock(this, new ResourceLocation(InfoSF.MOD_ID, "sp_" + name));
	}
	
	public SolarPanelBlock getBlock()
	{
		if(block == null)
		{
			block = createBlock();
		}
		return block;
	}
	
	public Supplier<Block> getBlockSupplier()
	{
		return () -> getBlock();
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
		
		float celestialAngleRadians = solar.getLevel().getSunAngle(1F);
		if(celestialAngleRadians > Math.PI)
			celestialAngleRadians = (float) (2 * Math.PI - celestialAngleRadians);
		int lowLightCount = 0;
		float multiplicator = 1.5F - (lowLightCount * .122F);
		float displacement = 1.2F + (lowLightCount * .08F);
		
		return Mth.clamp(multiplicator * Mth.cos(celestialAngleRadians / displacement), 0, 1);
	}
	
	public void recipes(RegisterRecipesEvent helper)
	{
		if(recipes != null)
			recipes.forEach(c -> c.accept(helper));
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
			this.generation = n.longValue();
			return this;
		}
		
		public Builder generation(String s)
		{
			this.generation = Long.parseLong(s);
			return this;
		}
		
		public Builder capacity(Number n)
		{
			this.capacity = n.longValue();
			return this;
		}
		
		public Builder capacity(String s)
		{
			this.capacity = Long.parseLong(s);
			return this;
		}
		
		public Builder transfer(Number n)
		{
			this.transfer = n.longValue();
			return this;
		}
		
		public Builder transfer(String s)
		{
			this.transfer = Long.parseLong(s);
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
			if(lang.equalsIgnoreCase("en_us"))
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
		
		final List<Consumer<ShapedRecipeBuilder>> handlers = new ArrayList<>();
		
		public RecipeBuilder(SolarPanel panel)
		{
			this.panel = panel;
		}
		
		public RecipeBuilder shape(String... strings)
		{
			handlers.add(b -> b.shape(strings));
			return this;
		}
		
		public RecipeBuilder bind(String ch, Object output)
		{
			if(ch.length() != 1)
				throw new IllegalArgumentException(ch + " is not a single character!");
			handlers.add(b -> b.map(ch.charAt(0), output));
			return this;
		}
		
		public SolarPanel build()
		{
			return build(1);
		}
		
		public SolarPanel build(int amount)
		{
			int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();
			
			this.panel.recipes.add(evt ->
			{
				ResourceLocation prn = panel.getBlock().getRegistryName();
				ShapedRecipeBuilder builder = evt.shaped()
						.id(new ResourceLocation(prn.getNamespace(), "builtin/generated_" + prn.getPath() + "_x_" + amount + "_ln" + lineNumber))
						.result(new ItemStack(panel, amount));
				handlers.forEach(c -> c.accept(builder));
				builder.register();
			});
			return this.panel;
		}
	}
	
	public static class SolarPanelData
	{
		public final long generation, capacity, transfer;
		public final float height;
		
		public SolarPanelData(FriendlyByteBuf buf)
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
		
		public SolarPanelData(ConfigCategory cat, SolarPanel base)
		{
			this.generation = cat.getElement(ConfiguredLib.INT, "Generation Rate")
					.withRange(IntValueRange.rangeClosed(1, Long.MAX_VALUE))
					.withDefault(base.delegateDataBase.generation)
					.withComment("How much FE does this solar panel produce per tick?")
					.getValue()
					.longValue();
			
			this.transfer = cat.getElement(ConfiguredLib.INT, "Transfer Rate")
					.withRange(IntValueRange.rangeClosed(1, Long.MAX_VALUE))
					.withDefault(base.delegateDataBase.transfer)
					.withComment("How much FE does this solar panel emit to other blocks, per tick?")
					.getValue()
					.longValue();
			
			this.capacity = cat.getElement(ConfiguredLib.INT, "Capacity")
					.withRange(IntValueRange.rangeClosed(1, Long.MAX_VALUE))
					.withDefault(base.delegateDataBase.capacity)
					.withComment("How much FE does this solar panel store?")
					.getValue()
					.longValue();
			
			
			this.height = cat.getElement(ConfiguredLib.DECIMAL, "Height")
					.withRange(DecimalValueRange.range(0, 16))
					.withDefault(base.delegateDataBase.height * 16F)
					.withComment("How high is this solar panel?")
					.getValue()
					.floatValue() / 16F;
		}
		
		public void write(FriendlyByteBuf buf)
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