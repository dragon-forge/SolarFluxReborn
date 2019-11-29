package tk.zeitheron.solarflux.util.charging.modules;

import java.util.List;

import tk.zeitheron.solarflux.util.charging.IPlayerInventoryLister;

public interface IChargeModule
{
	default void registerInvListers(List<IPlayerInventoryLister> listers)
	{
	}
}