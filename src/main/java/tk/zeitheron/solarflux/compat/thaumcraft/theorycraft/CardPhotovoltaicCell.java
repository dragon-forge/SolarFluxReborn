package tk.zeitheron.solarflux.compat.thaumcraft.theorycraft;

import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.compat.thaumcraft.CompatThaumcraft;
import tk.zeitheron.solarflux.init.ItemsSF;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import scala.util.Random;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardPhotovoltaicCell extends TheorycraftCard
{
	@Override
	public int getInspirationCost()
	{
		return 1;
	}
	
	@Override
	public ItemStack[] getRequiredItems()
	{
		return new ItemStack[] { new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_1) };
	}
	
	@Override
	public String getResearchCategory()
	{
		return CompatThaumcraft.RES_CAT.key;
	}
	
	@Override
	public String getLocalizedName()
	{
		return new TextComponentTranslation("card." + InfoSF.MOD_ID + ":photovoltaic_cell.name").getFormattedText();
	}
	
	@Override
	public String getLocalizedText()
	{
		int min = 1 + new Random(getSeed()).nextInt(5);
		int max = min + new Random(getSeed()).nextInt(10) + 1;
		
		return I18n.translateToLocalFormatted("card." + InfoSF.MOD_ID + ":photovoltaic_cell.text", min, max);
	}
	
	@Override
	public boolean activate(EntityPlayer player, ResearchTableData data)
	{
		int min = 1 + new Random(getSeed()).nextInt(5);
		int max = min + new Random(getSeed()).nextInt(10) + 1;
		
		data.addTotal(CompatThaumcraft.RES_CAT.key, min + player.getRNG().nextInt(max - min));
		data.bonusDraws++;
		
		return true;
	}
}