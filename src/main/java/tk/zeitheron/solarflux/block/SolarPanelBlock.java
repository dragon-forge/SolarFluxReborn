package tk.zeitheron.solarflux.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import tk.zeitheron.solarflux.panels.SolarPanel;

public class SolarPanelBlock extends ContainerBlock
{
	public final SolarPanel panel;
	
	public SolarPanelBlock(SolarPanel panel)
	{
		super(Properties.create(Material.IRON).harvestLevel(1).harvestTool(ToolType.PICKAXE).hardnessAndResistance(1.5F).variableOpacity().sound(SoundType.METAL));
		this.panel = panel;
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState p_149645_1_)
	{
		return BlockRenderType.MODEL;
	}
	
	@Override
	public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		return false;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
	{
		TileEntity tile = world.getTileEntity(pos);
		SolarPanelTile spt = tile instanceof SolarPanelTile ? (SolarPanelTile) tile : null;
		if(spt != null)
			return spt.getShape(this);
		return recalcShape(world, pos);
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
	{
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
		TileEntity tile = worldIn.getTileEntity(pos);
		SolarPanelTile spt = tile instanceof SolarPanelTile ? (SolarPanelTile) tile : null;
		if(spt != null)
			spt.resetVoxelShape();
	}
	
	public VoxelShape recalcShape(IBlockReader world, BlockPos pos)
	{
		VoxelShape baseShape = VoxelShapes.create(0, 0, 0, 1, panel.networkData.height, 1);
		List<VoxelShape> shapes = new ArrayList<>(8);
		
		boolean west = false, east = false, north = false, south = false;
		
		float h = panel.getClientPanelData().height, h2 = h + 0.25F / 16F;
		
		if(west = world.getBlockState(pos.west()).getBlock() != this)
			shapes.add(VoxelShapes.create(0, h, 1 / 16F, 1 / 16F, h2, 15 / 16F));
		
		if(east = world.getBlockState(pos.east()).getBlock() != this)
			shapes.add(VoxelShapes.create(15 / 16F, h, 1 / 16F, 1, h2, 15 / 16F));
		
		if(north = world.getBlockState(pos.north()).getBlock() != this)
			shapes.add(VoxelShapes.create(1 / 16F, h, 0, 15 / 16F, h2, 1 / 16F));
		
		if(south = world.getBlockState(pos.south()).getBlock() != this)
			shapes.add(VoxelShapes.create(1 / 16F, h, 15 / 16F, 15 / 16F, h2, 1));
		
		if(west || north || world.getBlockState(pos.west().north()).getBlock() != this)
			shapes.add(VoxelShapes.create(0, h, 0, 1 / 16F, h2, 1 / 16F));
		
		if(east || north || world.getBlockState(pos.east().north()).getBlock() != this)
			shapes.add(VoxelShapes.create(15 / 16F, h, 0, 1, h2, 1 / 16F));
		
		if(south || east || world.getBlockState(pos.south().east()).getBlock() != this)
			shapes.add(VoxelShapes.create(15 / 16F, h, 15 / 16F, 1, h2, 1));
		
		if(west || south || world.getBlockState(pos.west().south()).getBlock() != this)
			shapes.add(VoxelShapes.create(0, h, 15 / 16F, 1 / 16F, h2, 1));
		
		return VoxelShapes.or(baseShape, shapes.toArray(new VoxelShape[shapes.size()]));
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		return true;
	}
	
	@Override
	public boolean isSolid(BlockState state)
	{
		return false;
	}
	
	@Override
	public boolean doesSideBlockRendering(BlockState state, IEnviromentBlockReader world, BlockPos pos, Direction face)
	{
		if(world.getBlockState(pos.offset(face)).getBlock() == this)
			return false;
		return super.doesSideBlockRendering(state, world, pos, face);
	}
	
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn)
	{
		return new SolarPanelTile();
	}
}