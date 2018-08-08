package com.zeitheron.solarfluxreborn.config;

import java.util.Set;

import com.zeitheron.hammercore.cfg.gui.HCConfigGui;
import com.zeitheron.solarfluxreborn.reference.InfoSFR;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class ConfigurationGuiFactory implements IModGuiFactory
{
	@Override
	public void initialize(Minecraft pMinecraftInstance)
	{
	}
	
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
	{
		return null;
	}
	
	@Override
	public boolean hasConfigGui()
	{
		return true;
	}
	
	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen)
	{
		return new HCConfigGui(parentScreen, ModConfiguration.getConfiguration(), InfoSFR.MOD_ID);
	}
}
