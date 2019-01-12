package com.zeitheron.solarflux.items;

import java.util.UUID;

import com.zeitheron.solarflux.InfoSF;
import com.zeitheron.solarflux.api.attribute.AttributeModMultiply;
import com.zeitheron.solarflux.block.tile.TileBaseSolar;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTransferRateUpgrade extends ItemUpgrade
{
	public ItemTransferRateUpgrade()
	{
		setRegistryName(InfoSF.MOD_ID, "transfer_rate_upgrade");
	}
	
	@Override
	public int getMaxUpgrades()
	{
		return 10;
	}
	
	public static final UUID TRANSFER_RATE_ATTRIBUTE_UUID = UUID.fromString("28575922-b562a-c364d-788af-337a6b8f5a8a");
	
	@Override
	public void update(TileBaseSolar tile, ItemStack stack, int amount)
	{
		amount = Math.min(amount, 10);
		tile.transfer.applyModifier(new AttributeModMultiply(1F + (amount * .15F)), TRANSFER_RATE_ATTRIBUTE_UUID);
	}
}