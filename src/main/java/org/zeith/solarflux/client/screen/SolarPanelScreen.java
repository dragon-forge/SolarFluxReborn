package org.zeith.solarflux.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.solarflux.block.SolarPanelTile;
import org.zeith.solarflux.container.SolarPanelContainer;
import org.zeith.solarflux.util.ComplexProgressManager;

import java.util.Arrays;
import java.util.List;

public class SolarPanelScreen
		extends ScreenWTFMojang<SolarPanelContainer>
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

	public final ComplexProgressManager data;

	private List<IReorderingProcessor> tooltip = Lists.newArrayList();

	public SolarPanelScreen(SolarPanelContainer screenContainer, PlayerInventory inv, ITextComponent titleIn)
	{
		super(screenContainer, inv, titleIn);
		this.data = screenContainer.progressHandler;
		this.pinv = inv;
		this.solar = screenContainer.panel;
		imageWidth = 176;
		imageHeight = 180;
	}

	long energy, capacity, currentGeneration, generation;
	float sunIntensity;

	@Override
	public void tick()
	{
		energy = data.getLong(0);
		capacity = data.getLong(8);
		currentGeneration = data.getLong(16);
		generation = data.getLong(24);
		sunIntensity = data.getFloat(32);
		super.tick();
	}

	@Override
	protected void renderLabels(MatrixStack mstack, int mouseX, int mouseY)
	{
		mstack.pushPose();
		font.draw(mstack, pinv.getName(), BORDER_OFFSET, imageHeight - 96 + 2, 0x404040);
		font.draw(mstack, solar.getBlockState().getBlock().getName(), BORDER_OFFSET, 4, 0x404040);
		mstack.translate(BORDER_OFFSET, BORDER_OFFSET + 6, 0);
		mstack.scale(.9F, .9F, .9F);
		font.draw(mstack, I18n.get("info.solarflux.energy.stored1", energy), 0, 0, 0x404040);
		font.draw(mstack, I18n.get("info.solarflux.energy.capacity", capacity), 0, 10, 0x404040);
		font.draw(mstack, I18n.get("info.solarflux.energy.generation", currentGeneration), 0, 20, 0x404040);
		font.draw(mstack, I18n.get("info.solarflux.energy.efficiency", generation > 0 ? Math.round(100D * currentGeneration / generation) : 0), 0, 30, 0x404040);
		mstack.popPose();

		int //
				x = imageWidth - GAUGE_WIDTH - BORDER_OFFSET, //
				y = BORDER_OFFSET + 32;

		boolean hover = inBounds(x + leftPos, y + topPos, GAUGE_WIDTH, GAUGE_HEIGHT, mouseX, mouseY);

		if(hover)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			fillGradient(mstack, x + 1, y + 1, x + GAUGE_WIDTH - 1, y + GAUGE_HEIGHT - 1, 0x88FFFFFF, 0x88FFFFFF);
			GL11.glEnable(GL11.GL_TEXTURE_2D);

			GL11.glColor4f(1, 1, 1, 1);
			minecraft.getTextureManager().bind(ELEMENTS);
			blit(mstack, x, y, GAUGE_SRC_X + 18, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);

			if(pinv.getCarried().isEmpty())
				drawMouseOver(I18n.get("info.solarflux.energy.stored2", energy, capacity));
		}

		x = imageWidth - 2 * GAUGE_WIDTH - BORDER_OFFSET - BORDER_OFFSET / 2;
		y = BORDER_OFFSET + 32;

		hover = inBounds(x + leftPos, y + topPos, GAUGE_WIDTH, GAUGE_HEIGHT, mouseX, mouseY);
		if(hover)
		{
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			fillGradient(mstack, x + 1, y + 1, x + GAUGE_WIDTH - 1, y + GAUGE_HEIGHT - 1, 0x88FFFFFF, 0x88FFFFFF);
			GL11.glEnable(GL11.GL_TEXTURE_2D);

			GL11.glColor4f(1, 1, 1, 1);
			minecraft.getTextureManager().bind(ELEMENTS);
			blit(mstack, x, y, GAUGE_SRC_X + 18, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);

			if(pinv.getCarried().isEmpty())
				drawMouseOver(I18n.get("info.solarflux.sun.intensity", Math.round(100 * sunIntensity)));
		}
	}

	@Override
	public void render(MatrixStack ms, int mouseX, int mouseY, float partialTime)
	{
		tooltip.clear();
		super.render(ms, mouseX, mouseY, partialTime);
		renderTooltip(ms, tooltip, mouseX, mouseY);
	}

	@Override
	protected void renderBackground(MatrixStack mstack, float partialTicks, int mouseX, int mouseY)
	{
		int xStart = (width - imageWidth) / 2;
		int yStart = (height - imageHeight) / 2;

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		blit(mstack, xStart, yStart, 0, 0, imageWidth, imageHeight);

		minecraft.getTextureManager().bind(ELEMENTS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(2896);

		drawPower(mstack, xStart + imageWidth - GAUGE_WIDTH - BORDER_OFFSET, yStart + BORDER_OFFSET + 32, mouseX, mouseY);
		drawSun(mstack, xStart + imageWidth - 2 * GAUGE_WIDTH - BORDER_OFFSET - BORDER_OFFSET / 2, yStart + BORDER_OFFSET + 32, mouseX, mouseY);

		for(int i = 0; i < solar.upgradeInventory.getSlots(); ++i)
			blit(mstack, xStart + i * 18 + 8, yStart + 60, 18, 0, 18, 18);
		blit(mstack, xStart + 150, yStart + 8, 18, 18, 18, 18);
	}

	private void drawPower(MatrixStack mstack, int x, int y, int mx, int my)
	{
		blit(mstack, x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y, GAUGE_INNER_SRC_X + GAUGE_INNER_WIDTH, GAUGE_INNER_SRC_Y, GAUGE_INNER_WIDTH, GAUGE_INNER_HEIGHT);

		float height = energy * ((float) GAUGE_INNER_HEIGHT) / capacity;
		float offset = GAUGE_INNER_HEIGHT - height;

		drawTMR(mstack, x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y + offset, GAUGE_INNER_SRC_X, GAUGE_INNER_SRC_Y + offset, GAUGE_INNER_WIDTH, height);
		blit(mstack, x, y, GAUGE_SRC_X, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);
	}

	private void drawSun(MatrixStack mstack, int x, int y, int mouseX, int mouseY)
	{
		blit(mstack, x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y, 32 + GAUGE_INNER_SRC_X + GAUGE_INNER_WIDTH, GAUGE_INNER_SRC_Y, GAUGE_INNER_WIDTH, GAUGE_INNER_HEIGHT);

		float height = GAUGE_INNER_HEIGHT * sunIntensity;
		float offset = GAUGE_INNER_HEIGHT - height;

		drawTMR(mstack, x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y + offset, 32 + GAUGE_INNER_SRC_X, GAUGE_INNER_SRC_Y + offset, GAUGE_INNER_WIDTH, height);
		blit(mstack, x, y, GAUGE_SRC_X, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);
	}

	public void drawTMR(MatrixStack mstack, float x, float y, float textureX, float textureY, float width, float height)
	{
		Matrix4f mat = mstack.last().pose();

		float zLevel = 0;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuilder();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.vertex(mat, x + 0, y + height, zLevel).uv((float) (textureX + 0) * 0.00390625F, (float) (textureY + height) * 0.00390625F).endVertex();
		bufferbuilder.vertex(mat, x + width, y + height, zLevel).uv(((float) (textureX + width) * 0.00390625F), ((float) (textureY + height) * 0.00390625F)).endVertex();
		bufferbuilder.vertex(mat, x + width, y + 0, zLevel).uv(((float) (textureX + width) * 0.00390625F), ((float) (textureY + 0) * 0.00390625F)).endVertex();
		bufferbuilder.vertex(mat, x + 0, y + 0, zLevel).uv(((float) (textureX + 0) * 0.00390625F), ((float) (textureY + 0) * 0.00390625F)).endVertex();
		tessellator.end();
	}

	public boolean inBounds(int x, int y, int w, int h, int mx, int my)
	{
		return mx >= x && mx < x + w && my >= y && my <= y + h;
	}

	public void drawMouseOver(String line)
	{
		if(line != null)
			Arrays.stream(line.split("\n")).map(StringTextComponent::new).map(StringTextComponent::getVisualOrderText).forEach(tooltip::add);
	}
}