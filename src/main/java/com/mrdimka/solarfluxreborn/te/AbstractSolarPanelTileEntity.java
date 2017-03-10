package com.mrdimka.solarfluxreborn.te;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import com.mrdimka.solarfluxreborn.blocks.StatefulEnergyStorage;
import com.mrdimka.solarfluxreborn.reference.Reference;

public class AbstractSolarPanelTileEntity extends SolarPanelTileEntity
{
	public AbstractSolarPanelTileEntity() {}
	
	protected int maxGen, cap, transfer;
	protected String name;
	
	public AbstractSolarPanelTileEntity(String name, int cap, int transfer, int maxGen)
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
		return new ResourceLocation(Reference.MOD_ID + ":blocks/" + name + "_1");
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