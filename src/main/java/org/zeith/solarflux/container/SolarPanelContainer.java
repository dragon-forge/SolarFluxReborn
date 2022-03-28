package org.zeith.solarflux.container;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.api.inv.IScreenContainer;
import org.zeith.solarflux.block.SolarPanelTile;
import org.zeith.solarflux.client.screen.SolarPanelScreen;
import org.zeith.solarflux.util.ComplexProgressManager;

public class SolarPanelContainer
		extends AbstractContainerMenu
		implements IScreenContainer
{
	public final SolarPanelTile panel;

	public final ComplexProgressManager progressHandler = new ComplexProgressManager(4 * 8 + 4, 0);

	public SolarPanelContainer(int id, Inventory playerInv, SolarPanelTile tile)
	{
		super(ContainerAPI.TILE_CONTAINER, id);
		this.panel = tile;

		addPlayerInventorySlotsToContainer(playerInv, 8, 98);
		addPlayerActionSlotsToContainer(playerInv, 8, 156);

		for(int i = 0; i < tile.upgradeInventory.getSlots(); ++i)
			addSlot(new SlotUpgrade(tile, i, i * 18 + 9, 61));

		addSlot(new SlotChargable(tile.chargeInventory, 0, 151, 9));
	}

	protected void addPlayerInventorySlotsToContainer(Inventory pInventoryPlayer, int pLeft, int pTop)
	{
		for(int inventoryRowIndex = 0; inventoryRowIndex < 3; ++inventoryRowIndex)
			for(int inventoryColumnIndex = 0; inventoryColumnIndex < 9; ++inventoryColumnIndex)
				addSlot(new Slot(pInventoryPlayer, inventoryColumnIndex + inventoryRowIndex * 9 + 9, pLeft + inventoryColumnIndex * 18, pTop + inventoryRowIndex * 18));
	}

	protected void addPlayerActionSlotsToContainer(Inventory player, int x, int y)
	{
		for(int i = 0; i < 9; ++i)
			addSlot(new Slot(player, i, x + i * 18, y));
	}

	@Override
	public void broadcastChanges()
	{
		progressHandler.putLong(0, panel.energy);
		progressHandler.putLong(8, panel.capacity.getValueL());
		progressHandler.putLong(16, panel.currentGeneration);
		progressHandler.putLong(24, panel.generation.getValueL());
		progressHandler.putFloat(32, panel.sunIntensity);

		progressHandler.detectAndSendChanges(this);
		super.broadcastChanges();
	}

	@Override
	public void setData(int id, int data)
	{
		progressHandler.updateChange(id, data);
	}

	@Override
	public boolean stillValid(Player playerIn)
	{
		return panel.getBlockPos().closerToCenterThan(playerIn.position(), 64) && !panel.isRemoved();
	}

	@Override
	public ItemStack quickMoveStack(Player p_82846_1_, int p_82846_2_)
	{
		return ItemStack.EMPTY;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Screen openScreen(Inventory inv, Component label)
	{
		return new SolarPanelScreen(this, inv, label);
	}
}