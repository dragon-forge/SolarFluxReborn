package org.zeith.solarflux.compat.twilightforest.items;

import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.items._base.*;

import java.util.Objects;
import java.util.Optional;

public class TwiLightUpgrade
		extends UpgradeItem
		implements ISunIntensityMod
{
	public TwiLightUpgrade()
	{
		super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
	}
	
	private static final ResourceLocation TWILIGHT = new ResourceLocation("twilightforest", "twilightforest");
	
	public static boolean isTwilight(World level)
	{
		return level != null && Objects.equals(level.dimension().location(), TWILIGHT);
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
		return Optional.ofNullable(
				ability.findIn(this)
						.orElseGet(() -> super.findAbility(ability).orElse(null))
		);
	}
}