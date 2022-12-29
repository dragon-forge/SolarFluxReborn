package org.zeith.solarflux;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.client.adapter.ChatMessageAdapter;
import org.zeith.hammerlib.client.adapter.ResourcePackAdapter;
import org.zeith.hammerlib.compat.base.CompatList;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.core.adapter.ModSourceAdapter;
import org.zeith.solarflux.client.SolarFluxResourcePack;
import org.zeith.solarflux.client.SolarPanelBakedModel;
import org.zeith.solarflux.compat._base.SFCompatList;
import org.zeith.solarflux.compat._base.SolarFluxCompat;
import org.zeith.solarflux.init.ItemsSF;
import org.zeith.solarflux.init.SolarPanelsSF;
import org.zeith.solarflux.net.PacketSyncPanelData;
import org.zeith.solarflux.proxy.SFRClientProxy;
import org.zeith.solarflux.proxy.SFRCommonProxy;

@Mod(SolarFlux.MOD_ID)
public class SolarFlux
{
	public static final String MOD_ID = "solarflux";
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
	
	public static final SFCompatList SF_COMPAT = CompatList.gather(SolarFluxCompat.class, SFCompatList::new);
	
	public SolarFlux()
	{
		var modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		MinecraftForge.EVENT_BUS.register(this);
		HammerLib.EVENT_BUS.addListener(RecipesSF::addRecipes);
		
		SolarPanelsSF.init();
		SF_COMPAT.setupSolarPanels();
		LanguageAdapter.registerMod(MOD_ID);
		
		ResourcePackAdapter.registerResourcePack(SolarFluxResourcePack.getPackInstance());
		
		var illegalSourceNotice = ModSourceAdapter.getModSource(SolarFlux.class)
				.filter(ModSourceAdapter.ModSource::wasDownloadedIllegally)
				.orElse(null);
		
		if(illegalSourceNotice != null)
		{
			LOG.fatal("====================================================");
			LOG.fatal("WARNING: Solar Flux Reborn was downloaded from " + illegalSourceNotice.referrerDomain() +
					", which has been marked as illegal site over at stopmodreposts.org.");
			LOG.fatal("Please download the mod from https://www.curseforge.com/minecraft/mc-mods/solar-flux-reborn");
			LOG.fatal("====================================================");
			
			var illegalUri = Component.literal(illegalSourceNotice.referrerDomain())
					.withStyle(s -> s.withColor(ChatFormatting.RED));
			var smrUri = Component.literal("stopmodreposts.org")
					.withStyle(s -> s.withColor(ChatFormatting.BLUE)
							.withUnderlined(true)
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://stopmodreposts.org/"))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open webpage."))));
			var curseforgeUri = Component.literal("curseforge.com")
					.withStyle(s -> s.withColor(ChatFormatting.BLUE)
							.withUnderlined(true)
							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/solar-flux-reborn"))
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to open webpage."))));
			ChatMessageAdapter.sendOnFirstWorldLoad(Component.literal("WARNING: Solar Flux Reborn was downloaded from ")
					.append(illegalUri)
					.append(", which has been marked as illegal site over at ")
					.append(smrUri)
					.append(". Please download the mod from ")
					.append(curseforgeUri)
					.append(".")
			);
		}
	}
	
	public static ResourceLocation id(String s)
	{
		return new ResourceLocation(MOD_ID, s);
	}
	
	@SubscribeEvent
	public void startServer(RegisterCommandsEvent e)
	{
		e.getDispatcher().register(
				Commands.literal(MOD_ID)
						.then(Commands.literal("reload")
								.executes(src ->
								{
									SolarPanelsSF.refreshConfigs();
									src.getSource().getServer().getPlayerList().getPlayers().forEach(PacketSyncPanelData::sendAllPanels);
									return 1;
								})
						)
		);
	}
	
	@SubscribeEvent
	public void playerLogin(PlayerEvent.PlayerLoggedInEvent e)
	{
		if(e.getEntity() instanceof ServerPlayer sp)
		{
			LOG.info("Sending solar panels to " + sp.getGameProfile().getName() + ".");
			PacketSyncPanelData.sendAllPanels(sp);
		}
	}
	
	@EventBusSubscriber(bus = Bus.MOD)
	public static class ModEvents
	{
		@SubscribeEvent
		public static void commonSetup(FMLCommonSetupEvent e)
		{
			PROXY.commonSetup();
		}
		
		@SubscribeEvent
		public static void loadComplete(FMLLoadCompleteEvent e)
		{
			SolarPanelsSF.refreshConfigs();
		}
		
		@SubscribeEvent
		@OnlyIn(Dist.CLIENT)
		public static void clientSetup(FMLClientSetupEvent e)
		{
			PROXY.clientSetup();
		}
		
		@SubscribeEvent
		@OnlyIn(Dist.CLIENT)
		public static void modelBake(ModelEvent.BakingCompleted e)
		{
			SolarPanelsSF.listPanelBlocks()
					.forEach(spb ->
							e.getModels().put(new ModelResourceLocation(spb.getRegistryName(), ""), new SolarPanelBakedModel(spb))
					);
		}
	}
}