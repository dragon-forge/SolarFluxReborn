package com.zeitheron.solarfluxreborn.gui;

import com.zeitheron.solarfluxreborn.te.SolarPanelTileEntity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity != null)
		{
			if(tileEntity instanceof SolarPanelTileEntity)
			{
				return new ContainerSolarPanel(player.inventory, (SolarPanelTileEntity) tileEntity);
			}
		}
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity != null)
		{
			if(tileEntity instanceof SolarPanelTileEntity)
			{
				return new GuiSolarPanel(player.inventory, (SolarPanelTileEntity) tileEntity);
			}
		}
		return null;
	}
}
