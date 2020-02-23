package tk.zeitheron.solarflux.gui;

import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import tk.zeitheron.solarflux.InfoSF;
import tk.zeitheron.solarflux.block.tile.TileBaseSolar;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiBaseSolar extends GuiContainer
{
	private static final ResourceLocation ELEMENTS = new ResourceLocation(InfoSF.MOD_ID, "textures/gui/elements.png");
	private static final ResourceLocation TEXTURE = new ResourceLocation(InfoSF.MOD_ID, "textures/gui/solar.png");
	private static final int GAUGE_WIDTH = 18;
	private static final int GAUGE_HEIGHT = 50;
	private static final int GAUGE_INNER_WIDTH = GAUGE_WIDTH - 2;
	private static final int GAUGE_INNER_HEIGHT = GAUGE_HEIGHT - 2;
	private static final int GAUGE_INNER_OFFSET_X = (GAUGE_WIDTH - GAUGE_INNER_WIDTH) / 2;
	private static final int GAUGE_INNER_OFFSET_Y = (GAUGE_HEIGHT - GAUGE_INNER_HEIGHT) / 2;
	private static final int GAUGE_SRC_X = 64;
	private static final int GAUGE_SRC_Y = 62;
	private static final int GAUGE_INNER_SRC_X = 0;
	private static final int GAUGE_INNER_SRC_Y = 64;
	private static final int BORDER_OFFSET = 8;
	
	private final TileBaseSolar solar;
	private final InventoryPlayer pinv;
	
	private List<String> tooltip = Lists.newArrayList();
	
	public GuiBaseSolar(TileBaseSolar tile, InventoryPlayer playerInv)
	{
		super(new ContainerBaseSolar(tile, playerInv));
		this.solar = tile;
		this.pinv = playerInv;
		xSize = 176;
		ySize = 180;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		GlStateManager.pushMatrix();
		fontRenderer.drawString(pinv.hasCustomName() ? pinv.getName() : I18n.format(pinv.getName()), BORDER_OFFSET, ySize - 96 + 2, 0x404040);
		fontRenderer.drawString(solar.getBlockType().getLocalizedName(), BORDER_OFFSET, 4, 0x404040);
		GlStateManager.translate(BORDER_OFFSET, BORDER_OFFSET + 6, 0);
		GlStateManager.scale(.9F, .9F, .9F);
		fontRenderer.drawString(I18n.format("info." + InfoSF.MOD_ID + ".energy.stored1", solar.energy), 0, 0, 0x404040);
		fontRenderer.drawString(I18n.format("info." + InfoSF.MOD_ID + ".energy.capacity", solar.capacity.getValueL()), 0, 10, 0x404040);
		fontRenderer.drawString(I18n.format("info." + InfoSF.MOD_ID + ".energy.generation", solar.getVar(4)), 0, 20, 0x404040);
		fontRenderer.drawString(I18n.format("info." + InfoSF.MOD_ID + ".energy.efficiency", Math.round(100D * solar.getVar(4) / solar.getVar(2))), 0, 30, 0x404040);
		
		GlStateManager.popMatrix();
		
		int //
		x = xSize - GAUGE_WIDTH - BORDER_OFFSET, //
		        y = BORDER_OFFSET + 32;
		
		boolean hover = inBounds(x + guiLeft, y + guiTop, GAUGE_WIDTH, GAUGE_HEIGHT, mouseX, mouseY);
		
		if(hover)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			drawGradientRect(x + 1, y + 1, x + GAUGE_WIDTH - 1, y + GAUGE_HEIGHT - 1, 0x88FFFFFF, 0x88FFFFFF);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			GlStateManager.color(1, 1, 1, 1);
			this.mc.getTextureManager().bindTexture(ELEMENTS);
			drawTexturedModalRect(x, y, GAUGE_SRC_X + 18, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);
			
			if(pinv.getItemStack().isEmpty())
				drawMouseOver(I18n.format("info." + InfoSF.MOD_ID + ".energy.stored2", solar.energy, solar.capacity.getValueL()));
		}
		
		x = xSize - 2 * GAUGE_WIDTH - BORDER_OFFSET - BORDER_OFFSET / 2;
		y = BORDER_OFFSET + 32;
		
		hover = inBounds(x + guiLeft, y + guiTop, GAUGE_WIDTH, GAUGE_HEIGHT, mouseX, mouseY);
		if(hover)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			drawGradientRect(x + 1, y + 1, x + GAUGE_WIDTH - 1, y + GAUGE_HEIGHT - 1, 0x88FFFFFF, 0x88FFFFFF);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			GlStateManager.color(1, 1, 1, 1);
			this.mc.getTextureManager().bindTexture(ELEMENTS);
			drawTexturedModalRect(x, y, GAUGE_SRC_X + 18, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);
			
			if(pinv.getItemStack().isEmpty())
				drawMouseOver(I18n.format("info." + InfoSF.MOD_ID + ".sun.intensity", Math.round(100 * solar.sunIntensity)));
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		int xStart = (width - xSize) / 2;
		int yStart = (height - ySize) / 2;
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);
		
		this.mc.getTextureManager().bindTexture(ELEMENTS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(2896);
		
		drawPower(xStart + xSize - GAUGE_WIDTH - BORDER_OFFSET, yStart + BORDER_OFFSET + 32, mouseX, mouseY);
		drawSun(xStart + xSize - 2 * GAUGE_WIDTH - BORDER_OFFSET - BORDER_OFFSET / 2, yStart + BORDER_OFFSET + 32, mouseX, mouseY);
		
		for(int i = 0; i < solar.upgradeInventory.getSizeInventory(); ++i)
		{
			drawTexturedModalRect(xStart + i * 18 + 8, yStart + 60, 18, 0, 18, 18);
		}
		
		drawTexturedModalRect(xStart + 150, yStart + 8, 18, 18, 18, 18);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		solar.setBaseValuesOnGet = false;
		drawDefaultBackground();
		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1);
		tooltip.clear();
		super.drawScreen(mouseX, mouseY, partialTicks);
		drawHoveringText(tooltip, mouseX, mouseY, fontRenderer);
		renderHoveredToolTip(mouseX, mouseY);
		solar.setBaseValuesOnGet = true;
	}
	
	private void drawPower(int x, int y, int mx, int my)
	{
		drawTexturedModalRect(x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y, GAUGE_INNER_SRC_X + GAUGE_INNER_WIDTH, GAUGE_INNER_SRC_Y, GAUGE_INNER_WIDTH, GAUGE_INNER_HEIGHT);
		
		double height = solar.energy * ((double) GAUGE_INNER_HEIGHT) / solar.capacity.getValueL();
		double offset = GAUGE_INNER_HEIGHT - height;
		
		drawTMR(x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y + offset, GAUGE_INNER_SRC_X, GAUGE_INNER_SRC_Y + offset, GAUGE_INNER_WIDTH, height);
		drawTexturedModalRect(x, y, GAUGE_SRC_X, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);
	}
	
	private void drawSun(int x, int y, int mouseX, int mouseY)
	{
		drawTexturedModalRect(x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y, 32 + GAUGE_INNER_SRC_X + GAUGE_INNER_WIDTH, GAUGE_INNER_SRC_Y, GAUGE_INNER_WIDTH, GAUGE_INNER_HEIGHT);
		
		float height = GAUGE_INNER_HEIGHT * solar.sunIntensity;
		float offset = GAUGE_INNER_HEIGHT - height;
		
		drawTMR(x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y + offset, 32 + GAUGE_INNER_SRC_X, GAUGE_INNER_SRC_Y + offset, GAUGE_INNER_WIDTH, height);
		drawTexturedModalRect(x, y, GAUGE_SRC_X, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);
	}
	
	public void drawTMR(double x, double y, double textureX, double textureY, double width, double height)
	{
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), (double) this.zLevel).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), (double) this.zLevel).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), (double) this.zLevel).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), (double) this.zLevel).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
		tessellator.draw();
	}
	
	public boolean inBounds(int x, int y, int w, int h, int mx, int my)
	{
		return mx >= x && mx < x + w && my >= y && my <= y + h;
	}
	
	public void drawMouseOver(String line)
	{
		if(line != null)
			Collections.addAll(tooltip, line.split("\n"));
	}
}