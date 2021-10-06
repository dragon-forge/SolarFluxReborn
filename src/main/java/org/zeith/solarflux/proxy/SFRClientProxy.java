package org.zeith.solarflux.proxy;

import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.client.screen.SolarPanelScreen;
import org.zeith.solarflux.panels.SolarPanels;

public class SFRClientProxy
		extends SFRCommonProxy
{
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup()
	{
	}

	@Override
	public void commonSetup()
	{
		super.commonSetup();
	}

	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre e)
	{
		if(e.getMap().location().equals(PlayerContainer.BLOCK_ATLAS))
			SolarPanels.listPanelBlocks().forEach(spb ->
			{
				e.addSprite(new ResourceLocation(spb.getRegistryName().getNamespace(), "blocks/" + spb.getRegistryName().getPath() + "_base"));
				e.addSprite(new ResourceLocation(spb.getRegistryName().getNamespace(), "blocks/" + spb.getRegistryName().getPath() + "_top"));
			});
	}
}