package com.zeitheron.solarfluxreborn.blocks.modules;

import net.minecraft.tileentity.TileEntity;

public abstract class AbstractTileEntityModule<T extends TileEntity> implements iTileEntityModule
{
	private final T mTileEntity;
	
	protected AbstractTileEntityModule(T pTileEntity)
	{
		mTileEntity = pTileEntity;
	}
	
	public T getTileEntity()
	{
		return mTileEntity;
	}
}
