package org.zeith.solarflux.compat.draconicevolution;

import com.brandon3055.brandonscore.api.TechLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.compat.base.BaseCompat;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.compat._base.SolarFluxCompat;
import org.zeith.solarflux.init.SolarPanelsSF;
import org.zeith.solarflux.panels.SolarPanel;

import java.util.List;
import java.util.function.*;

import static org.zeith.solarflux.compat.draconicevolution.FusionRecipeAPI.ingr;

@BaseCompat.LoadCompat(
		modid = "draconicevolution",
		compatType = SolarFluxCompat.class
)
public class DraconicEvolutionCompat
		extends SolarFluxCompat
{
	public final ResourceLocation wyvernRecipe = SolarFlux.id("solar_panels/draconicevolution/wyvern");
	public final ResourceLocation draconicRecipe = SolarFlux.id("solar_panels/draconicevolution/draconic");
	public final ResourceLocation chaoticRecipe = SolarFlux.id("solar_panels/draconicevolution/chaotic");
	
	public SolarPanel wyvern, draconic, chaotic;
	
	@Override
	public void registerSolarPanels(Supplier<SolarPanel.Builder> factory, Function<SolarPanel.Builder, SolarPanel> registrar)
	{
		wyvern = registrar.apply(
				factory.get()
						.name("de.wyvern")
						.generation(64 * 1024)
						.transfer(512 * KILO)
						.capacity(256 * MEGA)
		);
		
		draconic = registrar.apply(
				factory.get()
						.name("de.draconic")
						.generation(256 * 1024)
						.transfer(1024 * KILO)
						.capacity(512 * MEGA)
		);
		
		chaotic = registrar.apply(
				factory.get()
						.name("de.chaotic")
						.generation(512 * 1024)
						.transfer(4096 * KILO)
						.capacity(2048 * MEGA)
		);
	}
	
	@Override
	public void indexRecipes(Consumer<ResourceLocation> recipes)
	{
		recipes.accept(wyvernRecipe);
		recipes.accept(draconicRecipe);
		recipes.accept(chaoticRecipe);
	}
	
	@Override
	public void registerRecipes(RegisterRecipesEvent e)
	{
		try
		{
			Function<String, Item> items = (id) -> ForgeRegistries.ITEMS.getValue(new ResourceLocation("draconicevolution", id));
			
			var chaoticCore = items.apply("chaotic_core");
			var awakenedCore = items.apply("awakened_core");
			var wyvernEnergyCore = items.apply("wyvern_energy_core");
			var wyvernCore = items.apply("wyvern_core");
			var draconicEnergyCore = items.apply("draconic_energy_core");
			
			e.shaped()
					.id(wyvernRecipe)
					.result(wyvern, 2)
					.shape("sps", "pcp", "sps")
					.map('s', SolarPanelsSF.getGeneratingSolars(SolarPanelsSF.CORE_PANELS[7]))
					.map('p', new ItemStack(wyvernEnergyCore))
					.map('c', new ItemStack(wyvernCore))
					.registerIf(SolarPanelsSF::isRecipeActive);
			
			e.shaped()
					.id(draconicRecipe)
					.result(draconic, 2)
					.shape("sps", "pcp", "sps")
					.map('s', SolarPanelsSF.getGeneratingSolars(wyvern))
					.map('p', new ItemStack(draconicEnergyCore))
					.map('c', new ItemStack(awakenedCore))
					.registerIf(SolarPanelsSF::isRecipeActive);
			
			if(SolarPanelsSF.isRecipeActive(chaoticRecipe))
				FusionRecipeAPI.register(chaoticRecipe, FusionRecipeAPI.create(chaoticRecipe, new ItemStack(chaotic.getBlock(), 4), Ingredient.of(chaoticCore), 256L * MEGA, TechLevel.CHAOTIC, List.of(
						ingr(new ItemStack(awakenedCore)),
						ingr(SolarPanelsSF.getGeneratingSolars(draconic)),
						ingr(new ItemStack(awakenedCore)),
						ingr(SolarPanelsSF.getGeneratingSolars(draconic)),
						ingr(new ItemStack(awakenedCore)),
						ingr(SolarPanelsSF.getGeneratingSolars(draconic)),
						ingr(new ItemStack(awakenedCore))
				)), e);
		} catch(LinkageError | ReflectiveOperationException error)
		{
			SolarFlux.LOG.fatal("Failed to register DraconicEvolution recipes!", error);
		}
	}
}