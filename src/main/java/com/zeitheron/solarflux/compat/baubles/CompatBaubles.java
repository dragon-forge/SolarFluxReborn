package com.zeitheron.solarflux.compat.baubles;

import java.util.List;

import com.zeitheron.solarflux.api.compat.ISolarFluxCompat;
import com.zeitheron.solarflux.api.compat.SFCompat;
import com.zeitheron.solarflux.utils.charging.IPlayerInventoryLister;

import baubles.api.BaublesApi;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandlerModifiable;

@SFCompat(modid = "baubles")
public class CompatBaubles implements ISolarFluxCompat, IPlayerInventoryLister
{
	@Override
	public void registerInvListers(List<IPlayerInventoryLister> listers)
	{
		listers.add(this);
	}
	
	@Override
	public void listItemHandlers(EntityPlayer player, List<IItemHandlerModifiable> handlers)
	{
		handlers.add(BaublesApi.getBaublesHandler(player));
	}
}