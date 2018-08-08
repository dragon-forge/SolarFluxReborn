package com.zeitheron.solarfluxreborn.proxy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.render.item.ItemRenderingHandler;
import com.zeitheron.hammercore.client.render.tesr.TESR;
import com.zeitheron.hammercore.utils.math.vec.Cuboid6;
import com.zeitheron.hammercore.utils.math.vec.Vector3;
import com.zeitheron.solarfluxreborn.blocks.BlockAbstractCable;
import com.zeitheron.solarfluxreborn.client.tesr.TESRSolarPanel;
import com.zeitheron.solarfluxreborn.config.ModConfiguration;
import com.zeitheron.solarfluxreborn.config.RemoteConfigs;
import com.zeitheron.solarfluxreborn.init.BlocksSFR;
import com.zeitheron.solarfluxreborn.reference.InfoSFR;
import com.zeitheron.solarfluxreborn.te.AbstractSolarPanelTileEntity;
import com.zeitheron.solarfluxreborn.te.SolarPanelTileEntity;
import com.zeitheron.solarfluxreborn.utility.SFRLog;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class ClientProxy extends CommonProxy
{
	@Override
	public void init()
	{
		TESRSolarPanel solarRender = new TESRSolarPanel();
		
		ClientRegistry.bindTileEntitySpecialRenderer(SolarPanelTileEntity.class, solarRender);
		ClientRegistry.bindTileEntitySpecialRenderer(AbstractSolarPanelTileEntity.class, solarRender);
		
		for(Block solar : BlocksSFR.getSolarPanels())
			ItemRenderingHandler.INSTANCE.bindItemRender(Item.getItemFromBlock(solar), solarRender);
			
		// ItemRenderingHandler.INSTANCE.bindItemRender(Item.getItemFromBlock(BlocksSFR.cable1),
		// cableRender);
		// ItemRenderingHandler.INSTANCE.bindItemRender(Item.getItemFromBlock(BlocksSFR.cable2),
		// cableRender);
		// ItemRenderingHandler.INSTANCE.bindItemRender(Item.getItemFromBlock(BlocksSFR.cable3),
		// cableRender);
	}
	
	private <T extends TileEntity> void registerRender(Class<T> tileEntityClass, Block block, TESR<? super T> specialRenderer)
	{
		ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, specialRenderer);
		ItemRenderingHandler.INSTANCE.bindItemRender(Item.getItemFromBlock(block), specialRenderer);
	}
	
	@SubscribeEvent
	public void pte(RenderGameOverlayEvent e)
	{
		if(ModConfiguration.willNotify)
		{
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("[" + InfoSFR.MOD_NAME + "] WARNING: Your configs have been replaced."));
			ModConfiguration.updateNotification(false);
		}
	}
	
	@SubscribeEvent
	public void renderWorld(DrawBlockHighlightEvent e)
	{
		RayTraceResult res = e.getTarget();
		IBlockState state = null;
		
		if(res != null && res.typeOfHit == Type.BLOCK)
		{
			World w = Minecraft.getMinecraft().world;
			state = w.getBlockState(res.getBlockPos());
			state = state.getActualState(w, res.getBlockPos());
		}
		
		if(state != null && state.getBlock() instanceof BlockAbstractCable)
		{
			BlockAbstractCable c = (BlockAbstractCable) state.getBlock();
			e.setCanceled(true);
			
			boolean up = BlockAbstractCable.connectsTo(state, EnumFacing.UP);
			boolean down = BlockAbstractCable.connectsTo(state, EnumFacing.DOWN);
			boolean east = BlockAbstractCable.connectsTo(state, EnumFacing.EAST);
			boolean west = BlockAbstractCable.connectsTo(state, EnumFacing.WEST);
			boolean south = BlockAbstractCable.connectsTo(state, EnumFacing.SOUTH);
			boolean north = BlockAbstractCable.connectsTo(state, EnumFacing.NORTH);
			
			GL11.glPushMatrix();
			
			GL11.glEnable(GL11.GL_BLEND);
			
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			
			GL11.glTranslated(res.getBlockPos().getX() - TileEntityRendererDispatcher.staticPlayerX, res.getBlockPos().getY() - TileEntityRendererDispatcher.staticPlayerY, res.getBlockPos().getZ() - TileEntityRendererDispatcher.staticPlayerZ);
			
			Cuboid6[] cbd = c.getCuboids(Minecraft.getMinecraft().world, res.getBlockPos(), state);
			
			double t = .0020000000949949026;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
			
			Map<Vector3, Vector3> vecs = new HashMap<>();
			
			for(Cuboid6 cc : cbd)
			{
				double mx = cc.min.x - t;
				double my = cc.min.y - t;
				double mz = cc.min.z - t;
				
				double xx = cc.max.x + t;
				double xy = cc.max.y + t;
				double xz = cc.max.z + t;
				
				RenderGlobal.drawBoundingBox(buffer, mx, my, mz, xx, xy, xz, 0, 0, 0, .35F);
			}
			
			tessellator.draw();
			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
			GL11.glPopMatrix();
		}
	}
	
	public static void drawBlockLine(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		GL11.glColor4f(0, 0, 0, .35F);
		GL11.glVertex3d(x1 / 16D, y1 / 16D, z1 / 16D);
		GL11.glVertex3d(x2 / 16D, y2 / 16D, z2 / 16D);
	}
	
	@SubscribeEvent
	public void disconnect(PlayerLoggedOutEvent evt)
	{
		if(evt.player.world.isRemote)
			RemoteConfigs.reset();
	}
	
	@Override
	public void postInit()
	{
		textureStitch(new TextureStitchEvent.Pre(Minecraft.getMinecraft().getTextureMapBlocks()));
	}
	
	@SubscribeEvent
	public void textureStitch(TextureStitchEvent.Pre evt)
	{
		try
		{
			int sprite = 0;
			SFRLog.info("Loading sprites...");
			Scanner s = new Scanner(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(InfoSFR.MOD_ID, "textures/blocks/_sprites.txt")).getInputStream());
			while(s.hasNextLine())
			{
				String ln = s.nextLine();
				if(ln.isEmpty())
					continue;
				evt.getMap().registerSprite(new ResourceLocation(InfoSFR.MOD_ID, "blocks/" + ln));
				sprite++;
			}
			s.close();
			SFRLog.info("Loaded " + sprite + " sprites!");
		} catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}