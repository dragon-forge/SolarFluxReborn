package com.zeitheron.solarflux.api;

import java.util.function.Consumer;

import com.zeitheron.solarflux.block.BlockBaseSolar;
import com.zeitheron.solarflux.block.tile.TileBaseSolar;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class SolarInfo extends IForgeRegistryEntry.Impl<SolarInfo> implements Consumer<SolarInstance>
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
		if(!solar.getWorld().canBlockSeeSky(solar.getPos()))
			return 0F;
		
		float celestialAngleRadians = solar.getWorld().getCelestialAngleRadians(1F);
		if(celestialAngleRadians > Math.PI)
			celestialAngleRadians = (float) (2 * Math.PI - celestialAngleRadians);
		int lowLightCount = 0;
		float multiplicator = 1.5F - (lowLightCount * .122F);
		float displacement = 1.2F + (lowLightCount * .08F);
		
		return MathHelper.clamp(multiplicator * MathHelper.cos(celestialAngleRadians / displacement), 0, 1);
	}
}