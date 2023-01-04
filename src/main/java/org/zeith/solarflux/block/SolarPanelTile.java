package org.zeith.solarflux.block;

import net.minecraft.block.Block;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.tiles.IContainerTile;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.util.java.tuples.Tuple2;
import org.zeith.hammerlib.util.java.tuples.Tuples;
import org.zeith.solarflux.api.ISolarPanelTile;
import org.zeith.solarflux.attribute.SimpleAttributeProperty;
import org.zeith.solarflux.compat._abilities.SFAbilities;
import org.zeith.solarflux.container.SolarPanelContainer;
import org.zeith.solarflux.items._base.*;
import org.zeith.solarflux.panels.*;
import org.zeith.solarflux.util.BlockPosFace;

import java.util.*;
import java.util.stream.Stream;

public class SolarPanelTile
		extends TileSyncableTickable
		implements IContainerTile, IEnergyStorage, ISolarPanelTile
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
	
	public SolarPanelTile()
	{
		super(SolarPanels.SOLAR_PANEL_TYPE);
		onConstructed();
	}
	
	protected void onConstructed()
	{
	}
	
	@Override
	public Container openContainer(PlayerEntity player, int windowId)
	{
		return new SolarPanelContainer(windowId, player.inventory, this);
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
				.map(i -> !i.isEmpty() && i.getItem() instanceof UpgradeItem ? Tuples.immutable((UpgradeItem) i.getItem(), i) : null)
				.filter(Objects::nonNull);
	}
	
	public boolean isSameLevel(SolarPanelTile other)
	{
		if(other == null)
			return false;
		if(other.getDelegate() == null || getDelegate() == null)
			return false;
		return Objects.equals(other.getDelegate(), getDelegate());
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
				delegate = SolarPanels.CORE_PANELS[0];
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
				if(stack.getItem() instanceof UpgradeItem && ((UpgradeItem) stack.getItem()).canStayInPanel(this, stack, upgradeInventory))
				{
					id = stack.getItem().getRegistryName();
					if(!tickedUpgrades.contains(id))
					{
						UpgradeItem iu = (UpgradeItem) stack.getItem();
						iu.update(this, stack, getUpgrades(iu));
						tickedUpgrades.add(id);
					}
				} else
				{
					// Why non-upgrade items would end up in this inventory? idk, let's drop them!
					ItemStack s = upgradeInventory.getStackInSlot(i);
					s.copy();
					upgradeInventory.setStackInSlot(i, ItemStack.EMPTY);
					if(!level.isClientSide)
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
					stack.getCapability(CapabilityEnergy.ENERGY, null).filter(e -> e.getEnergyStored() < e.getMaxEnergyStored()).ifPresent(e ->
					{
						transfer.setBaseValue(getInstance().transfer);
						int transfer = this.transfer.getValueI();
						energy -= e.receiveEnergy(Math.min(getEnergyStored(), transfer), false);
					});
				}
			}
		
		tickedUpgrades.clear();
	}
	
	@Override
	public void setRemoved()
	{
		super.setRemoved();
		setRemovedSFR();
	}
	
	@Override
	public void clearRemoved()
	{
		super.clearRemoved();
		clearRemovedSFR();
	}
	
	@Override
	public void onChunkUnloaded()
	{
		super.onChunkUnloaded();
		onChunkUnloadedSFR();
	}
	
	protected void clearRemovedSFR()
	{
	}
	
	protected void onChunkUnloadedSFR()
	{
	}
	
	protected void setRemovedSFR()
	{
	}
	
	@Override
	public void update()
	{
		if(voxelTimer > 0)
			--voxelTimer;
		Block blk = getBlockState().getBlock();
		if(blk instanceof SolarPanelBlock)
			this.delegate = ((SolarPanelBlock) blk).panel;
		else
			return;
		
		if(cache$seeSkyTimer > 0)
			--cache$seeSkyTimer;
		
		if(level.isClientSide)
			return;
		
		if(level.getGameTime() % 20L == 0L)
			traversal.clear();
		
		tickUpgrades();
		
		int gen = getGeneration();
		capacity.setBaseValue(getInstance().cap);
		energy += Math.min(capacity.getValueL() - energy, gen);
		currentGeneration = gen;
		
		energy = Math.min(Math.max(energy, 0), capacity.getValueL());
		{
			for(Direction hor : Direction.values())
				if(hor.getAxis() != Axis.Y)
				{
					TileEntity tile = level.getBlockEntity(worldPosition.relative(hor));
					if(tile instanceof SolarPanelTile)
						autoBalanceEnergy((SolarPanelTile) tile);
				}
			
			transfer.setBaseValue(getInstance().transfer);
			int transfer = this.transfer.getValueI();
			
			for(Direction hor : Direction.values())
			{
				if(hor == Direction.UP)
					continue;
				
				TileEntity tile = level.getBlockEntity(worldPosition.relative(hor));
				
				if(tile == null)
					continue;
				
				tile.getCapability(CapabilityEnergy.ENERGY, hor.getOpposite()).ifPresent(storage ->
				{
					if(storage.canReceive())
						energy -= storage.receiveEnergy(Math.min(getEnergyStored(), transfer), false);
				});
			}
			
			if(!traversal.isEmpty())
			{
				for(BlockPosFace traverse : traversal)
				{
					TileEntity tile = level.getBlockEntity(traverse.pos);
					
					if(energy < 1L)
						break;
					if(tile == null)
						continue;
					
					tile.getCapability(CapabilityEnergy.ENERGY, traverse.face).ifPresent(storage ->
					{
						if(storage.canReceive())
							energy -= storage.receiveEnergy(Math.min(getEnergyStored(), Math.round(transfer * traverse.rate)), false);
					});
				}
			}
		}
		
		level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
		
		if(effCacheTime > 0) --effCacheTime;
	}
	
	int effCacheTime;
	float effCache;
	
	public int getGeneration()
	{
		float eff = effCache;
		if(effCacheTime <= 0)
		{
			eff = getInstance().computeSunIntensity(this);
			for(ISunIntensityMod mod : UpgradeSystem.findAbilitiesIn(this, SFAbilities.ITEM_UPGRADE_SUN_INTENSITY_MUL))
				eff = mod.applySunIntensityModifier(this, eff);
			{
				float raining = level.getRainLevel(1F);
				raining = raining > 0.2F ? (raining - 0.2F) / 0.8F : 0F;
				raining = (float) Math.sin(raining * Math.PI / 2F);
				raining = 1F - raining * (1F - SolarPanels.RAIN_MULTIPLIER);
				
				float thundering = level.getThunderLevel(1F);
				thundering = thundering > 0.75F ? (thundering - 0.75F) / 0.25F : 0F;
				thundering = (float) Math.sin(thundering * Math.PI / 2F);
				thundering = 1F - thundering * (1F - SolarPanels.THUNDER_MULTIPLIER);
				
				eff *= raining * thundering;
			}
			effCache = eff;
			effCacheTime = 2;
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
	
	public boolean doesSeeSky()
	{
		if(cache$seeSkyTimer < 1)
		{
			cache$seeSkyTimer = 20;
			cache$seeSky = level != null && level.getBrightness(LightType.SKY, worldPosition) > 0 && worldPosition != null && level.canSeeSky(worldPosition);
		}
		return cache$seeSky;
	}
	
	@Override
	public World level()
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
		this.energy = newEnergy;
	}
	
	@Override
	public SimpleAttributeProperty generation()
	{
		return generation;
	}
	
	@Override
	public SimpleAttributeProperty capacity()
	{
		return capacity;
	}
	
	@Override
	public SimpleAttributeProperty transfer()
	{
		return transfer;
	}
	
	public static final ModelProperty<World> WORLD_PROP = new ModelProperty<>();
	public static final ModelProperty<BlockPos> POS_PROP = new ModelProperty<>();
	
	@Override
	public IModelData getModelData()
	{
		return new ModelDataMap.Builder().withInitial(WORLD_PROP, level).withInitial(POS_PROP, worldPosition).build();
	}
	
	@Override
	public CompoundNBT writeNBT(CompoundNBT nbt)
	{
		upgradeInventory.writeToNBT(nbt, "Upgrades");
		chargeInventory.writeToNBT(nbt, "Chargeable");
		nbt.putLong("Energy", energy);
		return nbt;
	}
	
	@Override
	public void readNBT(CompoundNBT nbt)
	{
		upgradeInventory.readFromNBT(nbt, "Upgrades");
		chargeInventory.readFromNBT(nbt, "Chargeable");
		energy = nbt.getLong("Energy");
	}
	
	LazyOptional chargeableItems, energyStorageTile;
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
	{
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
		{
			if(chargeableItems == null)
				chargeableItems = LazyOptional.of(() -> chargeInventory);
			return chargeableItems.cast();
		} else if(cap == CapabilityEnergy.ENERGY)
		{
			if(energyStorageTile == null)
				energyStorageTile = LazyOptional.of(() -> SolarPanelTile.this);
			return energyStorageTile.cast();
		}
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
	public ITextComponent getDisplayName()
	{
		return new TranslationTextComponent(getBlockState().getBlock().getDescriptionId());
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
		return (int) Math.min(energy, (long) Integer.MAX_VALUE);
	}
	
	@Override
	public int getMaxEnergyStored()
	{
		return (int) Math.min(getInstance().cap, (long) Integer.MAX_VALUE);
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
	
	public ItemStack generateItem(IItemProvider item)
	{
		ItemStack stack = new ItemStack(item);
		stack.setTag(new CompoundNBT());
		stack.getTag().putLong("Energy", energy - Math.round(energy * SolarPanels.LOOSE_ENERGY / 100D));
		upgradeInventory.writeToNBT(stack.getTag(), "Upgrades");
		chargeInventory.writeToNBT(stack.getTag(), "Chargeable");
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