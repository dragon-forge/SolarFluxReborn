package org.zeith.solarflux.proxy;

import net.neoforged.neoforge.common.NeoForge;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.api.proxy.IClientProxy;
import org.zeith.hammerlib.event.LanguageReloadEvent;
import org.zeith.solarflux.init.ItemsSF;
import org.zeith.solarflux.init.SolarPanelsSF;
import org.zeith.solarflux.items.JSItem;

public class SFRClientProxy
		extends SFRCommonProxy
		implements IClientProxy
{
	public SFRClientProxy()
	{
		HammerLib.EVENT_BUS.addListener(this::reloadLangs);
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