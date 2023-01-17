package org.zeith.solarflux.compat._base;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import org.zeith.api.registry.RegistryMapping;
import org.zeith.hammerlib.compat.base.Ability;
import org.zeith.hammerlib.compat.base.BaseCompat;
import org.zeith.hammerlib.core.adapter.RegistryAdapter;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.compat._abilities.AddedSolarPanels;
import org.zeith.solarflux.panels.SolarPanel;

import java.util.*;
import java.util.function.*;

/**
 * A base class for compatibility implementations for the SolarFlux mod.
 *
 * <p>This class provides a framework for creating compatibility between mods and the SolarFlux mod. It
 * allows you to create a single compatibility class that can be used to interact with the SolarFlux mod
 * and modify its behavior.
 *
 * <p>To use this class, simply extend it and override the desired methods to implement the desired
 * compatibility behavior. For example, you can override the {@link #registerSolarPanels(Supplier, Function)}
 * method to add custom solar panels to the game.
 *
 * <pre>
 * {@code
 *
 * public class ExampleSolarFluxCompat extends SolarFluxCompat {
 *
 *     // Override the registerSolarPanels() method to add custom solar panels to the game.
 *     public void registerSolarPanels(Supplier<SolarPanel.Builder> factory, Function<SolarPanel.Builder, SolarPanel> registrar) {
 *         // Use the factory and registrar to create and register a custom solar panel.
 *         registrar.apply(factory.get()
 *                  .name("custom_panel")
 *                  .generation(10_000)
 *                  .transfer(100_000)
 *                  .capacity(1_000_000)
 *                  .build()
 *         );
 *     }
 * }
 * }
 * </pre>
 *
 * <p>You can also override the {@link #indexRecipes(Consumer)} and {@link #registerRecipes(RegisterRecipesEvent)}
 * methods to add custom recipes for the solar panels or other items in the mod.
 */
public class SolarFluxCompat
		extends BaseCompat<SolarFluxCompat>
{
	private final List<SolarPanel> panels = new ArrayList<>();
	private final AddedSolarPanels panelsAbility = new AddedSolarPanels(panels::stream);
	private final SFCompatList.PanelsListWrapper panelsListWrapper = new SFCompatList.PanelsListWrapper(panels);
	
	public static final long KILO = 1000L;
	public static final long MEGA = KILO * 1000L;
	
	private final List<Class<?>> simplyRegisterClasses;
	
	public SolarFluxCompat(Class<?>... simplyRegisterClasses)
	{
		this.simplyRegisterClasses = Arrays.asList(simplyRegisterClasses);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerStuff);
	}
	
	private void registerStuff(RegisterEvent event)
	{
		IForgeRegistry<?> reg = event.getForgeRegistry();
		if(reg == null)
			reg = RegistryMapping.getRegistryByType(RegistryMapping.getSuperType(event.getRegistryKey()));
		for(var cls : simplyRegisterClasses)
			RegistryAdapter.register(event, reg, cls, SolarFlux.MOD_ID, getCompatModID() + "/");
	}
	
	/**
	 * Registers a new solar panel with the given factory and registrar functions.
	 *
	 * @param factory
	 * 		a supplier of a new {@link SolarPanel.Builder} instance
	 * @param registrar
	 * 		a function that takes a {@link SolarPanel.Builder} instance and returns a new {@link SolarPanel} instance
	 */
	public void registerSolarPanels(Supplier<SolarPanel.Builder> factory, Function<SolarPanel.Builder, SolarPanel> registrar)
	{
	}
	
	/**
	 * Indexes all the recipes this compatibility should provide.
	 *
	 * @param recipes
	 * 		a consumer that accepts a {@link ResourceLocation} representing the recipe to be added
	 */
	public void indexRecipes(Consumer<ResourceLocation> recipes)
	{
	}
	
	/**
	 * This method is called to register the compatibility's recipes to the game.
	 *
	 * @param e
	 * 		the event object for this recipe registration
	 */
	public void registerRecipes(RegisterRecipesEvent e)
	{
	}
	
	@Override
	public <R> Optional<R> getAbility(Ability<R> ability)
	{
		return ability.findIn(
				panelsAbility,
				panelsListWrapper
		).or(() -> super.getAbility(ability));
	}
	
	public final String getCompatModID()
	{
		return getClass().getAnnotation(BaseCompat.LoadCompat.class).modid();
	}
}