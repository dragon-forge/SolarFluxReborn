package tk.zeitheron.solarflux.block.tile;

import tk.zeitheron.solarflux.api.SolarInfo;
import tk.zeitheron.solarflux.api.SolarInstance;
import tk.zeitheron.solarflux.api.attribute.SimpleAttributeProperty;
import tk.zeitheron.solarflux.block.BlockBaseSolar;
import tk.zeitheron.solarflux.gui.ContainerBaseSolar;
import tk.zeitheron.solarflux.init.SolarsSF;
import tk.zeitheron.solarflux.items.ItemUpgrade;
import tk.zeitheron.solarflux.utils.BlockPosFace;
import tk.zeitheron.solarflux.utils.FByteHelper;
import tk.zeitheron.solarflux.utils.IVariableHandler;
import tk.zeitheron.solarflux.utils.InventoryDummy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TileBaseSolar
		extends TileEntity
		implements ITickable, IEnergyStorage, IVariableHandler
{
	public long energy;

	public long currentGeneration;
	public float sunIntensity;

	public SolarInstance instance;

	public boolean renderConnectedTextures = true;

	public List<EntityPlayer> crafters = new ArrayList<>();

	public final SimpleAttributeProperty generation = new SimpleAttributeProperty();
	public final SimpleAttributeProperty transfer = new SimpleAttributeProperty();
	public final SimpleAttributeProperty capacity = new SimpleAttributeProperty();

	public final InventoryDummy upgradeInventory = new InventoryDummy(5);
	public final InventoryDummy chargeInventory = new InventoryDummy(1);

	public final List<BlockPosFace> traversal = new ArrayList<>();

	public final InvWrapper itemWrapper = new InvWrapper(chargeInventory);

	{
		upgradeInventory.validSlots = (slot, stack) ->
		{
			if(!(stack.getItem() instanceof ItemUpgrade))
				return false;
			return getUpgrades(stack.getItem()) < ((ItemUpgrade) stack.getItem()).getMaxUpgrades() && ((ItemUpgrade) stack.getItem()).canInstall(this, stack, upgradeInventory);
		};
		chargeInventory.validSlots = (slot, stack) ->
		{
			IEnergyStorage e;
			return !stack.isEmpty() && stack.hasCapability(CapabilityEnergy.ENERGY, null) && (e = stack.getCapability(CapabilityEnergy.ENERGY, null)).canReceive() && e.getEnergyStored() < e.getMaxEnergyStored();
		};
		upgradeInventory.fields = this;
		chargeInventory.fields = this;
		upgradeInventory.openInv = crafters::add;
		chargeInventory.openInv = crafters::add;
		upgradeInventory.closeInv = crafters::remove;
		chargeInventory.closeInv = crafters::remove;
	}

	public TileBaseSolar(SolarInstance instance)
	{
		this.instance = instance;
	}

	public TileBaseSolar()
	{
	}

	public int getUpgrades(Item type)
	{
		int c = 0;
		for(int i = 0; i < upgradeInventory.getSizeInventory(); ++i)
		{
			ItemStack stack = upgradeInventory.getStackInSlot(i);
			if(!stack.isEmpty() && stack.getItem() == type)
				c += stack.getCount();
		}
		return c;
	}

	public boolean isSameLevel(TileBaseSolar other)
	{
		if(other == null)
			return false;
		if(other.instance == null || instance == null)
			return false;
		return Objects.equals(other.instance.delegate, instance.delegate);
	}

	// ***
	// Implementation to fix #56
	// ...so ideally this solution would use 9 bits, but this is Java...
	public boolean cache$seeSky;
	public byte cache$seeSkyTimer;

	public boolean doesSeeSky()
	{
		if(cache$seeSkyTimer < 1)
		{
			cache$seeSkyTimer = 20;
			cache$seeSky = world != null && world.getLightFor(EnumSkyBlock.SKY, pos) > 0 && pos != null ? world.canBlockSeeSky(pos) : false;
		}
		return cache$seeSky;
	}
	// ***

	List<ResourceLocation> tickedUpgrades = new ArrayList<>();

	public void tickUpgrades()
	{
		ItemStack stack;
		ResourceLocation id;

		generation.clearAttributes();
		transfer.clearAttributes();
		capacity.clearAttributes();

		for(int i = 0; i < upgradeInventory.getSizeInventory(); ++i)
		{
			stack = upgradeInventory.getStackInSlot(i);
			if(!stack.isEmpty())
			{
				if(stack.getItem() instanceof ItemUpgrade && ((ItemUpgrade) stack.getItem()).canStayInPanel(this, stack, upgradeInventory))
				{
					id = stack.getItem().getRegistryName();
					if(!tickedUpgrades.contains(id))
					{
						ItemUpgrade iu = (ItemUpgrade) stack.getItem();
						iu.update(this, stack, getUpgrades(iu));
						tickedUpgrades.add(id);
					}
				} else
				{
					// Why non-upgrade items would end up in this inventory?
					// idk, let's drop them!
					ItemStack s = upgradeInventory.removeStackFromSlot(i);
					s.copy();

					upgradeInventory.setInventorySlotContents(i, ItemStack.EMPTY);

					if(!world.isRemote)
						world.spawnEntity(new EntityItem(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, stack));
				}
			}
		}

		IEnergyStorage e;

		for(int i = 0; i < chargeInventory.getSizeInventory(); ++i)
		{
			stack = chargeInventory.getStackInSlot(i);
			if(!stack.isEmpty() && stack.hasCapability(CapabilityEnergy.ENERGY, null) && (e = stack.getCapability(CapabilityEnergy.ENERGY, null)).canReceive() && e.getEnergyStored() < e.getMaxEnergyStored())
			{
				transfer.setBaseValue(instance.transfer);
				int transfer = this.transfer.getValueI();
				energy -= e.receiveEnergy(Math.min(getEnergyStored(), transfer), false);
			}
		}

		tickedUpgrades.clear();
	}

	@Override
	public void update()
	{
		if(cache$seeSkyTimer > 0)
			--cache$seeSkyTimer;

		if(getBlockType() instanceof BlockBaseSolar)
		{
			SolarInfo si = ((BlockBaseSolar) getBlockType()).solarInfo;
			renderConnectedTextures = si.hasConnectedTextures();

			if(si.getGeneration() <= 0)
			{
				world.destroyBlock(pos, true);
				return;
			}

			if(instance == null || !instance.isValid())
			{
				instance = new SolarInstance();
				si.accept(instance);
				return;
			}

			instance.reset();
		}

		if(world.isRemote)
			return;

		if(world.getTotalWorldTime() % 20L == 0L)
			traversal.clear();

		tickUpgrades();

		int gen = getGeneration();
		capacity.setBaseValue(instance.cap);
		energy += Math.min(capacity.getValueL() - energy, gen);
		currentGeneration = gen;

		for(int i = 0; i < crafters.size(); ++i)
		{
			try
			{
				EntityPlayer player = crafters.get(i);
				if(player.openContainer instanceof ContainerBaseSolar)
					player.openContainer.detectAndSendChanges();
				else
					crafters.remove(i);
			} catch(Throwable err)
			{
			}
		}

		energy = Math.min(Math.max(energy, 0), capacity.getValueL());
		{
			for(EnumFacing hor : EnumFacing.HORIZONTALS)
			{
				TileEntity tile = world.getTileEntity(pos.offset(hor));
				if(tile instanceof TileBaseSolar)
					autoBalanceEnergy((TileBaseSolar) tile);
			}

			transfer.setBaseValue(instance.transfer);
			int transfer = this.transfer.getValueI();

			for(EnumFacing hor : EnumFacing.VALUES)
			{
				if(hor == EnumFacing.UP)
					continue;

				TileEntity tile = world.getTileEntity(pos.offset(hor));

				if(tile == null)
					continue;

				if(tile.hasCapability(CapabilityEnergy.ENERGY, hor.getOpposite()))
				{
					IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, hor.getOpposite());
					if(storage.canReceive())
						energy -= storage.receiveEnergy(Math.min(getEnergyStored(), transfer), false);
				}
			}

			if(!traversal.isEmpty())
			{
				for(BlockPosFace traverse : traversal)
				{
					TileEntity tile = world.getTileEntity(traverse.pos);

					if(tile == null)
						continue;

					if(tile.hasCapability(CapabilityEnergy.ENERGY, traverse.face))
					{
						IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, traverse.face);
						if(storage.canReceive())
						{
							energy -= storage.receiveEnergy(Math.min(getEnergyStored(), Math.round(transfer * traverse.rate)), false);
							continue;
						}
					}
				}
			}
		}
	}

	public int getGeneration()
	{
		float effUpgrIncr = .15F;
		float eff = instance.computeSunIntensity(this);
		if(!world.isRemote)
			sunIntensity = eff;
		float energyGeneration = instance.gen * eff;
		generation.setBaseValue(energyGeneration);
		return generation.getValueI();
	}

	public NBTTagCompound write(NBTTagCompound nbt)
	{
		nbt.merge(instance.serializeNBT());
		nbt.setTag("Items", upgradeInventory.writeToNBT(new NBTTagCompound()));
		nbt.setTag("ChargeableItem", chargeInventory.writeToNBT(new NBTTagCompound()));
		nbt.setLong("Energy", energy);
		return nbt;
	}

	public void read(NBTTagCompound nbt)
	{
		instance = SolarInstance.deserialize(nbt);
		upgradeInventory.readFromNBT(nbt.getCompoundTag("Items"));
		chargeInventory.readFromNBT(nbt.getCompoundTag("ChargeableItem"));
		energy = nbt.getLong("Energy");
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY && facing != EnumFacing.UP)
			return true;
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return true;
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY && facing != EnumFacing.UP)
			return (T) this;
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) itemWrapper;
		return super.getCapability(capability, facing);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		return new SPacketUpdateTileEntity(pos, 0, write(new NBTTagCompound()));
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		read(pkt.getNbtCompound());
	}

	public void sync()
	{
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 11);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		read(nbt);
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		return super.writeToNBT(write(nbt));
	}

	public int autoBalanceEnergy(TileBaseSolar solar)
	{
		int delta = getEnergyStored() - solar.getEnergyStored();
		if(delta < 0)
			return solar.autoBalanceEnergy(this);
		else if(delta > 0)
			return extractEnergy(solar.receiveEnergyInternal(extractEnergy(solar.receiveEnergyInternal(delta / 2, true), true), false), false);
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		transfer.setBaseValue(instance.transfer);
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
		transfer.setBaseValue(instance.transfer);
		int transfer = this.transfer.getValueI();
		capacity.setBaseValue(instance.cap);
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
		return (int) Math.min(getVar(1), (long) Integer.MAX_VALUE);
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

	public boolean setBaseValuesOnGet = true;

	@Override
	public long getVar(int id)
	{
		switch(id)
		{
			case 0:
				if(setBaseValuesOnGet)
					capacity.setBaseValue(instance.cap);
				return capacity.getValueL();
			case 1:
				return energy;
			case 2:
				if(setBaseValuesOnGet)
					generation.setBaseValue(instance.gen);
				return generation.getValueL();
			case 3:
				if(setBaseValuesOnGet)
					transfer.setBaseValue(instance.transfer);
				return transfer.getValueL();
			case 4:
				return currentGeneration;
			case 5:
				return FByteHelper.toInt(sunIntensity);
		}

		return 0;
	}

	@Override
	public void setVar(int id, long value)
	{
		switch(id)
		{
			case 0:
				capacity.setValue(value);
				break;
			case 1:
				energy = Math.min(Math.max(value, 0), capacity.getValueL());
				break;
			case 2:
				generation.setValue(value);
				break;
			case 3:
				transfer.setValue(value);
				break;
			case 4:
				currentGeneration = value;
				break;
			case 5:
				sunIntensity = FByteHelper.toFloat((int) value);
				break;
		}
	}

	@Override
	public int getVarCount()
	{
		return 6;
	}

	public ItemStack generateItem(Item item)
	{
		ItemStack stack = new ItemStack(item);
		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setLong("Energy", energy - Math.round(energy * SolarsSF.LOOSE_ENERGY / 100D));
		upgradeInventory.writeToNBT(stack.getTagCompound(), "Upgrades");
		chargeInventory.writeToNBT(stack.getTagCompound(), "Chargeable");
		return stack;
	}

	public void loadFromItem(ItemStack stack)
	{
		if(stack.hasTagCompound())
		{
			energy = stack.getTagCompound().getLong("Energy");
			upgradeInventory.readFromNBT(stack.getTagCompound(), "Upgrades");
			chargeInventory.readFromNBT(stack.getTagCompound(), "Chargeable");
		}
	}
}