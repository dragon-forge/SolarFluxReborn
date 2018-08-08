package com.zeitheron.solarflux.proxy;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.zeitheron.solarflux.SolarFlux;
import com.zeitheron.solarflux.block.tile.TileBaseSolar;
import com.zeitheron.solarflux.client.TESRSolarPanel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
	
	private void registerRender(Item item)
	{
		SolarFlux.LOG.info("Model definition for item " + item.getRegistryName());
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}