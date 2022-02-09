package org.zeith.solarflux.net;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.event.EventNetworkChannel;
import org.zeith.solarflux.panels.SolarPanel;
import org.zeith.solarflux.panels.SolarPanel.SolarPanelData;
import org.zeith.solarflux.panels.SolarPanels;

public class SFNetwork
{
	public static final ResourceLocation CHANNEL_NAME = new ResourceLocation("solarflux", "main");
	public static EventNetworkChannel channel;

	public static void init()
	{
		channel = NetworkRegistry.newEventChannel(CHANNEL_NAME, () -> "18.1.1", v -> true, v -> true);
		channel.registerObject(SFNetwork.class);
	}

	@SubscribeEvent
	public static void onPacket(NetworkEvent e)
	{
		if(e instanceof NetworkEvent.LoginPayloadEvent)
		{
			NetworkDirection dir = e.getSource().get().getDirection();
			if(dir == NetworkDirection.LOGIN_TO_CLIENT)
			{
				FriendlyByteBuf pb = e.getPayload();
				if((int) pb.readShort() == 0x16)
				{
					String name = new String(pb.readByteArray());
					SolarPanel sp = SolarPanels.PANELS.get(name);
					if(sp != null)
						sp.networkData = new SolarPanelData(pb);
				}
			}
		} else
		{
			NetworkDirection dir = e.getSource().get().getDirection();
			if(dir == NetworkDirection.PLAY_TO_CLIENT && e instanceof NetworkEvent.ServerCustomPayloadEvent)
			{
				FriendlyByteBuf pb = e.getPayload();
				if(pb != null && (int) pb.readShort() == 0x16)
				{
					String name = new String(pb.readByteArray());
					SolarPanel sp = SolarPanels.PANELS.get(name);
					if(sp != null)
						sp.networkData = new SolarPanelData(pb);
					e.getSource().get().setPacketHandled(true);
				}
			}
		}
	}

	public static void sendAllPanels(ServerPlayer mp)
	{
		PacketDistributor.PacketTarget pt = PacketDistributor.PLAYER.with(() -> mp);
		SolarPanels.listPanels().forEach(i ->
		{
			FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

			buf.writeShort(0x16);
			buf.writeByteArray(i.name.getBytes());
			i.delegateData.write(buf);

			pt.send(new ClientboundCustomPayloadPacket(CHANNEL_NAME, buf));
		});
	}
}