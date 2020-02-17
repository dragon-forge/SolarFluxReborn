package tk.zeitheron.solarflux;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tk.zeitheron.solarflux.block.SolarPanelBlockItem;
import tk.zeitheron.solarflux.block.SolarPanelTile;
import tk.zeitheron.solarflux.container.SolarPanelContainer;
import tk.zeitheron.solarflux.items.ItemsSF;
import tk.zeitheron.solarflux.net.SFNetwork;
import tk.zeitheron.solarflux.panels.SolarPanels;
import tk.zeitheron.solarflux.proxy.SFRClientProxy;
import tk.zeitheron.solarflux.proxy.SFRCommonProxy;

@Mod(value = "solarflux")
public class SolarFlux
{
	public static final ContainerType<SolarPanelContainer> SOLAR_PANEL_CONTAINER = IForgeContainerType.create((windowId, playerInv, extraData) ->
	{
		TileEntity tile = playerInv.player.world.getTileEntity(extraData.readBlockPos());
		if(tile instanceof SolarPanelTile)
			return new SolarPanelContainer(windowId, playerInv, (SolarPanelTile) tile);
		return null;
	});
	
	public static final Logger LOG = LogManager.getLogger();
	public static final SFRCommonProxy PROXY = DistExecutor.runForDist(() -> () -> new SFRClientProxy(), () -> () -> new SFRCommonProxy());
	public static final ItemGroup ITEM_GROUP = new ItemGroup(InfoSF.MOD_ID)
	{
		@Override
		public ItemStack createIcon()
		{
			return new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_3);
		}
	};
	
	public SolarFlux()
	{
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		FMLJavaModLoadingContext.get().getModEventBus().register(PROXY);
		MinecraftForge.EVENT_BUS.register(this);
		SolarPanels.init();
	}
	
	@SubscribeEvent
	public void commonSetup(FMLCommonSetupEvent e)
	{
		PROXY.commonSetup();
		SFNetwork.init();
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void clientSetup(FMLClientSetupEvent e)
	{
		PROXY.clientSetup();
	}
	
	@EventBusSubscriber(bus = Bus.MOD)
	public static class Registration
	{
		@SubscribeEvent
		public static void registerTiles(RegistryEvent.Register<TileEntityType<?>> e)
		{
			e.getRegistry().register(SolarPanels.SOLAR_PANEL_TYPE);
		}
		
		@SubscribeEvent
		public static void registerContainers(RegistryEvent.Register<ContainerType<?>> e)
		{
			e.getRegistry().register(SOLAR_PANEL_CONTAINER.setRegistryName("solar_panel"));
		}
		
		@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> e)
		{
			SolarPanels.listPanelBlocks().forEach(e.getRegistry()::register);
		}
		
		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> e)
		{
			ItemsSF.register(e.getRegistry());
			SolarPanels.listPanelBlocks().forEach(b ->
			{
				SolarPanelBlockItem item = new SolarPanelBlockItem(b, new Item.Properties().group(ITEM_GROUP));
				e.getRegistry().register(item);
			});
		}
	}
}