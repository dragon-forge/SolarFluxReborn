package com.zeitheron.solarflux.client;

import org.lwjgl.opengl.GL11;

import com.zeitheron.solarflux.block.tile.TileBaseSolar;
import com.zeitheron.solarflux.proxy.ClientProxy;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;

public class TESRSolarPanel extends TileEntitySpecialRenderer<TileBaseSolar>
{
	@Override
	public void render(TileBaseSolar te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		if(te.instance == null || te.instance.getDelegate() == null)
			return;
		
		Block type = te.getWorld().getBlockState(te.getPos()).getBlock();
		
		RenderBlocks rb = RenderBlocks.getInstance();
		
		int i = getBrightnessForRB(te, rb);
		
		GL11.glPushMatrix();
		GL11.glDisable(2884);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslated(x, y + 2.01D - (1 - .375F), z + 1D);
		GL11.glRotatef(180F, 1F, 0F, 0F);
		GL11.glColor3f(1f, 1f, 1f);
		
		Tessellator tess = Tessellator.getInstance();
		
		TextureAtlasSprite s = ClientProxy.TOPFS.get(te.instance.delegate);
		if(s == null)
		{
			s = TextureAtlasSpriteFull.sprite;
			bindTexture(te.instance.getDelegate().getTexture());
		} else
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		
		// Draw center
		tess.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
		rb.setRenderBounds(1D / 16D, 0, 1D / 16D, 1D - 1D / 16D, 1D, 1D - 1D / 16D);
		rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
		
		connections:
		{
			if(!te.renderConnectedTextures)
				break connections;
			
			boolean eastNorth_ = false, eastNorth = false;
			boolean westSouth_ = false, westSouth = false;
			boolean westNorth_ = false, westNorth = false;
			boolean southEast_ = false, southEast = false;
			
			if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST)) instanceof TileBaseSolar && te.isSameLevel((TileBaseSolar) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST))))
			{
				rb.setRenderBounds(0, 0, 1D / 16D, 2D / 16D, 1D, 1D - 1D / 16D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
				westSouth_ = true;
				westNorth_ = true;
			}
			
			if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.EAST)) instanceof TileBaseSolar && te.isSameLevel((TileBaseSolar) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.EAST))))
			{
				rb.setRenderBounds(1D - 1D / 16D, 0, 1D / 16D, 1D, 1D, 1D - 1D / 16D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
				eastNorth_ = true;
				southEast_ = true;
			}
			
			if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH)) instanceof TileBaseSolar && te.isSameLevel((TileBaseSolar) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH))))
			{
				rb.setRenderBounds(1D / 16D, 0, 1D - 1D / 16D, 1D - 1D / 16D, 1D, 1D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
				if(eastNorth_)
					eastNorth = true;
				if(westNorth_)
					westNorth = true;
			}
			
			if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH)) instanceof TileBaseSolar && te.isSameLevel((TileBaseSolar) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH))))
			{
				rb.setRenderBounds(1D / 16D, 0, 0D, 1D - 1D / 16D, 1D, 1D - 2D / 16D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
				if(westSouth_)
					westSouth = true;
				if(southEast_)
					southEast = true;
			}
			
			if(eastNorth && te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH).offset(EnumFacing.EAST)) instanceof TileBaseSolar && te.isSameLevel((TileBaseSolar) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH).offset(EnumFacing.EAST))))
			{
				rb.setRenderBounds(15D / 16D, 0D, 15D / 16D, 1D, 1D, 1D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
			}
			
			if(westSouth && te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.SOUTH)) instanceof TileBaseSolar && te.isSameLevel((TileBaseSolar) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.SOUTH))))
			{
				rb.setRenderBounds(0, 0D, 0, 1D / 16D, 1D, 1D / 16D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
			}
			
			if(westNorth && te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.NORTH)) instanceof TileBaseSolar && te.isSameLevel((TileBaseSolar) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.NORTH))))
			{
				rb.setRenderBounds(1D / 16D, 0D, 1D / 16D, 0D, 1D, 1D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
			}
			
			if(southEast && te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH).offset(EnumFacing.EAST)) instanceof TileBaseSolar && te.isSameLevel((TileBaseSolar) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH).offset(EnumFacing.EAST))))
			{
				rb.setRenderBounds(1D / 15D, 0D, 0D, 1D, 1D, 1D / 15D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
			}
		}
		
		tess.draw();
		GL11.glEnable(2884);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
	
	protected int getBrightnessForRB(TileBaseSolar te, RenderBlocks rb)
	{
		return te != null ? rb.setLighting(te.getWorld(), te.getPos()) : rb.setLighting(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player.getPosition());
	}
}