package com.zeitheron.solarflux.proxy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zeitheron.solarflux.InfoSF;
import com.zeitheron.solarflux.SolarFlux;
import com.zeitheron.solarflux.api.SolarFluxAPI;
import com.zeitheron.solarflux.api.SolarInfo;
import com.zeitheron.solarflux.block.tile.TileBaseSolar;
import com.zeitheron.solarflux.client.TESRSolarPanel;
import com.zeitheron.solarflux.gui.ContainerBaseSolar;
import com.zeitheron.solarflux.init.SolarsSF;
import com.zeitheron.solarflux.net.NetworkSF;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.TextureStitchEvent;
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
	public void updateWindow(int window, int key, long val)
	{
		Minecraft.getMinecraft().addScheduledTask(() ->
		{
			EntityPlayer pl = Minecraft.getMinecraft().player;
			if(pl != null && pl.openContainer != null && pl.openContainer.windowId == window && pl.openContainer instanceof ContainerBaseSolar)
				((ContainerBaseSolar) pl.openContainer).updateProgressBar2(key, val);
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
	
	public static final Map<ResourceLocation, TextureAtlasSprite> TOPFS = new HashMap<>();
	
	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre e)
	{
		for(SolarInfo si : SolarFluxAPI.SOLAR_PANELS.getValuesCollection())
			TOPFS.put(si.getRegistryName(), e.getMap().registerSprite(si.getTexture()));
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