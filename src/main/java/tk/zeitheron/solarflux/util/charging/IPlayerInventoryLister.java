package tk.zeitheron.solarflux.util.charging;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IPlayerInventoryLister
{
	void listItemHandlers(PlayerEntity player, List<IItemHandlerModifiable> handlers);
}