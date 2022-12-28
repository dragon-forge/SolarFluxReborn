package org.zeith.solarflux.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.zeith.solarflux.attribute.SimpleAttributeProperty;
import org.zeith.solarflux.panels.SolarPanel;
import org.zeith.solarflux.panels.SolarPanelInstance;
import org.zeith.solarflux.util.BlockPosFace;

import java.util.List;

public interface ISolarPanelTile
{
	int getUpgrades(Item type);
	
	SolarPanel getDelegate();
	
	SolarPanelInstance getInstance();
	
	int getGeneration();
	
	boolean doesSeeSky();
	
	Level level();
	
	BlockPos pos();
	
	List<BlockPosFace> traversal();
	
	long energy();
	
	void energy(long newEnergy);
	
	SimpleAttributeProperty generation();
	
	SimpleAttributeProperty capacity();
	
	SimpleAttributeProperty transfer();
}