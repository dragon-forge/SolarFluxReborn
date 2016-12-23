package com.mrdimka.solarfluxreborn.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

import com.mrdimka.solarfluxreborn.reference.Reference;

public class ModConfigurationGui extends GuiConfig {
	public ModConfigurationGui(GuiScreen pGuiScreen) {
		super(pGuiScreen, new ConfigElement(ModConfiguration.getConfiguration().getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), Reference.MOD_ID, true, true, GuiConfig
				.getAbridgedConfigPath(ModConfiguration.getConfiguration().toString()));
	}
}
