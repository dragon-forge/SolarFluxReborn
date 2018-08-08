package com.zeitheron.solarflux.gui;

import java.util.Set;

import com.zeitheron.solarflux.block.tile.TileBaseSolar;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandlerSF implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 0)
		{
			TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
			if(tile instanceof TileBaseSolar)
				return new ContainerBaseSolar((TileBaseSolar) tile, player.inventory);
		}
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == 0)
		{
			TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
			if(tile instanceof TileBaseSolar)
				return new GuiBaseSolar((TileBaseSolar) tile, player.inventory);
		}
		return null;
	}
}