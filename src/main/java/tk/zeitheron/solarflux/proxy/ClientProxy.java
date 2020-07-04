package tk.zeitheron.solarflux.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.api.SolarFluxAPI;
import tk.zeitheron.solarflux.api.SolarInfo;
import tk.zeitheron.solarflux.client.SolarFluxResourcePack;
import tk.zeitheron.solarflux.client.SolarPanelBakedModel;
import tk.zeitheron.solarflux.gui.ContainerBaseSolar;
import tk.zeitheron.solarflux.init.SolarsSF;
import tk.zeitheron.solarflux.net.NetworkSF;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientProxy
		implements ISFProxy
{
	private List<Item> render = new ArrayList<>();

	public static final SolarFluxResourcePack builtin = new SolarFluxResourcePack();

	@Override
	public void construct()
	{
	}

	@Override
	public void onPanelRegistered(SolarInfo info)
	{
		builtin.addPanel(info);
	}

	@Override
	public void preInit()
	{
		try
		{
			builtin.domains.addAll(SolarFluxAPI.resourceDomains);

			Field resourcePackList = FMLClientHandler.class.getDeclaredField("resourcePackList");
			resourcePackList.setAccessible(true);
			List<IResourcePack> rps = (List<IResourcePack>) resourcePackList.get(FMLClientHandler.instance());
			rps.add(3, builtin);
			((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(builtin);
			((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).reloadResourcePack(builtin);
			SolarFlux.LOG.info("Injected custom resource pack.");
		} catch(ReflectiveOperationException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void init()
	{
		render.forEach(this::registerRender);
		render.clear();
		render = null;
	}

	@Override
	public void postInit()
	{
		SolarFluxResourcePack.injectSolarPanelLanguages();
	}

	@Override
	public void render(Item item)
	{
		render.add(item);
	}

	@Override
	public void updateWindow(int window, int key, long val)
	{
		Minecraft.getMinecraft().addScheduledTask(() ->
		{
			EntityPlayer pl = Minecraft.getMinecraft().player;
			if(pl != null && pl.openContainer != null && pl.openContainer.windowId == window && pl.openContainer instanceof ContainerBaseSolar)
				((ContainerBaseSolar) pl.openContainer).updateProgressBar2(key, val);
		});
	}

	@SubscribeEvent
	public void guiInit(InitGuiEvent.Post e)
	{
		if(e.getGui() instanceof GuiMainMenu)
		{
			Calendar c = Calendar.getInstance();
			if(c.get(Calendar.MONTH) == Calendar.NOVEMBER && c.get(Calendar.DAY_OF_MONTH) == 10)
			{
				GuiMainMenu gmm = (GuiMainMenu) e.getGui();
				gmm.splashText = "Happy birthday, Zeitheron!";
			}
		}
	}

	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre e)
	{
		SolarsSF.listPanels()
				.stream()
				.map(SolarInfo::getBlock)
				.forEach(spb ->
				{
					e.getMap().registerSprite(new ResourceLocation(spb.getRegistryName().getNamespace(), "blocks/" + spb.getRegistryName().getPath() + "_base"));
					e.getMap().registerSprite(new ResourceLocation(spb.getRegistryName().getNamespace(), "blocks/" + spb.getRegistryName().getPath() + "_top"));
				});
	}

	@SubscribeEvent
	public void modelBake(ModelBakeEvent e)
	{
		SolarsSF.listPanels()
				.stream()
				.map(SolarInfo::getBlock)
				.forEach(spb -> e.getModelRegistry().putObject(new ModelResourceLocation(spb.getRegistryName(), ""), new SolarPanelBakedModel(spb)));
	}

	public boolean renderedWorld;

	@SubscribeEvent
	public void renderWorldLast(RenderWorldLastEvent e)
	{
		if(!renderedWorld)
		{
			renderedWorld = true;
			NetworkSF.INSTANCE.requestConfigs();
		}
	}

	@SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent e)
	{
		if(Minecraft.getMinecraft().world == null && renderedWorld)
		{
			renderedWorld = false;

			SolarFlux.LOG.info("Resetting configs to local.");
			SolarFluxAPI.SOLAR_PANELS.forEach(SolarInfo::resetConfigInstance);
		}
	}

	private void registerRender(Item item)
	{
		SolarFlux.LOG.info("Model definition for item " + item.getRegistryName());
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}