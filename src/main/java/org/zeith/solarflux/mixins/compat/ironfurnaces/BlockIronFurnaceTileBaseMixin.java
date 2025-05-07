package org.zeith.solarflux.mixins.compat.ironfurnaces;

import ironfurnaces.items.augments.*;
import ironfurnaces.tileentity.TileEntityInventory;
import ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zeith.solarflux.api.*;

@Implements({
		@Interface(iface = IFurnaceBlockEntity.class, prefix = "ifbe$")
})
@Mixin(BlockIronFurnaceTileBase.class)
public abstract class BlockIronFurnaceTileBaseMixin
		extends TileEntityInventory
{
	@Unique
	private boolean sfr$chargedByPanel;
	
	@Shadow
	public abstract boolean isFurnace();
	
	@Shadow
	protected abstract RecipeHolder<? extends AbstractCookingRecipe> getRecipeNonCached(ItemStack stack);
	
	@Shadow protected abstract boolean canSmelt(@Nullable RecipeHolder<?> recipe);
	
	public BlockIronFurnaceTileBaseMixin(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state, int sizeInventory)
	{
		super(tileEntityTypeIn, pos, state, sizeInventory);
	}
	
	public boolean ifbe$activateWithSolarPanel(ISolarPanelTile solar)
	{
		if(sfr$chargedByPanel || !isFurnace()) return false;
		sfr$chargedByPanel = true;
		solar.energy(solar.energy() - 1000L);
		setChanged();
		return true;
	}
	
	public Direction ifbe$getSideForSolarPanel()
	{
		return isFurnace() ? Direction.UP : null;
	}
	
	@Inject(
			method = "loadAdditional",
			at = @At("HEAD")
	)
	private void SolarFlux_loadAdditional(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci)
	{
		var ct = tag.getCompound("SolarFlux");
		sfr$chargedByPanel = ct.getBoolean("Charged");
	}
	
	@Inject(
			method = "saveAdditional",
			at = @At("HEAD")
	)
	private void SolarFlux_saveAdditional(CompoundTag tag, HolderLookup.Provider provider, CallbackInfo ci)
	{
		CompoundTag ct = new CompoundTag();
		ct.putBoolean("Charged", sfr$chargedByPanel);
		tag.put("SolarFlux", ct);
	}
	
	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lironfurnaces/tileentity/furnaces/BlockIronFurnaceTileBase;checkRecipeType()V",
					ordinal = 1
			)
	)
	private static void SolarFlux_tickPrepareTarget(Level level, BlockPos worldPosition, BlockState blockState, BlockIronFurnaceTileBase e, CallbackInfo ci)
	{
		TileEntityInventory te = e;
		
		// This BS is used to access mixin-injected bool without adding extra interfaces
		BlockIronFurnaceTileBaseMixin mix = (BlockIronFurnaceTileBaseMixin) te;
		assert mix != null;
		
		if(!mix.sfr$chargedByPanel || e.isBurning()) return;
		
		ItemStack inputItem = e.getItem(0);
		if(inputItem.isEmpty()) return;
		
		if(!mix.canSmelt(mix.getRecipeNonCached(inputItem))) return;
		
		mix.sfr$chargedByPanel = false;
		
		int cookTime = e.getCookTime();
		
		ItemStack upgradeSlot = e.getItem(4);
		
		// Upgrades are also included in calculations
		if(!upgradeSlot.isEmpty())
		{
			if(upgradeSlot.getItem() instanceof ItemAugmentFuel)
				e.furnaceBurnTime = cookTime * 2;
			else if(upgradeSlot.getItem() instanceof ItemAugmentSpeed)
				e.furnaceBurnTime = cookTime / 2;
		} else
			e.furnaceBurnTime = cookTime;
		
		e.setChanged();
	}
}