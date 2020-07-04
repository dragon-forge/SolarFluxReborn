package tk.zeitheron.solarflux.api;

import com.google.common.reflect.TypeToken;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.IRegistryDelegate;
import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.block.BlockBaseSolar;
import tk.zeitheron.solarflux.block.tile.TileBaseSolar;
import tk.zeitheron.solarflux.init.SolarsSF;
import tk.zeitheron.solarflux.shaded.hammerlib.cfg.ConfigEntryCategory;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SolarInfo
		implements Consumer<SolarInstance>, IForgeRegistryEntry<SolarInfo>
{
	public List<Supplier<IRecipe>> recipes = new ArrayList<>();

	public String compatMod;

	/**
	 * Base values supplied by the constructor.
	 */

	public long baseGeneration, baseTransfer, baseCapacity;
	public float baseHeight = 6 / 16F;
	public boolean baseConnectTextures = true;

	/**
	 * Internal properties that may store custom properties
	 */

	public boolean isCustom = false;
	public Map<String, String> localizations = null;

	/**
	 * Accessible config instance of this panel. Represents in-world panel properties.
	 * The properties like {@link #baseGeneration}, {@link #baseTransfer}, {@link #baseCapacity}, {@link #baseHeight} and {@link #baseConnectTextures} are only for boot time and config setup.
	 * They are the base values, that are sent from host to client.
	 */
	public SolarConfigInstance configInstance;

	public SolarInfo(long mgen, long mtranf, long mcap)
	{
		this.baseGeneration = mgen;
		this.baseTransfer = mtranf;
		this.baseCapacity = mcap;
	}

	public SolarInfo(long mgen, long mtranf, long mcap, float height)
	{
		this.baseGeneration = mgen;
		this.baseTransfer = mtranf;
		this.baseCapacity = mcap;
		this.baseHeight = height;
	}

	public SolarInfo noConnectTexture()
	{
		baseConnectTextures = false;
		return this;
	}

	private BlockBaseSolar block;

	protected BlockBaseSolar createBlock()
	{
		return new BlockBaseSolar(this);
	}

	public BlockBaseSolar getBlock()
	{
		if(block == null)
			block = createBlock();
		return block;
	}

	protected ResourceLocation tex;

	public ResourceLocation getTexture()
	{
		if(tex != null)
			return tex;
		tex = new ResourceLocation(getRegistryName().getNamespace(), "blocks/solar_topf_" + getRegistryName().getPath());
		return tex;
	}

	@Override
	public void accept(SolarInstance t)
	{
		SolarConfigInstance data = getConfigInstance();
		t.gen = data.generation;
		t.cap = data.capacity;
		t.transfer = data.transfer;
		t.infoDelegate = this;
		t.delegate = getRegistryName();
	}

	public SolarInfo setCompatMod(String compatMod)
	{
		this.compatMod = compatMod;
		return this;
	}

	public String getCompatMod()
	{
		return compatMod;
	}

	public float getHeight()
	{
		return getConfigInstance().height;
	}

	public long getGeneration()
	{
		return getConfigInstance().generation;
	}

	public long getTransfer()
	{
		return getConfigInstance().transfer;
	}

	public long getCapacity()
	{
		return getConfigInstance().capacity;
	}

	public void configureBase(ConfigEntryCategory cat)
	{
		this.configInstance = new SolarConfigInstance(cat, this);
	}

	public void resetConfigInstance()
	{
		this.configInstance = new SolarConfigInstance(this);
	}

	public SolarConfigInstance getConfigInstance()
	{
		if(configInstance == null) resetConfigInstance();
		return configInstance;
	}

	public float computeSunIntensity(TileBaseSolar solar)
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

	// REGISTRY STUFF //

	private TypeToken<SolarInfo> token = new TypeToken<SolarInfo>(getClass())
	{
	};
	public final IRegistryDelegate<SolarInfo> delegate = new RegistryDelegate<SolarInfo>(this, (Class<SolarInfo>) token.getRawType());
	private ResourceLocation registryName = null;

	public final SolarInfo setRegistryName(String name)
	{
		if(getRegistryName() != null)
			throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());
		this.registryName = checkPrefix(name);
		return (SolarInfo) this;
	}

	public static ResourceLocation checkPrefix(String name)
	{
		int index = name.lastIndexOf(':');
		String oldPrefix = index == -1 ? "" : name.substring(0, index).toLowerCase(Locale.ROOT);
		name = index == -1 ? name : name.substring(index + 1);
		ModContainer mc = Loader.instance().activeModContainer();
		String prefix = mc == null || (mc instanceof InjectedModContainer && ((InjectedModContainer) mc).wrappedContainer instanceof FMLContainer) ? InfoSF.MOD_ID : mc.getModId().toLowerCase(Locale.ROOT);
		if(!oldPrefix.equals(prefix) && oldPrefix.length() > 0)
			prefix = oldPrefix;
		return new ResourceLocation(prefix, name);
	}

	// Helper functions
	@Override
	public final SolarInfo setRegistryName(ResourceLocation name)
	{
		return setRegistryName(name.toString());
	}

	public final SolarInfo setRegistryName(String modID, String name)
	{
		return setRegistryName(modID + ":" + name);
	}

	@Nullable
	@Override
	public final ResourceLocation getRegistryName()
	{
		if(delegate.name() != null)
			return delegate.name();
		return registryName != null ? registryName : null;
	}

	@Override
	public final Class<SolarInfo> getRegistryType()
	{
		return SolarInfo.class;
	}

	// Builder stuff

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

	public RecipeBuilder recipeBuilder()
	{
		return new RecipeBuilder(this);
	}

	public boolean hasConnectedTextures()
	{
		return getConfigInstance().connectTextures;
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

		public SolarInfo build()
		{
			if(name == null)
				throw new NullPointerException("name == null");
			if(generation == null)
				throw new NullPointerException("generation == null");
			if(capacity == null)
				throw new NullPointerException("capacity == null");
			if(transfer == null)
				throw new NullPointerException("transfer == null");
			SolarInfo info = new SolarInfo(generation, transfer, capacity);
			info.isCustom = custom;
			info.setRegistryName(name);
			info.baseHeight = height;
			return info;
		}

		public SolarInfo buildAndRegister()
		{
			SolarInfo info = build();
			SolarsSF.modSolars.add(info);
			return info;
		}
	}

	public static class LanguageData
	{
		public final Map<String, String> langToName = new HashMap<>();
		public String def;

		final SolarInfo panel;

		public LanguageData(SolarInfo panel)
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

		public SolarInfo build()
		{
			if(def == null)
				throw new RuntimeException("Unable to apply languages: no 'en_us' value found!");
			panel.localizations = langToName;
			return panel;
		}
	}

	public static class RecipeBuilder
	{
		final SolarInfo panel;

		List<Object> args = new ArrayList<>();

		public RecipeBuilder(SolarInfo panel)
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

		public SolarInfo build()
		{
			return build(1);
		}

		public SolarInfo build(int amount)
		{
			this.panel.recipes.add(() ->
			{
				Object[] objs = args.toArray(new Object[args.size()]);
				for(int i = 0; i < objs.length; ++i)
				{
					Object j = objs[i];
					if(j instanceof Supplier) objs[i] = ((Supplier) j).get();
				}
				return new ShapedOreRecipe(new ResourceLocation(InfoSF.MOD_ID), new ItemStack(panel.getBlock(), amount), objs);
			});
			return this.panel;
		}
	}

	public static class SolarConfigInstance
	{
		public final long generation, capacity, transfer;
		public final float height;
		public final boolean connectTextures;

		public SolarConfigInstance(ConfigEntryCategory cat, SolarInfo base)
		{
			this.generation = cat.getLongEntry("Generation Rate", base.baseGeneration, 1, Long.MAX_VALUE).setDescription("How many RF/FE does this solar panel produce per tick?").getValue();
			this.transfer = cat.getLongEntry("Transfer Rate", base.baseTransfer, 1, Long.MAX_VALUE).setDescription("How many RF/FE does this solar panel emit to other blocks, per tick?").getValue();
			this.capacity = cat.getLongEntry("Capacity", base.baseCapacity, 1, Long.MAX_VALUE).setDescription("How many RF/FE does this solar panel store?").getValue();
			this.connectTextures = cat.getBooleanEntry("Connected Texture", base.baseConnectTextures).setDescription("Does this solar panel connect textures with other panels of this type?").getValue();
			this.height = cat.getFloatEntry("Height", base.baseHeight * 16F, 0, 16).setDescription("How high is this solar panel?").getValue() / 16F;
		}

		public SolarConfigInstance(SolarInfo base)
		{
			this.generation = base.baseGeneration;
			this.capacity = base.baseCapacity;
			this.transfer = base.baseTransfer;
			this.height = base.baseHeight;
			this.connectTextures = base.baseConnectTextures;
		}

		public SolarConfigInstance(long generation, long capacity, long transfer, float height, boolean connectTextures)
		{
			this.generation = generation;
			this.capacity = capacity;
			this.transfer = transfer;
			this.height = height;
			this.connectTextures = connectTextures;
		}

		public NBTTagCompound serialize()
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setLong("MG", generation);
			nbt.setLong("MC", capacity);
			nbt.setLong("MT", transfer);
			nbt.setFloat("SH", height);
			nbt.setBoolean("CT", connectTextures);
			return nbt;
		}
	}
}