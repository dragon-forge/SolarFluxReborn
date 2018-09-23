package com.zeitheron.solarflux.net;

import java.io.IOException;

import com.zeitheron.solarflux.InfoSF;
import com.zeitheron.solarflux.SolarFlux;
import com.zeitheron.solarflux.api.SolarFluxAPI;
import com.zeitheron.solarflux.api.SolarInfo;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetworkSF
{
	public static final NetworkSF INSTANCE = null;
	
	private final FMLEventChannel channel;
	
	{
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(InfoSF.MOD_ID);
		channel.register(this);
	}
	
	@SubscribeEvent
	public void client(FMLNetworkEvent.ClientCustomPacketEvent e)
	{
		PacketBuffer buf = new PacketBuffer(e.getPacket().payload());
		try
		{
			NBTTagCompound nbt = buf.readCompoundTag();
			switch(nbt.getInteger("Action"))
			{
			case 0x01:
				System.out.println(nbt.getString("SolarInfo"));
				SolarInfo si = SolarFluxAPI.SOLAR_PANELS.getValue(new ResourceLocation(nbt.getString("SolarInfo")));
				if(si != null)
				{
					si.connectTextures = nbt.getBoolean("CT");
					si.maxTransfer = nbt.getInteger("MT");
					si.maxCapacity = nbt.getInteger("MC");
					si.maxGeneration = nbt.getInteger("MG");
				}
			break;
			case 0x02:
				SolarFlux.proxy.updateWindow(nbt.getInteger("id"), nbt.getInteger("k"), nbt.getInteger("v"));
			break;
			default:
			break;
			}
		} catch(IOException e1)
		{
			e1.printStackTrace();
		}
		buf.release();
	}
	
	@SubscribeEvent
	public void server(FMLNetworkEvent.ServerCustomPacketEvent e)
	{
		PacketBuffer buf = new PacketBuffer(e.getPacket().payload());
		try
		{
			NBTTagCompound nbt = buf.readCompoundTag();
			switch(nbt.getInteger("Action"))
			{
			case 0x01:
				INetHandlerPlayServer inet = e.getHandler();
				if(inet instanceof NetHandlerPlayServer)
				{
					NetHandlerPlayServer net = (NetHandlerPlayServer) inet;
					EntityPlayerMP sender = net.player;
					
					// Sync them
					SolarFluxAPI.SOLAR_PANELS.getValuesCollection().forEach(si -> send(sender, si));
					SolarFlux.LOG.info("Sent " + SolarFluxAPI.SOLAR_PANELS.getValuesCollection().size() + " Panel Info Packets to " + sender);
				}
			break;
			default:
			break;
			}
		} catch(IOException e1)
		{
			e1.printStackTrace();
		}
		buf.release();
	}
	
	public void send(EntityPlayerMP mp, SolarInfo si)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("Action", 0x01);
		tag.setString("SolarInfo", si.getRegistryName().toString());
		tag.setBoolean("CT", si.connectTextures);
		tag.setInteger("MT", si.maxTransfer);
		tag.setInteger("MC", si.maxCapacity);
		tag.setInteger("MG", si.maxGeneration);
		PacketBuffer payload = new PacketBuffer(Unpooled.buffer());
		payload.writeCompoundTag(tag);
		channel.sendTo(new FMLProxyPacket(payload, InfoSF.MOD_ID), mp);
	}
	
	public void sendWindowProperty(EntityPlayerMP player, Container ctr, int var, int val)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("Action", 0x02);
		tag.setInteger("id", ctr.windowId);
		tag.setInteger("k", var);
		tag.setInteger("v", val);
		PacketBuffer payload = new PacketBuffer(Unpooled.buffer());
		payload.writeCompoundTag(tag);
		channel.sendTo(new FMLProxyPacket(payload, InfoSF.MOD_ID), player);
	}
	
	@SideOnly(Side.CLIENT)
	public void request()
	{
		NBTTagCompound tag = new NBTTagCompound();
		
		tag.setInteger("Action", 0x01);
		
		SolarFlux.LOG.info("Requesting solar configurations from server...");
		
		PacketBuffer payload = new PacketBuffer(Unpooled.buffer());
		payload.writeCompoundTag(tag);
		channel.sendToServer(new FMLProxyPacket(payload, InfoSF.MOD_ID));
	}
}