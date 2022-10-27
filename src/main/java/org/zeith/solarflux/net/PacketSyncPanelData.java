package org.zeith.solarflux.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.zeith.hammerlib.net.*;
import org.zeith.solarflux.init.SolarPanelsSF;
import org.zeith.solarflux.panels.SolarPanel;

@MainThreaded
public class PacketSyncPanelData
		implements IPacket
{
	private String name;
	private SolarPanel.SolarPanelData data;
	
	public PacketSyncPanelData(String name, SolarPanel.SolarPanelData data)
	{
		this.name = name;
		this.data = data;
	}
	
	public PacketSyncPanelData()
	{
	}
	
	@Override
	public void write(FriendlyByteBuf buf)
	{
		buf.writeUtf(name);
		data.write(buf);
	}
	
	@Override
	public void read(FriendlyByteBuf buf)
	{
		name = buf.readUtf();
		data = new SolarPanel.SolarPanelData(buf);
	}
	
	@Override
	public void clientExecute(PacketContext ctx)
	{
		SolarPanel sp = SolarPanelsSF.PANELS.get(name);
		if(sp != null) sp.networkData = data;
	}
	
	public static void sendAllPanels(ServerPlayer mp)
	{
		SolarPanelsSF.listPanels().forEach(i ->
		{
			Network.sendTo(new PacketSyncPanelData(i.name, i.delegateData), mp);
		});
	}
}