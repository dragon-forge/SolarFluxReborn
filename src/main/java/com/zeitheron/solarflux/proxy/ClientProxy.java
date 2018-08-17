package com.zeitheron.solarflux.proxy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.zeitheron.solarflux.SolarFlux;
import com.zeitheron.solarflux.block.tile.TileBaseSolar;
import com.zeitheron.solarflux.client.TESRSolarPanel;
import com.zeitheron.solarflux.init.SolarsSF;
import com.zeitheron.solarflux.net.NetworkSF;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ClientProxy implements ISFProxy
{
	private List<Item> render = new ArrayList<>();
	
	@Override
	public void init()
	{
		render.forEach(this::registerRender);
		render.clear();
		render = null;
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileBaseSolar.class, new TESRSolarPanel());
	}
	
	@Override
	public void render(Item item)
	{
		render.add(item);
	}
	
	@Override
	public void updateWindow(int window, int key, int val)
	{
		Minecraft.getMinecraft().addScheduledTask(() ->
		{
			EntityPlayer pl = Minecraft.getMinecraft().player;
			if(pl != null && pl.openContainer != null && pl.openContainer.windowId == window)
				pl.openContainer.updateProgressBar(key, val);
		});
	}
	
	@SubscribeEvent
	public void guiInit(InitGuiEvent.Post e)
	{
		if(e.getGui() instanceof GuiMainMenu)
		{
			Calendar c = Calendar.getInstance();
			if(c.get(Calendar.MONTH) == Calendar.NOVEMBER && c.get(Calendar.DAY_OF_MONTH) == 10)
			{
				GuiMainMenu gmm = (GuiMainMenu) e.getGui();
				gmm.splashText = "Happy hatchday, Zeitheron!";
			}
		}
	}
	
	boolean requested;
	
	@SubscribeEvent
	public void clientTick(ClientTickEvent e)
	{
		if(e.phase != Phase.END)
			return;
		boolean ign = Minecraft.getMinecraft().player != null;
		
		if(!ign && requested)
		{
			requested = false;
			SolarFlux.LOG.info("Restoring client settings for solar panels...");
			SolarsSF.reloadConfigs();
			SolarFlux.LOG.info("Solar Configs Restored!");
		}
	}
	
	@SubscribeEvent
	public void render(RenderGameOverlayEvent.Post e)
	{
		if(e.getType() == ElementType.ALL)
			if(!requested)
			{
				requested = true;
				NetworkSF.INSTANCE.request();
			}
	}
	
	private void registerRender(Item item)
	{
		SolarFlux.LOG.info("Model definition for item " + item.getRegistryName());
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}