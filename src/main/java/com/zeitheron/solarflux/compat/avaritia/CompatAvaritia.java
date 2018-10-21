package com.zeitheron.solarflux.compat.avaritia;

import java.util.List;

import com.zeitheron.solarflux.InfoSF;
import com.zeitheron.solarflux.api.SolarInfo;
import com.zeitheron.solarflux.api.compat.ISolarFluxCompat;
import com.zeitheron.solarflux.api.compat.SFCompat;
import com.zeitheron.solarflux.init.SolarsSF;
import com.zeitheron.solarflux.utils.MetricUnits;

import morph.avaritia.init.ModItems;
import morph.avaritia.recipe.AvaritiaRecipeManager;
import morph.avaritia.recipe.extreme.ExtremeCraftingManager;
import morph.avaritia.recipe.extreme.ExtremeShapedRecipe;
import morph.avaritia.recipe.extreme.IExtremeRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;

@SFCompat(modid = "avaritia")
public class CompatAvaritia implements ISolarFluxCompat
{
	private SolarInfo neutronium, infinite;
	
	@Override
	public void registerSolarInfos(List<SolarInfo> panels)
	{
		panels.add(neutronium = new SolarInfo(8_192 * 1024, 32_768 * MetricUnits.KILO, Integer.MAX_VALUE - 1 /* prevent buffer reset */).setRegistryName(InfoSF.MOD_ID, "neutronium"));
	}
	
	@Override
	public void init()
	{
		extremeShaped(new ResourceLocation(InfoSF.MOD_ID, "solar_panel_neutronium"), new ItemStack(neutronium.getBlock(), 2), "  nn nn  ", " nccsccn ", "nc  g  cn", "nc ppp cn", " sgpipgs ", "nc ppp cn", "nc  g  cn", " nccsccn ", "  nn nn  ", 'n', ModItems.neutronium_ingot.copy(), 'c', ModItems.crystal_matrix_ingot.copy(), 'g', ModItems.neutron_nugget.copy(), 'p', ModItems.neutron_pile.copy(), 'i', ModItems.infinity_catalyst.copy(), 's', SolarsSF.getGeneratingSolars(SolarsSF.SOLAR_8.maxGeneration));
	}
	
	public static void extremeShaped(ResourceLocation id, ItemStack output, Object... recipe)
	{
		IExtremeRecipe r = new ExtremeShapedRecipe(output, CraftingHelper.parseShaped(recipe)).setRegistryName(id);
		
		// Do some lewd registration stuff
		AvaritiaRecipeManager.EXTREME_RECIPES.put(id, r);
		ExtremeCraftingManager.REGISTRY.putObject(id, r);
	}
}