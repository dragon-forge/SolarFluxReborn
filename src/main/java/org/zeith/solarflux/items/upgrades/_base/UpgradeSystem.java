package org.zeith.solarflux.items.upgrades._base;

import net.minecraft.Util;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.compat.base.Ability;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.api.ISolarPanelTile;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class UpgradeSystem
{
	private static List<UpgradeItem> UPGRADE_ITEMS;
	
	private static final Function<Ability<?>, List<?>> UPGRADE_ABILITY_CACHE = Util.memoize(ability ->
			UPGRADE_ITEMS.stream()
					.flatMap(u -> u.findAbility(ability).stream())
					.collect(Collectors.toList())
	);
	
	public static <T> List<T> getAllAbilities(Ability<T> ability)
	{
		return Cast.cast(UPGRADE_ABILITY_CACHE.apply(ability));
	}
	
	public static <T> List<T> findAbilitiesIn(ISolarPanelTile tile, Ability<T> ability)
	{
		return tile.getUpgrades()
				.flatMap(u -> u.a().findAbility(ability).stream())
				.distinct()
				.toList();
	}
	
	@SubscribeEvent
	public static void loadComplete(FMLLoadCompleteEvent e)
	{
		UPGRADE_ITEMS = ForgeRegistries.ITEMS.getValues().stream().filter(UpgradeItem.class::isInstance).map(UpgradeItem.class::cast).collect(Collectors.toList());
		SolarFlux.LOG.info("Registered " + UPGRADE_ITEMS.size() + " upgrades for Solar Panels.");
	}
}