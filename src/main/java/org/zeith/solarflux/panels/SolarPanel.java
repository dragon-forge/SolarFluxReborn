package org.zeith.solarflux.panels;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.zeith.hammerlib.core.adapter.recipe.ShapedRecipeBuilder;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.util.configured.ConfiguredLib;
import org.zeith.hammerlib.util.configured.data.DecimalValueRange;
import org.zeith.hammerlib.util.configured.data.IntValueRange;
import org.zeith.hammerlib.util.configured.types.ConfigCategory;
import org.zeith.hammerlib.util.java.functions.Function3;
import org.zeith.solarflux.InfoSF;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.block.SolarPanelBlock;
import org.zeith.solarflux.init.SolarPanelsSF;
import org.zeith.solarflux.net.PacketSyncPanelData;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The base class for solar panels.
 *
 * @author Zeith
 */
public class SolarPanel
		implements ItemLike
{
	/**
	 * The base data of the solar panel.
	 */
	private final SolarPanelData delegateDataBase;
	
	/**
	 * The name of the solar panel.
	 */
	public final String name;
	
	/**
	 * The data of the solar panel.
	 */
	private SolarPanelData delegateData;
	
	/**
	 * The network data of the solar panel.
	 */
	private SolarPanelData networkData;
	
	/**
	 * The mod ID of a compat that has created this {@link SolarPanel} instance.
	 */
	private String compatMod;
	
	/**
	 * A list of consumers for recipe events. Each consumer should add only one recipe.
	 */
	public List<Consumer<RegisterRecipesEvent>> recipes = new ArrayList<>();
	
	/**
	 * Whether this solar panel is a custom solar panel, that was created by JavaScript configs.
	 */
	public final boolean isCustom;
	
	/**
	 * The block for this solar panel.
	 */
	private SolarPanelBlock block;
	
	/**
	 * The language data for this solar panel.
	 */
	private LanguageData langs;
	
	/**
	 * Creates a new solar panel with the given name and data.
	 *
	 * @param name
	 * 		the name of the solar panel
	 * @param data
	 * 		the data of the solar panel
	 * @param isCustom
	 * 		whether this solar panel is a custom solar panel
	 */
	protected SolarPanel(String name, SolarPanelData data, boolean isCustom)
	{
		this.delegateData = this.delegateDataBase = data;
		this.name = (isCustom ? "custom_" : "") + name;
		this.isCustom = isCustom;
	}
	
	/**
	 * Gets the panel data.
	 *
	 * @return the panel data
	 */
	public SolarPanelData getPanelData()
	{
		return networkData != null ? networkData : delegateData;
	}
	
	/**
	 * Gets the current solar panel information (it's generation, transfer and capacity) AFTER configs have been applied.
	 */
	public SolarPanelData getDelegateData()
	{
		return delegateData;
	}
	
	/**
	 * Registers this solar panel.
	 *
	 * @return this solar panel
	 *
	 * @throws IllegalArgumentException
	 * 		if a solar panel with the same name already exists
	 */
	public SolarPanel register()
	{
		if(SolarPanelsSF.PANELS.containsKey(name))
			throw new IllegalArgumentException("Solar panel with id " + name + " already exists.");
		SolarPanelsSF.PANELS.put(name, this);
		return this;
	}
	
	/**
	 * Sets the mod this solar panel is compatible with.
	 *
	 * @param compatMod
	 * 		the mod this solar panel is compatible with
	 *
	 * @return this solar panel
	 */
	@ApiStatus.Internal
	public SolarPanel setCompatMod(String compatMod)
	{
		this.compatMod = compatMod;
		return this;
	}
	
	/**
	 * Gets the mod this solar panel is compatible with.
	 *
	 * @return the mod this solar panel is compatible with
	 */
	public String getCompatMod()
	{
		return compatMod;
	}
	
	/**
	 * Configures the base data for this solar panel.
	 *
	 * @param category
	 * 		the config category to use for this solar panel
	 */
	public void configureBase(ConfigCategory category)
	{
		this.delegateData = new SolarPanelData(category, this);
	}
	
	protected SolarPanelBlock createBlock()
	{
		return new SolarPanelBlock(this, new ResourceLocation(InfoSF.MOD_ID, "sp_" + name));
	}
	
	/**
	 * Gets the block instance for this solar panel.
	 *
	 * @return the block instance for this solar panel
	 */
	public SolarPanelBlock getBlock()
	{
		if(block == null)
		{
			block = createBlock();
		}
		return block;
	}
	
	/**
	 * Gets a supplier for the block instance for this solar panel.
	 *
	 * @return a supplier for the block instance for this solar panel
	 */
	public Supplier<Block> getBlockSupplier()
	{
		return this::getBlock;
	}
	
	/**
	 * Gets the solar panel as item version of the block instance for this solar panel.
	 *
	 * @return the item version of the block instance for this solar panel
	 */
	@Override
	public Item asItem()
	{
		return getBlock().asItem();
	}
	
	/**
	 * Creates a new builder for creating a solar panel.
	 *
	 * @return a new builder for creating a solar panel
	 */
	public static Builder builder()
	{
		return new Builder();
	}
	
	/**
	 * Creates a new builder for creating a custom solar panel using JavaScript configs.
	 *
	 * @return a new builder for creating a custom solar panel
	 */
	@ApiStatus.Internal
	public static Builder customBuilder()
	{
		Builder b = new Builder();
		b.custom = true;
		return b;
	}
	
	/**
	 * Creates a new language data builder for this solar panel.
	 *
	 * @return a new language data builder for this solar panel
	 */
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
	
	/**
	 * Computes the sun intensity for this solar panel.
	 *
	 * @param solar
	 * 		the solar panel tile to compute the sun intensity for
	 *
	 * @return the sun intensity for this solar panel
	 */
	public float computeSunIntensity(ISolarPanelTile solar)
	{
		if(!solar.doesSeeSky())
			return 0F;
		
		float celestialAngleRadians = solar.level().getSunAngle(1F);
		if(celestialAngleRadians > Math.PI)
			celestialAngleRadians = (float) (2 * Math.PI - celestialAngleRadians);
		int lowLightCount = 0;
		float multiplicator = 1.5F - (lowLightCount * .122F);
		float displacement = 1.2F + (lowLightCount * .08F);
		
		return Mth.clamp(multiplicator * Mth.cos(celestialAngleRadians / displacement), 0, 1);
	}
	
	/**
	 * Registers all the recipes for this solar panel.
	 *
	 * @param helper
	 * 		the event helper
	 *
	 * @author Zeith
	 */
	@ApiStatus.Internal
	public void recipes(RegisterRecipesEvent helper)
	{
		if(recipes != null)
			recipes.forEach(c -> c.accept(helper));
	}
	
	/**
	 * Creates a new {@link SolarPanelInstance} instance and assigns it the current {@link SolarPanel} as its delegate.
	 *
	 * @param tile
	 * 		the {@link ISolarPanelTile} to associate with the instance
	 *
	 * @return the created {@link SolarPanelInstance} instance
	 */
	public SolarPanelInstance createInstance(ISolarPanelTile tile)
	{
		SolarPanelInstance inst = new SolarPanelInstance();
		inst.delegate = name;
		inst.infoDelegate = this;
		inst.reset();
		return inst;
	}
	
	/**
	 * Copies the current data (either delegate, or client-side synced values) over to the {@link SolarPanelInstance}
	 *
	 * @param t
	 * 		the instance object to copy values into
	 */
	public void accept(SolarPanelInstance t)
	{
		SolarPanelData data = getPanelData();
		t.gen = data.generation;
		t.cap = data.capacity;
		t.transfer = data.transfer;
		t.delegate = name;
	}
	
	/**
	 * Applies panel data for this panel. Used to obtain custom values from server and display them correctly.
	 */
	public void handle(PacketSyncPanelData packet)
	{
		this.networkData = packet.getData();
	}
	
	/**
	 * A builder class for creating new instances of the SolarPanel class.
	 *
	 * @author Zeith
	 */
	public static class Builder
	{
		private String name;
		private Long generation, capacity, transfer;
		private float height = 6 / 16F;
		private boolean custom = false;
		
		private Builder()
		{
		}
		
		/**
		 * Sets the name of the solar panel.
		 *
		 * @param s
		 * 		the name to set
		 *
		 * @return this Builder instance, for chaining
		 */
		public Builder name(String s)
		{
			this.name = s;
			return this;
		}
		
		/**
		 * Sets the energy values of the solar panel to the same as the given solar panel.
		 *
		 * @param panel
		 * 		the solar panel to copy energy values from
		 *
		 * @return this Builder instance, for chaining
		 */
		public Builder copyEnergy(SolarPanel panel)
		{
			return generation(panel.delegateData.generation)
					.transfer(panel.delegateData.transfer)
					.capacity(panel.delegateData.capacity);
		}
		
		/**
		 * Sets the energy values of the solar panel to the same as the given solar panel, multiplied by the given multiplier.
		 *
		 * @param panel
		 * 		the solar panel to copy energy values from
		 * @param multiplier
		 * 		the multiplier to apply to the energy values
		 *
		 * @return this Builder instance, for chaining
		 */
		public Builder copyEnergy(SolarPanel panel, double multiplier)
		{
			return generation(panel.delegateData.generation * multiplier)
					.transfer(panel.delegateData.transfer * multiplier)
					.capacity(panel.delegateData.capacity * multiplier);
		}
		
		/**
		 * Sets the height of the solar panel.
		 *
		 * @param f
		 * 		the height to set, as a fraction of a block
		 *
		 * @return this Builder instance, for chaining
		 */
		public Builder height(float f)
		{
			this.height = f;
			return this;
		}
		
		/**
		 * Sets the generation rate of the solar panel.
		 *
		 * @param n
		 * 		the generation rate to set, in FE per tick
		 *
		 * @return this Builder instance, for chaining
		 */
		public Builder generation(Number n)
		{
			this.generation = n.longValue();
			return this;
		}
		
		/**
		 * Sets the generation rate of the solar panel.
		 *
		 * @param s
		 * 		the generation rate to set, as a string, in FE per tick
		 *
		 * @return this Builder instance, for chaining
		 */
		public Builder generation(String s)
		{
			this.generation = Long.parseLong(s);
			return this;
		}
		
		/**
		 * Sets the capacity of the solar panel.
		 *
		 * @param n
		 * 		the capacity to set, in FE
		 *
		 * @return this Builder instance, for chaining
		 */
		public Builder capacity(Number n)
		{
			this.capacity = n.longValue();
			return this;
		}
		
		/**
		 * Sets the capacity of the solar panel.
		 *
		 * @param s
		 * 		the capacity to set, as a string, in FE
		 *
		 * @return this Builder instance, for chaining
		 */
		public Builder capacity(String s)
		{
			this.capacity = Long.parseLong(s);
			return this;
		}
		
		/**
		 * Sets the transfer rate of the solar panel.
		 *
		 * @param n
		 * 		the transfer rate to set, in FE per tick
		 *
		 * @return this Builder instance, for chaining
		 */
		public Builder transfer(Number n)
		{
			this.transfer = n.longValue();
			return this;
		}
		
		/**
		 * Sets the transfer rate of the solar panel.
		 *
		 * @param s
		 * 		the transfer rate to set, as a string, in FE per tick
		 *
		 * @return this Builder instance, for chaining
		 */
		public Builder transfer(String s)
		{
			this.transfer = Long.parseLong(s);
			return this;
		}
		
		/**
		 * Creates a new SolarPanel instance using the current settings of this Builder.
		 *
		 * @return a new SolarPanel instance
		 *
		 * @throws NullPointerException
		 * 		if the name, generation rate, capacity, or transfer rate are not set
		 */
		public SolarPanel build()
		{
			return build(SolarPanel::new);
		}
		
		/**
		 * Creates a new SolarPanel instance using the current settings of this Builder.
		 *
		 * @return a new SolarPanel instance
		 *
		 * @throws NullPointerException
		 * 		if the name, generation rate, capacity, or transfer rate are not set
		 */
		public SolarPanel build(Function3<String, SolarPanelData, Boolean, SolarPanel> factory)
		{
			if(name == null)
				throw new NullPointerException("name == null");
			if(generation == null)
				throw new NullPointerException("generation == null");
			if(capacity == null)
				throw new NullPointerException("capacity == null");
			if(transfer == null)
				throw new NullPointerException("transfer == null");
			return factory.apply(name, new SolarPanelData(generation, capacity, transfer, height), custom);
		}
		
		/**
		 * Creates a new SolarPanel instance using the current settings of this Builder, and registers it.
		 *
		 * @return the registered SolarPanel instance
		 *
		 * @throws NullPointerException
		 * 		if the name, generation rate, capacity, or transfer rate are not set
		 */
		public SolarPanel buildAndRegister()
		{
			return build().register();
		}
	}
	
	/**
	 * A class for storing and applying language-specific names for solar panels.
	 *
	 * @author Zeith
	 */
	public static class LanguageData
	{
		/**
		 * A map of language codes to localized names for the solar panel.
		 */
		public final Map<String, String> langToName = new HashMap<>();
		
		/**
		 * The default (English) name for the solar panel.
		 */
		public String def;
		
		/**
		 * The solar panel that this LanguageData instance is associated with.
		 */
		final SolarPanel panel;
		
		/**
		 * Creates a new LanguageData instance for the given solar panel.
		 *
		 * @param panel
		 * 		the solar panel to create language data for
		 */
		@ApiStatus.Internal
		private LanguageData(SolarPanel panel)
		{
			this.panel = panel;
		}
		
		/**
		 * Adds a localized name for the given language code.
		 *
		 * @param lang
		 * 		the language code to add a name for
		 * @param loc
		 * 		the localized name to add
		 *
		 * @return this LanguageData instance, for chaining
		 */
		public LanguageData put(String lang, String loc)
		{
			lang = lang.toLowerCase();
			if(lang.equalsIgnoreCase("en_us"))
				def = loc;
			langToName.put(lang, loc);
			return this;
		}
		
		/**
		 * Gets the localized name for the given language code.
		 * If no name is found for the given language code, the default (English) name is returned.
		 *
		 * @param lang
		 * 		the language code to get the name for
		 *
		 * @return the localized name for the given language code
		 */
		public String getName(String lang)
		{
			return langToName.getOrDefault(lang, def);
		}
		
		/**
		 * Applies this LanguageData to the solar panel it is associated with.
		 *
		 * @return the solar panel that this LanguageData instance is associated with
		 *
		 * @throws RuntimeException
		 * 		if no default (English) name is found
		 */
		public SolarPanel build()
		{
			if(def == null)
				throw new RuntimeException("Unable to apply languages: no 'en_us' value found!");
			panel.langs = this;
			return panel;
		}
	}
	
	/**
	 * A builder class for creating recipes for solar panels.
	 *
	 * @author Zeith
	 */
	public static class RecipeBuilder
	{
		/**
		 * The solar panel that this builder is creating recipes for.
		 */
		final SolarPanel panel;
		
		/**
		 * The list of recipe building handlers that will be applied when the recipe is built.
		 */
		final List<Consumer<ShapedRecipeBuilder>> handlers = new ArrayList<>();
		
		/**
		 * Creates a new RecipeBuilder instance for the given solar panel.
		 *
		 * @param panel
		 * 		the solar panel to create recipes for
		 */
		@ApiStatus.Internal
		private RecipeBuilder(SolarPanel panel)
		{
			this.panel = panel;
		}
		
		/**
		 * Sets the shape of the recipe using the given strings.
		 *
		 * @param strings
		 * 		the strings representing the shape of the recipe
		 *
		 * @return this RecipeBuilder instance, for chaining
		 */
		public RecipeBuilder shape(String... strings)
		{
			handlers.add(b -> b.shape(strings));
			return this;
		}
		
		/**
		 * Binds the given output to the given character in the recipe.
		 *
		 * @param ch
		 * 		the character to bind the output to
		 * @param output
		 * 		the output to bind to the character
		 *
		 * @return this RecipeBuilder instance, for chaining
		 *
		 * @throws IllegalArgumentException
		 * 		if the given character is not a single character
		 */
		public RecipeBuilder bind(String ch, Object output)
		{
			if(ch.length() != 1)
				throw new IllegalArgumentException(ch + " is not a single character!");
			handlers.add(b -> b.map(ch.charAt(0), output));
			return this;
		}
		
		/**
		 * Builds the recipe with a result of 1 solar panel.
		 *
		 * @return the solar panel that this builder is creating recipes for
		 */
		public SolarPanel build()
		{
			return build(1);
		}
		
		/**
		 * Builds the recipe with a result of the given amount of solar panels.
		 *
		 * @param amount
		 * 		the amount of solar panels to include in the recipe result
		 *
		 * @return the solar panel that this builder is creating recipes for
		 */
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
	
	/**
	 * A class that represents data for a solar panel.
	 * This includes generation rate, capacity, transfer rate, and height.
	 *
	 * @author Zeith
	 */
	public static class SolarPanelData
	{
		/**
		 * The rate at which the solar panel generates energy, in units of FE per tick
		 */
		public final long generation;
		
		/**
		 * The capacity of the solar panel, in units of FE
		 */
		public final long capacity;
		
		/**
		 * The rate at which the solar panel transfers energy to other block, in units of FE per tick
		 */
		public final long transfer;
		
		/**
		 * The height of the solar panel, as a ratio of block height (e.g. 0.5 for half a block)
		 */
		public final float height;
		
		/**
		 * Creates a new SolarPanelData instance by reading data from the given byte buffer.
		 *
		 * @param buf
		 * 		the byte buffer to read from
		 */
		@ApiStatus.Internal
		public SolarPanelData(FriendlyByteBuf buf)
		{
			this.generation = buf.readLong();
			this.capacity = buf.readLong();
			this.transfer = buf.readLong();
			this.height = buf.readFloat();
		}
		
		/**
		 * Creates a new SolarPanelData instance with the given values.
		 *
		 * @param generation
		 * 		the generation rate of the solar panel, in units of FE per tick
		 * @param capacity
		 * 		the capacity of the solar panel, in units of FE
		 * @param transfer
		 * 		the transfer rate of the solar panel, in units of FE per tick
		 * @param height
		 * 		the height of the solar panel, as a ratio of block height (e.g. 0.5 for half a block)
		 */
		@ApiStatus.Internal
		private SolarPanelData(long generation, long capacity, long transfer, float height)
		{
			this.generation = generation;
			this.capacity = capacity;
			this.transfer = transfer;
			this.height = height;
		}
		
		/**
		 * Creates a new SolarPanelData instance by reading data from the given configuration category.
		 * The values in the configuration category will be compared to the default values in the given base solar panel,
		 * and the resulting values will be used to initialize the new SolarPanelData instance.
		 *
		 * @param cat
		 * 		the configuration category to read from
		 * @param base
		 * 		the base solar panel to compare values with
		 */
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
					.withComment("How much FE does this solar panel emit to other block, per tick?")
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
		
		/**
		 * Writes the data of this solar panel to the given byte buffer.
		 *
		 * @param buf
		 * 		the byte buffer to write to
		 */
		public void write(FriendlyByteBuf buf)
		{
			buf.writeLong(generation);
			buf.writeLong(capacity);
			buf.writeLong(transfer);
			buf.writeFloat(height);
		}
	}
}