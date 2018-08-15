package com.zeitheron.solarflux.block;

import java.util.List;

import com.zeitheron.solarflux.SolarFlux;
import com.zeitheron.solarflux.api.SolarFluxAPI;
import com.zeitheron.solarflux.api.SolarInfo;
import com.zeitheron.solarflux.api.SolarInstance;
import com.zeitheron.solarflux.block.tile.TileBaseSolar;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBaseSolar extends Block implements ITileEntityProvider
{
	public final SolarInfo solarInfo;
	
	public BlockBaseSolar(SolarInfo solarInfo)
	{
		super(Material.IRON);
		setSoundType(SoundType.METAL);
		this.solarInfo = solarInfo;
		ResourceLocation r = solarInfo.getRegistryName();
		setRegistryName(r.getNamespace(), "solar_panel_" + r.getPath());
		setTranslationKey(getRegistryName().toString());
		setHardness(4F);
		setHarvestLevel("pickaxe", 2);
	}
	
	protected AxisAlignedBB aabb = new AxisAlignedBB(0, 0, 0, 1, 0.375, 1);
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return aabb;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isFullBlock(IBlockState state)
	{
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
	{
		if(isSideSolid(state, worldIn, pos, face))
			return BlockFaceShape.SOLID;
		return BlockFaceShape.UNDEFINED;
	}
	
	@Override
	public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		return side == EnumFacing.DOWN;
	}
	
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(solarInfo.maxGeneration <= 0 || solarInfo.maxCapacity <= 0 || solarInfo.maxTransfer <= 0)
			return;
		if(tab == SolarFluxAPI.tab)
			items.add(new ItemStack(this));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(I18n.format("info.solarflux.energy.generation", solarInfo.maxGeneration));
		tooltip.add(I18n.format("info.solarflux.energy.transfer", solarInfo.maxTransfer));
		tooltip.add(I18n.format("info.solarflux.energy.capacity", solarInfo.maxCapacity));
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		SolarInstance i = new SolarInstance();
		i.delegate = solarInfo.getRegistryName();
		i.reset();
		return new TileBaseSolar(i);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TileBaseSolar)
			playerIn.openGui(SolarFlux.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
}