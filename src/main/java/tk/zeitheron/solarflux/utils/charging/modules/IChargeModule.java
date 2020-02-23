package tk.zeitheron.solarflux.utils.charging.modules;

import java.util.List;

import tk.zeitheron.solarflux.utils.charging.IPlayerInventoryLister;

public interface IChargeModule
{
	default void registerInvListers(List<IPlayerInventoryLister> listers)
	{
	}
}