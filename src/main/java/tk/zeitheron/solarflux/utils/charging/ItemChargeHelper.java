package tk.zeitheron.solarflux.utils.charging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import tk.zeitheron.solarflux.utils.charging.fe.FECharge;
import tk.zeitheron.solarflux.utils.charging.fe.FEChargeHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ItemChargeHelper
{
	private static final Map<Class<? extends AbstractCharge>, IChargeHandler> CHARGE_HANDLERS = new HashMap<>();
	private static final Map<String, IChargeHandler> CHARGE_HANDLERS_BY_ID = new HashMap<>();
	
	public static final List<IPlayerInventoryLister> playerInvListers = new ArrayList<>();
	
	static
	{
		handle(FECharge.class, new FEChargeHandler());
		playerInvListers.add(new VanillaPlayerInvLister());
	}
	
	/**
	 * Maps a handler to a charge type.
	 * 
	 * @param <T>
	 *            The charge type
	 * @param chargeType
	 *            the class of the charge
	 * @param handler
	 *            the handler to be assigned
	 */
	public static <T extends AbstractCharge> void handle(Class<T> chargeType, IChargeHandler<T> handler)
	{
		CHARGE_HANDLERS.put(chargeType, handler);
		CHARGE_HANDLERS_BY_ID.put(handler.getId(), handler);
	}
	
	public static <T extends AbstractCharge> IChargeHandler<T> getHandler(String id)
	{
		return CHARGE_HANDLERS_BY_ID.get(id);
	}
	
	public static <T extends AbstractCharge> IChargeHandler<T> getHandler(T charge)
	{
		return CHARGE_HANDLERS.get(charge.getClass());
	}
	
	public static <T extends AbstractCharge> T charge(ItemStack stack, T charge, boolean simulate)
	{
		IChargeHandler<T> h = getHandler(charge);
		if(h == null)
			return charge;
		return h.charge(stack, charge, simulate);
	}
	
	private static final Predicate<Integer> I_TRUE = i -> true;
	
	public static <T extends AbstractCharge> T chargeInventory(IInventory inv, T charge, boolean simulate)
	{
		return chargeInventory(inv, I_TRUE, charge, simulate);
	}
	
	/**
	 * Charges player's inventory. Supports modules, HammerCore supports
	 * player's vanilla inventory and baubles inventory.
	 * 
	 * @param <T>
	 *            The charge type
	 * @param player
	 *            The player
	 * @param charge
	 *            The charge
	 * @param simulate
	 *            true to simulate, false to perform
	 */
	public static <T extends AbstractCharge> T chargePlayer(EntityPlayer player, T charge, boolean simulate)
	{
		IChargeHandler<T> h = getHandler(charge);
		if(h == null)
			return charge;
		
		List<IItemHandlerModifiable> handlers = new ArrayList<>();
		playerInvListers.forEach(pil -> pil.listItemHandlers(player, handlers));
		
		for(int j = 0; j < handlers.size() && charge.containsCharge(); ++j)
		{
			IItemHandlerModifiable handler = handlers.get(j);
			for(int i = 0; i < handler.getSlots() && charge.containsCharge(); ++i)
				if(h.canCharge(handler.getStackInSlot(i), charge))
				{
					ItemStack stack = handler.getStackInSlot(i).copy();
					charge = h.charge(stack, charge, simulate);
					handler.setStackInSlot(i, stack);
				}
		}
		
		return charge;
	}
	
	public static <T extends AbstractCharge> T chargeInventory(IInventory inv, Predicate<Integer> chargeSlot, T charge, boolean simulate)
	{
		IChargeHandler<T> h = getHandler(charge);
		if(h == null)
			return charge;
		for(int i = 0; i < inv.getSizeInventory() && charge.containsCharge(); ++i)
			if(chargeSlot.test(i) && h.canCharge(inv.getStackInSlot(i), charge))
				charge = h.charge(inv.getStackInSlot(i), charge, simulate);
		return charge;
	}
}