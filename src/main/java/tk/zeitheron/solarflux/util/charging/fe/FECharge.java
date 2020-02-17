package tk.zeitheron.solarflux.util.charging.fe;

import tk.zeitheron.solarflux.util.charging.AbstractCharge;

public class FECharge extends AbstractCharge
{
	public int FE;
	
	public FECharge(int fe)
	{
		this.FE = Math.max(fe, 0);
	}
	
	public FECharge discharge(int fe)
	{
		return new FECharge(this.FE - fe);
	}
	
	@Override
	public boolean containsCharge()
	{
		return FE > 0;
	}
	
	@Override
	public AbstractCharge copy()
	{
		return new FECharge(FE);
	}
}