package tk.zeitheron.solarflux.compat.avaritia;

import java.util.List;

import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.api.SolarInfo;
import tk.zeitheron.solarflux.api.compat.ISolarFluxCompat;
import tk.zeitheron.solarflux.api.compat.SFCompat;
import tk.zeitheron.solarflux.init.SolarsSF;
import tk.zeitheron.solarflux.utils.MetricUnits;

import morph.avaritia.init.ModBlocks;
import morph.avaritia.init.ModItems;
import morph.avaritia.recipe.AvaritiaRecipeManager;
import morph.avaritia.recipe.extreme.ExtremeShapedRecipe;
import morph.avaritia.recipe.extreme.IExtremeRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

@SFCompat(modid = "avaritia")
public class CompatAvaritia implements ISolarFluxCompat
{
	private SolarInfo neutronium, infinity;
	
	@Override
	public void registerSolarInfos(List<SolarInfo> panels)
	{
		panels.add(neutronium = new SolarInfo(8_192 * 1024, 32_768 * MetricUnits.KILO, 131_072_000_000L).setRegistryName(InfoSF.MOD_ID, "neutronium"));
		panels.add(infinity = new SolarInfo(16_384 * 1024, 65_536 * MetricUnits.KILO, 262_144_000_000L).setRegistryName(InfoSF.MOD_ID, "infinity"));
	}
	
	@Override
	public void preInit()
	{
		extremeShaped(new ResourceLocation(InfoSF.MOD_ID, "solar_panel_neutronium"), new ItemStack(neutronium.getBlock(), 2), "  nn nn  ", " nccsccn ", "nc  g  cn", "nc ppp cn", " sgpipgs ", "nc ppp cn", "nc  g  cn", " nccsccn ", "  nn nn  ", 'n', ModItems.neutronium_ingot.copy(), 'c', ModItems.crystal_matrix_ingot.copy(), 'g', ModItems.neutron_nugget.copy(), 'p', ModItems.neutron_pile.copy(), 'i', ModItems.infinity_catalyst.copy(), 's', SolarsSF.getGeneratingSolars(SolarsSF.CORE_PANELS[7].getGeneration()));
		extremeShaped(new ResourceLocation(InfoSF.MOD_ID, "solar_panel_infinity"), new ItemStack(infinity.getBlock(), 3), "  nn nn  ", " nccsccn ", "nc  g  cn", "nc pip cn", " sgiFigs ", "nc pip cn", "nc  g  cn", " nccsccn ", "  nn nn  ", 'n', ModItems.neutronium_ingot.copy(), 'c', ModItems.crystal_matrix_ingot.copy(), 'g', ModItems.neutron_nugget.copy(), 'p', ModItems.neutron_pile.copy(), 'F', new ItemStack(neutronium.getBlock()), 'i', ModItems.infinity_ingot.copy(), 's', new ItemStack(ModBlocks.resource));
	}
	
	public static void extremeShaped(ResourceLocation id, ItemStack output, Object... recipe)
	{
		IExtremeRecipe r = new ExtremeShapedRecipe(output, CraftingHelper.parseShaped(recipe)).setRegistryName(id);
		
		// Do some lewd registration stuff
		AvaritiaRecipeManager.EXTREME_RECIPES.put(id, r);
	}
}