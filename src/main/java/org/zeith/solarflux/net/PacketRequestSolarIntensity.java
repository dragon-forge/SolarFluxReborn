package org.zeith.solarflux.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import org.zeith.hammerlib.api.lighting.ColoredLightManager;
import org.zeith.hammerlib.net.*;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.solarflux.block.SolarPanelTile;

@MainThreaded
public class PacketRequestSolarIntensity
		implements IPacket
{
	protected BlockPos pos;
	protected float sunIntensity;
	
	public PacketRequestSolarIntensity()
	{
	}
	
	public PacketRequestSolarIntensity(BlockPos pos, float sunIntensity)
	{
		this.pos = pos;
		this.sunIntensity = sunIntensity;
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(pos).writeFloat(sunIntensity);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		pos = buf.readBlockPos();
		sunIntensity = buf.readFloat();
	}
	
	@Override
	public void serverExecute(PacketContext ctx)
	{
		var player = ctx.getSender();
		if(player == null) return;
		var level = player.serverLevel();
		var spt = Cast.cast(level.getBlockEntity(pos), SolarPanelTile.class);
		if(spt == null) return;
		sunIntensity = spt.sunIntensity;
		ctx.withReply(this);
	}
	
	@Override
	public void clientExecute(PacketContext ctx)
	{
		var player = ColoredLightManager.getClientPlayer();
		if(player == null) return;
		var spt = Cast.cast(player.level().getBlockEntity(pos), SolarPanelTile.class);
		if(spt == null) return;
		spt.sunIntensity = sunIntensity;
	}
}