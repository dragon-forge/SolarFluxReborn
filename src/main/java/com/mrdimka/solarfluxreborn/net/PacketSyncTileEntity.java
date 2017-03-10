package com.mrdimka.solarfluxreborn.net;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.mrdimka.hammercore.net.packetAPI.IPacket;
import com.mrdimka.hammercore.net.packetAPI.IPacketListener;

public class PacketSyncTileEntity implements IPacket, IPacketListener<PacketSyncTileEntity, IPacket>
{
	private BlockPos pos;
	private int dim;
	private NBTTagCompound nbt;
	
	public PacketSyncTileEntity() {}
	public PacketSyncTileEntity(TileEntity tile)
	{
		dim = tile.getWorld().provider.getDimension();
		nbt = tile.getUpdateTag();
		pos = tile.getPos();
	}
	
	@Override
	public IPacket onArrived(PacketSyncTileEntity packet, MessageContext context)
	{
		if(context.side == Side.CLIENT) handleClient(packet);
		else
		{
			World world = context.getServerHandler().playerEntity.mcServer.worldServerForDimension(packet.dim);
			if(world.isAreaLoaded(packet.pos, packet.pos))
			{
				TileEntity te = world.getTileEntity(packet.pos);
				if(te != null) te.handleUpdateTag(packet.nbt);
			}
		}
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public void handleClient(PacketSyncTileEntity p)
	{
		if(p.dim == Minecraft.getMinecraft().theWorld.provider.getDimension())
		{
			World world = Minecraft.getMinecraft().theWorld;
			if(world.isAreaLoaded(p.pos, p.pos))
			{
				TileEntity te = world.getTileEntity(p.pos);
				if(te != null) te.handleUpdateTag(p.nbt);
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setLong("p", pos.toLong());
		nbt.setTag("d", this.nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		pos = BlockPos.fromLong(nbt.getLong("p"));
		this.nbt = nbt.getCompoundTag("d");
	}
	
}