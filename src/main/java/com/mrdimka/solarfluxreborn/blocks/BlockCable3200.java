package com.mrdimka.solarfluxreborn.blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import cofh.api.energy.IEnergyConnection;

import com.mrdimka.hammercore.ext.TeslaAPI;
import com.mrdimka.solarfluxreborn.creativetab.ModCreativeTab;
import com.mrdimka.solarfluxreborn.reference.Reference;
import com.mrdimka.solarfluxreborn.te.cable.TileCustomCable;

public class BlockCable3200 extends BlockContainer
{
	public static double TRANSFER_RATE = 3200D;
	
	public static final AxisAlignedBB
									CENTER = new AxisAlignedBB(6D / 16D, 6D / 16D, 6D / 16D, 10D / 16D, 10D / 16D, 10D / 16D),
									CENTER_UP = new AxisAlignedBB(6D / 16D, 10D / 16D, 6D / 16D, 10D / 16D, 1D, 10D / 16D),
									CENTER_DOWN = new AxisAlignedBB(6D / 16D, 6D / 16D, 6D / 16D, 10D / 16D, 0D, 10D / 16D),
									CENTER_EAST = new AxisAlignedBB(6D / 16D, 6D / 16D, 6D / 16D, 1D, 10D / 16D, 10D / 16D),
									CENTER_WEST = new AxisAlignedBB(0D, 6D / 16D, 6D / 16D, 6D / 16D, 10D / 16D, 10D / 16D),
									CENTER_SOUTH = new AxisAlignedBB(6D / 16D, 6D / 16D, 6D / 16D, 10D / 16D, 10D / 16D, 1D),
									CENTER_NORTH = new AxisAlignedBB(6D / 16D, 6D / 16D, 0D, 10D / 16D, 10D / 16D, 6D / 16D);
	
	public BlockCable3200()
	{
		super(Material.IRON);
		setSoundType(SoundType.METAL);
		setUnlocalizedName(Reference.MOD_ID + ":wire_2");
		setLightOpacity(255);
        useNeighborBrightness = true;
        setCreativeTab(ModCreativeTab.MOD_TAB);
        setHardness(3.0F);
        setHarvestLevel("pickaxe", 0);
        setResistance(5.0F);
	}
	
	@Override
	public TileEntity createNewTileEntity(World arg0, int arg1)
	{
		return new TileCustomCable(TRANSFER_RATE, 2);
	}
	
	@Override
    public boolean isOpaqueCube(IBlockState p_isOpaqueCube_1_)
    {
    	return false;
    }
    
    @Override
    public boolean isFullyOpaque(IBlockState p_isFullyOpaque_1_)
    {
    	return false;
    }
    
    @Override
    public boolean isFullCube(IBlockState p_isFullCube_1_)
    {
    	return false;
    }
    
    @Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess w, BlockPos p)
	{
		Map<EnumFacing, Boolean> conns = new HashMap<EnumFacing, Boolean>();
		
		for(EnumFacing f : EnumFacing.VALUES)
		{
			conns.put(f, false);
			
			TileEntity te = w.getTileEntity(p.offset(f));
			
			if(te == null) continue;
			
			if(te != null && TileCustomCable.class.isAssignableFrom(te.getClass())) conns.put(f, true);
			else if(te instanceof IEnergyConnection && ((IEnergyConnection) te).canConnectEnergy(f.getOpposite())) conns.put(f, true);
			else if(te.hasCapability(CapabilityEnergy.ENERGY, f.getOpposite())) conns.put(f, true);
			else if(TeslaAPI.isTeslaConsumer(te)) conns.put(f, true);
		}
		
		double nx = 5D / 16D, ny = 5D / 16D, nz = 5D / 16D, xx = 11D / 16D, xy = 11D / 16D, xz = 11D / 16D;
		
		if(conns.get(EnumFacing.SOUTH)) xz = 1D;
		if(conns.get(EnumFacing.NORTH)) nz = 0D;
		if(conns.get(EnumFacing.EAST)) xx = 1D;
		if(conns.get(EnumFacing.WEST)) nx = 0D;
		if(conns.get(EnumFacing.UP)) xy = 1D;
		if(conns.get(EnumFacing.DOWN)) ny = 0D;
		
		return new AxisAlignedBB(nx, ny, nz, xx, xy, xz);
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState s, World w, BlockPos p, AxisAlignedBB box, List<AxisAlignedBB> l, Entity ent, boolean something)
	{
		addCollisionBoxToList(p, box, l, CENTER);
		
		Map<EnumFacing, Boolean> conns = new HashMap<EnumFacing, Boolean>();
		
		for(EnumFacing f : EnumFacing.VALUES)
		{
			conns.put(f, false);
			
			TileEntity te = w.getTileEntity(p.offset(f));
			
			if(te == null) continue;
			
			if(te != null && TileCustomCable.class.isAssignableFrom(te.getClass())) conns.put(f, true);
			else if(te instanceof IEnergyConnection && ((IEnergyConnection) te).canConnectEnergy(f.getOpposite())) conns.put(f, true);
			else if(te.hasCapability(CapabilityEnergy.ENERGY, f.getOpposite())) conns.put(f, true);
			else if(TeslaAPI.isTeslaConsumer(te)) conns.put(f, true);
		}
		
		double nx = 5D / 16D, ny = 5D / 16D, nz = 5D / 16D, xx = 11D / 16D, xy = 11D / 16D, xz = 11D / 16D;
		
		if(conns.get(EnumFacing.SOUTH)) addCollisionBoxToList(p, box, l, CENTER_SOUTH);
		if(conns.get(EnumFacing.NORTH)) addCollisionBoxToList(p, box, l, CENTER_NORTH);
		if(conns.get(EnumFacing.EAST)) addCollisionBoxToList(p, box, l, CENTER_EAST);
		if(conns.get(EnumFacing.WEST)) addCollisionBoxToList(p, box, l, CENTER_WEST);
		if(conns.get(EnumFacing.UP)) addCollisionBoxToList(p, box, l, CENTER_UP);
		if(conns.get(EnumFacing.DOWN)) addCollisionBoxToList(p, box, l, CENTER_DOWN);
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
}