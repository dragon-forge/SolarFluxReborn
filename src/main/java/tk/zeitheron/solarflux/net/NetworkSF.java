package tk.zeitheron.solarflux.net;

import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.api.SolarFluxAPI;
import tk.zeitheron.solarflux.api.SolarInfo;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

import java.io.IOException;

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
					SolarInfo si = SolarFluxAPI.SOLAR_PANELS.getValue(new ResourceLocation(nbt.getString("SolarInfo")));
					if(si != null)
					{
						si.connectTextures = nbt.getBoolean("CT");
						si.maxTransfer = nbt.getInteger("MT");
						si.maxCapacity = nbt.getLong("MC");
						si.maxGeneration = nbt.getLong("MG");
					}
					break;
				case 0x02:
					SolarFlux.proxy.updateWindow(nbt.getInteger("id"), nbt.getInteger("k"), nbt.getLong("v"));
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
				default:
					break;
			}
		} catch(IOException e1)
		{
			e1.printStackTrace();
		}
		buf.release();
	}

	public void sendWindowProperty(EntityPlayerMP player, Container ctr, int var, long val)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("Action", 0x02);
		tag.setInteger("id", ctr.windowId);
		tag.setInteger("k", var);
		tag.setLong("v", val);
		channel.sendTo(new FMLProxyPacket(new PacketBuffer(Unpooled.buffer()).writeCompoundTag(tag), InfoSF.MOD_ID), player);
	}
}