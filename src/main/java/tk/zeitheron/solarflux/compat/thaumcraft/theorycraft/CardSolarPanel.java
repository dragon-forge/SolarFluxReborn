package tk.zeitheron.solarflux.compat.thaumcraft.theorycraft;

import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.compat.thaumcraft.CompatThaumcraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import scala.util.Random;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;

public class CardSolarPanel extends TheorycraftCard
{
	@Override
	public boolean isAidOnly()
	{
		return true;
	}
	
	@Override
	public int getInspirationCost()
	{
		return 1;
	}
	
	@Override
	public String getResearchCategory()
	{
		return CompatThaumcraft.RES_CAT.key;
	}
	
	@Override
	public String getLocalizedName()
	{
		return new TextComponentTranslation("card." + InfoSF.MOD_ID + ":solar_panel.name").getFormattedText();
	}
	
	@Override
	public String getLocalizedText()
	{
		int min = 21 + new Random(getSeed()).nextInt(10);
		int max = min + new Random(getSeed()).nextInt(48) + 1;
		
		return I18n.translateToLocalFormatted("card." + InfoSF.MOD_ID + ":solar_panel.text", min, max);
	}
	
	@Override
	public boolean activate(EntityPlayer player, ResearchTableData data)
	{
		int min = 21 + new Random(getSeed()).nextInt(10);
		int max = min + new Random(getSeed()).nextInt(48) + 1;
		
		data.addTotal(CompatThaumcraft.RES_CAT.key, min + player.getRNG().nextInt(max - min));
		data.bonusDraws++;
		
		if(player.getRNG().nextFloat() < .7F)
			data.addInspiration(1);
		
		return true;
	}
}