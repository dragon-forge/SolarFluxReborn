package tk.zeitheron.solarflux.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import tk.zeitheron.solarflux.SolarFlux;
import tk.zeitheron.solarflux.block.SolarPanelTile;

public class SolarPanelContainer extends Container
{
	public final SolarPanelTile panel;
	
	public SolarPanelContainer(int id, PlayerInventory playerInv, SolarPanelTile tile)
	{
		super(SolarFlux.SOLAR_PANEL_CONTAINER, id);
		this.panel = tile;
		
		addPlayerInventorySlotsToContainer(playerInv, 8, 98);
		addPlayerActionSlotsToContainer(playerInv, 8, 156);
		
		for(int i = 0; i < tile.items.getSlots(); ++i)
			addSlot(new SlotUpgrade(tile, i, i * 18 + 9, 61));
		
		addSlot(new SlotChargable(tile.itemChargeable, 0, 151, 9));
	}
	
	protected void addPlayerInventorySlotsToContainer(PlayerInventory pInventoryPlayer, int pLeft, int pTop)
	{
		for(int inventoryRowIndex = 0; inventoryRowIndex < 3; ++inventoryRowIndex)
			for(int inventoryColumnIndex = 0; inventoryColumnIndex < 9; ++inventoryColumnIndex)
				addSlot(new Slot(pInventoryPlayer, inventoryColumnIndex + inventoryRowIndex * 9 + 9, pLeft + inventoryColumnIndex * 18, pTop + inventoryRowIndex * 18));
	}
	
	protected void addPlayerActionSlotsToContainer(PlayerInventory player, int x, int y)
	{
		for(int i = 0; i < 9; ++i)
			addSlot(new Slot(player, i, x + i * 18, y));
	}
	
	@Override
	public void detectAndSendChanges()
	{
		
		super.detectAndSendChanges();
	}
	
	@Override
	public boolean canInteractWith(PlayerEntity playerIn)
	{
		return panel.getPos().distanceSq(playerIn.posX, playerIn.posY, playerIn.posZ, true) <= 64;
	}
	
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index)
	{
		return ItemStack.EMPTY;
	}
}