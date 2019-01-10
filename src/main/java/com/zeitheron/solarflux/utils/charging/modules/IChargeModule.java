package com.zeitheron.solarflux.utils.charging.modules;

import java.util.List;

import com.zeitheron.solarflux.utils.charging.IPlayerInventoryLister;

public interface IChargeModule
{
	default void registerInvListers(List<IPlayerInventoryLister> listers)
	{
	}
}