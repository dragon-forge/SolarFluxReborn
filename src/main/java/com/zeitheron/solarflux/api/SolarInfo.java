package com.zeitheron.solarflux.api;

import java.util.function.Consumer;

import com.zeitheron.solarflux.block.BlockBaseSolar;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class SolarInfo extends IForgeRegistryEntry.Impl<SolarInfo> implements Consumer<SolarInstance>
{
	public int maxGeneration;
	public int maxTransfer;
	public int maxCapacity;
	
	public SolarInfo(int mgen, int mtranf, int mcap)
	{
		this.maxGeneration = mgen;
		this.maxTransfer = mtranf;
		this.maxCapacity = mcap;
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
}