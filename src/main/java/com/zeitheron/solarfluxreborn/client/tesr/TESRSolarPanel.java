package com.zeitheron.solarfluxreborn.client.tesr;

import org.lwjgl.opengl.GL11;

import com.zeitheron.hammercore.client.render.tesr.TESR;
import com.zeitheron.hammercore.client.render.vertex.SimpleBlockRendering;
import com.zeitheron.hammercore.client.utils.DestroyStageTexture;
import com.zeitheron.hammercore.client.utils.RenderBlocks;
import com.zeitheron.solarfluxreborn.blocks.AbstractSolarPanelBlock;
import com.zeitheron.solarfluxreborn.blocks.SolarPanelBlock;
import com.zeitheron.solarfluxreborn.config.ModConfiguration;
import com.zeitheron.solarfluxreborn.config.RemoteConfigs;
import com.zeitheron.solarfluxreborn.items.SolarPanelItemBlock;
import com.zeitheron.solarfluxreborn.reference.InfoSFR;
import com.zeitheron.solarfluxreborn.te.AbstractSolarPanelTileEntity;
import com.zeitheron.solarfluxreborn.te.SolarPanelTileEntity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TESRSolarPanel extends TESR<SolarPanelTileEntity>
{
	@Override
	public void renderTileEntityAt(SolarPanelTileEntity te, double x, double y, double z, float partialTicks, ResourceLocation destroyStage, float alpha)
	{
		Block type = te.getWorld().getBlockState(te.getPos()).getBlock();
		
		RenderBlocks rb = RenderBlocks.forMod(InfoSFR.MOD_ID);
		
		int i = getBrightnessForRB(te, rb);
		
		GL11.glPushMatrix();
		GL11.glDisable(2884);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glTranslated(x, y + 2.0001D - (1 - RemoteConfigs.getSolarHeight()), z + 1D);
		GL11.glRotatef(180F, 1F, 0F, 0F);
		GL11.glColor3f(1f, 1f, 1f);
		
		Tessellator t = Tessellator.getInstance();
		
		TextureAtlasSprite s = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(te.getTopResource());
		
		bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		
		// Draw center
		t.getBuffer().begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
		rb.setRenderBounds(1D / 16D, 0, 1D / 16D, 1D - 1D / 16D, 1D, 1D - 1D / 16D);
		rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
		
		connections:
		{
			boolean abst = type instanceof AbstractSolarPanelBlock;
			if(abst && !((AbstractSolarPanelBlock) type).renderConnectedTextures)
				break connections;
			if(!ModConfiguration.useConnectedTextures())
				break connections;
			
			boolean eastNorth_ = false, eastNorth = false;
			boolean westSouth_ = false, westSouth = false;
			boolean westNorth_ = false, westNorth = false;
			boolean southEast_ = false, southEast = false;
			
			c: if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST))).getTier() == te.getTier())
			{
				if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST)) instanceof AbstractSolarPanelTileEntity)
					break c;
				rb.setRenderBounds(0, 0, 1D / 16D, 2D / 16D, 1D, 1D - 1D / 16D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
				westSouth_ = true;
				westNorth_ = true;
			}
			
			c: if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.EAST)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.EAST))).getTier() == te.getTier())
			{
				if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.EAST)) instanceof AbstractSolarPanelTileEntity)
					break c;
				rb.setRenderBounds(1D - 1D / 16D, 0, 1D / 16D, 1D, 1D, 1D - 1D / 16D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
				eastNorth_ = true;
				southEast_ = true;
			}
			
			c: if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH))).getTier() == te.getTier())
			{
				if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH)) instanceof AbstractSolarPanelTileEntity)
					break c;
				rb.setRenderBounds(1D / 16D, 0, 1D - 1D / 16D, 1D - 1D / 16D, 1D, 1D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
				if(eastNorth_)
					eastNorth = true;
				if(westNorth_)
					westNorth = true;
			}
			
			c: if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH))).getTier() == te.getTier())
			{
				if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH)) instanceof AbstractSolarPanelTileEntity)
					break c;
				rb.setRenderBounds(1D / 16D, 0, 0D, 1D - 1D / 16D, 1D, 1D - 2D / 16D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
				if(westSouth_)
					westSouth = true;
				if(southEast_)
					southEast = true;
			}
			
			c: if(eastNorth && te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH).offset(EnumFacing.EAST)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH).offset(EnumFacing.EAST))).getTier() == te.getTier())
			{
				if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.NORTH).offset(EnumFacing.EAST)) instanceof AbstractSolarPanelTileEntity)
					break c;
				rb.setRenderBounds(15D / 16D, 0D, 15D / 16D, 1D, 1D, 1D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
			}
			
			c: if(westSouth && te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.SOUTH)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.SOUTH))).getTier() == te.getTier())
			{
				if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.SOUTH)) instanceof AbstractSolarPanelTileEntity)
					break c;
				rb.setRenderBounds(0, 0D, 0, 1D / 16D, 1D, 1D / 16D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
			}
			
			c: if(westNorth && te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.NORTH)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.NORTH))).getTier() == te.getTier())
			{
				if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.WEST).offset(EnumFacing.NORTH)) instanceof AbstractSolarPanelTileEntity)
					break c;
				rb.setRenderBounds(1D / 16D, 0D, 1D / 16D, 0D, 1D, 1D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
			}
			
			c: if(southEast && te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH).offset(EnumFacing.EAST)) instanceof SolarPanelTileEntity && ((SolarPanelTileEntity) te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH).offset(EnumFacing.EAST))).getTier() == te.getTier())
			{
				if(te.getWorld().getTileEntity(te.getPos().offset(EnumFacing.SOUTH).offset(EnumFacing.EAST)) instanceof AbstractSolarPanelTileEntity)
					break c;
				rb.setRenderBounds(1D / 15D, 0D, 0D, 1D, 1D, 1D / 15D);
				rb.renderFaceYPos(0, -.00005D, 0, s, 1F, 1F, 1F, i);
			}
		}
		
		t.draw();
		
		GL11.glEnable(2884);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
	
	@Override
	public void renderBase(SolarPanelTileEntity tile, ItemStack stack, double x, double y, double z, ResourceLocation destroyStage, float alpha)
	{
		for(int j = 0; j < (destroyStage != null ? 2 : 1); ++j)
		{
			double dis = j == 1 ? .001 : 0;
			String txt = "";
			
			if(tile != null)
				txt = tile.getBaseResource();
			if(stack != null && stack.getItem() instanceof SolarPanelItemBlock && ((SolarPanelItemBlock) stack.getItem()).getBlock() instanceof SolarPanelBlock)
				txt = ((SolarPanelBlock) ((SolarPanelItemBlock) stack.getItem()).getBlock()).getSubResource("base");
			
			TextureAtlasSprite spr = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(txt);
			TextureAtlasSprite gls = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(InfoSFR.MOD_ID + ":blocks/solar_overlay");
			if(j == 1)
				spr = gls = DestroyStageTexture.getAsSprite(destroyProgress);
			
			SimpleBlockRendering sbr = RenderBlocks.forMod(InfoSFR.MOD_ID).simpleRenderer;
			
			sbr.begin();
			sbr.setBrightness(getBrightnessForRB(tile, sbr.rb));
			sbr.setSprite(spr);
			sbr.setRenderBounds(-dis, -dis, -dis, 1 + dis, RemoteConfigs.getSolarHeight() + dis, 1 + dis);
			sbr.drawBlock(x, y, z);
			sbr.disableFaces();
			sbr.enableFace(EnumFacing.UP);
			sbr.setSprite(gls);
			sbr.setRenderBounds(-dis, -dis, -dis, 1 + dis, RemoteConfigs.getSolarHeight() + .0005 + dis, 1 + dis);
			sbr.drawBlock(x, y, z);
			
			sbr.end();
		}
	}
	
	@Override
	public void renderItem(ItemStack stack)
	{
		super.renderItem(stack);
		
		if(stack != null && stack.getItem() instanceof SolarPanelItemBlock && ((SolarPanelItemBlock) stack.getItem()).getBlock() instanceof SolarPanelBlock)
		{
			String txt = ((SolarPanelBlock) ((SolarPanelItemBlock) stack.getItem()).getBlock()).getSubResource("top");
			TextureAtlasSprite s = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(txt);
			
			SimpleBlockRendering sbr = RenderBlocks.forMod(InfoSFR.MOD_ID).simpleRenderer;
			
			sbr.begin();
			sbr.setBrightness(getBrightnessForRB(null, sbr.rb));
			sbr.setSprite(s);
			sbr.setRenderBounds(1 / 16D, 0, 1 / 16D, 15 / 16D, RemoteConfigs.getSolarHeight() + .0001, 15 / 16D);
			sbr.disableFaces();
			sbr.enableFace(EnumFacing.UP);
			sbr.drawBlock(0, 0, 0);
			sbr.end();
		}
	}
}