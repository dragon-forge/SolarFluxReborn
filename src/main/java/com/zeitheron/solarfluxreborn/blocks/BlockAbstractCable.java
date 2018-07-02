package com.zeitheron.solarfluxreborn.blocks;

import com.zeitheron.hammercore.api.mhb.BlockTraceable;
import com.zeitheron.hammercore.api.mhb.ICubeManager;
import com.zeitheron.hammercore.internal.TeslaAPI;
import com.zeitheron.hammercore.internal.capabilities.CapabilityEJ;
import com.zeitheron.hammercore.utils.WorldUtil;
import com.zeitheron.hammercore.utils.math.vec.Cuboid6;
import com.zeitheron.solarfluxreborn.creativetab.CreativeTabSFR;
import com.zeitheron.solarfluxreborn.te.cable.TileCustomCable;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

public class BlockAbstractCable extends BlockTraceable implements ITileEntityProvider, ICubeManager
{
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	
	public BlockAbstractCable()
	{
		super(Material.IRON);
		setSoundType(SoundType.METAL);
		setLightOpacity(255);
		useNeighborBrightness = true;
		setCreativeTab(CreativeTabSFR.MOD_TAB);
		setHardness(3.0F);
		setHarvestLevel("pickaxe", 0);
		setResistance(5.0F);
	}
	
	public double getTransferRate()
	{
		return 0D;
	}
	
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1)
	{
		return new TileCustomCable(getTransferRate());
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState p_isOpaqueCube_1_)
	{
		return false;
	}
	
	@Override
	public boolean isFullCube(IBlockState p_isFullCube_1_)
	{
		return false;
	}
	
	@Override
	public Cuboid6[] getCuboids(World world, BlockPos pos, IBlockState state)
	{
		TileCustomCable cc = WorldUtil.cast(world.getTileEntity(pos), TileCustomCable.class);
		return cc != null ? cc.getCuboids() : new Cuboid6[0];
	}
	
	@Override
	public int getLightOpacity(IBlockState p_getLightOpacity_1_)
	{
		return 0;
	}
	
	@Override
	public int getLightOpacity(IBlockState p_getLightOpacity_1_, IBlockAccess p_getLightOpacity_2_, BlockPos p_getLightOpacity_3_)
	{
		return 0;
	}
	
	@Override
	public AxisAlignedBB getFullBoundingBox(IBlockAccess world, BlockPos pos, IBlockState state)
	{
		return new AxisAlignedBB(6 / 16D, 6 / 16D, 6 / 16D, 10 / 16D, 10 / 16D, 10 / 16D);
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
	{
		TileCustomCable tcc = WorldUtil.cast(world.getTileEntity(pos), TileCustomCable.class);
		if(tcc != null)
			tcc.cubs = tcc.bake();
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return 0;
	}
	
	@Override
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState();
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}
	
	public boolean connects(IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		TileEntity tile = world.getTileEntity(pos.offset(side));
		if(tile != null && (tile.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite()) || tile.hasCapability(CapabilityEJ.ENERGY, side.getOpposite()) || TeslaAPI.isTeslaHolder(tile)))
			return true;
		return false;
	}
	
	public static boolean connectsTo(IBlockState state, EnumFacing to)
	{
		if(to == EnumFacing.UP)
			return state.getValue(UP);
		if(to == EnumFacing.DOWN)
			return state.getValue(DOWN);
		if(to == EnumFacing.EAST)
			return state.getValue(EAST);
		if(to == EnumFacing.WEST)
			return state.getValue(WEST);
		if(to == EnumFacing.SOUTH)
			return state.getValue(SOUTH);
		if(to == EnumFacing.NORTH)
			return state.getValue(NORTH);
		return false;
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		return state //
		        .withProperty(NORTH, connects(worldIn, pos, EnumFacing.NORTH)) //
		        .withProperty(EAST, connects(worldIn, pos, EnumFacing.EAST)) //
		        .withProperty(SOUTH, connects(worldIn, pos, EnumFacing.SOUTH)) //
		        .withProperty(WEST, connects(worldIn, pos, EnumFacing.WEST)) //
		        .withProperty(UP, connects(worldIn, pos, EnumFacing.UP)) //
		        .withProperty(DOWN, connects(worldIn, pos, EnumFacing.DOWN));
	}
}