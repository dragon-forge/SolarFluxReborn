package org.zeith.solarflux;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.client.adapter.ResourcePackAdapter;
import org.zeith.solarflux.block.SolarPanelBlockItem;
import org.zeith.solarflux.client.SolarFluxResourcePack;
import org.zeith.solarflux.client.SolarPanelBakedModel;
import org.zeith.solarflux.items.ItemsSF;
import org.zeith.solarflux.net.SFNetwork;
import org.zeith.solarflux.panels.SolarPanels;
import org.zeith.solarflux.proxy.SFRClientProxy;
import org.zeith.solarflux.proxy.SFRCommonProxy;

@Mod("solarflux")
public class SolarFlux
{
	public static final Logger LOG = LogManager.getLogger();
	public static final SFRCommonProxy PROXY = DistExecutor.unsafeRunForDist(() -> SFRClientProxy::new, () -> SFRCommonProxy::new);
	public static final CreativeModeTab ITEM_GROUP = new CreativeModeTab(InfoSF.MOD_ID)
	{
		@Override
		public ItemStack makeIcon()
		{
			return new ItemStack(ItemsSF.PHOTOVOLTAIC_CELL_3);
		}
	};

	public SolarFlux()
	{
		MinecraftForge.EVENT_BUS.register(this);
		HammerLib.EVENT_BUS.addListener(RecipesSF::addRecipes);
		SolarPanels.init();

		ResourcePackAdapter.registerResourcePack(SolarFluxResourcePack.getPackInstance());
	}

	@SubscribeEvent
	public void startServer(RegisterCommandsEvent e)
	{
		e.getDispatcher().register(
				Commands.literal("solarflux")
						.then(Commands.literal("reload")
								.executes(src ->
								{
									SolarPanels.refreshConfigs();
									src.getSource().getServer().getPlayerList().getPlayers().forEach(SFNetwork::sendAllPanels);
									return 1;
								})
						)
		);
	}

	@SubscribeEvent
	public void playerLogin(PlayerEvent.PlayerLoggedInEvent e)
	{
		if(e.getPlayer() instanceof ServerPlayer sp)
		{
			LOG.info("Sending solar panels to " + sp.getGameProfile().getName() + ".");
			SFNetwork.sendAllPanels(sp);
		}
	}

	@EventBusSubscriber(bus = Bus.MOD)
	public static class ModEvents
	{
		@SubscribeEvent
		public void commonSetup(FMLCommonSetupEvent e)
		{
			PROXY.commonSetup();
			SFNetwork.init();
		}

		@SubscribeEvent
		public void loadComplete(FMLLoadCompleteEvent e)
		{
			SolarPanels.refreshConfigs();
		}

		@SubscribeEvent
		@OnlyIn(Dist.CLIENT)
		public void clientSetup(FMLClientSetupEvent e)
		{
			PROXY.clientSetup();
		}

		@SubscribeEvent
		@OnlyIn(Dist.CLIENT)
		public static void modelBake(ModelBakeEvent e)
		{
			SolarPanels.listPanelBlocks()
					.forEach(spb ->
							e.getModelRegistry().put(new ModelResourceLocation(spb.getRegistryName(), ""), new SolarPanelBakedModel(spb))
					);
		}

		@SubscribeEvent
		public static void registerTiles(RegistryEvent.Register<BlockEntityType<?>> e)
		{
			e.getRegistry().register(SolarPanels.SOLAR_PANEL_TYPE);
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
				SolarPanelBlockItem item = new SolarPanelBlockItem(b, new Item.Properties().tab(ITEM_GROUP));
				e.getRegistry().register(item);
			});
		}
	}
}