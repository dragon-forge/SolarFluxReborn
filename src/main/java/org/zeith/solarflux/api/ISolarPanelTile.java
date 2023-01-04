package org.zeith.solarflux.api;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.solarflux.attribute.SimpleAttributeProperty;
import org.zeith.solarflux.items._base.UpgradeItem;
import org.zeith.solarflux.panels.SolarPanel;
import org.zeith.solarflux.panels.SolarPanelInstance;
import org.zeith.solarflux.util.BlockPosFace;

import java.util.List;
import java.util.stream.Stream;

public interface ISolarPanelTile
{
	int getUpgrades(Item type);
	
	Stream<Tuple2<UpgradeItem, ItemStack>> getUpgrades();
	
	SolarPanel getDelegate();
	
	SolarPanelInstance getInstance();
	
	int getGeneration();
	
	boolean doesSeeSky();
	
	World level();
	
	BlockPos pos();
	
	List<BlockPosFace> traversal();
	
	long energy();
	
	void energy(long newEnergy);
	
	SimpleAttributeProperty generation();
	
	SimpleAttributeProperty capacity();
	
	SimpleAttributeProperty transfer();
}