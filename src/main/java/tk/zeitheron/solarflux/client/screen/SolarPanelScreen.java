package tk.zeitheron.solarflux.client.screen;

import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import tk.zeitheron.solarflux.block.SolarPanelTile;
import tk.zeitheron.solarflux.container.SolarPanelContainer;

public class SolarPanelScreen extends ContainerScreen<SolarPanelContainer>
{
	private static final ResourceLocation ELEMENTS = new ResourceLocation("solarflux", "textures/gui/elements.png");
	private static final ResourceLocation TEXTURE = new ResourceLocation("solarflux", "textures/gui/solar.png");
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
	
	public final SolarPanelTile solar;
	private final PlayerInventory pinv;
	
	private List<String> tooltip = Lists.newArrayList();
	
	public SolarPanelScreen(SolarPanelContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
	{
		super(screenContainer, inv, titleIn);
		this.pinv = inv;
		this.solar = screenContainer.panel;
		xSize = 176;
		ySize = 180;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		GlStateManager.pushMatrix();
		font.drawString(pinv.getName().getFormattedText(), BORDER_OFFSET, ySize - 96 + 2, 0x404040);
		font.drawString(solar.getBlockState().getBlock().getNameTextComponent().getFormattedText(), BORDER_OFFSET, 4, 0x404040);
		GL11.glTranslatef(BORDER_OFFSET, BORDER_OFFSET + 6, 0);
		GL11.glScalef(.9F, .9F, .9F);
		font.drawString(I18n.format("info.solarflux.energy.stored1", solar.energy), 0, 0, 0x404040);
		font.drawString(I18n.format("info.solarflux.energy.capacity", solar.capacity.getValueL()), 0, 10, 0x404040);
		font.drawString(I18n.format("info.solarflux.energy.generation", solar.currentGeneration), 0, 20, 0x404040);
		font.drawString(I18n.format("info.solarflux.energy.efficiency", solar.generation.getValueL() > 0 ? Math.round(100D * solar.currentGeneration / solar.generation.getValueL()) : 0), 0, 30, 0x404040);
		
		GlStateManager.popMatrix();
		
		int //
		x = xSize - GAUGE_WIDTH - BORDER_OFFSET, //
		        y = BORDER_OFFSET + 32;
		
		boolean hover = inBounds(x + guiLeft, y + guiTop, GAUGE_WIDTH, GAUGE_HEIGHT, mouseX, mouseY);
		
		if(hover)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			fillGradient(x + 1, y + 1, x + GAUGE_WIDTH - 1, y + GAUGE_HEIGHT - 1, 0x88FFFFFF, 0x88FFFFFF);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			GL11.glColor4f(1, 1, 1, 1);
			minecraft.getTextureManager().bindTexture(ELEMENTS);
			blit(x, y, GAUGE_SRC_X + 18, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);
			
			if(pinv.getItemStack().isEmpty())
				drawMouseOver(I18n.format("info.solarflux.energy.stored2", solar.energy, solar.capacity.getValueL()));
		}
		
		x = xSize - 2 * GAUGE_WIDTH - BORDER_OFFSET - BORDER_OFFSET / 2;
		y = BORDER_OFFSET + 32;
		
		hover = inBounds(x + guiLeft, y + guiTop, GAUGE_WIDTH, GAUGE_HEIGHT, mouseX, mouseY);
		if(hover)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			fillGradient(x + 1, y + 1, x + GAUGE_WIDTH - 1, y + GAUGE_HEIGHT - 1, 0x88FFFFFF, 0x88FFFFFF);
			GL11.glEnable(GL11.GL_TEXTURE_2D);

			GL11.glColor4f(1, 1, 1, 1);
			minecraft.getTextureManager().bindTexture(ELEMENTS);
			blit(x, y, GAUGE_SRC_X + 18, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);
			
			if(pinv.getItemStack().isEmpty())
				drawMouseOver(I18n.format("info.solarflux.sun.intensity", Math.round(100 * solar.sunIntensity)));
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		int xStart = (width - xSize) / 2;
		int yStart = (height - ySize) / 2;
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		blit(xStart, yStart, 0, 0, xSize, ySize);
		
		minecraft.getTextureManager().bindTexture(ELEMENTS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(2896);
		
		drawPower(xStart + xSize - GAUGE_WIDTH - BORDER_OFFSET, yStart + BORDER_OFFSET + 32, mouseX, mouseY);
		drawSun(xStart + xSize - 2 * GAUGE_WIDTH - BORDER_OFFSET - BORDER_OFFSET / 2, yStart + BORDER_OFFSET + 32, mouseX, mouseY);
		
		for(int i = 0; i < solar.items.getSlots(); ++i)
			blit(xStart + i * 18 + 8, yStart + 60, 18, 0, 18, 18);
		blit(xStart + 150, yStart + 8, 18, 18, 18, 18);
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		renderBackground();
		GlStateManager.enableBlend();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		tooltip.clear();
		super.render(mouseX, mouseY, partialTicks);
		renderTooltip(tooltip, mouseX, mouseY, font);
		renderHoveredToolTip(mouseX, mouseY);
	}
	
	private void drawPower(int x, int y, int mx, int my)
	{
		blit(x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y, GAUGE_INNER_SRC_X + GAUGE_INNER_WIDTH, GAUGE_INNER_SRC_Y, GAUGE_INNER_WIDTH, GAUGE_INNER_HEIGHT);
		
		double height = solar.energy * ((double) GAUGE_INNER_HEIGHT) / solar.capacity.getValueL();
		double offset = GAUGE_INNER_HEIGHT - height;
		
		drawTMR(x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y + offset, GAUGE_INNER_SRC_X, GAUGE_INNER_SRC_Y + offset, GAUGE_INNER_WIDTH, height);
		blit(x, y, GAUGE_SRC_X, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);
	}
	
	private void drawSun(int x, int y, int mouseX, int mouseY)
	{
		blit(x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y, 32 + GAUGE_INNER_SRC_X + GAUGE_INNER_WIDTH, GAUGE_INNER_SRC_Y, GAUGE_INNER_WIDTH, GAUGE_INNER_HEIGHT);
		
		float height = GAUGE_INNER_HEIGHT * solar.sunIntensity;
		float offset = GAUGE_INNER_HEIGHT - height;
		
		drawTMR(x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y + offset, 32 + GAUGE_INNER_SRC_X, GAUGE_INNER_SRC_Y + offset, GAUGE_INNER_WIDTH, height);
		blit(x, y, GAUGE_SRC_X, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);
	}
	
	public void drawTMR(double x, double y, double textureX, double textureY, double width, double height)
	{
		double zLevel = 0;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double) (x + 0), (double) (y + height), (double) zLevel).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + height), (double) zLevel).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + width), (double) (y + 0), (double) zLevel).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
		bufferbuilder.pos((double) (x + 0), (double) (y + 0), (double) zLevel).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
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