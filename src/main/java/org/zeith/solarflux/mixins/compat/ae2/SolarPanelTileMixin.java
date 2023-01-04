package org.zeith.solarflux.mixins.compat.ae2;

import appeng.api.config.*;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IAEPowerStorage;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.*;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.solarflux.attribute.SimpleAttributeProperty;
import org.zeith.solarflux.block.SolarPanelTile;
import org.zeith.solarflux.compat.ae2.AE2Compat;
import org.zeith.solarflux.compat.ae2.ContentsSFAE2;
import org.zeith.solarflux.compat.ae2.tile.IAE2SolarPanelTile;
import org.zeith.solarflux.panels.SolarPanel;
import org.zeith.solarflux.panels.SolarPanels;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Mixin(SolarPanelTile.class)
@Implements({
		@Interface(iface = IAEPowerStorage.class, prefix = "ae$"),
		@Interface(iface = IGridProxyable.class, prefix = "aegrid$"),
		@Interface(iface = IActionHost.class, prefix = "aeinw$"),
		@Interface(iface = INetworkToolAgent.class, prefix = "aenta$"),
		@Interface(iface = IAE2SolarPanelTile.class, prefix = "sfr$")
})
public abstract class SolarPanelTileMixin
		extends TileSyncableTickable
		implements IAEPowerStorage, IGridProxyable, IAE2SolarPanelTile
{
	public SolarPanelTileMixin(TileEntityType<?> type)
	{
		super(type);
	}
	
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
	private SolarPanel delegate;
	private AENetworkProxy gridProxy;
	
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
		if(gridProxy != null)
			gridProxy.remove();
		
		ItemStack visual = new ItemStack(SolarPanels.CORE_PANELS[0]);
		if(delegate != null) visual = new ItemStack(delegate);
		
		gridProxy = new AENetworkProxy(this, "AEGrid", visual, true);
		gridProxy.setValidSides(EnumSet.copyOf(Arrays.stream(Direction.values()).filter(f -> f != Direction.UP).collect(Collectors.toSet())));
	}
	
	@Inject(
			method = "setDelegate",
			at = @At("HEAD"),
			remap = false
	)
	private void updateAEDelegate(SolarPanel delegate, CallbackInfo ci)
	{
		if(gridProxy != null)
			gridProxy.setVisualRepresentation(new ItemStack(delegate));
	}
	
	@Inject(method = "onChunkUnloadedSFR", at = @At("HEAD"), remap = false)
	public void onChunkUnloadedAE(CallbackInfo ci)
	{
		if(gridProxy != null)
			gridProxy.onChunkUnloaded();
	}
	
	@Inject(method = "setRemovedSFR", at = @At("HEAD"), remap = false)
	public void setRemovedAE(CallbackInfo ci)
	{
		if(gridProxy != null)
			gridProxy.remove();
	}
	
	@Inject(method = "clearRemovedSFR", at = @At("HEAD"), remap = false)
	public void clearRemovedAE(CallbackInfo ci)
	{
		if(gridProxy != null)
			validate();
	}
	
	public void validate()
	{
		if(gridProxy != null)
			AE2Compat.addInit(this);
	}
	
	public void sfr$onReady()
	{
		if(gridProxy != null)
			gridProxy.onReady();
	}
	
	@Inject(
			method = "writeNBT",
			at = @At("HEAD"),
			remap = false
	)
	public void writeNBT_AE(CompoundNBT nbt, CallbackInfoReturnable<CompoundNBT> cir)
	{
		gridProxy.writeToNBT(nbt);
	}
	
	@Inject(
			method = "readNBT",
			at = @At("TAIL"),
			remap = false
	)
	public void readNBT_AE(CompoundNBT nbt, CallbackInfo ci)
	{
		if(getUpgrades(ContentsSFAE2.ENERGY_UPGRADE) > 0)
		{
			createAe2MainNode();
			gridProxy.readFromNBT(nbt);
		}
	}
	
	public double ae$extractAEPower(double toExtract, Actionable action, PowerMultiplier mult)
	{
		if(getUpgrades(ContentsSFAE2.ENERGY_UPGRADE) <= 0) return 0;
		int fe = (int) Math.min(Math.floor(PowerUnits.AE.convertTo(PowerUnits.RF, toExtract)), Integer.MAX_VALUE - 1);
		fe = extractEnergy(fe, action == Actionable.SIMULATE);
		return mult.multiply(PowerUnits.RF.convertTo(PowerUnits.AE, fe));
	}
	
	public void aegrid$securityBreak()
	{
	}
	
	public double ae$getAECurrentPower()
	{
		return PowerUnits.RF.convertTo(PowerUnits.AE, energy);
	}
	
	public double ae$getAEMaxPower()
	{
		return PowerUnits.RF.convertTo(PowerUnits.AE, capacity.getValue());
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
	
	public boolean aenta$showNetworkInfo(RayTraceResult context)
	{
		return getUpgrades(ContentsSFAE2.ENERGY_UPGRADE) > 0;
	}
	
	@Nullable
	public IGridNode aeinw$getActionableNode()
	{
		if(getUpgrades(ContentsSFAE2.ENERGY_UPGRADE) <= 0)
			return null;
		return this.getProxy().getNode();
	}
	
	public void sfr$setConnectedToAENetwork(boolean connected)
	{
		boolean isAe2NodeConnected = gridProxy != null && gridProxy.getNode() != null;
		
		if(isAe2NodeConnected != connected)
		{
			if(connected)
			{
				createAe2MainNode();
				validate();
			} else if(gridProxy != null)
			{
				gridProxy.remove();
				gridProxy = null;
			}
		}
	}
	
	@Nullable
	public IGridNode aegrid$getGridNode(@Nonnull AEPartLocation aePartLocation)
	{
		if(aePartLocation == AEPartLocation.UP || getUpgrades(ContentsSFAE2.ENERGY_UPGRADE) <= 0)
			return null;
		return this.getProxy().getNode();
	}
	
	public void aegrid$gridChanged()
	{
	}
	
	public DimensionalCoord aegrid$getLocation()
	{
		return new DimensionalCoord(this);
	}
	
	@Nonnull
	public AECableType aegrid$getCableConnectionType(@Nonnull AEPartLocation aePartLocation)
	{
		return AECableType.GLASS;
	}
	
	public AENetworkProxy aegrid$getProxy()
	{
		return gridProxy;
	}
}