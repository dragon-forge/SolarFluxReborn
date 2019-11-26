package tk.zeitheron.solarflux.net;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.GatherLoginPayloadsEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
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
		channel = NetworkRegistry.newEventChannel(CHANNEL_NAME, () -> "14.1.0", v -> true, v -> true);
		channel.registerObject(SFNetwork.class);
	}
	
	@SubscribeEvent
	public static void loginPacket(GatherLoginPayloadsEvent e)
	{
		SolarPanels.listPanels().forEach(sp ->
		{
			PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
			buf.writeShort(0xF016);
			buf.writeByteArray(sp.name.getBytes());
			sp.delegateData.write(buf);
//			e.add(buf, CHANNEL_NAME, "solar panel data for [" + sp.name + "]");
		});
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
				switch((int) pb.readShort())
				{
				case 0xF016:
				{
					String name = new String(pb.readByteArray());
					SolarPanel sp = SolarPanels.PANELS.get(name);
					if(sp != null)
						sp.networkData = new SolarPanelData(pb);
				}
				break;
				default:
				break;
				}
			}
		} else
		{
			NetworkDirection dir = e.getSource().get().getDirection();
			if(dir == NetworkDirection.PLAY_TO_CLIENT)
			{
				
			}
		}
	}
}