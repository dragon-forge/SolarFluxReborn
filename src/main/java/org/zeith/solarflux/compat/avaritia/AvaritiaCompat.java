package org.zeith.solarflux.compat.avaritia;

import morph.avaritia.init.AvaritiaModContent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.zeith.hammerlib.compat.base.BaseCompat;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.compat._base.SolarFluxCompat;
import org.zeith.solarflux.init.SolarPanelsSF;
import org.zeith.solarflux.panels.SolarPanel;

import java.util.function.*;

@BaseCompat.LoadCompat(
		modid = "avaritia",
		compatType = SolarFluxCompat.class
)
public class AvaritiaCompat
		extends SolarFluxCompat
{
	public final ResourceLocation neutroniumRecipe = SolarFlux.id("solar_panels/avaritia/neutronium");
	public final ResourceLocation infinityRecipe = SolarFlux.id("solar_panels/avaritia/infinity");
	
	public SolarPanel neutronium, infinity;
	
	@Override
	public void registerSolarPanels(Supplier<SolarPanel.Builder> factory, Function<SolarPanel.Builder, SolarPanel> registrar)
	{
		neutronium = registrar.apply(
				factory.get()
						.name("avaritia.neutronium")
						.generation(8_192 * 1024)
						.transfer(32_768 * 1000)
						.capacity(131_072_000_000L)
		);
		
		infinity = registrar.apply(
				factory.get()
						.name("avaritia.infinity")
						.generation(16_384 * 1024)
						.transfer(65_536 * 1000)
						.capacity(262_144_000_000L)
		);
	}
	
	@Override
	public void indexRecipes(Consumer<ResourceLocation> recipes)
	{
		recipes.accept(neutroniumRecipe);
		recipes.accept(infinityRecipe);
	}
	
	@Override
	public void registerRecipes(RegisterRecipesEvent e)
	{
		try
		{
			extremeShaped(e)
					.id(neutroniumRecipe)
					.result(neutronium, 2)
					.shape("  nn nn  ", " nccsccn ", "nc  g  cn", "nc ppp cn", " sgpipgs ", "nc ppp cn", "nc  g  cn", " nccsccn ", "  nn nn  ")
					.map('n', AvaritiaModContent.NEUTRONIUM_INGOT.get())
					.map('c', AvaritiaModContent.CRYSTAL_MATRIX_INGOT.get())
					.map('g', AvaritiaModContent.NEUTRON_NUGGET.get())
					.map('p', AvaritiaModContent.NEUTRON_PILE.get())
					.map('i', AvaritiaModContent.INFINITY_CATALYST.get())
					.map('s', SolarPanelsSF.getGeneratingSolars(SolarPanelsSF.CORE_PANELS[7].delegateData.generation))
					.registerIf(SolarPanelsSF::isRecipeActive);
			
			extremeShaped(e)
					.id(infinityRecipe)
					.result(infinity, 3)
					.shape("  nn nn  ", " nccsccn ", "nc  g  cn", "nc pip cn", " sgiFigs ", "nc pip cn", "nc  g  cn", " nccsccn ", "  nn nn  ")
					.map('n', AvaritiaModContent.NEUTRONIUM_INGOT.get())
					.map('c', AvaritiaModContent.CRYSTAL_MATRIX_INGOT.get())
					.map('g', AvaritiaModContent.NEUTRON_NUGGET.get())
					.map('p', AvaritiaModContent.NEUTRON_PILE.get())
					.map('F', new ItemStack(neutronium.getBlock()))
					.map('i', AvaritiaModContent.INFINITY_INGOT.get())
					.map('s', new ItemStack(AvaritiaModContent.NEUTRONIUM_STORAGE_BLOCK.get()))
					.registerIf(SolarPanelsSF::isRecipeActive);
		} catch(LinkageError error)
		{
			SolarFlux.LOG.fatal("Failed to register Avaritia recipes!", error);
		}
	}
	
	public static ExtremeShapedRecipeBuilder extremeShaped(RegisterRecipesEvent e)
	{
		return new ExtremeShapedRecipeBuilder(e);
	}
}