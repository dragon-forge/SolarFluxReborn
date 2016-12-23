package com.mrdimka.solarfluxreborn.gui;

import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.mrdimka.hammercore.client.RenderUtil;
import com.mrdimka.solarfluxreborn.reference.Reference;
import com.mrdimka.solarfluxreborn.te.SolarPanelTileEntity;
import com.mrdimka.solarfluxreborn.utility.Lang;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiSolarPanel extends GuiContainer
{
    private static final ResourceLocation ELEMENTS = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/elements.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID.toLowerCase(), "textures/gui/solar.png");
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
    // How far from the border of the GUI we start to draw.
    private static final int BORDER_OFFSET = 8;

    private final SolarPanelTileEntity mSolarPanelTileEntity;
    private final InventoryPlayer mInventoryPlayer;

    private List<String> mMouseHover = Lists.newArrayList();
    
    public GuiSolarPanel(InventoryPlayer pInventoryPlayer, SolarPanelTileEntity pSolarPanelTileEntity) {
        super(new ContainerSolarPanel(pInventoryPlayer, pSolarPanelTileEntity));
        mSolarPanelTileEntity = pSolarPanelTileEntity;
        mInventoryPlayer = pInventoryPlayer;
        xSize = 176;
        ySize = 180;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int pMouseX, int pMouseY) {
        fontRendererObj.drawString(
                mInventoryPlayer.hasCustomName() ? mInventoryPlayer.getName() : I18n.format(mInventoryPlayer.getName()),
                BORDER_OFFSET,
                ySize - 96 + 2,
                0x404040);

        fontRendererObj.drawString(
                String.format("%s: %,d %s", Lang.localise("energy.stored"), mSolarPanelTileEntity.getEnergyStored(), Lang.localise("rf")),
                BORDER_OFFSET,
                BORDER_OFFSET,
                0x404040);

        fontRendererObj.drawString(
                String.format("%s: %,d %s", Lang.localise("energy.capacity"), mSolarPanelTileEntity.getMaxEnergyStored(), Lang.localise("rf")),
                BORDER_OFFSET,
                BORDER_OFFSET + 10,
                0x404040);

        fontRendererObj.drawString(
                String.format("%s: %,d %s", Lang.localise("energy.generation"), mSolarPanelTileEntity.getCurrentEnergyGeneration(), Lang.localise("rfPerTick")),
                BORDER_OFFSET,
                BORDER_OFFSET + 20,
                0x404040);

        fontRendererObj.drawString(
                String.format(
                        "%s: %,d%%",
                        Lang.localise("energy.efficiency"),
                        Math.round(100D * mSolarPanelTileEntity.getCurrentEnergyGeneration() / mSolarPanelTileEntity.getMaximumEnergyGeneration())),
                BORDER_OFFSET,
                BORDER_OFFSET + 30,
                0x404040);
        super.drawGuiContainerForegroundLayer(pMouseX, pMouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float pOpacity, int pMouseX, int pMouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(TEXTURE);
        int xStart = (width - xSize) / 2;
        int yStart = (height - ySize) / 2;
        RenderUtil.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);

        // Prepare
        this.mc.getTextureManager().bindTexture(ELEMENTS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2896);

        // Draw slots.
        for (int i = 0; i < SolarPanelTileEntity.INVENTORY_SIZE; ++i) {
        	RenderUtil.drawTexturedModalRect(xStart + 17 + i * 18 - 1, yStart + 59 - 1, 0, 0, 18, 18);
        }

        drawPower(xStart + xSize - GAUGE_WIDTH - BORDER_OFFSET, yStart + BORDER_OFFSET + 32, pMouseX, pMouseY);
        drawSun(xStart + xSize - 2 * GAUGE_WIDTH - BORDER_OFFSET - BORDER_OFFSET / 2, yStart + BORDER_OFFSET + 32, pMouseX, pMouseY);
    }

    /**
     * Heavily inspired by Vswe.
     */
    private void drawPower(int pLeft, int pTop, int pMouseX, int pMouseY) {
        // Background color.
        RenderUtil.drawTexturedModalRect(
                pLeft + GAUGE_INNER_OFFSET_X,
                pTop + GAUGE_INNER_OFFSET_Y,
                GAUGE_INNER_SRC_X + GAUGE_INNER_WIDTH,
                GAUGE_INNER_SRC_Y,
                GAUGE_INNER_WIDTH,
                GAUGE_INNER_HEIGHT);

        double height = mSolarPanelTileEntity.getScaledEnergyStoredFraction(GAUGE_INNER_HEIGHT);
        double offset = GAUGE_INNER_HEIGHT - height;
        // Foreground color (level).
        RenderUtil.drawTexturedModalRect(
                pLeft + GAUGE_INNER_OFFSET_X,
                pTop + GAUGE_INNER_OFFSET_Y + offset,
                GAUGE_INNER_SRC_X,
                GAUGE_INNER_SRC_Y + offset,
                GAUGE_INNER_WIDTH,
                height);
        // Little bar.
        RenderUtil.drawTexturedModalRect(pLeft, pTop + GAUGE_INNER_OFFSET_Y + offset - 1, GAUGE_SRC_X, GAUGE_SRC_Y - 1, GAUGE_WIDTH, 1);

        int srcX = GAUGE_SRC_X;
        boolean hover = inBounds(pLeft, pTop, GAUGE_WIDTH, GAUGE_HEIGHT, pMouseX, pMouseY);
        if (hover) {
            // Highlighted gauge is just on the right.
            srcX += GAUGE_WIDTH;
        }
        // Gauge
        RenderUtil.drawTexturedModalRect(pLeft, pTop, srcX, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);

        if (hover) {
            String str = String.format(
                    "%s: %,d/%,d",
                    Lang.localise("energy.stored"),
                    mSolarPanelTileEntity.getEnergyStored(),
                    mSolarPanelTileEntity.getMaxEnergyStored());
            drawMouseOver(str);
        }
    }
    
    @Override
    public void drawScreen(int pMouseX, int pMouseY, float pOpacity) {
        clearMouseHoverCache();
        super.drawScreen(pMouseX, pMouseY, pOpacity);
        drawHoveringText(mMouseHover, pMouseX, pMouseY, fontRendererObj);
    }

    /**
     * Sets some text to be drawn later in a tooltip.
     */
    public void drawMouseOver(String pText) {
        if (pText != null) {
            clearMouseHoverCache();
            Collections.addAll(mMouseHover, pText.split("\n"));
        }
    }

    private void clearMouseHoverCache() {
        mMouseHover.clear();
    }
    
    public boolean inBounds(int pLeft, int pTop, int pWidth, int pHeight, int pMouseX, int pMouseY) {
        return (pLeft <= pMouseX) && (pMouseX < pLeft + pWidth) && (pTop <= pMouseY) && (pMouseY < pTop + pHeight);
    }

    /**
     * Heavily inspired by Vswe.
     */
    private void drawSun(int pLeft, int pTop, int pMouseX, int pMouseY) {
        // TODO Refactor to remove this ugly copy/paste.
        // Background color.
    	RenderUtil.drawTexturedModalRect(
                pLeft + GAUGE_INNER_OFFSET_X,
                pTop + GAUGE_INNER_OFFSET_Y,
                32 + GAUGE_INNER_SRC_X + GAUGE_INNER_WIDTH,
                GAUGE_INNER_SRC_Y,
                GAUGE_INNER_WIDTH,
                GAUGE_INNER_HEIGHT);
    	
    	mSolarPanelTileEntity.updateCurrentEnergyGeneration(mSolarPanelTileEntity.getPos().up());
        double height = (((double) GAUGE_INNER_HEIGHT) * ((double) mSolarPanelTileEntity.getSunIntensity()));
        double offset = GAUGE_INNER_HEIGHT - height;
        // Foreground color (level).
        RenderUtil.drawTexturedModalRect(
                pLeft + GAUGE_INNER_OFFSET_X,
                pTop + GAUGE_INNER_OFFSET_Y + offset,
                32 + GAUGE_INNER_SRC_X,
                GAUGE_INNER_SRC_Y + offset,
                GAUGE_INNER_WIDTH,
                height);
        // Little bar.
        RenderUtil.drawTexturedModalRect(pLeft, pTop + GAUGE_INNER_OFFSET_Y + offset - 1, GAUGE_SRC_X + GAUGE_WIDTH, GAUGE_SRC_Y - 1, GAUGE_WIDTH, 1);

        int srcX = GAUGE_SRC_X;
        boolean hover = inBounds(pLeft, pTop, GAUGE_WIDTH, GAUGE_HEIGHT, pMouseX, pMouseY);
        if (hover) {
            // Highlighted gauge is just on the right.
            srcX += GAUGE_WIDTH;
        }
        // Gauge
        RenderUtil.drawTexturedModalRect(pLeft, pTop, srcX, GAUGE_SRC_Y, GAUGE_WIDTH, GAUGE_HEIGHT);

        if (hover) {
            String str = String.format(
                    "%s: %d%%",
                    Lang.localise("sun.intensity"),
                    (int) (100 * mSolarPanelTileEntity.getSunIntensity()));
            drawMouseOver(str);
        }
    }
}
