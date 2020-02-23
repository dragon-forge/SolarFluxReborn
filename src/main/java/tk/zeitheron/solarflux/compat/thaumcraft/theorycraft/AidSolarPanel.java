package tk.zeitheron.solarflux.compat.thaumcraft.theorycraft;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class AidSolarPanel implements ITheorycraftAid
{
	final Block solar;
	
	public AidSolarPanel(Block solar)
	{
		this.solar = solar;
	}
	
	@Override
	public Object getAidObject()
	{
		return new ItemStack(solar);
	}
	
	@Override
	public Class<TheorycraftCard>[] getCards()
	{
		return new Class[] { CardSolarPanel.class, CardPhotovoltaicCell.class };
	}
}