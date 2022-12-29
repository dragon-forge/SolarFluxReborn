package org.zeith.solarflux.compat.twilightforest;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.compat.base.BaseCompat;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.compat._base.SolarFluxCompat;
import org.zeith.solarflux.init.ItemsSF;
import org.zeith.solarflux.init.SolarPanelsSF;
import org.zeith.solarflux.panels.SolarPanel;

import java.util.function.*;

@BaseCompat.LoadCompat(
		modid = "twilightforest",
		compatType = SolarFluxCompat.class
)
public class TwilightForestCompat
		extends SolarFluxCompat
{
	public final ResourceLocation fieryRecipe = SolarFlux.id("solar_panels/twilightforest/fiery");
	public final ResourceLocation carminiteRecipe = SolarFlux.id("solar_panels/twilightforest/carminite");
	
	public final ResourceLocation twiCell1Recipe = SolarFlux.id("twilightforest/twilight_cell_1");
	public final ResourceLocation twiCell2Recipe = SolarFlux.id("twilightforest/twilight_cell_2");
	public final ResourceLocation twiLightUpgradeRecipe = SolarFlux.id("twilightforest/twilight_upgrade");
	
	private SolarPanel fiery, carminite;
	
	public TwilightForestCompat()
	{
		super(ContentsSFTF.class);
	}
	
	@Override
	public void registerSolarPanels(Supplier<SolarPanel.Builder> factory, Function<SolarPanel.Builder, SolarPanel> registrar)
	{
		fiery = registrar.apply(factory.get()
				.name("tf.fiery")
				.copyEnergy(SolarPanelsSF.CORE_PANELS[5])
		);
		carminite = registrar.apply(factory.get()
				.name("tf.carminite")
				.copyEnergy(SolarPanelsSF.CORE_PANELS[6])
		);
	}
	
	@Override
	public void indexRecipes(Consumer<ResourceLocation> recipes)
	{
		recipes.accept(fieryRecipe);
		recipes.accept(carminiteRecipe);
		recipes.accept(twiCell1Recipe);
		recipes.accept(twiCell2Recipe);
		recipes.accept(twiLightUpgradeRecipe);
	}
	
	@Override
	public void registerRecipes(RegisterRecipesEvent e)
	{
		var fieryBlock = ItemTags.create(new ResourceLocation("forge", "storage_blocks/fiery"));
		var carminiteItem = ItemTags.create(new ResourceLocation("forge", "gems/carminite"));
		var ironwoodBlock = ItemTags.create(new ResourceLocation("forge", "storage_blocks/ironwood"));
		var ironwoodIngot = ItemTags.create(new ResourceLocation("forge", "ingots/ironwood"));
		var fieryIngot = ItemTags.create(new ResourceLocation("forge", "ingots/fiery"));
		var knightmetal = ItemTags.create(new ResourceLocation("forge", "ingots/knightmetal"));
		var steeleaf = ItemTags.create(new ResourceLocation("forge", "ingots/steeleaf"));
		var torchberries = ForgeRegistries.ITEMS.getValue(new ResourceLocation("twilightforest", "torchberries"));
		
		e.shaped()
				.id(twiLightUpgradeRecipe)
				.result(ContentsSFTF.TWI_LIGHT_UPGRADE)
				.shape("ici", "tbt", "iti")
				.map('i', ironwoodIngot)
				.map('c', Items.CLOCK)
				.map('t', torchberries)
				.map('b', ItemsSF.BLANK_UPGRADE)
				.registerIf(SolarPanelsSF::isRecipeActive);
		
		e.shaped()
				.id(twiCell1Recipe)
				.result(ContentsSFTF.TWILIGHT_CELL_1, 2)
				.shape("bbb", "gdg", "qcq")
				.map('b', ItemsSF.BLAZING_COATING)
				.map('g', fieryIngot)
				.map('d', steeleaf)
				.map('q', ironwoodBlock)
				.map('c', ItemsSF.PHOTOVOLTAIC_CELL_3)
				.registerIf(SolarPanelsSF::isRecipeActive);
		
		e.shaped()
				.id(twiCell2Recipe)
				.result(ContentsSFTF.TWILIGHT_CELL_2, 3)
				.shape("bbb", "gdg", "cqc")
				.map('b', ItemsSF.EMERALD_GLASS)
				.map('g', fieryBlock)
				.map('d', carminiteItem)
				.map('q', knightmetal)
				.map('c', ContentsSFTF.TWILIGHT_CELL_1)
				.registerIf(SolarPanelsSF::isRecipeActive);
		
		e.shaped()
				.id(fieryRecipe)
				.result(fiery, 2)
				.shape("ppp", "scs", "sbs")
				.map('s', SolarPanelsSF.getGeneratingSolars(SolarPanelsSF.CORE_PANELS[4]))
				.map('p', ContentsSFTF.TWILIGHT_CELL_1)
				.map('c', torchberries)
				.map('b', fieryBlock)
				.registerIf(SolarPanelsSF::isRecipeActive);
		
		e.shaped()
				.id(carminiteRecipe)
				.result(carminite, 2)
				.shape("ppp", "scs", "scs")
				.map('s', fiery)
				.map('p', ContentsSFTF.TWILIGHT_CELL_2)
				.map('c', carminiteItem)
				.registerIf(SolarPanelsSF::isRecipeActive);
	}
}