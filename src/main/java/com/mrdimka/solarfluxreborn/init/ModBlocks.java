package com.mrdimka.solarfluxreborn.init;

import java.util.List;

import com.google.common.collect.Lists;
import com.mrdimka.solarfluxreborn.blocks.BlockCable320;
import com.mrdimka.solarfluxreborn.blocks.BlockCable3200;
import com.mrdimka.solarfluxreborn.blocks.BlockCable320000;
import com.mrdimka.solarfluxreborn.blocks.DraconicSolarPanelBlock;
import com.mrdimka.solarfluxreborn.blocks.SolarPanelBlock;
import com.mrdimka.solarfluxreborn.config.DraconicEvolutionConfigs;
import com.mrdimka.solarfluxreborn.config.ModConfiguration;
import com.mrdimka.solarfluxreborn.items.CableItemBlock;
import com.mrdimka.solarfluxreborn.items.SolarPanelItemBlock;
import com.mrdimka.solarfluxreborn.utility.MetricUnits;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks
{
	private static final List<Block> mSolarPanels = Lists.newArrayList();
	
	public static final Block cable1 = new BlockCable320();
	public static final Block cable2 = new BlockCable3200();
	public static final Block cable3 = new BlockCable320000();
	
	public static DraconicSolarPanelBlock draconicSolar, chaoticSolar;
	
	private ModBlocks() {}
	
	public static void initialize()
	{
		mSolarPanels.clear();
		for(int tierIndex = 0; tierIndex < ModConfiguration.getTierConfigurations().size(); tierIndex++)
		{
			SolarPanelBlock block = new SolarPanelBlock("solar" + tierIndex, tierIndex);
			GameRegistry.registerBlock(block, SolarPanelItemBlock.class, "solar" + tierIndex);
			mSolarPanels.add(block);
		}
		
		if(DraconicEvolutionConfigs.canIntegrate)
		{
			if(DraconicEvolutionConfigs.draconicSolar)
			{
				DraconicSolarPanelBlock block = new DraconicSolarPanelBlock("solardraconic", 512 * MetricUnits.MEGA, 1024 * MetricUnits.KILO, 131072);
				GameRegistry.registerBlock(draconicSolar = block, SolarPanelItemBlock.class, "solardraconic");
				mSolarPanels.add(block);
			}
			
			if(DraconicEvolutionConfigs.chaoticSolar)
			{
				DraconicSolarPanelBlock block = new DraconicSolarPanelBlock("solarchaotic", 2048 * MetricUnits.MEGA, 4096 * MetricUnits.KILO, 524288);
				GameRegistry.registerBlock(chaoticSolar = block, SolarPanelItemBlock.class, "solarchaotic");
				mSolarPanels.add(block);
			}
		}
		
		GameRegistry.registerBlock(cable1, CableItemBlock.class, "wire_1");
		GameRegistry.registerBlock(cable2, CableItemBlock.class, "wire_2");
		GameRegistry.registerBlock(cable3, CableItemBlock.class, "wire_3");
	}
	
	public static List<Block> getSolarPanels()
	{
		return mSolarPanels;
	}
}
