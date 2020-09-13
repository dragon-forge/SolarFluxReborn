package tk.zeitheron.solarflux.util.charging;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

class VanillaPlayerInvLister implements IPlayerInventoryLister
{
	@Override
	public void listItemHandlers(PlayerEntity player, List<IItemHandlerModifiable> handlers)
	{
		handlers.add(new InvWrapper(player.inventory));
	}
}