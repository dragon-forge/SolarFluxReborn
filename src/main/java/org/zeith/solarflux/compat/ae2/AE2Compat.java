package org.zeith.solarflux.compat.ae2;

import appeng.core.definitions.AEBlocks;
import net.minecraft.resources.ResourceLocation;
import org.zeith.hammerlib.compat.base.BaseCompat;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.compat._base.SolarFluxCompat;
import org.zeith.solarflux.init.ItemsSF;
import org.zeith.solarflux.init.SolarPanelsSF;

import java.util.function.Consumer;

@BaseCompat.LoadCompat(
		modid = "ae2",
		compatType = SolarFluxCompat.class
)
public class AE2Compat
		extends SolarFluxCompat
{
	public final ResourceLocation aeuEnergyUpgrade = SolarFlux.id("ae2/energy_upgrade");
	
	public AE2Compat()
	{
		super(ContentsSFAE2.class);
	}
	
	@Override
	public void indexRecipes(Consumer<ResourceLocation> recipes)
	{
		recipes.accept(aeuEnergyUpgrade);
	}
	
	@Override
	public void registerRecipes(RegisterRecipesEvent e)
	{
		e.shapeless()
				.id(aeuEnergyUpgrade)
				.add(AEBlocks.ENERGY_ACCEPTOR.asItem())
				.add(ItemsSF.BLANK_UPGRADE)
				.result(ContentsSFAE2.ENERGY_UPGRADE)
				.registerIf(SolarPanelsSF::isRecipeActive);
	}
}