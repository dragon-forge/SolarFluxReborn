package org.zeith.solarflux.proxy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.event.LanguageReloadEvent;
import org.zeith.solarflux.init.ItemsSF;
import org.zeith.solarflux.init.SolarPanelsSF;
import org.zeith.solarflux.items.JSItem;

public class SFRClientProxy
		extends SFRCommonProxy
{
	{
		MinecraftForge.EVENT_BUS.register(this);
		HammerLib.EVENT_BUS.addListener(this::reloadLangs);
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
		if(e.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS))
			SolarPanelsSF.listPanelBlocks().forEach(spb ->
			{
				e.addSprite(new ResourceLocation(spb.getRegistryName().getNamespace(), "blocks/" + spb.getRegistryName().getPath() + "_base"));
				e.addSprite(new ResourceLocation(spb.getRegistryName().getNamespace(), "blocks/" + spb.getRegistryName().getPath() + "_top"));
			});
	}
	
	public void reloadLangs(LanguageReloadEvent e)
	{
		for(JSItem mat : ItemsSF.JS_MATERIALS)
		{
			e.translate(mat.getDescriptionId(), mat.getLang().getName(e.getLang()));
		}
		
		SolarPanelsSF.listPanels().forEach(sp ->
		{
			if(sp.isCustom)
			{
				e.translate(sp.getBlock().getDescriptionId(), sp.getLang().getName(e.getLang()));
			}
		});
	}
}