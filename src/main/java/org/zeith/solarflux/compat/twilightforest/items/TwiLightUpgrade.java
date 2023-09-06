package org.zeith.solarflux.compat.twilightforest.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.compat.base.Ability;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.items.upgrades._base.*;

import java.util.*;

public class TwiLightUpgrade
		extends UpgradeItem
		implements ISunIntensityMod
{
	public static final ResourceKey<DimensionType> TWILIGHT_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation("twilightforest", "twilight_forest_type"));
	
	public TwiLightUpgrade()
	{
		super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
	}
	
	public static boolean isTwilight(Level level)
	{
		return level != null && Objects.equals(level.dimensionTypeId(), TWILIGHT_DIM_TYPE);
	}
	
	@Override
	public float applySunIntensityModifier(ISolarPanelTile tile, float value)
	{
		// For TF dimension only!
		if(tile.doesSeeSky() && isTwilight(tile.level()))
			return 0.45F;
		
		return value;
	}
	
	@Override
	public boolean canInstall(ISolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return isTwilight(tile.level()) && super.canInstall(tile, stack, upgradeInv);
	}
	
	@Override
	public boolean canStayInPanel(ISolarPanelTile tile, ItemStack stack, SimpleInventory upgradeInv)
	{
		return isTwilight(tile.level()) && super.canStayInPanel(tile, stack, upgradeInv);
	}
	
	@Override
	public <T> Optional<T> findAbility(Ability<T> ability)
	{
		return ability.findIn(this)
				.or(() -> super.findAbility(ability));
	}
}