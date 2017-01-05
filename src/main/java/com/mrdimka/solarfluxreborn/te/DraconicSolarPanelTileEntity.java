package com.mrdimka.solarfluxreborn.te;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.mrdimka.common.utils.CommonTileEntity_SFR;
import com.mrdimka.hammercore.common.inventory.InventoryNonTile;
import com.mrdimka.hammercore.energy.IPowerProvider;
import com.mrdimka.solarfluxreborn.blocks.StatefulEnergyStorage;
import com.mrdimka.solarfluxreborn.blocks.modules.EnergySharingModule;
import com.mrdimka.solarfluxreborn.blocks.modules.ITileEntityModule;
import com.mrdimka.solarfluxreborn.blocks.modules.SimpleEnergyDispenserModule;
import com.mrdimka.solarfluxreborn.blocks.modules.TraversalEnergyDispenserModule;
import com.mrdimka.solarfluxreborn.config.ModConfiguration;
import com.mrdimka.solarfluxreborn.config.TierConfiguration;
import com.mrdimka.solarfluxreborn.init.ModItems;
import com.mrdimka.solarfluxreborn.items.UpgradeItem;
import com.mrdimka.solarfluxreborn.reference.NBTConstants;
import com.mrdimka.solarfluxreborn.reference.Reference;
import com.mrdimka.solarfluxreborn.utility.Utils;

import cofh.api.energy.IEnergyProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class DraconicSolarPanelTileEntity extends SolarPanelTileEntity
{
	public DraconicSolarPanelTileEntity() {}
	
	protected int maxGen, cap, transfer;
	protected String name;
	
	public DraconicSolarPanelTileEntity(String name, int cap, int transfer, int maxGen)
	{
		super(0);
		this.maxGen = maxGen;
		this.name = name;
		mEnergyStorage = new StatefulEnergyStorage(cap, transfer, transfer);
		this.cap = cap;
		this.transfer = transfer;
	}
	
	@Override
	public int getMaximumEnergyGeneration()
	{
		return maxGen;
	}
	
	@Override
	public ResourceLocation getTopResource()
	{
		return null;
	}
	
	@Override
	public void update()
	{
		mEnergyStorage.setMaxEnergyStored(cap);
		mEnergyStorage.setMaxTransfer(transfer);
		super.update();
	}
	
	@Override
	protected void loadDataFromNBT(NBTTagCompound pNBT)
	{
		super.loadDataFromNBT(pNBT);
		mEnergyStorage.setMaxEnergyStored(cap);
		mEnergyStorage.setMaxTransfer(transfer);
	}
}