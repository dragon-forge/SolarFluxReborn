package com.zeitheron.solarflux.api;

import java.util.Locale;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.reflect.TypeToken;
import com.zeitheron.solarflux.InfoSF;
import com.zeitheron.solarflux.block.BlockBaseSolar;
import com.zeitheron.solarflux.block.tile.TileBaseSolar;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.FMLContainer;
import net.minecraftforge.fml.common.InjectedModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.IRegistryDelegate;

public class SolarInfo implements Consumer<SolarInstance>, IForgeRegistryEntry<SolarInfo>
{
	public String compatMod;
	
	public int maxGeneration;
	public int maxTransfer;
	public int maxCapacity;
	
	public boolean connectTextures = true;
	
	public SolarInfo(int mgen, int mtranf, int mcap)
	{
		this.maxGeneration = mgen;
		this.maxTransfer = mtranf;
		this.maxCapacity = mcap;
	}
	
	public SolarInfo noConnectTexture()
	{
		connectTextures = false;
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
		tex = new ResourceLocation(getRegistryName().getNamespace(), "textures/blocks/solar_topf_" + getRegistryName().getPath() + ".png");
		return tex;
	}
	
	@Override
	public void accept(SolarInstance t)
	{
		t.gen = maxGeneration;
		t.cap = maxCapacity;
		t.transfer = maxTransfer;
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
	
	public void read(PacketBuffer buf)
	{
		maxGeneration = buf.readInt();
		maxTransfer = buf.readInt();
		maxCapacity = buf.readInt();
	}
	
	public void write(PacketBuffer buf)
	{
		buf.writeInt(maxGeneration);
		buf.writeInt(maxTransfer);
		buf.writeInt(maxCapacity);
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
}