package tk.zeitheron.solarflux.proxy;

import java.util.Map;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Locale;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.client.SolarPanelBakedModel;
import tk.zeitheron.solarflux.client.screen.SolarPanelScreen;
import tk.zeitheron.solarflux.panels.SolarPanel;
import tk.zeitheron.solarflux.panels.SolarPanels;

public class SFRClientProxy extends SFRCommonProxy
{
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup()
	{
		ScreenManager.registerFactory(SolarFlux.SOLAR_PANEL_CONTAINER, SolarPanelScreen::new);
	}
	
	@Override
	public void commonSetup()
	{
		super.commonSetup();
	}
	
	private Locale prevLocale;
	
	@SubscribeEvent
	public void clientTick(ClientTickEvent e)
	{
		if(prevLocale != I18n.i18nLocale)
			prevLocale = I18n.i18nLocale;
		if(prevLocale != null)
		{
			String lang = Minecraft.getInstance().gameSettings.language;
			Map<String, String>[] langMaps = new Map[] { prevLocale.properties, LanguageMap.getInstance().languageList };
			for(Map<String, String> langs : langMaps)
			{
				if(!Objects.equals(langs.get("solarflux.langsapplied"), lang))
				{
					langs.put("solarflux.langsapplied", lang);
					SolarPanels.listPanels().filter(SolarPanel::hasLang).forEach(p ->
					{
						ResourceLocation path = p.getBlock().getRegistryName();
						langs.put("block." + path.getNamespace() + "." + path.getPath(), p.getLang().getName(lang));
					});
				}
			}
		}
	}
	
	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre e)
	{
		if(e.getMap().getBasePath().equals("terrain"))
			SolarPanels.listPanelBlocks().forEach(spb ->
			{
				e.addSprite(new ResourceLocation(spb.getRegistryName().getNamespace(), "blocks/" + spb.getRegistryName().getPath() + "_base"));
				e.addSprite(new ResourceLocation(spb.getRegistryName().getNamespace(), "blocks/" + spb.getRegistryName().getPath() + "_top"));
			});
	}
	
	@SubscribeEvent
	public void modelBake(ModelBakeEvent e)
	{
		SolarPanels.listPanelBlocks().forEach(spb -> e.getModelRegistry().put(new ModelResourceLocation(spb.getRegistryName(), ""), new SolarPanelBakedModel(spb)));
	}
}