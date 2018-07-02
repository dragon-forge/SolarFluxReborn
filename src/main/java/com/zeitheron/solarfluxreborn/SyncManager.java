package com.zeitheron.solarfluxreborn;

import com.zeitheron.hammercore.annotations.MCFBus;
import com.zeitheron.hammercore.event.PlayerLoadReadyEvent;
import com.zeitheron.hammercore.net.transport.NetTransport;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.solarfluxreborn.config.RemoteConfigs;
import com.zeitheron.solarfluxreborn.net.RemoteCfgAcceptor;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@MCFBus
public class SyncManager
{
	@SubscribeEvent
	public void playerReady(PlayerLoadReadyEvent evt)
	{
		EntityPlayerMP mp = WorldUtil.cast(evt.getEntityPlayer(), EntityPlayerMP.class);
		if(mp != null)
			NetTransport.builder().addData(RemoteConfigs.pack()).setAcceptor(RemoteCfgAcceptor.class).build().sendTo(mp);
	}
}