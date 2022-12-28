package org.zeith.solarflux.compat._base;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.compat.base.Ability;
import org.zeith.hammerlib.compat.base.CompatList;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.solarflux.panels.SolarPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SFCompatList
		extends CompatList<SolarFluxCompat>
{
	public SFCompatList(List<? extends SolarFluxCompat> lst)
	{
		super(lst);
	}
	
	private static final Ability<PanelsListWrapper> PANELS_LIST_WRAPPER_ABILITY = new Ability<>(PanelsListWrapper.class);
	
	record PanelsListWrapper(List<SolarPanel> panels)
	{
	}
	
	public void setupSolarPanels()
	{
		Supplier<SolarPanel.Builder> factory = SolarPanel::builder;
		
		for(var compat : getActive())
		{
			var panels = compat.getAbility(PANELS_LIST_WRAPPER_ABILITY).orElseThrow().panels();
			compat.registerSolarPanels(factory, builder ->
			{
				var sp = builder.buildAndRegister()
						// Ensure that we have set the
						.setCompatMod(compat.getCompatModID());
				panels.add(sp);
				return sp;
			});
		}
	}
	
	public void indexRecipes(Consumer<ResourceLocation[]> recipes)
	{
		List<ResourceLocation> all = new ArrayList<>();
		for(var compat : getActive())
		{
			compat.indexRecipes(all::add);
		}
		recipes.accept(all.toArray(ResourceLocation[]::new));
	}
	
	public void registerRecipes(RegisterRecipesEvent e)
	{
		for(var compat : getActive())
		{
			compat.registerRecipes(e);
		}
	}
}