package org.zeith.solarflux.compat.avaritia;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.zeith.hammerlib.compat.base.BaseCompat;
import org.zeith.hammerlib.compat.base.CompatContext;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.util.mcf.Resources;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.compat._base.SolarFluxCompat;
import org.zeith.solarflux.init.SolarPanelsSF;
import org.zeith.solarflux.panels.SolarPanel;

import java.util.HashMap;
import java.util.Map;
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
	
	public AvaritiaCompat(CompatContext ctx)
	{
		super(ctx);
	}
	
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
			Map<String, Item> itemMap = new HashMap<>();
			Function<String, Item> aItemF = k -> BuiltInRegistries.ITEM.get(Resources.location("avaritia", k));
			Function<String, Item> item = key -> itemMap.computeIfAbsent(key, aItemF);
			
			extremeShaped(e)
					.id(neutroniumRecipe)
					.result(neutronium, 2)
					.shape("  nn nn  ", " nccsccn ", "nc  g  cn", "nc ppp cn", " sgpipgs ", "nc ppp cn", "nc  g  cn", " nccsccn ", "  nn nn  ")
					.map('n', item.apply("neutronium_ingot"))
					.map('c', item.apply("crystal_matrix_ingot"))
					.map('g', item.apply("neutron_nugget"))
					.map('p', item.apply("neutron_pile"))
					.map('i', item.apply("infinity_catalyst"))
					.map('s', SolarPanelsSF.getGeneratingSolars(SolarPanelsSF.CORE_PANELS[7]))
					.registerIf(SolarPanelsSF::isRecipeActive);
			
			extremeShaped(e)
					.id(infinityRecipe)
					.result(infinity, 3)
					.shape("  nn nn  ", " nccsccn ", "nc  g  cn", "nc pip cn", " sgiFigs ", "nc pip cn", "nc  g  cn", " nccsccn ", "  nn nn  ")
					.map('n', item.apply("neutronium_ingot"))
					.map('c', item.apply("crystal_matrix_ingot"))
					.map('g', item.apply("neutron_nugget"))
					.map('p', item.apply("neutron_pile"))
					.map('F', SolarPanelsSF.getGeneratingSolars(neutronium))
					.map('i', item.apply("infinity_catalyst"))
					.map('s', item.apply("neutronium_block"))
					.registerIf(SolarPanelsSF::isRecipeActive);
		} catch(LinkageError error)
		{
			SolarFlux.LOG.error("Failed to register Avaritia recipes!", error);
		}
	}
	
	public static ExtremeShapedRecipeBuilder extremeShaped(RegisterRecipesEvent e)
	{
		return new ExtremeShapedRecipeBuilder(e);
	}
}