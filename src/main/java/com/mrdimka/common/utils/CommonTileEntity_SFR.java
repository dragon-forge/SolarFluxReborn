package com.mrdimka.common.utils;

import com.mrdimka.hammercore.net.HCNetwork;
import com.mrdimka.solarfluxreborn.net.PacketSyncTileEntity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

/**
 * This is a part of Mostly all of MrDimka's Mods
 * This file was generated 2015 at 15:34:59 and modified 2016 at 17:08:37
 * @author MrDimka's Studio (MrDimka)
 */
public abstract class CommonTileEntity_SFR extends TileEntity implements ITickable
{
	protected boolean isDirty;
	
	/** User-friendly methods */
	public abstract void readCustomNBT(NBTTagCompound nbt);
	public abstract void writeCustomNBT(NBTTagCompound nbt);
	
	public boolean atTickRate(int rate) { return ((world.getTotalWorldTime() + pos.toLong() * 50L + (rate * 6000L)) % ((long)rate)) == 0L; }
	
	@Override
	public void markDirty()
	{
		super.markDirty();
		if(!isDirty && world != null) sync();
		world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), world.getBlockState(pos), world.getBlockState(pos), 3);
	}
	
	/** Final methods, that do tile entity sync */
	public final void readFromNBT(NBTTagCompound nbt) { super.readFromNBT(nbt); readCustomNBT(nbt); }
	public final NBTTagCompound writeToNBT(NBTTagCompound nbt){super.writeToNBT(nbt); writeCustomNBT(nbt); return nbt;}
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) { super.onDataPacket(net, pkt); readCustomNBT(pkt.getNbtCompound()); }
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) { return oldState.getBlock() != newState.getBlock(); }
	public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos, 9, getUpdateTag()); }
	public NBTTagCompound getUpdateTag() { NBTTagCompound nbt = new NBTTagCompound(); writeCustomNBT(nbt); return nbt; }
	public void handleUpdateTag(NBTTagCompound nbt) { readCustomNBT(nbt); }
	
	public void sync()
	{
		if(world.isRemote) return;
		HCNetwork.manager.sendToAllAround(new PacketSyncTileEntity(this), getEffectiveUpdateTargetPoint());
	}
	
	public TargetPoint getEffectiveUpdateTargetPoint()
	{
		return new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 272);
	}
}