package org.zeith.solarflux.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.tiles.IContainerTile;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.hammerlib.util.mcf.NormalizedTicker;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.attribute.SimpleAttributeProperty;
import org.zeith.solarflux.compat._abilities.SFAbilities;
import org.zeith.solarflux.container.SolarPanelContainer;
import org.zeith.solarflux.init.SolarPanelsSF;
import org.zeith.solarflux.init.TilesSF;
import org.zeith.solarflux.items.upgrades._base.UpgradeItem;
import org.zeith.solarflux.items.upgrades._base.UpgradeSystem;
import org.zeith.solarflux.panels.SolarPanel;
import org.zeith.solarflux.panels.SolarPanelInstance;
import org.zeith.solarflux.util.BlockPosFace;

import java.util.*;
import java.util.stream.Stream;

public class SolarPanelTile
		extends TileSyncableTickable
		implements IEnergyStorage, IContainerTile, ISolarPanelTile
{
	public long energy;
	
	public long currentGeneration;
	public float sunIntensity;
	
	private SolarPanel delegate;
	private SolarPanelInstance instance;
	
	public final SimpleInventory upgradeInventory = new SimpleInventory(5);
	public final SimpleInventory chargeInventory = new SimpleInventory(1);
	
	public final List<BlockPosFace> traversal = new ArrayList<>();
	
	public final SimpleAttributeProperty generation = new SimpleAttributeProperty();
	public final SimpleAttributeProperty transfer = new SimpleAttributeProperty();
	public final SimpleAttributeProperty capacity = new SimpleAttributeProperty();
	
	public SolarPanelTile(BlockPos pos, BlockState state)
	{
		super(TilesSF.SOLAR_PANEL, pos, state);
		onConstructed();
	}
	
	/**
	 * Mostly for mixin injection.
	 */
	protected void onConstructed()
	{
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		setRemovedSFR();
	}
	
	/**
	 * Mostly for mixin injection.
	 */
	public void setRemovedSFR()
	{
	}
	
	@Override
	public void onChunkUnloaded()
	{
		super.onChunkUnloaded();
		onChunkUnloadedSFR();
	}
	
	/**
	 * Mostly for mixin injection.
	 */
	public void onChunkUnloadedSFR()
	{
	}
	
	@Override
	public void clearRemoved()
	{
		super.clearRemoved();
		clearRemovedSFR();
	}
	
	/**
	 * Mostly for mixin injection.
	 */
	public void clearRemovedSFR()
	{
	}
	
	@Override
	public int getUpgrades(Item type)
	{
		int c = 0;
		for(int i = 0; i < upgradeInventory.getSlots(); ++i)
		{
			ItemStack stack = upgradeInventory.getStackInSlot(i);
			if(!stack.isEmpty() && stack.getItem() == type)
				c += stack.getCount();
		}
		return c;
	}
	
	@Override
	public Stream<Tuple2<UpgradeItem, ItemStack>> getUpgrades()
	{
		return upgradeInventory.stream()
				.map(i -> !i.isEmpty() && i.getItem() instanceof UpgradeItem u ? Tuples.immutable(u, i) : null)
				.filter(Objects::nonNull);
	}
	
	@Override
	public SolarPanel getDelegate()
	{
		if(delegate == null)
		{
			Block blk = getBlockState().getBlock();
			if(blk instanceof SolarPanelBlock)
				this.delegate = ((SolarPanelBlock) blk).panel;
			else
				delegate = SolarPanelsSF.CORE_PANELS[0];
		}
		return delegate;
	}
	
	@Override
	public SolarPanelInstance getInstance()
	{
		if(instance == null || instance.getDelegate() != getDelegate())
			instance = getDelegate().createInstance(this);
		return instance;
	}
	
	List<ResourceLocation> tickedUpgrades = new ArrayList<>();
	
	public void tickUpgrades()
	{
		ItemStack stack;
		ResourceLocation id;
		
		generation.clearAttributes();
		transfer.clearAttributes();
		capacity.clearAttributes();
		
		for(int i = 0; i < upgradeInventory.getSlots(); ++i)
		{
			stack = upgradeInventory.getStackInSlot(i);
			if(!stack.isEmpty())
			{
				if(stack.getItem() instanceof UpgradeItem upgrade && upgrade.canStayInPanel(this, stack, upgradeInventory))
				{
					id = ForgeRegistries.ITEMS.getKey(stack.getItem());
					if(!tickedUpgrades.contains(id))
					{
						upgrade.update(this, stack, getUpgrades(upgrade));
						tickedUpgrades.add(id);
					}
				} else
				{
					// Why non-upgrade item would end up in this inventory?
					// idk, let's drop them!
					ItemStack s = upgradeInventory.getStackInSlot(i);
					s.copy();
					upgradeInventory.setStackInSlot(i, ItemStack.EMPTY);
					if(isOnServer())
						level.addFreshEntity(new ItemEntity(level, worldPosition.getX() + .5, worldPosition.getY() + .5, worldPosition.getZ() + .5, stack));
				}
			}
		}
		
		if(energy > 0L && getInstance() != null)
			for(int i = 0; i < chargeInventory.getSlots(); ++i)
			{
				stack = chargeInventory.getStackInSlot(i);
				if(!stack.isEmpty())
				{
					stack.getCapability(ForgeCapabilities.ENERGY).filter(e -> e.getEnergyStored() < e.getMaxEnergyStored()).ifPresent(e ->
					{
						transfer.setBaseValue(getInstance().transfer);
						int transfer = this.transfer.getValueI();
						energy -= e.receiveEnergy(Math.min(getEnergyStored(), transfer), false);
					});
				}
			}
		
		tickedUpgrades.clear();
	}
	
	// We really don't need to make a copy of all values every tick, so this constant is here to save the day.
	private static final Direction[] DIRECTIONS_NO_UP = Direction.stream().filter(f -> f != Direction.UP).toArray(Direction[]::new);
	private static final Direction[] DIRECTIONS_HORIZONTAL = Direction.stream().filter(f -> f.getAxis() != Direction.Axis.Y).toArray(Direction[]::new);
	
	protected final NormalizedTicker ticker = NormalizedTicker.create(this::normTick);
	
	@Override
	public void update()
	{
		ticker.tick(level);
	}
	
	@Override
	public boolean atTickRate(int rate)
	{
		return ticker.atTickRate(rate);
	}
	
	public void normTick(int suppressed)
	{
		if(voxelTimer > 0)
			--voxelTimer;
		
		Block blk = getBlockState().getBlock();
		if(blk instanceof SolarPanelBlock spb)
			this.delegate = spb.panel;
		else
			return;
		
		if(cache$seeSkyTimer > 0)
			--cache$seeSkyTimer;
		
		if(level.isClientSide)
			return;
		
		if(level.getGameTime() % 20L == 0L)
			traversal.clear();
		
		tickUpgrades();
		
		transfer.setBaseValue(getInstance().transfer);
		int transfer = this.transfer.getValueI() * suppressed;
		
		long gen = getGeneration();
		capacity.setBaseValue(getInstance().cap);
		energy += Math.min(capacity.getValueL() - energy, gen * suppressed);
		currentGeneration = gen;
		
		energy = Mth.clamp(energy, 0L, capacity.getValueL());
		
		for(Direction hor : DIRECTIONS_HORIZONTAL)
		{
			BlockEntity tile = level.getBlockEntity(worldPosition.relative(hor));
			if(tile instanceof SolarPanelTile spt)
				autoBalanceEnergy(spt);
		}
		
		for(Direction hor : DIRECTIONS_NO_UP)
		{
			BlockEntity tile = level.getBlockEntity(worldPosition.relative(hor));
			if(tile == null) continue;
			
			tile.getCapability(ForgeCapabilities.ENERGY, hor.getOpposite()).ifPresent(storage ->
			{
				if(storage.canReceive())
					energy -= storage.receiveEnergy(Math.min(getEnergyStored(), transfer), false);
			});
		}
		
		if(!traversal.isEmpty() && energy > 0L)
		{
			for(BlockPosFace traverse : traversal)
			{
				BlockEntity tile = level.getBlockEntity(traverse.pos);
				if(tile == null) continue;
				
				tile.getCapability(ForgeCapabilities.ENERGY, traverse.face).ifPresent(storage ->
				{
					if(storage.canReceive())
						energy -= storage.receiveEnergy(Math.min(getEnergyStored(), Math.round(transfer * traverse.rate)), false);
				});
				
				if(energy < 1L) break;
			}
		}
		
		level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
		
		if(effCacheTime > 0) --effCacheTime;
	}
	
	int effCacheTime;
	float effCache;
	
	@Override
	public int getGeneration()
	{
		float eff = effCache;
		
		if(effCacheTime <= 0)
		{
			eff = getInstance().computeSunIntensity(this);
			for(var mod : UpgradeSystem.findAbilitiesIn(this, SFAbilities.ITEM_UPGRADE_SUN_INTENSITY_MUL))
				eff = mod.applySunIntensityModifier(this, eff);
			
			{
				float raining = level.getRainLevel(1F);
				raining = raining > 0.2F ? (raining - 0.2F) / 0.8F : 0F;
				raining = (float) Math.sin(raining * Math.PI / 2F);
				eff *= 1F - raining * (1F - SolarPanelsSF.RAIN_MULTIPLIER);
				
				float thundering = level.getThunderLevel(1F);
				thundering = thundering > 0.75F ? (thundering - 0.75F) / 0.25F : 0F;
				thundering = (float) Math.sin(thundering * Math.PI / 2F);
				eff *= 1F - thundering * (1F - SolarPanelsSF.THUNDER_MULTIPLIER);
			}
			effCache = eff;
			effCacheTime = 5;
		}
		
		if(!level.isClientSide) sunIntensity = eff;
		float gen = getInstance().gen * eff;
		generation.setBaseValue(gen);
		return generation.getValueI();
	}
	
	public int autoBalanceEnergy(SolarPanelTile solar)
	{
		int delta = getEnergyStored() - solar.getEnergyStored();
		if(delta < 0)
			return solar.autoBalanceEnergy(this);
		else if(delta > 0)
			return extractEnergy(solar.receiveEnergyInternal(extractEnergy(solar.receiveEnergyInternal(delta / 2, true), true), false), false);
		return 0;
	}
	
	public boolean cache$seeSky;
	public byte cache$seeSkyTimer;
	
	@Override
	public boolean doesSeeSky()
	{
		if(cache$seeSkyTimer < 1)
		{
			cache$seeSkyTimer = 20;
			cache$seeSky = level != null &&
					level.getBrightness(LightLayer.SKY, worldPosition) > 0 &&
					level.canSeeSky(worldPosition.above());
		}
		return cache$seeSky;
	}
	
	@Override
	public Level level()
	{
		return level;
	}
	
	@Override
	public BlockPos pos()
	{
		return worldPosition;
	}
	
	@Override
	public List<BlockPosFace> traversal()
	{
		return traversal;
	}
	
	@Override
	public long energy()
	{
		return energy;
	}
	
	@Override
	public void energy(long newEnergy)
	{
		energy = Mth.clamp(newEnergy, 0L, capacity.getValueL());
	}
	
	@Override
	public SimpleAttributeProperty generation()
	{
		return generation;
	}
	
	@Override
	public SimpleAttributeProperty transfer()
	{
		return transfer;
	}
	
	@Override
	public SimpleAttributeProperty capacity()
	{
		return capacity;
	}
	
	public static final ModelProperty<Level> WORLD_PROP = new ModelProperty<>();
	public static final ModelProperty<BlockPos> POS_PROP = new ModelProperty<>();
	
	@Override
	public ModelData getModelData()
	{
		return ModelData.builder()
				.with(WORLD_PROP, level)
				.with(POS_PROP, worldPosition)
				.build();
	}
	
	@Override
	public CompoundTag writeNBT(CompoundTag nbt)
	{
		upgradeInventory.writeToNBT(nbt, "Upgrades");
		chargeInventory.writeToNBT(nbt, "Chargeable");
		nbt.putLong("Energy", energy);
		return nbt;
	}
	
	@Override
	public void readNBT(CompoundTag nbt)
	{
		upgradeInventory.readFromNBT(nbt, "Upgrades");
		chargeInventory.readFromNBT(nbt, "Chargeable");
		energy = nbt.getLong("Energy");
	}
	
	LazyOptional<IItemHandler> chargeableItems = LazyOptional.of(() -> chargeInventory);
	LazyOptional<IEnergyStorage> energyStorageTile = LazyOptional.of(() -> SolarPanelTile.this);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if(cap == ForgeCapabilities.ITEM_HANDLER)
			return chargeableItems.cast();
		else if(cap == ForgeCapabilities.ENERGY)
			return energyStorageTile.cast();
		return super.getCapability(cap, side);
	}
	
	int voxelTimer = 0;
	VoxelShape shape;
	
	public void resetVoxelShape()
	{
		shape = null;
	}
	
	public VoxelShape getShape(SolarPanelBlock block)
	{
		if(shape == null || voxelTimer <= 0)
		{
			shape = block.recalcShape(level, worldPosition);
			voxelTimer = 20;
		}
		return shape;
	}
	
	@Override
	public AbstractContainerMenu openContainer(Player player, int windowId)
	{
		return new SolarPanelContainer(windowId, player.getInventory(), this);
	}
	
	@Nullable
	@Override
	public Component getDisplayName()
	{
		return getBlockState().getBlock().getName();
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		transfer.setBaseValue(getInstance().transfer);
		int transfer = this.transfer.getValueI();
		int energyExtracted = Math.min(getEnergyStored(), Math.min(transfer, maxExtract));
		if(!simulate)
			energy -= energyExtracted;
		return energyExtracted;
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
		return 0;
	}
	
	public int receiveEnergyInternal(int maxReceive, boolean simulate)
	{
		transfer.setBaseValue(getInstance().transfer);
		int transfer = this.transfer.getValueI();
		capacity.setBaseValue(getInstance().cap);
		long cap = capacity.getValueL();
		int energyReceived = Math.min((int) Math.min(cap - energy, Integer.MAX_VALUE), Math.min(transfer, maxReceive));
		if(!simulate)
			energy += energyReceived;
		return energyReceived;
	}
	
	@Override
	public int getEnergyStored()
	{
		return (int) Math.min(energy, Integer.MAX_VALUE);
	}
	
	@Override
	public int getMaxEnergyStored()
	{
		return (int) Math.min(getInstance().cap, Integer.MAX_VALUE);
	}
	
	@Override
	public boolean canExtract()
	{
		return true;
	}
	
	@Override
	public boolean canReceive()
	{
		return false;
	}
	
	public ItemStack generateItem(ItemLike item)
	{
		ItemStack stack = new ItemStack(item);
		CompoundTag tag = new CompoundTag();
		tag.putLong("Energy", energy - Math.round(energy * SolarPanelsSF.LOOSE_ENERGY / 100D));
		upgradeInventory.writeToNBT(tag, "Upgrades");
		chargeInventory.writeToNBT(tag, "Chargeable");
		stack.setTag(tag);
		return stack;
	}
	
	public void loadFromItem(ItemStack stack)
	{
		if(stack.hasTag())
		{
			energy = stack.getTag().getLong("Energy");
			upgradeInventory.readFromNBT(stack.getTag(), "Upgrades");
			chargeInventory.readFromNBT(stack.getTag(), "Chargeable");
		}
	}
	
	public void setDelegate(SolarPanel delegate)
	{
		this.delegate = delegate;
	}
}