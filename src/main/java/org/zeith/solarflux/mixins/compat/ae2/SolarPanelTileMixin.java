package org.zeith.solarflux.mixins.compat.ae2;

import appeng.api.config.*;
import appeng.api.networking.*;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.util.INetworkToolAware;
import appeng.me.helpers.BlockEntityNodeListener;
import appeng.me.helpers.IGridConnectedBlockEntity;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.solarflux.attribute.SimpleAttributeProperty;
import org.zeith.solarflux.block.SolarPanelTile;
import org.zeith.solarflux.compat.ae2.ContentsSFAE2;
import org.zeith.solarflux.compat.ae2.tile.IAE2SolarPanelTile;

import java.util.Arrays;
import java.util.stream.Collectors;

@Mixin(SolarPanelTile.class)
@Implements({
		@Interface(iface = IAEPowerStorage.class, prefix = "ae$"),
		@Interface(iface = IGridConnectedBlockEntity.class, prefix = "aegrid$"),
		@Interface(iface = IInWorldGridNodeHost.class, prefix = "aeinw$"),
		@Interface(iface = INetworkToolAware.class, prefix = "aenta$"),
		@Interface(iface = IAE2SolarPanelTile.class, prefix = "sfr$")
})
public abstract class SolarPanelTileMixin
		extends TileSyncableTickable
		implements IAEPowerStorage, IGridConnectedBlockEntity, IAE2SolarPanelTile
{
	@Shadow
	public long energy;
	
	@Shadow
	@Final
	public SimpleAttributeProperty capacity;
	
	@Shadow
	public abstract int getUpgrades(Item type);
	
	@Shadow
	public abstract int extractEnergy(int maxExtract, boolean simulate);
	
	@Shadow
	public abstract SimpleAttributeProperty capacity();
	
	@Unique
	private IManagedGridNode ae$mainNode;
	
	public SolarPanelTileMixin(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Inject(
			method = "onConstructed",
			at = @At("HEAD"),
			remap = false
	)
	private void constructed(CallbackInfo ci)
	{
		createAe2MainNode();
	}
	
	private void createAe2MainNode()
	{
		if(ae$mainNode != null)
			ae$mainNode.destroy();
		
		ae$mainNode = GridHelper.createManagedNode(Cast.cast(this), BlockEntityNodeListener.INSTANCE)
				.setVisualRepresentation(getBlockState().getBlock())
				.setInWorldNode(true)
				.setExposedOnSides(Arrays.stream(Direction.values()).filter(f -> f != Direction.UP).collect(Collectors.toSet()))
				.addService(IAEPowerStorage.class, this);
	}
	
	@Inject(method = "onChunkUnloadedSFR", at = @At("HEAD"), remap = false)
	public void onChunkUnloadedAE(CallbackInfo ci)
	{
		getMainNode().destroy();
	}
	
	@Inject(method = "setRemovedSFR", at = @At("HEAD"), remap = false)
	public void setRemovedAE(CallbackInfo ci)
	{
		getMainNode().destroy();
	}
	
	@Inject(method = "clearRemovedSFR", at = @At("HEAD"), remap = false)
	public void clearRemovedAE(CallbackInfo ci)
	{
		scheduleInit();
	}
	
	@Inject(
			method = "writeNBT",
			at = @At("TAIL")
	)
	public void writeNBT_AE(CallbackInfoReturnable<CompoundTag> cir)
	{
		CompoundTag ae2 = new CompoundTag();
		ae$mainNode.saveToNBT(ae2);
		cir.getReturnValue().put("AEGrid", ae2);
	}
	
	@Inject(
			method = "readNBT",
			at = @At("TAIL")
	)
	public void readNBT_AE(CompoundTag nbt, HolderLookup.Provider provider, CallbackInfo ci)
	{
		if(getUpgrades(ContentsSFAE2.ENERGY_UPGRADE) > 0)
		{
			createAe2MainNode();
			ae$mainNode.loadFromNBT(nbt.getCompound("AEGrid"));
		}
	}
	
	public double ae$extractAEPower(double toExtract, Actionable action, PowerMultiplier mult)
	{
		if(getUpgrades(ContentsSFAE2.ENERGY_UPGRADE) <= 0) return 0;
		int fe = (int) Math.min(Math.floor(PowerUnit.AE.convertTo(PowerUnit.FE, toExtract)), Integer.MAX_VALUE - 1);
		fe = extractEnergy(fe, action.isSimulate());
		return mult.multiply(PowerUnit.FE.convertTo(PowerUnit.AE, fe));
	}
	
	public IManagedGridNode aegrid$getMainNode()
	{
		return ae$mainNode;
	}
	
	public double ae$getAECurrentPower()
	{
		return PowerUnit.FE.convertTo(PowerUnit.AE, energy);
	}
	
	public double ae$getAEMaxPower()
	{
		return PowerUnit.FE.convertTo(PowerUnit.AE, capacity().getValue());
	}
	
	public AccessRestriction ae$getPowerFlow()
	{
		return getUpgrades(ContentsSFAE2.ENERGY_UPGRADE) > 0 ? AccessRestriction.READ_WRITE : AccessRestriction.NO_ACCESS;
	}
	
	public double ae$injectAEPower(double amt, Actionable mode)
	{
		return 0;
	}
	
	public boolean ae$isAEPublicPowerStorage()
	{
		return true;
	}
	
	public int ae$getPriority()
	{
		return 0;
	}
	
	public void aegrid$saveChanges()
	{
	}
	
	public boolean aenta$showNetworkInfo(UseOnContext context)
	{
		return getUpgrades(ContentsSFAE2.ENERGY_UPGRADE) > 0;
	}
	
	protected void scheduleInit()
	{
		GridHelper.onFirstTick(Cast.cast(this, SolarPanelTile.class), e ->
		{
			if(e instanceof IGridConnectedBlockEntity h && getUpgrades(ContentsSFAE2.ENERGY_UPGRADE) > 0)
				h.getMainNode().create(level, worldPosition);
		});
	}
	
	@Nullable
	public IGridNode aeinw$getGridNode(Direction dir)
	{
		if(getUpgrades(ContentsSFAE2.ENERGY_UPGRADE) <= 0)
			return null;
		
		return getMainNode().getNode();
	}
	
	public void sfr$setConnectedToAENetwork(boolean connected)
	{
		var isAe2NodeConnected = ae$mainNode.getNode() != null;
		if(isAe2NodeConnected != connected)
		{
			if(connected)
			{
				createAe2MainNode();
				ae$mainNode.create(level, worldPosition);
			} else
			{
				ae$mainNode.destroy();
			}
		}
	}
}