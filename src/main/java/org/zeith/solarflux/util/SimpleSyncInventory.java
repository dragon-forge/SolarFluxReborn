package org.zeith.solarflux.util;

import org.zeith.hammerlib.api.inv.SimpleInventory;

public class SimpleSyncInventory
		extends SimpleInventory
{
	private Runnable sync;
	
	public SimpleSyncInventory(int slots, Runnable sync)
	{
		super(slots);
		this.sync = sync;
	}
	
	@Override
	public void setChanged()
	{
		if(sync != null)
			sync.run();
		super.setChanged();
	}
}
