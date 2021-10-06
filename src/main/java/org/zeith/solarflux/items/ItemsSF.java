package org.zeith.solarflux.items;

import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.event.LanguageReloadEvent;
import org.zeith.solarflux.SolarFlux;
import org.zeith.solarflux.panels.SolarPanel;
import org.zeith.solarflux.panels.SolarPanels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@SimplyRegister
@Mod.EventBusSubscriber
public class ItemsSF
{
	@RegistryName("mirror")
	public static final Item MIRROR = newItem();

	@RegistryName("photovoltaic_cell_1")
	public static final Item PHOTOVOLTAIC_CELL_1 = newItem();

	@RegistryName("photovoltaic_cell_2")
	public static final Item PHOTOVOLTAIC_CELL_2 = newItem();

	@RegistryName("photovoltaic_cell_3")
	public static final Item PHOTOVOLTAIC_CELL_3 = newItem();

	@RegistryName("photovoltaic_cell_4")
	public static final Item PHOTOVOLTAIC_CELL_4 = newItem();

	@RegistryName("photovoltaic_cell_5")
	public static final Item PHOTOVOLTAIC_CELL_5 = newItem();

	@RegistryName("photovoltaic_cell_6")
	public static final Item PHOTOVOLTAIC_CELL_6 = newItem();

	@RegistryName("blank_upgrade")
	public static final Item BLANK_UPGRADE = newItem();

	@RegistryName("blazing_coating")
	public static final Item BLAZING_COATING = newItem();

	@RegistryName("emerald_glass")
	public static final Item EMERALD_GLASS = newItem();

	@RegistryName("ender_glass")
	public static final Item ENDER_GLASS = newItem();

	private static Item newItem()
	{
		return new Item(new Item.Properties().tab(SolarFlux.ITEM_GROUP));
	}

	private static final List<JSItem> ITEMS2REG = new ArrayList<>();
	public static final List<JSItem> JS_MATERIALS = Collections.unmodifiableList(ITEMS2REG);

	public static Item newJSItem(String name)
	{
		JSItem i = new JSItem(new Item.Properties().tab(SolarFlux.ITEM_GROUP));
		i.setRegistryName(name);
		ITEMS2REG.add(i);
		return i;
	}

	@SimplyRegister
	public static void register(Consumer<Item> items)
	{
		ITEMS2REG.forEach(items);
	}

	@SubscribeEvent
	public static void localize(LanguageReloadEvent e)
	{
		for(JSItem item : ITEMS2REG)
		{
			e.translate(item.getDescriptionId(), item.getLang().getName(e.getLang()));
		}

		SolarPanels.listPanels().forEach(sp ->
		{
			SolarPanel.LanguageData lang = sp.getLang();
			if(sp.isCustom && lang != null)
				e.translate(sp.getBlock().getDescriptionId(), lang.getName(e.getLang()));
		});
	}
}