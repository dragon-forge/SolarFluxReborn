package org.zeith.solarflux.compat.twilightforest;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.core.adapter.RegistryAdapter;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.util.cfg.ConfigFile;
import org.zeith.hammerlib.util.cfg.entries.ConfigEntryCategory;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.compat.ISFCompat;
import org.zeith.solarflux.compat.SFCompat;
import org.zeith.solarflux.items.ItemEfficiencyUpgrade;
import org.zeith.solarflux.items.ItemsSF;
import org.zeith.solarflux.panels.SolarPanel;
import org.zeith.solarflux.panels.SolarPanels;

import java.io.File;

@SFCompat("twilightforest")
public class TwilightForestCompat
		implements ISFCompat
{
	public final ResourceLocation fieryRecipe = SolarFlux.id("solar_panels/twilightforest/fiery");
	public final ResourceLocation carminiteRecipe = SolarFlux.id("solar_panels/twilightforest/carminite");
	
	public final ResourceLocation twiCell1Recipe = SolarFlux.id("twilightforest/twilight_cell_1");
	public final ResourceLocation twiCell2Recipe = SolarFlux.id("twilightforest/twilight_cell_2");
	public final ResourceLocation twiLightUpgradeRecipe = SolarFlux.id("twilightforest/twilight_upgrade");
	
	private SolarPanel fiery, carminite;
	
	public TwilightForestCompat()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItems);
	}
	
	public void registerItems(RegistryEvent.Register<Item> itemRegistry)
	{
		RegistryAdapter.register(itemRegistry.getRegistry(), ContentsSFTF.class, SolarFlux.MOD_ID);
	}
	
	public boolean enabled = true;
	
	private long[] fieryCfg = {
			2048,
			16348,
			32000000
	};
	
	private long[] carminiteCfg = {
			8192,
			65536,
			64000000
	};
	
	private ConfigFile cfg;
	
	@Override
	public void setupConfigFile(File file)
	{
		cfg = new ConfigFile(file);
		ConfigEntryCategory general = cfg.getCategory("General");
		enabled = general.getBooleanEntry("Enabled", true).getValue();
		ConfigEntryCategory solar_panels = cfg.getCategory("Solar Panels");
		fieryCfg = SolarPanels.setupPanel(solar_panels, "Fiery", SolarPanels.CORE_PANELS[5]);
		carminiteCfg = SolarPanels.setupPanel(solar_panels, "Carminite", SolarPanels.CORE_PANELS[6]);
		if(cfg.hasChanged()) cfg.save();
	}
	
	@Override
	public void registerPanels()
	{
		fiery = SolarPanel.builder().name("tf.fiery").generation(fieryCfg[0]).transfer(fieryCfg[1]).capacity(fieryCfg[2]).buildAndRegister();
		carminite = SolarPanel.builder().name("tf.carminite").generation(carminiteCfg[0]).transfer(carminiteCfg[1]).capacity(carminiteCfg[2]).buildAndRegister();
	}
	
	@Override
	public void indexRecipes(IRecipeIndexer recipes)
	{
		recipes.index(fieryRecipe, carminiteRecipe, twiCell1Recipe, twiCell2Recipe, twiLightUpgradeRecipe);
	}
	
	@Override
	public void reloadRecipes(RegisterRecipesEvent e)
	{
		ITag.INamedTag<Item> fieryBlock = ItemTags.createOptional(new ResourceLocation("forge", "storage_blocks/fiery"));
		ITag.INamedTag<Item> carminiteItem = ItemTags.createOptional(new ResourceLocation("forge", "gems/carminite"));
		ITag.INamedTag<Item> ironwoodBlock = ItemTags.createOptional(new ResourceLocation("forge", "storage_blocks/ironwood"));
		ITag.INamedTag<Item> ironwoodIngot = ItemTags.createOptional(new ResourceLocation("forge", "ingots/ironwood"));
		ITag.INamedTag<Item> fieryIngot = ItemTags.createOptional(new ResourceLocation("forge", "ingots/fiery"));
		ITag.INamedTag<Item> knightmetal = ItemTags.createOptional(new ResourceLocation("forge", "ingots/knightmetal"));
		ITag.INamedTag<Item> steeleaf = ItemTags.createOptional(new ResourceLocation("forge", "ingots/steeleaf"));
		Item torchberries = ForgeRegistries.ITEMS.getValue(new ResourceLocation("twilightforest", "torchberries"));
		
		e.shaped()
				.id(twiLightUpgradeRecipe)
				.result(ContentsSFTF.TWI_LIGHT_UPGRADE)
				.shape("ici", "tbt", "iti")
				.map('i', ironwoodIngot)
				.map('c', Items.CLOCK)
				.map('t', torchberries)
				.map('b', ItemEfficiencyUpgrade.EFFICIENCY_UPGRADE)
				.registerIf(SolarPanels::isRecipeActive);
		
		e.shaped()
				.id(twiCell1Recipe)
				.result(ContentsSFTF.TWILIGHT_CELL_1, 2)
				.shape("bbb", "gdg", "qcq")
				.map('b', ItemsSF.BLAZING_COATING)
				.map('g', fieryIngot)
				.map('d', steeleaf)
				.map('q', ironwoodBlock)
				.map('c', ItemsSF.PHOTOVOLTAIC_CELL_3)
				.registerIf(SolarPanels::isRecipeActive);
		
		e.shaped()
				.id(twiCell2Recipe)
				.result(ContentsSFTF.TWILIGHT_CELL_2, 3)
				.shape("bbb", "gdg", "cqc")
				.map('b', ItemsSF.EMERALD_GLASS)
				.map('g', fieryBlock)
				.map('d', carminiteItem)
				.map('q', knightmetal)
				.map('c', ContentsSFTF.TWILIGHT_CELL_1)
				.registerIf(SolarPanels::isRecipeActive);
		
		e.shaped()
				.id(fieryRecipe)
				.result(fiery, 2)
				.shape("ppp", "scs", "sbs")
				.map('s', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[4]))
				.map('p', ContentsSFTF.TWILIGHT_CELL_1)
				.map('c', torchberries)
				.map('b', fieryBlock)
				.registerIf(SolarPanels::isRecipeActive);
		
		e.shaped()
				.id(carminiteRecipe)
				.result(carminite, 2)
				.shape("ppp", "scs", "scs")
				.map('s', fiery)
				.map('p', ContentsSFTF.TWILIGHT_CELL_2)
				.map('c', carminiteItem)
				.registerIf(SolarPanels::isRecipeActive);
	}
}