package org.zeith.solarflux.client.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import org.zeith.hammerlib.client.screen.ScreenWTFMojang;
import org.zeith.hammerlib.client.utils.FXUtils;
import org.zeith.hammerlib.client.utils.RenderUtils;
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
	private final Inventory pinv;

	public final ComplexProgressManager data;

	private final List<FormattedCharSequence> tooltip = Lists.newArrayList();

	public SolarPanelScreen(SolarPanelContainer screenContainer, Inventory inv, Component titleIn)
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
	public void containerTick()
	{
		energy = data.getLong(0);
		capacity = data.getLong(8);
		currentGeneration = data.getLong(16);
		generation = data.getLong(24);
		sunIntensity = data.getFloat(32);
		super.containerTick();
	}

	@Override
	protected boolean renderForeground(PoseStack pose, int mouseX, int mouseY)
	{
		pose.pushPose();

		font.draw(pose, pinv.getName().getString(), BORDER_OFFSET, imageHeight - 96 + 2, 0x404040);
		font.draw(pose, solar.getBlockState().getBlock().getName(), BORDER_OFFSET, 4, 0x404040);
		pose.translate(BORDER_OFFSET, BORDER_OFFSET + 6, 0);
		pose.scale(0.9F, 0.9F, 0.9F);
		font.draw(pose, I18n.get("info.solarflux.energy.stored1", energy), 0, 0, 0x404040);
		font.draw(pose, I18n.get("info.solarflux.energy.capacity", capacity), 0, 10, 0x404040);
		font.draw(pose, I18n.get("info.solarflux.energy.generation", currentGeneration), 0, 20, 0x404040);
		font.draw(pose, I18n.get("info.solarflux.energy.efficiency", generation > 0 ? Math.round(100D * currentGeneration / generation) : 0), 0, 30, 0x404040);

		pose.popPose();

		int //
				x = imageWidth - GAUGE_WIDTH - BORDER_OFFSET, //
				y = BORDER_OFFSET + 32;

		boolean hover = inBounds(x + leftPos, y + topPos, GAUGE_WIDTH, GAUGE_HEIGHT, mouseX, mouseY);

		if(hover)
		{
			RenderSystem.disableTexture();
			fillGradient(pose, x + 1, y + 1, x + GAUGE_WIDTH - 1, y + GAUGE_HEIGHT - 1, 0x88FFFFFF, 0x88FFFFFF);
			RenderSystem.enableTexture();

			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			FXUtils.bindTexture(ELEMENTS);
			drawTMR(pose, x, y, GAUGE_SRC_X + 18, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);

			if(menu.getCarried().isEmpty())
				drawMouseOver(I18n.get("info.solarflux.energy.stored2", energy, capacity));
		}

		x = imageWidth - 2 * GAUGE_WIDTH - BORDER_OFFSET - BORDER_OFFSET / 2;
		y = BORDER_OFFSET + 32;

		hover = inBounds(x + leftPos, y + topPos, GAUGE_WIDTH, GAUGE_HEIGHT, mouseX, mouseY);
		if(hover)
		{
			RenderSystem.disableTexture();
			fillGradient(pose, x + 1, y + 1, x + GAUGE_WIDTH - 1, y + GAUGE_HEIGHT - 1, 0x88FFFFFF, 0x88FFFFFF);
			RenderSystem.enableTexture();

			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
			FXUtils.bindTexture(ELEMENTS);
			drawTMR(pose, x, y, GAUGE_SRC_X + 18, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);

			if(menu.getCarried().isEmpty())
				drawMouseOver(I18n.get("info.solarflux.sun.intensity", Math.round(100 * sunIntensity)));
		}

		return true;
	}

	@Override
	protected void renderBackground(PoseStack pose, float partialTime, int mouseX, int mouseY)
	{
		int xStart = (width - imageWidth) / 2;
		int yStart = (height - imageHeight) / 2;

		FXUtils.bindTexture(TEXTURE);
		drawTMR(pose, xStart, yStart, 0, 0, imageWidth, imageHeight);

		FXUtils.bindTexture(ELEMENTS);

		drawPower(pose, xStart + imageWidth - GAUGE_WIDTH - BORDER_OFFSET, yStart + BORDER_OFFSET + 32, mouseX, mouseY);
		drawSun(pose, xStart + imageWidth - 2 * GAUGE_WIDTH - BORDER_OFFSET - BORDER_OFFSET / 2, yStart + BORDER_OFFSET + 32, mouseX, mouseY);

		for(int i = 0; i < solar.upgradeInventory.getSlots(); ++i)
			drawTMR(pose, xStart + i * 18 + 8, yStart + 60, 18, 0, 18, 18);
		drawTMR(pose, xStart + 150, yStart + 8, 18, 18, 18, 18);
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks)
	{
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		tooltip.clear();
		super.render(pose, mouseX, mouseY, partialTicks);
		renderTooltip(pose, tooltip, mouseX, mouseY);
	}

	private void drawPower(PoseStack mstack, int x, int y, int mx, int my)
	{
		drawTMR(mstack, x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y, GAUGE_INNER_SRC_X + GAUGE_INNER_WIDTH, GAUGE_INNER_SRC_Y, GAUGE_INNER_WIDTH, GAUGE_INNER_HEIGHT);

		float height = energy * ((float) GAUGE_INNER_HEIGHT) / capacity;
		float offset = GAUGE_INNER_HEIGHT - height;

		drawTMR(mstack, x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y + offset, GAUGE_INNER_SRC_X, GAUGE_INNER_SRC_Y + offset, GAUGE_INNER_WIDTH, height);
		drawTMR(mstack, x, y, GAUGE_SRC_X, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);
	}

	private void drawSun(PoseStack mstack, int x, int y, int mouseX, int mouseY)
	{
		drawTMR(mstack, x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y, 32 + GAUGE_INNER_SRC_X + GAUGE_INNER_WIDTH, GAUGE_INNER_SRC_Y, GAUGE_INNER_WIDTH, GAUGE_INNER_HEIGHT);

		float height = GAUGE_INNER_HEIGHT * sunIntensity;
		float offset = GAUGE_INNER_HEIGHT - height;

		drawTMR(mstack, x + GAUGE_INNER_OFFSET_X, y + GAUGE_INNER_OFFSET_Y + offset, 32 + GAUGE_INNER_SRC_X, GAUGE_INNER_SRC_Y + offset, GAUGE_INNER_WIDTH, height);
		drawTMR(mstack, x, y, GAUGE_SRC_X, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);
	}

	public void drawTMR(PoseStack pose, float x, float y, float textureX, float textureY, float width, float height)
	{
		RenderUtils.drawTexturedModalRect(pose, x, y, textureX, textureY, width, height);
	}

	public boolean inBounds(int x, int y, int w, int h, int mx, int my)
	{
		return mx >= x && mx < x + w && my >= y && my <= y + h;
	}

	public void drawMouseOver(String line)
	{
		if(line != null)
			Arrays.stream(line.split("\n"))
					.map(TextComponent::new)
					.map(TextComponent::getVisualOrderText)
					.forEach(tooltip::add);
	}
}