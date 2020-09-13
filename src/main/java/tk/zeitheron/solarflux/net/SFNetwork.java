package tk.zeitheron.solarflux.net;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.event.EventNetworkChannel;
import tk.zeitheron.solarflux.panels.SolarPanel;
import tk.zeitheron.solarflux.panels.SolarPanel.SolarPanelData;
import tk.zeitheron.solarflux.panels.SolarPanels;

public class SFNetwork
{
	public static final ResourceLocation CHANNEL_NAME = new ResourceLocation("solarflux", "main");
	public static EventNetworkChannel channel;

	public static void init()
	{
		channel = NetworkRegistry.newEventChannel(CHANNEL_NAME, () -> "15.1.0", v -> true, v -> true);
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
				PacketBuffer pb = e.getPayload();
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
				PacketBuffer pb = e.getPayload();
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

	public static void sendAllPanels(ServerPlayerEntity mp)
	{
		PacketDistributor.PacketTarget pt = PacketDistributor.PLAYER.with(() -> mp);
		SolarPanels.listPanels().forEach(i ->
		{
			PacketBuffer buf = new PacketBuffer(Unpooled.buffer());

			buf.writeShort(0x16);
			buf.writeByteArray(i.name.getBytes());
			i.delegateData.write(buf);

			pt.send(new SCustomPayloadPlayPacket(CHANNEL_NAME, buf));
		});
	}
}