package org.zeith.solarflux.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.flowgui.reader.XmlFlowgui;
import org.zeith.hammerlib.client.screen.FlowguiScreen;
import org.zeith.solarflux.block.SolarPanelTile;
import org.zeith.solarflux.container.SolarPanelContainer;
import org.zeith.solarflux.util.ComplexProgressManager;

@XmlFlowgui("solar_panel")
public class SolarPanelScreen
		extends FlowguiScreen<SolarPanelContainer>
{
	private final ComplexProgressManager data;
	public final Component name, inventoryName;
	
	public long energy, capacity, currentGeneration, generation;
	public float sunIntensity, energyFill;
	
	public SolarPanelScreen(SolarPanelContainer ctr, Inventory inv, Component titleIn)
	{
		super(ctr, inv, titleIn);
		this.data = ctr.progressHandler;
		SolarPanelTile solar = ctr.panel;
		this.name = solar.getBlockState().getBlock().getName();
		this.inventoryName = inv.getName();
	}
	
	@Override
	public void containerTick()
	{
		energy = data.getLong(0);
		capacity = data.getLong(8);
		currentGeneration = data.getLong(16);
		generation = data.getLong(24);
		sunIntensity = data.getFloat(32);
		energyFill = (float) (energy / (double) capacity);
		super.containerTick();
	}
}