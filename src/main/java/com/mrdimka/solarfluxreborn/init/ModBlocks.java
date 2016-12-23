package com.mrdimka.solarfluxreborn.init;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.google.common.collect.Lists;
import com.mrdimka.solarfluxreborn.blocks.BlockCable320;
import com.mrdimka.solarfluxreborn.blocks.BlockCable80;
import com.mrdimka.solarfluxreborn.blocks.BlockCableInf;
import com.mrdimka.solarfluxreborn.blocks.SolarPanelBlock;
import com.mrdimka.solarfluxreborn.config.ModConfiguration;
import com.mrdimka.solarfluxreborn.items.CableItemBlock;
import com.mrdimka.solarfluxreborn.items.SolarPanelItemBlock;

public class ModBlocks
{
	private static final List<Block> mSolarPanels = Lists.newArrayList();
	
	public static final Block cable1 = new BlockCable80();
	public static final Block cable2 = new BlockCable320();
	public static final Block instaCable = new BlockCableInf();
	
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
		
		GameRegistry.registerBlock(cable1, CableItemBlock.class, "wire_1");
		GameRegistry.registerBlock(cable2, CableItemBlock.class, "wire_2");
	}
	
	public static List<Block> getSolarPanels()
	{
		return mSolarPanels;
	}
}
