package org.zeith.solarflux.compat.de;

import com.brandon3055.brandonscore.api.TechLevel;
import com.brandon3055.draconicevolution.api.crafting.FusionRecipe;
import com.brandon3055.draconicevolution.init.DEContent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammerlib.event.recipe.RegisterRecipesEvent;
import org.zeith.hammerlib.util.cfg.ConfigFile;
import org.zeith.hammerlib.util.cfg.entries.ConfigEntryCategory;
import org.zeith.solarflux.InfoSF;
import org.zeith.solarflux.compat.ISFCompat;
import org.zeith.solarflux.compat.SFCompat;
import org.zeith.solarflux.panels.SolarPanel;
import org.zeith.solarflux.panels.SolarPanels;
import org.zeith.solarflux.util.MetricUnits;

import java.io.File;
import java.util.Arrays;

@SFCompat("draconicevolution")
public class DraconicEvoCompat
		implements ISFCompat
{
	public static final ResourceLocation WYVERN_PANEL = new ResourceLocation(InfoSF.MOD_ID, "wyvern_panel");
	public static final ResourceLocation DRACONIC_PANEL = new ResourceLocation(InfoSF.MOD_ID, "draconic_panel");
	public static final ResourceLocation CHAOTIC_PANEL = new ResourceLocation(InfoSF.MOD_ID, "chaotic_panel");

	private SolarPanel wyvernPanel, draconicPanel, chaoticPanel;

	public DraconicEvoCompat()
	{
		SolarPanels.indexRecipes(WYVERN_PANEL, DRACONIC_PANEL, CHAOTIC_PANEL);
	}

	public boolean enabled = true;

	private long[] wspCfg = {
			64 * 1024,
			512 * MetricUnits.KILO,
			256L * MetricUnits.MEGA
	};

	private long[] drspCfg = {
			256 * 1024,
			1024 * MetricUnits.KILO,
			512L * MetricUnits.MEGA
	};

	private long[] chspCfg = {
			512 * 1024,
			4096 * MetricUnits.KILO,
			2048L * MetricUnits.MEGA
	};

	private ConfigFile cfg;

	@Override
	public void setupConfigFile(File file)
	{
		cfg = new ConfigFile(file);
		ConfigEntryCategory general = cfg.getCategory("General");
		enabled = general.getBooleanEntry("Enabled", true).getValue();
		ConfigEntryCategory solar_panels = cfg.getCategory("Solar Panels");
		wspCfg = SolarPanels.setupPanel(solar_panels, "Wyvern", wspCfg[0], wspCfg[1], wspCfg[2]);
		drspCfg = SolarPanels.setupPanel(solar_panels, "Draconic", drspCfg[0], drspCfg[1], drspCfg[2]);
		chspCfg = SolarPanels.setupPanel(solar_panels, "Chaotic", chspCfg[0], chspCfg[1], chspCfg[2]);
		if(cfg.hasChanged()) cfg.save();
	}

	@Override
	public void registerPanels()
	{
		if(!enabled) return;

		wyvernPanel = SolarPanel.builder().name("de.wyvern").generation(wspCfg[0]).transfer(wspCfg[1]).capacity(wspCfg[2]).buildAndRegister();
		draconicPanel = SolarPanel.builder().name("de.draconic").generation(drspCfg[0]).transfer(drspCfg[1]).capacity(drspCfg[2]).buildAndRegister();
		chaoticPanel = SolarPanel.builder().name("de.chaotic").generation(chspCfg[0]).transfer(chspCfg[1]).capacity(chspCfg[2]).buildAndRegister();
	}

	@Override
	public void reloadRecipes(RegisterRecipesEvent e)
	{
		if(!enabled) return;

		e.shaped()
				.id(WYVERN_PANEL)
				.result(new ItemStack(wyvernPanel, 2))
				.shape("sps", "pcp", "sps")
				.map('s', SolarPanels.getGeneratingSolars(SolarPanels.CORE_PANELS[7].getPanelData().generation))
				.map('p', DEContent.energy_core_wyvern)
				.map('c', DEContent.core_wyvern)
				.registerIf(SolarPanels::isRecipeActive);

		e.shaped()
				.id(DRACONIC_PANEL)
				.result(new ItemStack(draconicPanel, 2))
				.shape("sps", "pcp", "sps")
				.map('s', SolarPanels.getGeneratingSolars(wyvernPanel.getPanelData().generation))
				.map('p', DEContent.energy_core_draconic)
				.map('c', DEContent.core_awakened)
				.registerIf(SolarPanels::isRecipeActive);

		if(SolarPanels.isRecipeActive(CHAOTIC_PANEL))
			e.add(new FusionRecipe(CHAOTIC_PANEL, new ItemStack(chaoticPanel, 2), Ingredient.of(DEContent.core_chaotic), 256 * MetricUnits.MEGA, TechLevel.CHAOTIC, Arrays.asList(
					new FusionRecipe.FusionIngredient(SolarPanels.getGeneratingSolars(draconicPanel.getPanelData().generation), true),
					new FusionRecipe.FusionIngredient(SolarPanels.getGeneratingSolars(draconicPanel.getPanelData().generation), true),
					new FusionRecipe.FusionIngredient(Ingredient.of(DEContent.core_awakened), true),
					new FusionRecipe.FusionIngredient(Ingredient.of(DEContent.core_awakened), true),
					new FusionRecipe.FusionIngredient(SolarPanels.getGeneratingSolars(draconicPanel.getPanelData().generation), true),
					new FusionRecipe.FusionIngredient(SolarPanels.getGeneratingSolars(draconicPanel.getPanelData().generation), true),
					new FusionRecipe.FusionIngredient(Ingredient.of(DEContent.core_awakened), true),
					new FusionRecipe.FusionIngredient(Ingredient.of(DEContent.core_awakened), true)
			)));
	}
}