package org.zeith.solarflux.items._base;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.api.ISolarPanelTile;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class UpgradeSystem
{
	private static List<UpgradeItem> UPGRADE_ITEMS;
	
	public static <T> Stream<T> fromOptional(Optional<T> o)
	{
		return o.map(Stream::of).orElseGet(Stream::empty);
	}
	
	private static final Function<Ability<?>, List<?>> UPGRADE_ABILITY_CACHE = (ability ->
			UPGRADE_ITEMS.stream()
					.flatMap(u -> fromOptional(u.findAbility(ability)))
					.collect(Collectors.toList())
	);
	
	public static <T> List<T> getAllAbilities(Ability<T> ability)
	{
		return Cast.cast(UPGRADE_ABILITY_CACHE.apply(ability));
	}
	
	public static <T> List<T> findAbilitiesIn(ISolarPanelTile tile, Ability<T> ability)
	{
		return tile.getUpgrades()
				.flatMap(u -> fromOptional(u.a().findAbility(ability)))
				.distinct()
				.collect(Collectors.toList());
	}
	
	@SubscribeEvent
	public static void loadComplete(FMLLoadCompleteEvent e)
	{
		UPGRADE_ITEMS = ForgeRegistries.ITEMS.getValues().stream().filter(UpgradeItem.class::isInstance).map(UpgradeItem.class::cast).collect(Collectors.toList());
		SolarFlux.LOG.info("Registered " + UPGRADE_ITEMS.size() + " upgrades for Solar Panels.");
	}
}