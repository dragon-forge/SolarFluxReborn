package tk.zeitheron.solarflux.compat.draconicevolution;

import java.util.List;

import com.brandon3055.draconicevolution.DEFeatures;
import com.brandon3055.draconicevolution.api.fusioncrafting.FusionRecipeAPI;
import com.brandon3055.draconicevolution.api.fusioncrafting.SimpleFusionRecipe;
import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.api.SolarInfo;
import tk.zeitheron.solarflux.api.compat.ISolarFluxCompat;
import tk.zeitheron.solarflux.api.compat.SFCompat;
import tk.zeitheron.solarflux.init.SolarsSF;
import tk.zeitheron.solarflux.utils.MetricUnits;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

@SFCompat(modid = "draconicevolution")
public class CompatDraconicEvolution implements ISolarFluxCompat
{
	private SolarInfo wyvern, draconic, chaotic;
	
	@Override
	public void registerSolarInfos(List<SolarInfo> panels)
	{
		panels.add(wyvern = new SolarInfo(64 * 1024, 512 * MetricUnits.KILO, 256 * MetricUnits.MEGA).setRegistryName(InfoSF.MOD_ID, "wyvern"));
		panels.add(draconic = new SolarInfo(256 * 1024, 1024 * MetricUnits.KILO, 512 * MetricUnits.MEGA).setRegistryName(InfoSF.MOD_ID, "draconic"));
		panels.add(chaotic = new SolarInfo(512 * 1024, 4096 * MetricUnits.KILO, 2048 * MetricUnits.MEGA).setRegistryName(InfoSF.MOD_ID, "chaotic"));
	}
	
	@Override
	public void init()
	{
		for(ItemStack solar : SolarsSF.getGeneratingSolars(draconic.getGeneration()).getMatchingStacks())
			FusionRecipeAPI.addRecipe(new SimpleFusionRecipe(new ItemStack(chaotic.getBlock(), 4), new ItemStack(DEFeatures.chaoticCore), 256 * MetricUnits.MEGA, 3, solar, new ItemStack(DEFeatures.awakenedCore), new ItemStack(draconic.getBlock()), new ItemStack(DEFeatures.awakenedCore), new ItemStack(draconic.getBlock()), new ItemStack(DEFeatures.awakenedCore), new ItemStack(draconic.getBlock()), new ItemStack(DEFeatures.awakenedCore)));
	}
	
	@Override
	public void registerRecipes(IForgeRegistry<IRecipe> ifr)
	{
		ifr.register(new ShapedOreRecipe(new ResourceLocation(InfoSF.MOD_ID), new ItemStack(wyvern.getBlock(), 2), "sps", "pcp", "sps", 's', SolarsSF.getGeneratingSolars(SolarsSF.CORE_PANELS[7].getGeneration()), 'p', new ItemStack(DEFeatures.wyvernEnergyCore), 'c', new ItemStack(DEFeatures.wyvernCore)).setRegistryName(InfoSF.MOD_ID, "solar_panel_wyvern"));
		ifr.register(new ShapedOreRecipe(new ResourceLocation(InfoSF.MOD_ID), new ItemStack(draconic.getBlock(), 2), "sps", "pcp", "sps", 's', SolarsSF.getGeneratingSolars(wyvern.getGeneration()), 'p', new ItemStack(DEFeatures.draconicEnergyCore), 'c', new ItemStack(DEFeatures.awakenedCore)).setRegistryName(InfoSF.MOD_ID, "solar_panel_draconic"));
	}
}