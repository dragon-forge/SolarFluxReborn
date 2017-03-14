package com.mrdimka.solarfluxreborn.client.tesr;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.energy.CapabilityEnergy;

import org.lwjgl.opengl.GL11;

import cofh.api.energy.IEnergyConnection;

import com.mrdimka.hammercore.client.utils.RenderBlocks;
import com.mrdimka.hammercore.ext.TeslaAPI;
import com.mrdimka.solarfluxreborn.client.DestroyStage;
import com.mrdimka.solarfluxreborn.te.cable.TileCustomCable;
import com.mrdimka.solarfluxreborn.utility.TileMapper;

public class RenderCustomCable extends TileEntitySpecialRenderer<TileCustomCable>
{
	@Override
	public void renderTileEntityAt(TileCustomCable te, double x, double y, double z, float pt, int tp)
	{
		ResourceLocation destroy = null;
		RayTraceResult over = Minecraft.getMinecraft().objectMouseOver;
		if(over != null && over.typeOfHit == Type.BLOCK && over.getBlockPos().equals(te.getPos()))
		{
			try
			{
				Field f = PlayerControllerMP.class.getDeclaredFields()[4];
				f.setAccessible(true);
				float progress = f.getFloat(Minecraft.getMinecraft().playerController);
				if(progress > 0F) destroy = DestroyStage.getByProgress(progress);
			}catch(Throwable err) {}
		}
		
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		renderWire(te, x, y, z, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(te.getResourceConnection()));
		if(destroy != null)
		{
			bindTexture(destroy);
			renderWire(te, x, y, z, Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(te.getResourceConnection()));
		}
	}
	
	public static void renderWire(TileCustomCable te, double x, double y, double z, TextureAtlasSprite texture)
	{
		int i = te.getWorld().getCombinedLight(te.getPos(), 0);
		GL11.glPushMatrix();
		GL11.glDisable(2884);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslated(x, y + 2.001D, z + 1D);
		GL11.glRotatef(180F, 1F, 0F, 0F);
		GL11.glColor3f(1f, 1f, 1f);
		
		RenderBlocks renderBlocks = RenderBlocks.getInstance();
		
		Tessellator t = Tessellator.getInstance();
		
		TextureAtlasSprite sprite2 = texture;
		
		boolean renderCore = true;
		
		Map<EnumFacing, Boolean> conns = TileMapper.request(te.getWorld(), te.getPos(), te.getClass());
		
		for(EnumFacing f : EnumFacing.VALUES)
		{
			if(conns.containsKey(f)) continue;
			TileEntity tt = te.getWorld().getTileEntity(te.getPos().offset(f));
			if(tt == null) continue;
			if(tt instanceof TileCustomCable || tt instanceof IEnergyConnection)
			{
				if(tt instanceof IEnergyConnection && ((IEnergyConnection) tt).canConnectEnergy(f.getOpposite())) conns.put(f, true);
				else if(tt instanceof TileCustomCable) conns.put(f, true);
				else conns.put(f, false);
				continue;
			}else if(tt.hasCapability(CapabilityEnergy.ENERGY, f.getOpposite())) conns.put(f, true);
			else if(TeslaAPI.isTeslaConsumer(tt)) conns.put(f, true);
		}
		
		EnumFacing dir = null;
		
		if(conns.size() > 0)
		{
			dir = conns.keySet().toArray(new EnumFacing[0])[0];
			if(conns.size() == 2 && conns.get(dir.getOpposite()) == Boolean.TRUE)
			{
				renderCore = false;
			}else dir = null;
		}
		
		if(renderCore)
		{
			t.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			
			renderBlocks.setRenderBounds(0D, 0D, 0D, 4D / 16D, 1D, 4D / 16D);
			
			renderBlocks.renderFaceYPos(6D / 16D, 6F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceYPos(6D / 16D, 10F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			
			
			renderBlocks.setRenderBounds(0, 12D / 16D, 0, 1, 1, 4D / 16D);
			
			renderBlocks.renderFaceXPos(-6D / 16D, 10F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceXPos(-10D / 16D, 10F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			
			
			renderBlocks.setRenderBounds(0, 12D / 16D, 0, 4D / 16D, 1, 1);
			
			renderBlocks.renderFaceZPos(6D / 16D, 10F / 16F, -6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceZPos(6D / 16D, 10F / 16F, -10D / 16D, sprite2, 1F, 1F, 1F, i);
			
			GL11.glPushMatrix();
//			GL11.glScalef(1.1F, 1.1F, 1.1F);
			t.draw();
			GL11.glScalef(1F, 1F, 1F);
			GL11.glPopMatrix();
		}
		
		t.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
		
		if(!renderCore) if(dir == EnumFacing.UP || dir == EnumFacing.DOWN)
		{
			renderBlocks.setRenderBounds(0, 2D / 16D, 5D / 16D, 1D, 6D / 16D, 10D / 16D);
			
			renderBlocks.renderFaceXPos(-6D / 16D, 20F / 16F, 0D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceXPos(-10D / 16D, 20F / 16F, 0D / 16D, sprite2, 1F, 1F, 1F, i);
			
			
			renderBlocks.setRenderBounds(5D / 16D, 2D / 16D, 0, 10D / 16D, 6D / 16D, 1);
			
			renderBlocks.renderFaceZPos(1D / 16D, 20F / 16F, -6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceZPos(1D / 16D, 20F / 16F, -10D / 16D, sprite2, 1F, 1F, 1F, i);
		}
		
		if(!renderCore) if(dir == EnumFacing.EAST || dir == EnumFacing.WEST)
		{
			renderBlocks.setRenderBounds(10D / 16D, 0, 0, 1, 6D / 16D, 1);
			
			renderBlocks.renderFaceZPos(-4D / 16D, 20F / 16F, -6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceZPos(-4D / 16D, 20F / 16F, -10D / 16D, sprite2, 1F, 1F, 1F, i);
			
			renderBlocks.setRenderBounds(10D / 16D, 0, 0, 1, 6D / 16D, 1);
			
			renderBlocks.renderFaceYPos(-4D / 16D, 20F / 16F, -4D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceYPos(-4D / 16D, 16F / 16F, -4D / 16D, sprite2, 1F, 1F, 1F, i);
		}
		
		if(!renderCore) if(dir == EnumFacing.SOUTH || dir == EnumFacing.NORTH)
		{
			renderBlocks.setRenderBounds(0, 0, 10D / 16D, 1, 6D / 16D, 1);
			
			renderBlocks.renderFaceXPos(-6D / 16D, 20F / 16F, -6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceXPos(-10D / 16D, 20F / 16F, -6D / 16D, sprite2, 1F, 1F, 1F, i);
			
			renderBlocks.setRenderBounds(0, 0, 10D / 16D, 9D / 16D, 6D / 16D, 1);
			
			renderBlocks.renderFaceYPos(1D / 16D, 20F / 16F, -4D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceYPos(1D / 16D, 16F / 16F, -4D / 16D, sprite2, 1F, 1F, 1F, i);
		}
		
		
		
		
		if(conns.get(EnumFacing.UP) != null && conns.get(EnumFacing.UP).booleanValue())
		{
			renderBlocks.setRenderBounds(0, 0, 0, 1, 12D / 16D, 4D / 16D);
			
			renderBlocks.renderFaceXPos(-6D / 16D, 10F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceXPos(-10D / 16D, 10F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			
			
			renderBlocks.setRenderBounds(0, 0, 0, 4D / 16D, 12D / 16D, 1);
			
			renderBlocks.renderFaceZPos(6D / 16D, 10F / 16F, -6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceZPos(6D / 16D, 10F / 16F, -10D / 16D, sprite2, 1F, 1F, 1F, i);
		}
		
		if(conns.get(EnumFacing.DOWN) != null && conns.get(EnumFacing.DOWN).booleanValue())
		{
			renderBlocks.setRenderBounds(0, 0, 0, 1D, 12D / 16D, 4D / 16D);
			
			renderBlocks.renderFaceXPos(-6D / 16D, 20F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceXPos(-10D / 16D, 20F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			
			
			renderBlocks.setRenderBounds(0, 0, 0, 4D / 16D, 12D / 16D, 1);
			
			renderBlocks.renderFaceZPos(6D / 16D, 20F / 16F, -6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceZPos(6D / 16D, 20F / 16F, -10D / 16D, sprite2, 1F, 1F, 1F, i);
		}
		
		if(conns.get(EnumFacing.EAST) != null && conns.get(EnumFacing.EAST).booleanValue())
		{
			renderBlocks.setRenderBounds(4D / 16D, 12D / 16D, 0, 10D / 16D, 1D, 4D / 16D);
			
			renderBlocks.renderFaceYPos(6D / 16D, 6F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceYPos(6D / 16D, 10F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			
			
			renderBlocks.setRenderBounds(4D / 16D, 12D / 16D, 0, 10D / 16D, 1D, 1);
			
			renderBlocks.renderFaceZPos(6D / 16D, 10F / 16F, -6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceZPos(6D / 16D, 10F / 16F, -10D / 16D, sprite2, 1F, 1F, 1F, i);
		}
		
		if(conns.get(EnumFacing.WEST) != null && conns.get(EnumFacing.WEST).booleanValue())
		{
			renderBlocks.setRenderBounds(4D / 16D, 12D / 16D, 0, 10D / 16D, 1D, 4D / 16D);
			
			renderBlocks.renderFaceYPos(-4D / 16D, 6F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceYPos(-4D / 16D, 10F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			
			
			renderBlocks.setRenderBounds(4D / 16D, 12D / 16D, 0, 10D / 16D, 1D, 1);
			
			renderBlocks.renderFaceZPos(-4D / 16D, 10F / 16F, -6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceZPos(-4D / 16D, 10F / 16F, -10D / 16D, sprite2, 1F, 1F, 1F, i);
		}
		
		if(conns.get(EnumFacing.SOUTH) != null && conns.get(EnumFacing.SOUTH).booleanValue())
		{
			renderBlocks.setRenderBounds(0, 0, 4D / 16D, 4D / 16D, 1, 10D / 16D);
			
			renderBlocks.renderFaceYPos(6D / 16D, 6F / 16F, -4D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceYPos(6D / 16D, 10F / 16F, -4D / 16D, sprite2, 1F, 1F, 1F, i);
			
			
			renderBlocks.setRenderBounds(0, 12D / 16D, 4D / 16D, 1, 1, 1);
			
			renderBlocks.renderFaceXPos(-6D / 16D, 10F / 16F, -10D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceXPos(-10D / 16D, 10F / 16F, -10D / 16D, sprite2, 1F, 1F, 1F, i);
		}
		
		if(conns.get(EnumFacing.NORTH) != null && conns.get(EnumFacing.NORTH).booleanValue())
		{
			renderBlocks.setRenderBounds(0, 0, 4D / 16D, 4D / 16D, 1, 10D / 16D);
			
			renderBlocks.renderFaceYPos(6D / 16D, 6F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceYPos(6D / 16D, 10F / 16F, 6D / 16D, sprite2, 1F, 1F, 1F, i);
			
			
			renderBlocks.setRenderBounds(0, 12D / 16D, 4D / 16D, 1, 1, 1);
			
			renderBlocks.renderFaceXPos(-6D / 16D, 10F / 16F, 0D, sprite2, 1F, 1F, 1F, i);
			renderBlocks.renderFaceXPos(-10D / 16D, 10F / 16F, 0D, sprite2, 1F, 1F, 1F, i);
		}
		
		t.draw();
		
		GL11.glEnable(2884);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
	
}