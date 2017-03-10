package com.mrdimka.solarfluxreborn.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

import com.mrdimka.solarfluxreborn.client.tesr.RenderCustomCable;
import com.mrdimka.solarfluxreborn.client.tesr.RenderSolarPanelTile;
import com.mrdimka.solarfluxreborn.config.ModConfiguration;
import com.mrdimka.solarfluxreborn.config.RemoteConfigs;
import com.mrdimka.solarfluxreborn.reference.Reference;
import com.mrdimka.solarfluxreborn.te.SolarPanelTileEntity;
import com.mrdimka.solarfluxreborn.te.cable.TileCustomCable;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(this);
		
		ClientRegistry.bindTileEntitySpecialRenderer(SolarPanelTileEntity.class, new RenderSolarPanelTile());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCustomCable.class, new RenderCustomCable());
	}
	
	@SubscribeEvent
	public void pte(RenderGameOverlayEvent e)
	{
		if(ModConfiguration.willNotify)
		{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("[" + Reference.MOD_NAME + "] WARNING: Your configs have been replaced."));
			ModConfiguration.updateNotification(false);
		}
	}
	
	@SubscribeEvent
	public void disconnect(PlayerLoggedOutEvent evt)
	{
		if(evt.player.world.isRemote)
			RemoteConfigs.reset();
	}
}