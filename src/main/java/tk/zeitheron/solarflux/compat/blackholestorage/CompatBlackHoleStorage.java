package tk.zeitheron.solarflux.compat.blackholestorage;

import java.util.List;

import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.api.SolarFluxAPI;
import tk.zeitheron.solarflux.api.SolarInfo;
import tk.zeitheron.solarflux.api.compat.ISolarFluxCompat;
import tk.zeitheron.solarflux.api.compat.SFCompat;
import tk.zeitheron.solarflux.init.ItemsSF;
import tk.zeitheron.solarflux.init.SolarsSF;
import tk.zeitheron.solarflux.utils.MetricUnits;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistry;

@SFCompat(modid = "blackholestorage")
public class CompatBlackHoleStorage implements ISolarFluxCompat
{
	private SolarInfo darkMatter, antiMatter;
	
	public static final Item dmPhotovoltaicCell = new Item().setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_dark_matter");
	
	@Override
	public void preInit()
	{
		SolarFluxAPI.registerItem.accept(dmPhotovoltaicCell);
	}
	
	@Override
	public void registerSolarInfos(List<SolarInfo> panels)
	{
		panels.add(darkMatter = new SolarInfo(64 * 1024, 512 * MetricUnits.KILO, 256 * MetricUnits.MEGA).setRegistryName(InfoSF.MOD_ID, "dark_matter"));
	}
	
	@Override
	public void registerRecipes(IForgeRegistry<IRecipe> ifr)
	{
		ifr.register(new ShapedOreRecipe(new ResourceLocation(InfoSF.MOD_ID), new ItemStack(dmPhotovoltaicCell), "ppp", "bmb", "gcg", 'p', Blocks.SEA_LANTERN, 'g', "glowstone", 'm', "matterDark", 'c', ItemsSF.PHOTOVOLTAIC_CELL_6, 'b', Items.BLAZE_ROD).setRegistryName(InfoSF.MOD_ID, "photovoltaic_cell_dark_matter"));
		ifr.register(new ShapedOreRecipe(new ResourceLocation(InfoSF.MOD_ID), new ItemStack(darkMatter.getBlock(), 2), "ccc", "sds", "sds", 'c', dmPhotovoltaicCell, 's', SolarsSF.getGeneratingSolars(SolarsSF.CORE_PANELS[7].getGeneration()), 'd', "matterDark").setRegistryName(InfoSF.MOD_ID, "solar_panel_dark_matter"));
	}
}