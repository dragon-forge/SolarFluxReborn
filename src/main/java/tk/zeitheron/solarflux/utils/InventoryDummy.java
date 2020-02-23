package tk.zeitheron.solarflux.utils;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * This is a part of Hammer Core InventoryDummy is used widely to make inventory
 * code much more simple.
 */
public class InventoryDummy
		implements IInventory, Iterable<ItemStack>
{
	public NonNullList<ItemStack> inventory = NonNullList.withSize(27, ItemStack.EMPTY);
	private final int[] allSlots;
	public int inventoryStackLimit = 64;

	public BiPredicate<Integer, ItemStack> validSlots;
	public Runnable markedDirty;
	public Consumer<EntityPlayer> openInv, closeInv;
	public IVariableHandler fields;

	public InventoryDummy(int inventorySize, NBTTagCompound boundNBT)
	{
		inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
		allSlots = new int[inventory.size()];
		for(int i = 0; i < allSlots.length; ++i)
			allSlots[i] = i;
	}

	public InventoryDummy(NBTTagCompound boundNBT, ItemStack... items)
	{
		inventory = NonNullList.withSize(items.length, ItemStack.EMPTY);
		for(int i = 0; i < items.length; ++i)
			inventory.set(i, items[i]);
		allSlots = new int[items.length];
		for(int i = 0; i < allSlots.length; ++i)
			allSlots[i] = i;
	}

	public InventoryDummy(int inventorySize)
	{
		inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
		allSlots = new int[inventorySize];
		for(int i = 0; i < allSlots.length; ++i)
			allSlots[i] = i;
	}

	public InventoryDummy(ItemStack... items)
	{
		inventory = NonNullList.withSize(items.length, ItemStack.EMPTY);
		for(int i = 0; i < items.length; ++i)
			inventory.set(i, items[i]);
		allSlots = new int[items.length];
		for(int i = 0; i < allSlots.length; ++i)
			allSlots[i] = i;
	}

	public int[] getAllAvaliableSlots()
	{
		return allSlots;
	}

	@Override
	public String getName()
	{
		return "Dummy Inventory";
	}

	@Override
	public boolean hasCustomName()
	{
		return false;
	}

	@Override
	public ITextComponent getDisplayName()
	{
		return new TextComponentString(getName());
	}

	@Override
	public int getSizeInventory()
	{
		return inventory.size();
	}

	@Override
	public ItemStack getStackInSlot(int index)
	{
		try
		{
			return inventory.get(index);
		} catch(Throwable err)
		{
		}
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int slot, int count)
	{
		try
		{
			if(!inventory.get(slot).isEmpty())
			{
				ItemStack is;

				if(inventory.get(slot).getCount() <= count)
				{
					is = inventory.get(slot);
					inventory.set(slot, ItemStack.EMPTY);
					return is;
				} else
				{
					is = inventory.get(slot).splitStack(count);
					if(inventory.get(slot).getCount() == 0)
						inventory.set(slot, ItemStack.EMPTY);
					return is;
				}
			}
		} catch(Throwable err)
		{
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		try
		{
			inventory.set(index, stack);
			if(inventory.get(index).getCount() > Math.min(inventory.get(index).getMaxStackSize(), getInventoryStackLimit()))
				inventory.get(index).setCount(Math.min(inventory.get(index).getMaxStackSize(), getInventoryStackLimit()));
		} catch(Throwable err)
		{
		}
	}

	@Override
	public int getInventoryStackLimit()
	{
		return inventoryStackLimit;
	}

	@Override
	public void markDirty()
	{
		if(markedDirty != null)
			markedDirty.run();
	}

	@Override
	public void openInventory(EntityPlayer player)
	{
		if(openInv != null)
			openInv.accept(player);
	}

	@Override
	public void closeInventory(EntityPlayer player)
	{
		if(closeInv != null)
			closeInv.accept(player);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return validSlots.test(index, stack);
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value)
	{
	}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		inventory.clear();
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		if(nbt != null)
			ItemStackHelper.saveAllItems(nbt, inventory);
		return nbt;
	}

	public InventoryDummy readFromNBT(NBTTagCompound nbt)
	{
		if(nbt != null)
		{
			inventory = NonNullList.withSize(inventory.size(), ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(nbt, inventory);
		}
		return this;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot)
	{
		ItemStack s = getStackInSlot(slot);
		setInventorySlotContents(slot, null);
		return s;
	}

	@Override
	public boolean isEmpty()
	{
		return inventory.isEmpty();
	}

	public boolean isUsableByPlayer(EntityPlayer player, BlockPos from)
	{
		return player.getDistanceSq(from) <= 64D;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return false;
	}

	public void drop(World world, BlockPos pos)
	{
		drop(this, world, pos);
	}

	public static void drop(IInventory inv, World world, BlockPos pos)
	{
		if(inv == null || world == null || pos == null)
			return;

		if(!world.isRemote)
			for(int i = 0; i < inv.getSizeInventory(); ++i)
			{
				ItemStack s = inv.getStackInSlot(i);
				if(s.isEmpty())
					continue;
				EntityItem drop;
				world.spawnEntity(drop = new EntityItem(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, s));
				drop.setNoPickupDelay();
			}

		inv.clear();
	}

	public void writeToNBT(NBTTagCompound tag, String key)
	{
		tag.setTag(key, writeToNBT(new NBTTagCompound()));
	}

	public void readFromNBT(NBTTagCompound tag, String key)
	{
		readFromNBT(tag.getCompoundTag(key));
	}

	@Override
	public Iterator<ItemStack> iterator()
	{
		return inventory.iterator();
	}

	public Stream<ItemStack> stream()
	{
		return inventory.stream();
	}
}