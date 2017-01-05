package com.mrdimka.solarfluxreborn.proxy;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import com.mrdimka.solarfluxreborn.client.render.tile.RenderCustomCable;
import com.mrdimka.solarfluxreborn.client.render.tile.RenderSolarPanelTile;
import com.mrdimka.solarfluxreborn.config.ModConfiguration;
import com.mrdimka.solarfluxreborn.init.ModBlocks;
import com.mrdimka.solarfluxreborn.init.ModItems;
import com.mrdimka.solarfluxreborn.reference.Reference;
import com.mrdimka.solarfluxreborn.te.SolarPanelTileEntity;
import com.mrdimka.solarfluxreborn.te.cable.TileCustomCable;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		MinecraftForge.EVENT_BUS.register(this);
		
		List<Item> items = new ArrayList<Item>();
		
		items.add(ModItems.mirror);
		items.add(ModItems.solarCell1);
		items.add(ModItems.solarCell2);
		items.add(ModItems.solarCell3);
		items.add(ModItems.solarCell4);
		items.add(ModItems.mUpgradeBlank);
		items.add(ModItems.mUpgradeCapacity);
		items.add(ModItems.mUpgradeEfficiency);
		items.add(ModItems.mUpgradeFurnace);
		items.add(ModItems.mUpgradeLowLight);
		items.add(ModItems.mUpgradeTransferRate);
		items.add(ModItems.mUpgradeTraversal);
		
		items.add(Item.getItemFromBlock(ModBlocks.cable1));
		items.add(Item.getItemFromBlock(ModBlocks.cable2));
		items.add(Item.getItemFromBlock(ModBlocks.cable3));
		
		for(int i = 0; i < ModBlocks.getSolarPanels().size(); ++i)
		{
			Block b = ModBlocks.getSolarPanels().get(i);
			items.add(Item.getItemFromBlock(b));
		}
		
		for(int i = 0; i < items.size(); ++i)
		{
			Item item = items.get(i);
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getUnlocalizedName().substring(5), "inventory"));
		}
		
		ClientRegistry.bindTileEntitySpecialRenderer(SolarPanelTileEntity.class, new RenderSolarPanelTile());
		ClientRegistry.bindTileEntitySpecialRenderer(TileCustomCable.class, new RenderCustomCable());
	}
	
	@SubscribeEvent
	public void pte(RenderGameOverlayEvent e)
	{
		if(ModConfiguration.willNotify)
		{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("[" + Reference.MOD_NAME + "] WARNING: Your configs have been replaced."));
			ModConfiguration.updateNotification(false);
		}
	}
}