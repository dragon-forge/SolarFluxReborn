package com.mrdimka.solarfluxreborn.client.render.tile;

import org.lwjgl.opengl.GL11;

import com.mrdimka.hammercore.client.RenderBlocks;
import com.mrdimka.solarfluxreborn.reference.Reference;
import com.mrdimka.solarfluxreborn.te.SolarPanelTileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

public class RenderSolarPanelTile extends TileEntitySpecialRenderer<SolarPanelTileEntity>
{
	@Override
	public void renderTileEntityAt(SolarPanelTileEntity te, double x, double y, double z, float var8, int var9)
	{
		int i = te.getWorld().getCombinedLight(te.getPos(), 0);
		
		GL11.glPushMatrix();
		GL11.glDisable(2884);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslated(x, y + 2.0001D - 10D / 16D, z + 1D);
		GL11.glRotatef(180F, 1F, 0F, 0F);
		GL11.glColor3f(1f, 1f, 1f);
		
		RenderBlocks renderBlocks = RenderBlocks.getInstance();
		
		Tessellator t = Tessellator.getInstance();
		
		TextureAtlasSprite sprite2 = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(Reference.MOD_ID.toLowerCase() + ":blocks/solar" + te.getTier() + "_1");
				
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		
		//Draw center
		t.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
		renderBlocks.setRenderBounds(1D / 16D, 0, 1D / 16D, 1D - 1D / 16D, 1D, 1D - 1D / 16D);
		renderBlocks.renderFaceYPos(0, -.00005D, 0, sprite2, 1F, 1F, 1F, i);
		t.draw();
		
		boolean eastNorth_ = false, eastNorth = false;
		boolean westSouth_ = false, westSouth = false;
		boolean westNorth_ = false, westNorth = false;
		boolean southEast_ = false, southEast = false;
		
		if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST))).getTier() == te.getTier())
		{
			t.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			renderBlocks.setRenderBounds(0, 0, 1D / 16D, 2D / 16D, 1D, 1D - 1D / 16D);
			renderBlocks.renderFaceYPos(0, -.00005D, 0, sprite2, 1F, 1F, 1F, i);
			t.draw();
			westSouth_ = true;
			westNorth_ = true;
		}
		
		if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.EAST)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.EAST))).getTier() == te.getTier())
		{
			t.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			renderBlocks.setRenderBounds(1D - 1D / 16D, 0, 1D / 16D, 1D, 1D, 1D - 1D / 16D);
			renderBlocks.renderFaceYPos(0, -.00005D, 0, sprite2, 1F, 1F, 1F, i);
			t.draw();
			eastNorth_ = true;
			southEast_ = true;
		}
		
		if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH))).getTier() == te.getTier())
		{
			t.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			renderBlocks.setRenderBounds(1D / 16D, 0, 1D - 1D / 16D, 1D - 1D / 16D, 1D, 1D);
			renderBlocks.renderFaceYPos(0, -.00005D, 0, sprite2, 1F, 1F, 1F, i);
			t.draw();
			if(eastNorth_) eastNorth = true;
			if(westNorth_) westNorth = true;
		}
		
		if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH))).getTier() == te.getTier())
		{
			t.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			renderBlocks.setRenderBounds(1D / 16D, 0, 0D, 1D - 1D / 16D, 1D, 1D - 2D / 16D);
			renderBlocks.renderFaceYPos(0, -.00005D, 0, sprite2, 1F, 1F, 1F, i);
			t.draw();
			if(westSouth_) westSouth = true;
			if(southEast_) southEast = true;
		}
		
		if(eastNorth && te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH).offset(EnumFacing.EAST)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH).offset(EnumFacing.EAST))).getTier() == te.getTier())
		{
			t.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			renderBlocks.setRenderBounds(15D / 16D, 0D, 15D / 16D, 1D, 1D, 1D);
			renderBlocks.renderFaceYPos(0, -.00005D, 0, sprite2, 1F, 1F, 1F, i);
			t.draw();
		}
		
		if(westSouth && te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.SOUTH)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.SOUTH))).getTier() == te.getTier())
		{
			t.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			renderBlocks.setRenderBounds(0, 0D, 0, 1D / 16D, 1D, 1D / 16D);
			renderBlocks.renderFaceYPos(0, -.00005D, 0, sprite2, 1F, 1F, 1F, i);
			t.draw();
		}
		
		if(westNorth && te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.NORTH)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.NORTH))).getTier() == te.getTier())
		{
			t.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			renderBlocks.setRenderBounds(1D / 16D, 0D, 1D / 16D, 0D, 1D, 1D);
			renderBlocks.renderFaceYPos(0, -.00005D, 0, sprite2, 1F, 1F, 1F, i);
			t.draw();
		}
		
		if(southEast && te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH).offset(EnumFacing.EAST)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH).offset(EnumFacing.EAST))).getTier() == te.getTier())
		{
			t.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			renderBlocks.setRenderBounds(1D / 15D, 0D, 0D, 1D, 1D, 1D / 15D);
			renderBlocks.renderFaceYPos(0, -.00005D, 0, sprite2, 1F, 1F, 1F, i);
			t.draw();
		}
		
		GL11.glEnable(2884);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}