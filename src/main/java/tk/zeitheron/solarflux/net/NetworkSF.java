package tk.zeitheron.solarflux.net;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.api.SolarFluxAPI;
import tk.zeitheron.solarflux.api.SolarInfo;

import javax.annotation.Nonnull;
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
			if(nbt == null)
			{
				buf.release();
				return;
			}
			switch(nbt.getInteger("Action"))
			{
				case 0x01:
					SolarInfo si = SolarFluxAPI.SOLAR_PANELS.getValue(new ResourceLocation(nbt.getString("SolarInfo")));
					if(si != null)
					{
						si.configInstance = new SolarInfo.SolarConfigInstance(nbt.getLong("MG"), nbt.getLong("MC"), nbt.getInteger("MT"), nbt.getFloat("SH"), nbt.getBoolean("CT"));
						SolarFlux.LOG.debug("Accepted network configs for " + si.getBlock().getLocalizedName());
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

	public void sendAllPanels(EntityPlayerMP mp)
	{
		SolarFluxAPI.SOLAR_PANELS.getValuesCollection().forEach(i ->
		{
			NBTTagCompound tag = i.getConfigInstance().serialize();
			tag.setInteger("Action", 0x01);
			tag.setString("SolarInfo", i.getRegistryName().toString());
			channel.sendTo(new FMLProxyPacket(new PacketBuffer(Unpooled.buffer()).writeCompoundTag(tag), InfoSF.MOD_ID), mp);
		});
	}

	@SubscribeEvent
	public void server(FMLNetworkEvent.ServerCustomPacketEvent e)
	{
		PacketBuffer buf = new PacketBuffer(e.getPacket().payload());
		try
		{
			NBTTagCompound nbt = buf.readCompoundTag();
			if(nbt == null)
			{
				buf.release();
				return;
			}

			int a = nbt.getInteger("Action");

			if(a == 0x01) sendAllPanels(((NetHandlerPlayServer) e.getHandler()).player);
		} catch(IOException e1)
		{
			e1.printStackTrace();
		}
		buf.release();
	}

	@SideOnly(Side.CLIENT)
	public void requestConfigs()
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("Action", 0x01);
		channel.sendToServer(new FMLProxyPacket(new PacketBuffer(Unpooled.buffer()).writeCompoundTag(tag), InfoSF.MOD_ID));
		SolarFlux.LOG.info("Requesting server panels...");
	}

	public void sendWindowProperty(EntityPlayerMP player, @Nonnull Container ctr, int var, long val)
	{
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("Action", 0x02);
		tag.setInteger("id", ctr.windowId);
		tag.setInteger("k", var);
		tag.setLong("v", val);
		channel.sendTo(new FMLProxyPacket(new PacketBuffer(Unpooled.buffer()).writeCompoundTag(tag), InfoSF.MOD_ID), player);
	}
}