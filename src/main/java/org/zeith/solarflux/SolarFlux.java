package org.zeith.solarflux;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
import org.zeith.hammerlib.api.items.CreativeTab;
import org.zeith.hammerlib.client.adapter.ResourcePackAdapter;
import org.zeith.hammerlib.compat.base.CompatList;
import org.zeith.hammerlib.core.adapter.LanguageAdapter;
import org.zeith.hammerlib.event.fml.FMLFingerprintCheckEvent;
import org.zeith.hammerlib.proxy.HLConstants;
import org.zeith.hammerlib.util.CommonMessages;
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
	
	@CreativeTab.RegisterTab
	public static final CreativeTab ITEM_GROUP = new CreativeTab(new ResourceLocation(InfoSF.MOD_ID, "root"),
			b -> b
					.icon(ItemsSF.PHOTOVOLTAIC_CELL_3::getDefaultInstance)
					.title(Component.translatable("itemGroup." + MOD_ID))
					.withTabsBefore(HLConstants.HL_TAB.id())
	);
	
	public static final SFCompatList SF_COMPAT = CompatList.gather(SolarFluxCompat.class, SFCompatList::new);
	
	public SolarFlux()
	{
		var modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		MinecraftForge.EVENT_BUS.register(this);
		modBus.addListener(RecipesSF::addRecipes);
		
		SolarPanelsSF.init();
		SF_COMPAT.setupSolarPanels();
		LanguageAdapter.registerMod(MOD_ID);
		
		ResourcePackAdapter.registerResourcePack(SolarFluxResourcePack.getPackInstance());
		
		CommonMessages.printMessageOnIllegalRedistribution(SolarFlux.class,
				LOG, "Solar Flux Reborn", "https://www.curseforge.com/minecraft/mc-mods/solar-flux-reborn");
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
		public static void fingerprintCheck(FMLFingerprintCheckEvent e)
		{
			CommonMessages.printMessageOnFingerprintViolation(e, "97e852e9b3f01b83574e8315f7e77651c6605f2b455919a7319e9869564f013c",
					LOG, "Solar Flux Reborn", "https://www.curseforge.com/minecraft/mc-mods/solar-flux-reborn");
		}
		
		@SubscribeEvent
		@OnlyIn(Dist.CLIENT)
		public static void modelBake(ModelEvent.ModifyBakingResult e)
		{
			SolarPanelsSF.listPanelBlocks()
					.forEach(spb ->
							e.getModels().put(new ModelResourceLocation(spb.getRegistryName(), ""), new SolarPanelBakedModel(spb))
					);
		}
	}
}