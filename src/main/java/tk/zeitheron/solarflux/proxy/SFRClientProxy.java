package tk.zeitheron.solarflux.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.resources.ClientLanguageMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.client.screen.SolarPanelScreen;
import tk.zeitheron.solarflux.panels.SolarPanel;
import tk.zeitheron.solarflux.panels.SolarPanels;
import tk.zeitheron.solarflux.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

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
		ScreenManager.registerFactory(SolarFlux.SOLAR_PANEL_CONTAINER, SolarPanelScreen::new);
	}

	@Override
	public void commonSetup()
	{
		super.commonSetup();
	}

	private LanguageMap map;
	private Map<String, String> prevLanguageList, languageList;

	int t = 0;

	@SubscribeEvent
	public void clientTick(ClientTickEvent e)
	{
		if(languageList == null || ++t % 10 == 0)
		{
			t = 0;

			map = I18n.field_239501_a_;

			if(map instanceof ClientLanguageMap)
			{
				ClientLanguageMap clm = (ClientLanguageMap) map;

				String testVar = UUID.randomUUID().toString();

				if(clm.field_239495_c_.getClass() != HashMap.class)
					try
					{
						clm.field_239495_c_.put(testVar, testVar);
						clm.field_239495_c_.remove(testVar);
					} catch(Throwable err)
					{
						try
						{
							Field map = ReflectionUtil.lookupField(ClientLanguageMap.class, Map.class);
							if(map != null)
							{
								map.setAccessible(true);
								map.set(clm, new HashMap<>(clm.field_239495_c_));
								SolarFlux.LOG.info("Successfully updated ClientLanguageMap's languageMap field with a modifiable Map!");
							} else throw new NullPointerException("Failed to find a Map field in ClientLanguageMap!");
						} catch(ReflectiveOperationException | NullPointerException err2)
						{
							err2.printStackTrace();
							return;
						}
					}

				languageList = clm.field_239495_c_;
			}
		}

		if(prevLanguageList != languageList)
			prevLanguageList = languageList;

		if(prevLanguageList != null)
		{
			String lang = Minecraft.getInstance().gameSettings.language;

			Map<String, String>[] langMaps = new Map[]{
					languageList
			};

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
		if(e.getMap().getTextureLocation().equals(PlayerContainer.LOCATION_BLOCKS_TEXTURE))
			SolarPanels.listPanelBlocks().forEach(spb ->
			{
				e.addSprite(new ResourceLocation(spb.getRegistryName().getNamespace(), "blocks/" + spb.getRegistryName().getPath() + "_base"));
				e.addSprite(new ResourceLocation(spb.getRegistryName().getNamespace(), "blocks/" + spb.getRegistryName().getPath() + "_top"));
			});
	}
}